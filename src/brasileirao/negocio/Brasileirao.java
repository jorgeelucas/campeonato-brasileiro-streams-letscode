package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Locale.forLanguageTag;

public class Brasileirao {

    private Map<Integer, List<Jogo>> brasileirao;
    private List<Jogo> jogos;
    private Predicate<Jogo> filtro;

    public Brasileirao(Path arquivo, Predicate<Jogo> filtro) throws IOException {
        this.jogos = lerArquivo(arquivo);
        this.filtro = filtro;
        this.brasileirao = jogos.stream()
                .filter(filtro) //filtrar por ano
                .collect(Collectors.groupingBy(
                        Jogo::rodada,
                        Collectors.mapping(Function.identity(), Collectors.toList())));

    }

    public Map<Jogo, Integer> mediaGolsPorJogo() {

        Function<Jogo, Integer> jogoIntegerFunction = jogo -> jogo.mandantePlacar() + jogo.visitantePlacar();

        Map<Jogo, Integer> collect = jogos
                .stream()
                .filter(filtro)
                .collect(Collectors.toMap(jogo -> jogo, jogoIntegerFunction));

        return collect;

    }

    public IntSummaryStatistics estatisticasPorJogo() {

        IntSummaryStatistics intSummaryStatistics = mediaGolsPorJogo()
                .values()
                .stream()
                .mapToInt(gol -> gol)
                .summaryStatistics();

        return intSummaryStatistics;
    }

    public List<Jogo> todosOsJogos() {

        List<Jogo> jogos = brasileirao
                .values()
                .stream()
                .flatMap(Collection::stream)
                .toList();

        return jogos;
    }

    public Long totalVitoriasEmCasa() {

        long total = todosOsJogos()
                .stream()
                .filter(jogo -> jogo.mandantePlacar() > jogo.visitantePlacar())
                .count();

        return total;
    }

    public Long totalVitoriasForaDeCasa() {

        Predicate<Jogo> jogoPredicate = jogo -> jogo.visitantePlacar() > jogo.mandantePlacar();

        long total = todosOsJogos()
                .stream()
                .filter(jogoPredicate)
                .count();

        return total;
    }

    public Long totalEmpates() {

        Predicate<Jogo> jogoPredicate = jogo -> jogo.visitantePlacar().equals(jogo.mandantePlacar());

        long count = todosOsJogos()
                .stream()
                .filter(jogoPredicate)
                .count();

        return count;
    }

    public Long totalJogosComMenosDe3Gols() {

        Predicate<Jogo> jogoPredicate = jogo -> jogo.mandantePlacar() + jogo.visitantePlacar() < 3;

        long count = todosOsJogos()
                .stream()
                .filter(jogoPredicate)
                .count();

        return count;
    }

    public Long totalJogosCom3OuMaisGols() {

        Predicate<Jogo> jogoPredicate = jogo -> jogo.mandantePlacar() + jogo.visitantePlacar() > 2;

        long count = todosOsJogos()
                .stream()
                .filter(jogoPredicate)
                .count();

        return count;
    }

    public Map<Resultado, Long> todosOsPlacares() {

        Collector<Jogo, ?, Map<Resultado, Long>> jogoMapCollector = Collectors.groupingBy(jogo ->
                new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar()), Collectors.counting());

        Map<Resultado, Long> placares = todosOsJogos()
                .stream()
                .collect(jogoMapCollector);

        return placares;
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {

        Map.Entry<Resultado, Long> resultadoLongEntry = todosOsPlacares()
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElse(null);

        return resultadoLongEntry;
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {

        Map.Entry<Resultado, Long> resultadoLongEntry = todosOsPlacares()
                .entrySet()
                .stream()
                .min(Comparator.comparing(Map.Entry::getValue))
                .orElse(null);

        return resultadoLongEntry;
    }

    private List<Time> todosOsTimes() {
        List<Time> mandantes = todosOsJogos()
                .stream()
                .map(Jogo::mandante)
                .toList();

        List<Time> visitantes = todosOsJogos()
                .stream()
                .map(Jogo::visitante)
                .toList();

        List<Time> times = Stream.of(mandantes, visitantes)
                .flatMap(List::stream)
                .distinct()
                .toList();

        return times;
    }

    /**
     * todos os jogos que cada time foi mandante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {

        Map<Time, List<Jogo>> jogos = todosOsJogos()
                .stream()
                .collect(Collectors.groupingBy(Jogo::mandante, Collectors.mapping(Function.identity(), Collectors.toList())));

        return jogos;
    }

    /**
     * todos os jogos que cada time foi visitante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {

        Map<Time, List<Jogo>> jogos = todosOsJogos()
                .stream()
                .collect(Collectors.groupingBy(Jogo::visitante, Collectors.mapping(Function.identity(), Collectors.toList())));

        return jogos;
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {


        return Stream.of(todosOsJogosPorTimeComoMandantes(), todosOsJogosPorTimeComoVisitante())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (t1, t2) -> Stream.of(t1, t2).flatMap(List::stream).toList()
                ));
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {

        return todosOsJogosPorTime()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue().stream().collect(Collectors.partitioningBy(
                                jogo -> entry.getKey().nome().equals(jogo.mandante())
                        ))
                ));
    }

    public Set<PosicaoTabela> tabela() {
        return null;
    }

    public List<Jogo> lerArquivo(Path file) throws IOException {

//        List<String[]> strings = Files.readAllLines(file)
//                .stream()
//                .map(line -> line.split(";"))
//                .toList();
//        strings.stream();

        Scanner ler = new Scanner(file);
        ler.nextLine();

        List<Jogo> jogos = new ArrayList<>();

        while (ler.hasNextLine()) {
            String linha = ler.next();
            Scanner scLinha = new Scanner(linha);
            Scanner sc = scLinha.useDelimiter(";");

            Integer rodada = sc.nextInt();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.parse(sc.next(), dtf);

            String time = sc.next();
//            if (time.contains("h")) {time.replace("h", ":");};
            LocalTime localTime = LocalTime.parse(time.replace("h", ":"));

//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", forLanguageTag("pt-br"));
//            TemporalAccessor accessor = formatter.parse(sc.next());
//            DayOfWeek dayOfWeek = DayOfWeek.from(accessor);
            DayOfWeek dayOfWeek = getDayOfWeek(sc.next());


            Time timeMandante = new Time(sc.next());
            Time timeVisitante = new Time(sc.next());
            Time vencedor = new Time(sc.next());
            String arena = sc.next();
            Integer mandantePlacar = sc.nextInt();
            Integer visitantePlacar = sc.nextInt();
            String estadoMandante = sc.next();
            String estadoVisitante = sc.next();
            String estadoVencedor = sc.next();

            DataDoJogo dataDoJogo = new DataDoJogo(localDate, localTime, dayOfWeek);

            jogos.add(new Jogo(rodada,
                    dataDoJogo,
                    timeMandante,
                    timeVisitante,
                    vencedor,
                    arena,
                    mandantePlacar,
                    visitantePlacar,
                    estadoMandante,
                    estadoVisitante,
                    estadoVencedor));
        }
        return jogos;
    }

    private DayOfWeek getDayOfWeek(String dia) {
        return Map.of(
                "Segunda-feira", DayOfWeek.SUNDAY,
                "Terça-feira", DayOfWeek.SUNDAY,
                "Quarta-feira", DayOfWeek.SUNDAY,
                "Quinta-feira", DayOfWeek.SUNDAY,
                "Sexta-feira", DayOfWeek.SUNDAY,
                "Sábado", DayOfWeek.SUNDAY,
                "Domingo", DayOfWeek.SUNDAY
        ).get(dia);
    }

    // METODOS EXTRA

    private Map<Integer, Integer> totalGolsPorRodada() {
        return null;
    }

    private Map<Time, Integer> totalDeGolsPorTime() {
        return null;
    }

    private Map<Integer, Double> mediaDeGolsPorRodada() {
        return null;
    }


}

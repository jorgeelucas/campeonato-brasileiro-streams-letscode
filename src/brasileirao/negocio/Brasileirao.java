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
        //Pegar a soma de gols de cada jogo, fazer a média de gols para cada jogo, retornar como um map

        Function<Jogo, Integer> jogoIntegerFunction = jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar())/2;

        Map<Jogo, Integer> media = jogos
                .stream()
                .collect(Collectors.toMap(Function.identity(), jogoIntegerFunction));

        return media;

    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return null;
    }

    public List<Jogo> todosOsJogos() {
        return null;
    }

    public Long totalVitoriasEmCasa() {
        return null;
    }

    public Long totalVitoriasForaDeCasa() {
        return null;
    }

    public Long totalEmpates() {
        return null;
    }

    public Long totalJogosComMenosDe3Gols() {
        return null;
    }

    public Long totalJogosCom3OuMaisGols() {
        return null;
    }

    public Map<Resultado, Long> todosOsPlacares() {
        return null;
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        return null;
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        return null;
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

        return null;
    }

    /**
     * todos os jogos que cada time foi mandante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {
        return null;
    }

    /**
     * todos os jogos que cada time foi visitante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {
        return null;
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {
        return null;
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {
        return null;
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
            if (time.contains("h")) time.replace("h", ":");
            LocalTime localTime = LocalTime.parse(time);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", forLanguageTag("pt-br"));
            TemporalAccessor accessor = formatter.parse(sc.next());
            DayOfWeek dayOfWeek = DayOfWeek.from(accessor);

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

            jogos.add(new Jogo(rodada, dataDoJogo, timeMandante, timeVisitante, vencedor, arena, mandantePlacar, visitantePlacar, estadoMandante, estadoVisitante, estadoVencedor));
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

package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Double mediaGolsPorJogo() {

        double contagemJogos = todosOsJogos().stream().count();

        double golsTotais = todosOsJogos().stream().mapToInt(x -> x.mandantePlacar() + x.visitantePlacar()).sum();

        return golsTotais/contagemJogos;

    }

    public IntSummaryStatistics estatisticasPorJogo() {

        IntSummaryStatistics statistics = todosOsJogos().stream()
                .mapToInt(x -> x.visitantePlacar() + x.mandantePlacar())
                .summaryStatistics();

        return statistics;
    }

    public List<Jogo> todosOsJogos() {

        return this.brasileirao.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public Long totalVitoriasEmCasa() {

        List<Jogo> jogos = todosOsJogos();

        return jogos.stream().filter(x -> x.estadoMandante().equals(x.estadoVencedor()) && x.mandantePlacar()> x.visitantePlacar()).count();
    }

    public Long totalVitoriasForaDeCasa() {

        return todosOsJogos().stream().filter(x -> x.estadoVisitante().equals(x.estadoVencedor()) && x.visitantePlacar() > x.mandantePlacar()).count();
    }

    public Long totalEmpates() {

        return todosOsJogos().stream().filter(x -> (x.mandantePlacar() == x.visitantePlacar())).count();
    }

    public Long totalJogosComMenosDe3Gols() {

        List<Jogo> jogos = todosOsJogos();

        return jogos.stream().filter(x -> (x.mandantePlacar()+ x.visitantePlacar()) < 3).count();
    }

    public Long totalJogosCom3OuMaisGols() {

        List<Jogo> jogos = todosOsJogos();

        return jogos.stream().filter(x -> (x.mandantePlacar()+ x.visitantePlacar()) >= 3).count();
    }

    public Map<Resultado, Long> todosOsPlacares() {

        Map<Resultado, Long> todosPlacares = todosOsJogos().stream()
                .map(x -> new Resultado(x.mandantePlacar(), x.visitantePlacar()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.mapping(Function.identity(), Collectors.counting())));

        return todosPlacares;
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {

        Optional<Map.Entry<Resultado, Long>> maxEntry = todosOsPlacares()
                                                    .entrySet()
                                                    .stream()
                                                    .max(Map.Entry.comparingByValue());
        return maxEntry.get();
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {

        Optional<Map.Entry<Resultado, Long>> minEntry = todosOsPlacares()
                                                    .entrySet()
                                                    .stream()
                                                    .min(Map.Entry.comparingByValue());

        return minEntry.get();
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

        List<Time> todosTimes = new ArrayList<>();
        todosTimes.addAll(mandantes);
        todosTimes.addAll(visitantes);

        return todosTimes;
    }

    /**
     * todos os jogos que cada time foi mandante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {

        Map<Time, List<Jogo>> jogosMandante = todosOsJogos().stream()
                                                .collect(Collectors.groupingBy(
                                                        Jogo::mandante,
                                                        Collectors.mapping(Function.identity(), Collectors.toList())));

        return jogosMandante;
    }

    /**
     * todos os jogos que cada time foi visitante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {

        Map<Time, List<Jogo>> jogosVisitante = todosOsJogos().stream()
                                                .collect(Collectors.groupingBy(
                                                        Jogo::visitante,
                                                        Collectors.mapping(Function.identity(), Collectors.toList())));
        return jogosVisitante;
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {

//        var m1 = todosOsJogosPorTimeComoMandantes();
//        var m2 = todosOsJogosPorTimeComoVisitante();
//
//         m2.forEach((K, V) -> {
//            m1.merge(K, V, (list1, list2) -> {
//                list1.addAll(list2);
//                return list1;
//            });
//        });

        Map<Time, List<Jogo>> todosJogosTime = Stream.of(todosOsJogosPorTimeComoMandantes(), todosOsJogosPorTimeComoVisitante())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue, (v1,v2) -> {v1.addAll(v2); return v1;} ));


        return todosJogosTime;
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {

//        Map<Time, List<Jogo>> collect = todosOsJogos().stream()
//                .collect(Collectors.groupingBy(Jogo::mandante,Collectors.partitioningBy(x -> x.mandante())))
//
//        todosOsJogos().stream()
//                .collect(
//                        Collectors.partitioningBy(x -> x.mandante().equals(x.))

        return null;
    }

    public Set<PosicaoTabela> tabela() {


//        todosOsJogosPorTime()
//                .entrySet()
//                .stream()
//                .map(x -> new PosicaoTabela(x.getKey(),
//                                x.getValue().stream().forEach(
//                                        i -> { if(i.estadoMandante().equals(i.estadoVencedor()) && i.mandantePlacar() > i.visitantePlacar()){
//                                        }
//                                        }
//                    //    x.getValue().stream().collect(Collectors.)))

//        Flamengo, pontos=71, vitorias=21, derrotas=9, empates=8, golsPositivos=68, golsSofridos=48, saldoDeGols=20}

        return null;
    }

    public List<Jogo> lerArquivo(Path file) throws IOException {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


        List<Jogo> jogosList = Files.lines(file)
                .skip(1)
                .map(line -> line.split(";"))
                .map(field -> new Jogo(Integer.parseInt(field[0]),
                        new DataDoJogo(LocalDate.parse(field[1], dateFormatter), LocalTime.parse(field[2].replace("h", ":"), timeFormatter), getDayOfWeek(field[3])),
                        new Time(field[4]),
                        new Time(field[5]),
                        new Time(field[6]),
                        field[7],
                        Integer.parseInt(field[8]),
                        Integer.parseInt(field[9]),
                        field[10],
                        field[11],
                        field[12]))
                .toList();


        return jogosList;
    }

    private DayOfWeek getDayOfWeek(String dia) {
        return Map.of(
                "Segunda-Feira", DayOfWeek.MONDAY,
                "Terça-Feira", DayOfWeek.TUESDAY,
                "Quarta-Feira", DayOfWeek.WEDNESDAY,
                "Quinta-Feira", DayOfWeek.THURSDAY,
                "Sexta-Feira", DayOfWeek.FRIDAY,
                "Sábado", DayOfWeek.SATURDAY,
                "Domingo", DayOfWeek.SUNDAY
        ).get(dia);
    }

    // METODOS EXTRA

    private Map<Integer, Integer> totalGolsPorRodada() {

        Map<Integer, Integer> golsPorRodada = todosOsJogos().stream()
                .collect(Collectors.groupingBy(Jogo::rodada, Collectors.summingInt(x -> x.visitantePlacar() + x.mandantePlacar())));


        return golsPorRodada;
    }

    private Map<Time, Integer> totalDeGolsPorTime() {


        Map<Time, Integer> golsPorTimeMandante = todosOsJogos().stream()
                .collect(Collectors.groupingBy(Jogo::mandante, Collectors.summingInt(x -> x.mandantePlacar())));

        Map<Time, Integer> golsTimeVencedor = todosOsJogos().stream()
                .collect(Collectors.groupingBy(Jogo::visitante, Collectors.summingInt(x -> x.visitantePlacar())));


        Map<Time, Integer> golsTotaisPorTime = Stream.of(golsPorTimeMandante, golsTimeVencedor)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue, (v1, v2) -> v1 + v2));

        return golsTotaisPorTime;
    }

    private Map<Integer, Double> mediaDeGolsPorRodada() {


        Map<Integer, Double> mediaGols = todosOsJogos().stream()
                .collect(Collectors.groupingBy(Jogo::rodada, Collectors.averagingDouble(x -> x.visitantePlacar() + x.mandantePlacar())));

        return mediaGols;
    }


}

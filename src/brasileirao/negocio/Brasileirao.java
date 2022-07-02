package brasileirao.negocio;

import brasileirao.dominio.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
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

    //public Map<Jogo, Integer> mediaGolsPorJogo() {
    public Map<Jogo, Double> mediaGolsPorJogo() {
        return jogos.stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.averagingDouble(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar())
                ));
    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return  this.jogos.stream()
                .filter(filtro)
                .collect(Collectors.summarizingInt(
                        jogo -> jogo.mandantePlacar()+jogo.visitantePlacar()));
    }

    public List<Jogo> todosOsJogos() {
        return this.jogos;
    }

    public Long totalVitoriasEmCasa() {
        return jogos.stream()
                .filter(filtro)
                .filter(jogo -> jogo.mandantePlacar() > jogo.visitantePlacar())
                .count();
    }

    public Long totalVitoriasForaDeCasa() {
        return jogos.stream()
                .filter(filtro)
                .filter(jogo -> jogo.mandantePlacar() < jogo.visitantePlacar())
                .count();
    }

    public Long totalEmpates() {
        return jogos.stream()
                .filter(filtro)
                .map(Jogo::vencedor)
                .filter(time -> time.nome().equals("-"))
                .count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return jogos.stream()
                .filter(filtro)
                .map(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())
                .filter(placar -> placar < 3)
                .count();
    }

    public Long totalJogosCom3OuMaisGols() {
        return jogos.stream()
                .filter(filtro)
                .map(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())
                .filter(placar -> placar >= 3)
                .count();
    }

    public Map<Resultado, Long> todosOsPlacares() {

        List<Resultado> resultados = jogos.stream()
                .filter(filtro)
                .map(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar())).toList();

        return resultados.stream()
                .collect(Collectors.toMap(
                        resultado -> resultado,
                        resultado -> (long) Collections.frequency(resultados, resultado),
                        (a, b) -> a
                ));
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {

        Map<Resultado, Long> todosPlacares = todosOsPlacares();
        Optional<Map.Entry<Resultado, Long>> placarMaisRepetido = todosPlacares.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        if (!placarMaisRepetido.isPresent()){
            throw new RuntimeException("Não existe jogo com maior número de repetição");
        }
        return placarMaisRepetido.get();
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {

        Map<Resultado, Long> todosPlacares = todosOsPlacares();
        Optional<Map.Entry<Resultado, Long>> placarMaisRepetido = todosPlacares.entrySet()
                .stream()
                .min(Comparator.comparing(Map.Entry::getValue));

        if (!placarMaisRepetido.isPresent()){
            throw new RuntimeException("Não existe jogo com menor número de repetição");
        }
        return placarMaisRepetido.get();
    }

    private List<Time> todosOsTimes() {
        List<Time> mandantes = todosOsJogos()
                .stream()
                .map(Jogo::mandante)
                .toList();

        return mandantes;
    }

    /**
     * todos os jogos que cada time foi mandante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {
        return jogos.stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Jogo::mandante));
    }

    /**
     * todos os jogos que cada time foi visitante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {
        return jogos.stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Jogo::visitante));
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {

        Map<Time, List<Jogo>> jogosMandante = todosOsJogosPorTimeComoMandantes();
        Map<Time, List<Jogo>> jogosVisitante = todosOsJogosPorTimeComoVisitante();
        Map<Time, List<Jogo>> todosJogoPortime = Stream.of(jogosMandante, jogosVisitante)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> {
                            v1.addAll(v2);
                            return v1;
                        }
                ));

       //        System.out.println("lista mandante");
//        jogosMandante.entrySet().forEach(System.out::println);
//        System.out.println("lista visitante");
//        jogosVisitante.entrySet().forEach(System.out::println);
//        System.out.println("lista total");
//        todosJogoPortime.entrySet().forEach(System.out::println);

        return todosJogoPortime;
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {

        Map<Time, Map<Boolean, List<Jogo>>> collect = todosOsJogosPorTime()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map -> Map.getValue().stream()
                                        .collect(Collectors.groupingBy(Jogo -> Jogo.mandante().nome().equals(Map.getKey().nome())))
                        )
                );

//        System.out.println("Relação de jogos mandante por visitante");
//        collect.entrySet().forEach(System.out::println);

        return collect;
    }

    public Set<PosicaoTabela> tabela() { // to do

        List<PosicaoTabela> collect = todosOsJogosPorTime().entrySet().stream()
                .map(Map -> {
                    Time time = Map.getKey();

                    Long vitorias = Map.getValue().stream()
                            .filter(jogo -> jogo.vencedor().nome().equals(Map.getKey().nome())).count();
                    Long diferenteDeVitoria = Map.getValue().stream()
                            .filter(jogo -> !jogo.vencedor().nome().equals(Map.getKey().nome())).count();
                    Long empates = Map.getValue().stream()
                            .filter(jogo -> jogo.vencedor().nome().equals("-")).count();
                    Long derrotas = diferenteDeVitoria - empates;

                    Integer golsPositivosComoMandante = Map.getValue().stream()
                            .filter(jogo -> jogo.mandante().equals(Map.getKey()))
                            .map(Jogo::mandantePlacar).reduce(0, Integer::sum);
                    Integer golsPositivosComoVisitante = Map.getValue().stream()
                            .filter(jogo -> jogo.visitante().equals(Map.getKey()))
                            .map(Jogo::visitantePlacar).reduce(0, Integer::sum);
                    Integer golsNegativosComoMandante = Map.getValue().stream()
                            .filter(jogo -> jogo.mandante().equals(Map.getKey()))
                            .map(Jogo::visitantePlacar).reduce(0, Integer::sum);
                    Integer golsNegativosComoVisitante = Map.getValue().stream()
                            .filter(jogo -> jogo.visitante().equals(Map.getKey()))
                            .map(Jogo::mandantePlacar).reduce(0, Integer::sum);

                    Long golsPositivos = Long.valueOf(golsPositivosComoMandante + golsPositivosComoVisitante);
                    Long golsSofridos = Long.valueOf(golsNegativosComoMandante + golsNegativosComoVisitante);
                    Long saldoDeGols = golsPositivos - golsSofridos;

                    return new PosicaoTabela(time, vitorias, derrotas, empates, golsPositivos, golsSofridos, saldoDeGols);
                }).sorted(Comparator.comparing(PosicaoTabela::getPontuacaoTotal).reversed())
                .toList();

        return new LinkedHashSet<>(collect);
    }

    public List<Jogo> lerArquivo(Path file) throws IOException {

        List<String[]> linhasFile = Files.readAllLines(file).stream()
                .map(linha -> linha.split(";")).toList();

        return linhasFile.stream()
                .skip(1)
                .map(jogo -> new JogoBuilder().setRodada(jogo[0])
                        .setDataDoJogo(jogo[1], jogo[2], getDayOfWeek(jogo[3]))
                        .setMandante(jogo[4])
                        .setVisitante(jogo[5])
                        .setVencedor(jogo[6])
                        .setArena(jogo[7])
                        .setMandantePlacar(jogo[8])
                        .setVisitantePlacar(jogo[9])
                        .setEstadoMandante(jogo[10])
                        .setEstadoVisitante(jogo[11])
                        .setEstadoVencedor(jogo[12])
                        .build()).toList();
    }

    private DayOfWeek getDayOfWeek(String dia) {
        return Map.of(
                "Segunda-feira", DayOfWeek.MONDAY,
                "Terça-feira", DayOfWeek.TUESDAY,
                "Quarta-feira", DayOfWeek.WEDNESDAY,
                "Quinta-feira", DayOfWeek.THURSDAY,
                "Sexta-feira", DayOfWeek.FRIDAY,
                "Sábado", DayOfWeek.SATURDAY,
                "Domingo", DayOfWeek.SUNDAY
        ).get(dia);
    }

    // METODOS EXTRA

    private Map<Integer, Integer> totalGolsPorRodada() {
        return jogos.stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(
                        Jogo::rodada,
                        Collectors.summingInt(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())
                ));
    }

    private Map<Time, Integer> totalDeGolsPorTime() {

        Map<Time, Integer> totalGolsTimeMandante = todosOsJogosPorTimeComoMandantes().entrySet().stream().
                collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map -> Map.getValue().stream()
                                .map(Jogo::mandantePlacar).reduce(0, Integer::sum)));


        Map<Time, Integer> totalGolsTimeVisitante = todosOsJogosPorTimeComoVisitante().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map -> Map.getValue().stream()
                                .map(Jogo::visitantePlacar).reduce(0, Integer::sum)));


        Map<Time, Integer> totalGolsPorTime = Stream.of(totalGolsTimeMandante, totalGolsTimeVisitante)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum
                ));

//        System.out.println("total de gol por mandante");
//        totalGolsTimeMandante.entrySet().forEach(System.out::println);
//        System.out.println("total de gol por visitante");
//        totalGolsTimeVisitante.entrySet().forEach(System.out::println);
//        System.out.println("total de gols por time");
//        totalGolsPorTime.entrySet().forEach(System.out::println);

        return totalGolsPorTime;
    }

    private Map<Integer, Double> mediaDeGolsPorRodada() {
        return jogos.stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(
                        Jogo::rodada,
                        Collectors.averagingDouble(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())
                ));
    }
}

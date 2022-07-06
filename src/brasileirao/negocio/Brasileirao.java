package brasileirao.negocio;

import brasileirao.dominio.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    public Map<Jogo, Double> mediaGolsPorJogo() {
        return todosOsJogos()
                .stream()
                .collect(Collectors
                        .groupingBy(Function.identity(),
                                Collectors.averagingDouble(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar())));
    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return todosOsJogos()
                .stream()
                .mapToInt(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar())
                .summaryStatistics();
    }

    public List<Jogo> todosOsJogos() {
        return this.jogos;
    }

    public Long totalVitoriasEmCasa() {
        return todosOsJogos()
                .stream()
                .filter(jogo -> jogo.mandante().toString().equals(jogo.vencedor().toString()))
                .count();
    }

    public Long totalVitoriasForaDeCasa() {
        return todosOsJogos()
                .stream()
                .filter(jogo ->  jogo.visitante().toString().equals(jogo.vencedor().toString()))
                .count();
    }

    public Long totalEmpates() {
        return todosOsJogos()
                .stream()
                .filter(jogo -> jogo.mandantePlacar().equals(jogo.mandantePlacar()))
                .count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return todosOsJogos()
                .stream()
                .filter(jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar()) < 3)
                .count();
    }

    public Long totalJogosCom3OuMaisGols() {
        return todosOsJogos()
                .stream()
                .filter(jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar()) >= 3)
                .count();
    }

    public Map<Resultado, Long> todosOsPlacares() {
        return todosOsJogos()
                .stream()
                .collect(Collectors
                        .groupingBy(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar()), Collectors.counting()));
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        Optional<Map.Entry<Resultado, Long>> max =
                todosOsPlacares()
                        .entrySet()
                        .stream()
                        .max(Comparator.comparingLong(Map.Entry::getValue));

        return max.orElseThrow(RuntimeException::new);
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        Optional<Map.Entry<Resultado, Long>> min =
                todosOsPlacares()
                        .entrySet()
                        .stream()
                        .min(Comparator.comparingLong(Map.Entry::getValue));

        return min.orElseThrow(RuntimeException::new);
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

        return Stream.concat(mandantes.stream(), visitantes.stream()).toList();
    }

    /**
     * todos os jogos que cada time foi mandante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {
        return todosOsJogos()
                .stream()
                .collect(Collectors.groupingBy(Jogo::mandante));
    }

    /**
     * todos os jogos que cada time foi visitante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {
        return todosOsJogos()
                .stream()
                .collect(Collectors.groupingBy(Jogo::visitante));
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {
        return Stream.of(todosOsJogosPorTimeComoMandantes(), todosOsJogosPorTimeComoVisitante())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            a.addAll(b);
                            return a;
                        }
                ));
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {
        return null;
    }

    public Set<PosicaoTabela> tabela() {
        List<PosicaoTabela> posicaoTabelas = todosOsJogosPorTime()
                .keySet()
                .stream()
                .map(time -> new PosicaoTabela(time,
                        totalDeVitoriasPorTime(time),
                        totalDeDerrotasPorTime(time),
                        totalDeEmpatesPorTime(time),
                        totalDeGolsPorTime(time),
                        totalDeGolsSofridosPorTime(time),
                        totalDeGolsPorTime(time) - totalDeGolsSofridosPorTime(time)))
                .sorted(Comparator.comparing(PosicaoTabela::getPontuacaoTotal).reversed())
                .toList();
        return new LinkedHashSet<>(posicaoTabelas);
    }

    public List<Jogo> lerArquivo(Path file) throws IOException {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH'h'mm");

        List<Jogo> jogos = new ArrayList<>();
        try (Stream<String> linhas = Files.lines(file).skip(1)) {
            linhas
                    .map(linha -> linha.split(";"))
                    .map(info -> new Jogo(
                            Integer.parseInt(info[0]),
                            new DataDoJogo( LocalDate.parse(info[1], dateFormatter),
                                    info[2].isEmpty() ? LocalTime.parse("16h00", timeFormatter)
                                            : LocalTime.parse(info[2].replace("h",":"), timeFormatter),
                                    getDayOfWeek(info[3].toLowerCase())),
                            new Time(info[4]),
                            new Time(info[5]),
                            new Time(info[6]),
                            info[7],
                            Integer.parseInt(info[8]),
                            Integer.parseInt(info[9]),
                            info[10],
                            info[11],
                            info[12]))
                    .forEach(jogos::add);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
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

    private Long totalDeVitoriasPorTime(Time time){
        return todosOsJogosPorTime()
                .get(time)
                .stream()
                .filter(jogo -> jogo.vencedor().toString().equals(time.toString()))
                .count();
    }

    private Long totalDeDerrotasPorTime(Time time){
        return todosOsJogosPorTime()
                .get(time)
                .stream()
                .filter(jogo -> !jogo.vencedor().toString().equals(time.toString()))
                .count();
    }

    private Long totalDeEmpatesPorTime(Time time){
        return todosOsJogosPorTime()
                .get(time)
                .stream()
                .filter(jogo -> jogo.estadoVencedor().equals("-"))
                .count();
    }

    private Long totalDeGolsPorTime(Time time){
        return todosOsJogosPorTime()
                .get(time)
                .stream()
                .mapToLong(jogo -> {
                    if(jogo.mandante().toString().equals(time.toString())){
                        return jogo.mandantePlacar();
                    } else if (jogo.visitante().toString().equals(time.toString())) {
                        return jogo.visitantePlacar();
                    }
                    return 0;
                })
                .sum();
    }

    private Long totalDeGolsSofridosPorTime(Time time){
        return todosOsJogosPorTime()
                .get(time)
                .stream()
                .mapToLong(jogo -> {
                    if(!jogo.mandante().toString().equals(time.toString())){
                        return jogo.mandantePlacar();
                    } else if (!jogo.visitante().toString().equals(time.toString())) {
                        return jogo.visitantePlacar();
                    }
                    return 0;
                }).sum();
    }

}

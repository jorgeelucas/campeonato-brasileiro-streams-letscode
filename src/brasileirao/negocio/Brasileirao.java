package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;
import brasileirao.recursos.JogoBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public Map<Jogo, Integer> mediaGolsPorJogo() {
        return null;
    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return todosOsJogos().stream()
                             .collect(Collectors.summarizingInt(value -> value.mandantePlacar() + value.visitantePlacar()));
    }

    public List<Jogo> todosOsJogos() {
        return jogos.stream()
                    .filter(filtro)
                    .toList();
    }

    public Long totalVitoriasEmCasa() {
        return todosOsJogos().stream()
                .filter(jogo -> jogo.mandantePlacar() > jogo.visitantePlacar())
                .count();
    }

    public Long totalVitoriasForaDeCasa() {
        return todosOsJogos().stream()
                             .filter(jogo -> jogo.mandantePlacar() < jogo.visitantePlacar())
                             .count();

    }

    public Long totalEmpates() {
        return todosOsJogos().stream()
                             .filter(jogo -> jogo.visitantePlacar().equals(jogo.mandantePlacar()))
                             .count();

    }

    public Long totalJogosComMenosDe3Gols() {
        return todosOsJogos().stream()
                .map(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())
                .filter(gols -> gols < 3)
                .count();
    }

    public Long totalJogosCom3OuMaisGols() {
         return todosOsJogos().stream()
                      .map(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())
                      .filter(gols -> gols > 3)
                      .count();

    }

    public Map<Resultado, Long> todosOsPlacares(){
        List<Resultado> placares = todosOsJogos().stream()
                                                 .map(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar()))
                                                 .toList();
        return placares.stream().collect(Collectors.toMap(
                resultado -> resultado,
                o -> (long) Collections.frequency(placares , o),
                (a, b) -> a
        ));
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        Optional<Map.Entry<Resultado, Long>> max = todosOsPlacares().entrySet()
                                                                    .stream()
                                                                    .max(Comparator.comparingLong(Map.Entry::getValue));
        return max.orElseThrow(RuntimeException::new);
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        Optional<Map.Entry<Resultado, Long>> min = todosOsPlacares().entrySet()
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

        return Stream.of(mandantes, visitantes).flatMap(List::stream).toList();
    }

    /**
     * todos os jogos que cada time foi mandante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {
        return todosOsJogos().stream()
                .collect(Collectors.groupingBy(Jogo::mandante));

    }

    /**
     * todos os jogos que cada time foi visitante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {
        return todosOsJogos().stream()
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


    public Set<PosicaoTabela> tabela() {
        List<PosicaoTabela> posicaoTabelas = todosOsJogosPorTime().keySet()
                                                                  .stream()
                                                                  .map(time -> new PosicaoTabela(time,
                                                                                   totalDeVitoriasPorTime(time),
                                                                                   totalDeDerrotasPorTime(time),
                                                                                   totalDeEmpatesPorTime(time),
                                                                                   totalDeGolsPorTime(time),
                                                                                   totalDeGolsSofridosPorTime(time),
                                                                         totalDeGolsPorTime(time) - totalDeGolsSofridosPorTime(time)))
                                                                  .sorted(Comparator.comparing(PosicaoTabela::getPontuacaoTotal).reversed()).toList();
        return new LinkedHashSet<>(posicaoTabelas);
    }

    public List<Jogo> lerArquivo(Path file) throws IOException {
        List<String> linhas = Files.readAllLines(file);
        linhas.replaceAll(s -> s.replace(":", "h"));
        List<String[]> dados = linhas.stream()
                                     .skip(1)
                                     .map(s -> s.split(";"))
                                     .toList();

        return  dados.stream().map(
                dado -> new JogoBuilder().withRodada(dado[0])
                                         .withData(dado[1],verificaCampoVazio(dado[2]),getDayOfWeek(dado[3]))
                                         .withMandante(dado[4])
                                         .withVisitante(dado[5])
                                         .withVencedor(dado[6])
                                         .withArena(dado[7])
                                         .withMandantePlacar(dado[8])
                                         .withVisitantePlacar(dado[9])
                                         .withEstadoMandante(dado[10])
                                         .withEstadoVisitante(dado[11])
                                         .withEstadoVencedor(dado[12])
                                         .build())
                                         .toList();
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

    private String verificaCampoVazio(String campo){
        if (campo.isEmpty() || campo.isBlank()){
            return "16h00";
        }
        return campo;
    }

    private Long totalDeVitoriasPorTime(Time time){
       return todosOsJogosPorTime().get(time)
                             .stream()
                             .filter(t -> t.vencedor().equals(time))
                             .count();
    }

    private Long totalDeDerrotasPorTime(Time time){
        return todosOsJogosPorTime().get(time)
                .stream()
                .filter(t -> !t.vencedor().equals(time))
                .count();
    }

    private Long totalDeEmpatesPorTime(Time time){
        return todosOsJogosPorTime().get(time)
                .stream()
                .filter(t -> t.estadoVencedor().equals("-"))
                .count();
    }

    private Long totalDeGolsPorTime(Time time){
        return (long) todosOsJogosPorTime().get(time)
                .stream()
                .mapToInt(jogo -> {
                    if(jogo.mandante().equals(time)){
                       return jogo.mandantePlacar();
                    } else if (jogo.visitante().equals(time)) {
                        return jogo.visitantePlacar();
                    }
                    return 0;
                }).sum();
    }

    private Long totalDeGolsSofridosPorTime(Time time){
        return (long) todosOsJogosPorTime().get(time)
                .stream()
                .mapToInt(jogo -> {
                    if(!jogo.mandante().equals(time)){
                        return jogo.mandantePlacar();
                    } else if (!jogo.visitante().equals(time)) {
                        return jogo.visitantePlacar();
                    }
                    return 0;
                }).sum();
    }


}

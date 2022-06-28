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
                .filter(x -> x.nome().equals("-"))
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
        return null;
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {

        List<Resultado> resultados = jogos.stream()
                .filter(filtro)
                .map(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar())).toList();

        Map<Resultado, Long> placarXRepeticao = resultados.stream()
                .collect(Collectors.toMap(
                        resultado -> resultado,
                        resultado -> (long) Collections.frequency(resultados, resultado),
                        (a, b) -> a
                ));

        placarXRepeticao.entrySet().stream().forEach(System.out::println);

        Optional<Map.Entry<Resultado, Long>> placarMaisRepetido = placarXRepeticao.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        if (!placarMaisRepetido.isPresent()){
            throw new RuntimeException("Não existe jogo com maior número de repetição");
        }
        return placarMaisRepetido.get();
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        List<Resultado> resultados = jogos.stream()
                .filter(filtro)
                .map(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar())).toList();

        Map<Resultado, Long> placarXRepeticao = resultados.stream()
                .collect(Collectors.toMap(
                        resultado -> resultado,
                        resultado -> (long) Collections.frequency(resultados, resultado),
                        (a, b) -> a
                ));

        Optional<Map.Entry<Resultado, Long>> placarMaisRepetido = placarXRepeticao.entrySet()
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

//        List<Time> visitantes = todosOsJogos()
//                .stream()
//                .map(Jogo::visitante)
//                .toList();

        return mandantes;
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

    public Set<PosicaoTabela> tabela() { // to do

        Set<Time> nomeTimes = this.jogos.stream()
                .filter(filtro)
                .map(Jogo::mandante)
                .collect(Collectors.toSet());

        //nomeTimes.forEach(System.out::println);
        return null;
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
        return null;
    }

    private Map<Time, Integer> totalDeGolsPorTime() {
        return null;
    }

    private Map<Integer, Double> mediaDeGolsPorRodada() {
        return null;
    }

}

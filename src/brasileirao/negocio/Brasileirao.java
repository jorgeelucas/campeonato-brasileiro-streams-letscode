package brasileirao.negocio;

import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;
import brasileirao.resources.JogoBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
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
        return jogos.stream().filter(filtro).collect(Collectors.summarizingInt(jogo-> jogo.mandantePlacar() + jogo.visitantePlacar()));
    }

    public List<Jogo> todosOsJogos() {
        return jogos.stream().filter(filtro).toList();
    }

    public Long totalVitoriasEmCasa() {
        return todosOsJogos().stream()
                .filter(jogo -> jogo.mandantePlacar() > jogo.visitantePlacar())
                .count();
    }

    public Long totalVitoriasForaDeCasa() {
        return todosOsJogos().stream()
                .filter(jogo -> jogo.visitantePlacar() > jogo.mandantePlacar())
                .count();
    }

    public Long totalEmpates() {
        return todosOsJogos().stream()
                .filter(jogo -> Objects.equals(jogo.mandantePlacar(), jogo.visitantePlacar()))
                .count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return todosOsJogos().stream()
                .map(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar())
                .filter(gols -> gols < 3)
                .count();
    }

    public Long totalJogosCom3OuMaisGols() {
        return todosOsJogos().stream()
                .map(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar())
                .filter(gols -> gols > 3)
                .count();
    }

    public Map<Resultado, Long> todosOsPlacares() {

        List<Resultado> resultados = todosOsJogos().stream()
                .map(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar())).toList();

        return resultados.stream()
                .collect(Collectors.toMap(
                        resultado -> resultado,
                        resultado -> (long) Collections.frequency(resultados, resultado),
                        (a,b) -> a
                ));
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        Optional<Map.Entry<Resultado, Long>> max = todosOsPlacares().entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue));
        return max.orElseThrow(()-> new RuntimeException("Error"));
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        Optional<Map.Entry<Resultado, Long>> min = todosOsPlacares().entrySet().stream()
                .min(Comparator.comparingLong(Map.Entry::getValue));
        return min.orElseThrow(()-> new RuntimeException("Error"));
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
        return todosOsJogos().stream().collect(Collectors.groupingBy(Jogo::mandante));
    }

    /**
     * todos os jogos que cada time foi visitante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {
         return todosOsJogos().stream().collect(Collectors.groupingBy(Jogo::visitante));
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

    public List<Jogo> lerArquivo(Path file) throws IOException{
        List<String> readFile = new ArrayList<>(Files.readAllLines(file));
        readFile.replaceAll(line -> line.replace(":", "h"));

        List<String[]> fieldsFile = readFile.stream().skip(1).map(line -> line.split(";")).toList();
        return fieldsFile.stream().map(
                field -> new JogoBuilder().withRodada(field[0])
                        .withData(field[1], isValidTime(field[2]), getDayOfWeek(field[3])).withMandante(field[4])
                        .withVisitante(field[5]).withVencedor(field[6]).withArena(field[7])
                        .withMandantePlacar(field[8]).withVisitantePlacar(field[9]).withEstadoMandante(field[10])
                        .withEstadoVisitante(field[11]).withEstadoVencedor(field[12]).build()).toList();
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

    private String isValidTime(String s){
        if (s.isEmpty() || s.isBlank()){
            return "16h00";
        }
        return s;
    }
}

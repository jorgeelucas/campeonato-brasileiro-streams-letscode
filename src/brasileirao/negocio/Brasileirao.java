package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;
import brasileirao.resources.JogoBuilder;

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
        return jogos.stream().collect(Collectors.summarizingInt(jogo-> jogo.mandantePlacar() + jogo.visitantePlacar()));
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

    public List<Jogo> lerArquivo(Path file) throws IOException{
        List<String> readFile = new ArrayList<>(Files.readAllLines(file));
        readFile.remove(0);
        readFile.replaceAll(line -> line.replace(":", "h"));

        List<String[]> fieldsFile = readFile.stream().map(line -> line.split(";")).toList();
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

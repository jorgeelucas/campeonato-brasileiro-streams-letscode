package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        List<Jogo> jogoList = new ArrayList<>();

        try (Stream<String> linhas = Files.lines(file).skip(1)) {
                    linhas
                        .map(linha -> linha.split(";"))
                        .map(str -> new Jogo(
                                Integer.parseInt(str[0]),
                                new DataDoJogo( LocalDate.parse(str[1],DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                        str[2].isBlank() ? LocalTime.parse("00:00",DateTimeFormatter.ofPattern("HH:mm"))
                                        : LocalTime.parse(str[2].replace("h",":"),DateTimeFormatter.ofPattern("HH:mm")),
                                        getDayOfWeek(str[3].toLowerCase())),
                                new Time(str[4]),
                                new Time(str[5]),
                                new Time(str[6]),
                                str[7],
                                Integer.parseInt(str[8]),
                                Integer.parseInt(str[9]),
                                str[10],
                                str[11],
                                str[12]))
                        .forEach(jogo -> jogoList.add(jogo));
        }catch (IOException e) {
            throw new RuntimeException(e);
        }


        return jogoList;
    }

    private DayOfWeek getDayOfWeek(String dia) {
        return Map.of(
                "segunda-feira", DayOfWeek.MONDAY,
                "terça-feira", DayOfWeek.TUESDAY,
                "terca-feira", DayOfWeek.TUESDAY,
                "quarta-feira", DayOfWeek.WEDNESDAY,
                "quinta-feira", DayOfWeek.THURSDAY,
                "sexta-feira", DayOfWeek.FRIDAY,
                "sábado", DayOfWeek.SATURDAY,
                "sabado", DayOfWeek.SATURDAY,
                "domingo", DayOfWeek.SUNDAY
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

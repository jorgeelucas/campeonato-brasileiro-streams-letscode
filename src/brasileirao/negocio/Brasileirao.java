package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import javax.xml.transform.Result;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Brasileirao {

    private Map<Integer, List<Jogo>> brasileirao;
    private List<Jogo> jogos;
    private Predicate<Jogo> filtro;

    public Brasileirao(Path arquivo, Predicate<Jogo> filtro) throws IOException {
        this.jogos = lerArquivo(arquivo);
        this.filtro = filtro;
        this.brasileirao = jogos.stream()
                .filter(filtro) //filtrar por ano
                .collect(groupingBy(
                        Jogo::rodada,
                        mapping(Function.identity(), toList())));

    }

    public Map<Jogo, Integer> mediaGolsPorJogo() {
        //List<Jogo> jogos = todosOsJogos();

        return todosOsJogos()
                .stream()
                .collect(toMap(jogo -> jogo, jogo -> jogo.visitantePlacar()+jogo.mandantePlacar()));
    }
    public IntSummaryStatistics estatisticasPorJogo() {
        //List<Jogo> jogos = todosOsJogos();

        return todosOsJogos()
                .stream()
                .mapToInt(jogo -> jogo.mandantePlacar()+ jogo.visitantePlacar())
                .summaryStatistics();
    }

    public List<Jogo> todosOsJogos() {
        //this.brasileirao.forEach((integer, jogos1) -> jogos1.stream().forEach(System.out::println));
        //List<Jogo> jogoList = new ArrayList<>();

        //this.brasileirao.forEach((integer, jogos1) -> jogoList.addAll(jogos1));
        return this.jogos
                .stream()
                .filter(filtro)
                .toList();
    }

    public Long totalVitoriasEmCasa() {

        return todosOsJogos()
                .stream()
                .filter(jogo -> jogo.mandante().equals(jogo.vencedor())).count();
    }

    public Long totalVitoriasForaDeCasa() {

        return todosOsJogos()
                .stream()
                .filter(jogo -> jogo.visitante().equals(jogo.vencedor())).count();
    }

    public Long totalEmpates() {

        return todosOsJogos()
                .stream()
                .filter(jogo -> (jogo.mandantePlacar() == jogo.visitantePlacar())).count();
    }

    public Long totalJogosComMenosDe3Gols() {

        return todosOsJogos()
                .stream()
                .filter(jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar()) < 3).count();
    }

    public Long totalJogosCom3OuMaisGols() {

        return todosOsJogos()
                .stream()
                .filter(jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar()) >= 3).count();
    }

    public Map<Resultado, Long> todosOsPlacares() {

        List<Resultado> resultados = todosOsJogos().stream()
                .map(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar())).toList();

        return resultados.stream()
                .collect(groupingBy(Function.identity(), counting()));
    }
    public Map.Entry<Resultado, Long> placarMaisRepetido() {


        return todosOsPlacares()
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElse(null);
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {

        return todosOsPlacares()
                .entrySet()
                .stream()
                .min(Comparator.comparing(Map.Entry::getValue))
                .orElse(null);
    }

    private List<Time> todosOsTimes() {

        return todosOsJogos()
                .stream()
                .map(Jogo::mandante)
                .toList();
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

        Map<Time, List<Jogo>> todosJogosPorTime = new HashMap<Time, List<Jogo>>();
        todosJogosPorTime.putAll(todosOsJogosPorTimeComoMandantes());
        todosJogosPorTime.putAll(todosOsJogosPorTimeComoVisitante());
        return todosJogosPorTime;
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {


        return null;
    }

    public Set<PosicaoTabela> tabela() {
        return null;
    }

    public List<Jogo> lerArquivo(Path file) throws IOException {
        List<Jogo> jogosArquivo = new ArrayList<>();
        try (Stream<String> linhas = Files.lines(file).skip(1)) {
            jogosArquivo = linhas
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
                        .toList();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        return jogosArquivo;
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

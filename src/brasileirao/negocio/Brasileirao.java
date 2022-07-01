package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

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
        return null;
    }

    public List<Jogo> todosOsJogos() {
        return jogos.stream().filter(filtro).toList();
    }

    public Long totalVitoriasEmCasa() {
        return todosOsJogos().stream()
                .filter(jogo -> jogo.vencedor().equals(jogo.mandante()))
                .count();
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

        return todosOsJogos()
                .stream()
                .map(Jogo::mandante)
                .toList();
    }

    /**
     * todos os jogos que cada time foi mandante
     *
     * @return Map<Time, List < Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {
        return null;
    }

    /**
     * todos os jogos que cada time foi visitante
     *
     * @return Map<Time, List < Jogo>>
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

        return Files.lines(file)
                .skip(1)
                .map(strings -> Arrays.asList(strings.split(";")))
                .map(this::convertListStringToJogo)
                .toList();

    }

    private Jogo convertListStringToJogo(List<String> listString) {
        Integer rodada = Integer.valueOf(listString.get(0));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        LocalDate data = LocalDate.parse(listString.get(1), formatter);
        LocalTime horario = LocalTime.of(16, 00);

        if (listString.get(2).contains("h") || listString.get(2).contains(":")) {
            String[] hours = listString.get(2).split("h|:");
            horario = LocalTime.of(Integer.valueOf(hours[0]).intValue(), Integer.valueOf(hours[1]).intValue());
        }
        DayOfWeek dia = getDayOfWeek(listString.get(3));
        DataDoJogo dataDoJogo = new DataDoJogo(data, horario, dia);
        Time mandante = new Time(listString.get(4));
        Time visitante = new Time(listString.get(5));
        Time vencedor = new Time(listString.get(6));
        String arena = listString.get(7);
        Integer mandantePlacar = Integer.valueOf(listString.get(8));
        Integer visitantePlacar = Integer.valueOf(listString.get(9));
        String estadoMandante = listString.get(10);
        String estadoVisitante = listString.get(11);
        String estadoVencedor = listString.get(12);
        return new Jogo(rodada, dataDoJogo, mandante, visitante, vencedor, arena, mandantePlacar, visitantePlacar, estadoMandante, estadoVisitante, estadoVencedor);


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

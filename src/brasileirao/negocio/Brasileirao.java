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
        return todosOsJogos().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        jogo -> jogo.mandantePlacar() + jogo.visitantePlacar()
                ));
    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return todosOsJogos().stream()
                .collect(Collectors.summarizingInt(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar()));
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
        return todosOsJogos().stream()
                .filter(jogo -> jogo.vencedor().equals(jogo.visitante()))
                .count();
    }

    public Long totalEmpates() {
        return todosOsJogos().stream()
                .filter(jogo -> jogo.mandantePlacar().equals(jogo.visitantePlacar()))
                .count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return todosOsJogos().stream()
                .filter(jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar()) < 3)
                .collect(Collectors.counting());
    }

    public Long totalJogosCom3OuMaisGols() {
        return todosOsJogos().stream()
                .filter(jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar()) >= 3)
                .collect(Collectors.counting());
    }

    public Map<Resultado, Long> todosOsPlacares() {

        return todosOsJogos().stream()
                .collect(Collectors.groupingBy(
                        jogos -> new Resultado(jogos.mandantePlacar(), jogos.visitantePlacar()),
                        Collectors.counting()
                ));
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        Optional<Map.Entry<Resultado, Long>> max = todosOsPlacares().entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
        return max.orElse(null);
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        Optional<Map.Entry<Resultado, Long>> min = todosOsPlacares().entrySet()
                .stream()
                .min(Map.Entry.comparingByValue());
        return min.orElse(null);
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
        return todosOsJogos().stream()
                .collect(Collectors.groupingBy(
                        jogo -> jogo.mandante()
                ));
    }

    /**
     * todos os jogos que cada time foi visitante
     *
     * @return Map<Time, List < Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {
        return todosOsJogos().stream()
                .collect(Collectors.groupingBy(
                        jogo -> jogo.visitante()
                ));
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {
        return Stream.concat(todosOsJogosPorTimeComoMandantes().entrySet().stream(),
                        todosOsJogosPorTimeComoVisitante().entrySet().stream())
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

        List<PosicaoTabela> posicaoTabelas = todosOsJogosPorTime().entrySet()
                .stream()
                .map(entry -> {
                    Long vitorias = entry.getValue().stream()
                            .filter(jogo -> jogo.vencedor().equals(entry.getKey())).count();
                    Long empates = entry.getValue().stream()
                            .filter(jogo -> jogo.mandantePlacar().equals(jogo.visitantePlacar())).count();
                    Long derrotas =  entry.getValue().size() - vitorias - empates;


                    Long golsPositivosMandante = Long.valueOf(entry.getValue().stream()
                            .filter(jogo -> jogo.mandante().equals(entry.getKey()))
                            .map(jogo -> jogo.mandantePlacar()).reduce(0, Integer::sum));
                    Long golsPositivosVisitante = Long.valueOf(entry.getValue().stream()
                            .filter(jogo -> jogo.visitante().equals(entry.getKey()))
                            .map(jogo -> jogo.visitantePlacar()).reduce(0, Integer::sum));
                    Long golsPositivos = golsPositivosMandante + golsPositivosVisitante;

                    Long golsNegativosMandante = Long.valueOf(entry.getValue().stream()
                            .filter(jogo -> jogo.mandante().equals(entry.getKey()))
                            .map(jogo -> jogo.visitantePlacar()).reduce(0, Integer::sum));
                    Long golsNegativosVisitante = Long.valueOf(entry.getValue().stream()
                            .filter(jogo -> jogo.visitante().equals(entry.getKey()))
                            .map(jogo -> jogo.mandantePlacar()).reduce(0, Integer::sum));
                    Long golsSofridos = golsNegativosMandante + golsNegativosVisitante;

                    Long saldoDeGols = golsPositivos - golsSofridos;
                    return new PosicaoTabela(
                            entry.getKey(), vitorias, derrotas, empates, golsPositivos, golsSofridos, saldoDeGols);

                }).sorted(Comparator.comparing(PosicaoTabela::getPontuacaoTotal).reversed()).toList();

        return new LinkedHashSet<>(posicaoTabelas);
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
        return null;
    }

    private Map<Time, Integer> totalDeGolsPorTime() {
        return null;
    }

    private Map<Integer, Double> mediaDeGolsPorRodada() {
        return null;
    }


}

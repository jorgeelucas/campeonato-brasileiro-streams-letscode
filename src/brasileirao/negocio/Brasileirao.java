package brasileirao.negocio;

import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Brasileirao {
    private final Map<Integer, List<Jogo>> brasileiraoByRound;
    private final List<Jogo> jogos;
    private final Predicate<Jogo> filtro;

    public Brasileirao(Path arquivo, Predicate<Jogo> filtro) {
        this.jogos = lerArquivo(arquivo);
        this.filtro = filtro;
        this.brasileiraoByRound = jogos.stream()
                                       .filter(filtro) //filtrar por ano
                                       .collect(Collectors.groupingBy(Jogo::getRodada,
                                                                      Collectors.mapping(Function.identity(),
                                                                                         Collectors.toList())));
    }

    public Map<Jogo, Integer> mediaGolsPorJogo() {
        return null;
    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return null;
    }

    private List<Jogo> todosOsJogos() {
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
        List<Time> mandantes = todosOsJogos().stream()
                                             .map(Jogo::getMandante)
                                             .collect(Collectors.toList());

        List<Time> visitantes = todosOsJogos().stream()
                                              .map(Jogo::getVisitante)
                                              .collect(Collectors.toList());

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

    private List<Jogo> lerArquivo(Path file) {
        return null;
    }

    private DayOfWeek getDayOfWeek(String dia) {
        Map<String, DayOfWeek> daysOfWeek = new HashMap<>();

        daysOfWeek.put("Segunda-feira", DayOfWeek.SUNDAY);
        daysOfWeek.put("Terça-feira", DayOfWeek.SUNDAY);
        daysOfWeek.put("Quarta-feira", DayOfWeek.SUNDAY);
        daysOfWeek.put("Quinta-feira", DayOfWeek.SUNDAY);
        daysOfWeek.put("Sexta-feira", DayOfWeek.SUNDAY);
        daysOfWeek.put("Sábado", DayOfWeek.SUNDAY);
        daysOfWeek.put("Domingo", DayOfWeek.SUNDAY);

        return daysOfWeek.get(dia);
    }

    // METODOS EXTRA

    private Map<Integer, Integer> totalGolsPorRodada() {
        return Collections.emptyMap();
    }

    private Map<Time, Integer> totalDeGolsPorTime() {
        return null;
    }

    private Map<Integer, Double> mediaDeGolsPorRodada() {
        return Collections.emptyMap();
    }


}

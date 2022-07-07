package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        return jogos.stream()
                .filter(filtro)
                .collect(Collectors.toMap(
                        jogo -> jogo,
                        jogo -> jogo.mandantePlacar() + jogo.visitantePlacar()
                ));
    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return mediaGolsPorJogo().values()
                .stream()
                .mapToInt(gols -> gols)
                .summaryStatistics();
    }

    public List<Jogo> todosOsJogos() {
        return jogos.stream()
                .filter(filtro)
                .toList();
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
                .filter(jogo -> jogo.vencedor().nome().equals("-"))
                .count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return todosOsJogos()
                .stream()
                .filter(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar() < 3)
                .count();
    }

    public Long totalJogosCom3OuMaisGols() {
        return todosOsJogos()
                .stream()
                .filter(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar() >= 3)
                .count();
    }

    public Map<Resultado, Long> todosOsPlacares() {
        return todosOsJogos().stream()
                .collect(Collectors.groupingBy(jogo -> new Resultado(jogo.mandantePlacar(),
                        jogo.visitantePlacar()), Collectors.counting()));
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        return todosOsPlacares().entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        return todosOsPlacares().entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .orElse(null);
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
        List<Jogo> partidas;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.toFile()))) {
            partidas = bufferedReader.lines()
                    .skip(1)
                    .map(linha -> {
                        String[] linhaSplited = linha.split(";");
                        Integer rodada = Integer.valueOf(linhaSplited[0]);
                        LocalDate data = LocalDate.parse(linhaSplited[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        String hora = linhaSplited[2].replace("h", ":");
                        LocalTime horario = null;
                        try {
                            horario = LocalTime.parse(hora, DateTimeFormatter.ofPattern("HH:mm"));
                        }catch (DateTimeParseException e){
                            horario = LocalTime.of(16, 0);
                        }
                        DayOfWeek dia = getDayOfWeek(linhaSplited[3]);
                        DataDoJogo dataDoJogo = new DataDoJogo(data, horario, dia);
                        Time mandante = new Time(linhaSplited[4]);
                        Time visitante = new Time(linhaSplited[5]);
                        Time vencedor = new Time(linhaSplited[6]);
                        String arena = linhaSplited[7];
                        Integer mandantePlacar = Integer.valueOf(linhaSplited[8]);
                        Integer visitantePlacar = Integer.valueOf(linhaSplited[9]);
                        String estadoMandante = linhaSplited[10];
                        String estadoVisitante = linhaSplited[11];
                        String estadoVencedor = linhaSplited[12];
                        return new Jogo(rodada, dataDoJogo, mandante, visitante, vencedor, arena, mandantePlacar, visitantePlacar, estadoMandante, estadoVisitante, estadoVencedor);
                    }).toList();
        }
        return partidas;
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

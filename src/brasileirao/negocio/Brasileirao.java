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

    public Map<Jogo, Double> mediaGolsPorJogo() {

        return todosOsJogos()
                .stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.averagingInt(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar())));
    }

    public IntSummaryStatistics estatisticasPorJogo() {

        return todosOsJogos()
                .stream()
                .filter(filtro)
                .collect(Collectors.summarizingInt(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar()))
                ;
    }

    public List<Jogo> todosOsJogos() {
        return this.jogos.stream().filter(filtro).toList();
    }

    public Long totalVitoriasEmCasa() {

        return jogos
                .stream()
                .filter(filtro)
                .filter(jogo -> jogo.mandantePlacar() > jogo.visitantePlacar())
                .count();
    }

    public Long totalVitoriasForaDeCasa() {

        return jogos
                .stream()
                .filter(filtro)
                .filter(jogo -> jogo.visitantePlacar() > jogo.mandantePlacar())
                .count();
    }

    public Long totalEmpates() {

        return jogos
                .stream()
                .filter(filtro)
                .map(Jogo::vencedor)
                .filter(vencedor -> vencedor.nome().equals("-"))
                .count();
    }

    public Long totalJogosComMenosDe3Gols() {

        return jogos
                .stream()
                .filter(filtro)
                .map(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())
                .filter(gols -> gols < 3)
                .count();
    }

    public Long totalJogosCom3OuMaisGols() {

        return jogos
                .stream()
                .filter(filtro)
                .map(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())
                .filter(gols -> gols >= 3)
                .count();
    }

    public Map<Resultado, Long> todosOsPlacares() {

        return jogos
                .stream()
                .filter(filtro)
                .map(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {

        Map<Resultado, Long> todosPlacares = todosOsPlacares();

        Optional <Map.Entry<Resultado, Long>> placarMaisRepetido = todosPlacares
               .entrySet()
               .stream()
               .max(Map.Entry.comparingByValue());

        return placarMaisRepetido.orElse(null);

    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {

        Map<Resultado, Long> todosPlacares = todosOsPlacares();

        Optional <Map.Entry<Resultado, Long>> placarMenosRepetido = todosPlacares
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue());

        return placarMenosRepetido.orElse(null);
    }

    public List<Time> todosOsTimes() {

         return jogos
                .stream()
                .filter(filtro)
                .map(Jogo::mandante)
                .distinct()
                .toList();
    }

    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {

        return jogos
                .stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Jogo::mandante));
    }

    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {

        return jogos
                .stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Jogo::visitante));

    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {

        Map<Time, List<Jogo>> todosJogosPorTime = todosOsJogosPorTimeComoVisitante();
        Map<Time, List<Jogo>> jogosMandante = todosOsJogosPorTimeComoMandantes();

        todosJogosPorTime.putAll(jogosMandante);

        return todosJogosPorTime;
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {
        return null;
    }

    public Set<PosicaoTabela> tabela() {
        return null;
    }

    public List<Jogo> lerArquivo(Path file) throws IOException {

        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return Files.lines(file)
                .skip(1)
                .map(linha -> linha.split(";"))
                .map(dados -> new Jogo(Integer.parseInt(dados[0]),
                              new DataDoJogo(LocalDate.parse(dados[1], formatoData),
                              LocalTime.parse(dados[2].isBlank() ? "12:00" : dados[2].replace("h", ":")),
                              getDayOfWeek(dados[3])),
                new Time(dados[4]),
                new Time(dados[5]),
                new Time(dados[6]),
                dados[7],
                Integer.parseInt(dados[8]),
                Integer.parseInt(dados[9]),
                dados[10],
                dados[11],
                dados[12]))
                .toList();

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

        return todosOsJogos()
                .stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Jogo::rodada, Collectors.summingInt(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())));

    }
/*
    private Map<Time, Integer> totalDeGolsPorTime() {
        return null;
    }
*/
    private Map<Integer, Double> mediaDeGolsPorRodada() {
        return todosOsJogos()
                .stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Jogo::rodada, Collectors.averagingDouble(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())));
    }


}

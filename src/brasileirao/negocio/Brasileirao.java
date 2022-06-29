package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.stream.IntStream;
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
        IntSummaryStatistics intSummaryStatistics
                = new IntSummaryStatistics();

        //VERIFICAR SE PRECISA O FILTRO*
//        jogos.stream().filter(filtro).forEach(jogo -> intSummaryStatistics.accept(retornarQuantidadeGolsPorJogo(jogo)));
        jogos.forEach(jogo -> intSummaryStatistics.accept(retornarQuantidadeGolsPorJogo(jogo)));

        return intSummaryStatistics;
    }

    public List<Jogo> todosOsJogos() {
        return null;
    }

    public Long totalVitoriasEmCasa() {
        return jogos.stream().filter(placar -> placar.mandantePlacar() > placar.visitantePlacar()).count();
    }

    public Long totalVitoriasForaDeCasa() {
        return jogos.stream().filter(placar -> placar.visitantePlacar() > placar.mandantePlacar()).count();
    }

    public Long totalEmpates() {
        return jogos.stream().filter(jogo -> jogo.visitantePlacar().equals(jogo.mandantePlacar())).count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return jogos.stream().filter(jogo -> retornarQuantidadeGolsPorJogo(jogo) < 3).count();
    }

    public Integer retornarQuantidadeGolsPorJogo(Jogo jogo) {
        return jogo.visitantePlacar() + jogo.mandantePlacar();
//        return jogos.stream().map(jogo -> {
//            return jogo.visitantePlacar() + jogo.mandantePlacar();
//        }).collect(Collectors.summingInt(Integer::intValue));
    }

    public Long totalJogosCom3OuMaisGols() {
        return jogos.stream().filter(jogo -> retornarQuantidadeGolsPorJogo(jogo) >= 3).count();
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
        List<String> jogosCsv = Files.lines(file).toList();
        return jogosCsv.stream().skip(1).map(item -> {
            String[] atributos = item.split(";");
            return criarJogo(atributos);
        }).collect(Collectors.toList());
    }

    private Jogo criarJogo(String[] atributos) {
        DataDoJogo dataDoJogo = criarDataJogo(atributos[1], atributos[2], atributos[3]);
        return new Jogo(Integer.parseInt(atributos[0]), dataDoJogo, new Time(atributos[4]),
                new Time(atributos[5]), new Time(atributos[6]),
                atributos[7], Integer.parseInt(atributos[8]),
                Integer.parseInt(atributos[9]), atributos[10], atributos[11], atributos[12]);
    }

    private DataDoJogo criarDataJogo(String dataJogo, String horarioJogo, String diaJogo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data = LocalDate.parse(dataJogo, formatter);
        LocalTime horario = (horarioJogo.isBlank() || horarioJogo.isEmpty()) ? null : LocalTime.parse(horarioJogo.replace("h", ":"));
        DayOfWeek dia = getDayOfWeek(diaJogo);
        return new DataDoJogo(data, horario, dia);
    }


    private DayOfWeek getDayOfWeek(String dia) {
        return Map.of(
                "Segunda-feira", DayOfWeek.SUNDAY,
                "Terça-feira", DayOfWeek.SUNDAY,
                "Quarta-feira", DayOfWeek.SUNDAY,
                "Quinta-feira", DayOfWeek.SUNDAY,
                "Sexta-feira", DayOfWeek.SUNDAY,
                "Sábado", DayOfWeek.SUNDAY,
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

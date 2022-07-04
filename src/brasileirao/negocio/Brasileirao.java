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
    private Time time;

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
        return todosOsJogos().stream()
                .collect(Collectors.summarizingInt(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar()));
    }

    public List<Jogo> todosOsJogos() {
        return this.jogos.stream()
                .filter(filtro)
                .toList();
    }

    public Long totalVitoriasEmCasa() {
        return jogos.stream()
                .filter(filtro) //filtrar por ano
                .filter(jogo -> jogo.mandantePlacar() > jogo.visitantePlacar())
                .count();
    }

    public Long totalVitoriasForaDeCasa() {
        return jogos.stream()
                .filter(filtro) //filtrar por ano
                .filter(jogo -> jogo.mandantePlacar() < jogo.visitantePlacar())
                .count();
    }

    public Long totalEmpates() {
        return jogos.stream()
                .filter(filtro)
                .filter(empate -> empate.vencedor().nome().equals("-"))
                .count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return  this.jogos.stream()
                .filter(filtro)
                .map(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())
                .filter(menosGols -> menosGols < 3)
                .count();
    }

    public Long totalJogosCom3OuMaisGols() {
        return  this.jogos.stream()
                .filter(filtro)
                .map(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar())
                .filter(menosGols -> menosGols >= 3)
                .count();
    }

    public Map<Resultado, Long> todosOsPlacares() {
        List<Resultado> todosOsPlacares = todosOsJogos().stream()
                .map(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar()))
                .toList();

        return todosOsPlacares.stream()
                .collect(Collectors.toMap(
                        placar -> placar, placar -> (long)Collections.frequency(todosOsPlacares,placar),
                        (a,b) -> a //sempre que uma chave se repetir, ele fica com a primeira.
                ));
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        Optional<Map.Entry<Resultado, Long>> max = todosOsPlacares().entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue));//chama placares - converte em entrySet - compara o valor e não a chave

        return max.orElse(null);
    }


    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        Optional<Map.Entry<Resultado, Long>> min = todosOsPlacares().entrySet().stream()
                .min(Comparator.comparingLong(Map.Entry::getValue));//chama placares - converte em entrySet - compara o valor e não a chave

        return min.orElse(null);
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
        return jogos.stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Jogo::mandante));
    }

    /**
     * todos os jogos que cada time foi visitante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {
        return jogos.stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Jogo::visitante));
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {
        return Stream.of(todosOsJogosPorTimeComoMandantes(), todosOsJogosPorTimeComoVisitante())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> {
                            v1.addAll(v2);
                            return v1;
                        }));
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {
        Map<Time, Map<Boolean, List<Jogo>>> collect = todosOsJogosPorTime()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map -> Map.getValue().stream()
                                        .collect(Collectors.groupingBy(Jogo -> Jogo.mandante().nome().equals(Map.getKey().nome())))
                        )
                );

        System.out.println("RELAÇÃO DE JOGOS MANDANTE X VISITANTE");
        collect.entrySet().forEach(System.out::println);

        return collect;
    }


    public Set<PosicaoTabela> tabela() {
        List<PosicaoTabela> collect = todosOsJogosPorTime().keySet().stream()
                .map(
                    time -> new PosicaoTabela(time, totalVitoriasPorTime(time), totalDerrotasPorTime(time),
                            totalEmpatePorTime(time), totalPositivoDeGolsPorTime(time),
                            totalDeGolsSofridosPorTime(time), totalPositivoDeGolsPorTime(time) - totalDeGolsSofridosPorTime(time)))
                .sorted(Comparator.comparing(PosicaoTabela::getPontuacaoTotal).reversed()).toList();

        return new LinkedHashSet<>(collect);
    }

    private Long totalVitoriasPorTime(Time time){
        return todosOsJogosPorTime().get(time).stream()
                .filter(vitoria -> vitoria.vencedor().equals(time)).count();
    }

    private Long totalDerrotasPorTime(Time time){
        return todosOsJogosPorTime().get(time).stream()
                .filter(derrota -> !derrota.vencedor().equals(time)).count();
    }

    private Long totalEmpatePorTime(Time team){
        return todosOsJogosPorTime().get(team).stream()
                .filter(empate -> empate.estadoVencedor().equals("-")).count();
    }

    private long totalPositivoDeGolsPorTime(Time time) {
        return todosOsJogosPorTime().get(time).stream()
                .mapToInt(
                    golPositivo -> {
                        if (golPositivo.mandante().equals(time)){
                            return golPositivo.mandantePlacar();
                        } else if (golPositivo.visitante().equals(time)){
                            return golPositivo.visitantePlacar();
                        }
                        return 0;
                    })
                .sum();
    }

    private long totalDeGolsSofridosPorTime(Time time) {
        this.time = time;
        return (long) todosOsJogosPorTime().get(time).stream()
                .mapToInt(
                    golSofrido -> {
                        if (!golSofrido.mandante().equals(time)){
                            return golSofrido.mandantePlacar();
                        } else if (!golSofrido.visitante().equals(time)){
                            return golSofrido.visitantePlacar();
                        }
                        return 0;
                    })
                .sum();
    }



    //LENDO OS ARQUIVOS
    public List<Jogo> lerArquivo(Path file) throws IOException {
        List<String> strings = Files.readAllLines(file);
        strings.replaceAll(formatandoH -> formatandoH.replace(":", "h"));

        List<String[]> cadaCampoArquivo = strings.stream()
                .skip(1)
                .map(linha -> linha.split(";"))
                .toList();

        return cadaCampoArquivo.stream()
                .map(campo ->
                        new Jogo(Integer.valueOf(campo[0]),
                        new DataDoJogo(LocalDate.parse(campo[1], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                LocalTime.parse(campoVazioTempo(campo[2]), DateTimeFormatter.ofPattern("HH'h'mm")),
                                getDayOfWeek(campo[3])),
                        new Time(campo[4]),
                        new Time(campo[5]),
                        new Time(campo[6]),
                        campo[7],
                        Integer.valueOf(campo[8]),
                        Integer.valueOf(campo[9]),
                        campo[10],
                        campo[11],
                        campo[12]
                        )).toList();
    }


    private String campoVazioTempo(String tempo){
        if(tempo.isEmpty()){
            return "16h00";
        }
        return tempo;
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


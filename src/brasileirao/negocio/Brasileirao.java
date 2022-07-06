package brasileirao.negocio;


import brasileirao.dominio.*;

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
        return jogos
                .stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.averagingDouble(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar())
                ));
    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return jogos
                .stream()
                .collect(Collectors.summarizingInt(jogo -> jogo.visitantePlacar() + jogo.mandantePlacar()));

    }

    public List<Jogo> todosOsJogos() {
        return jogos
                .stream()
                .filter(filtro)
                .toList();
    }

    public Long totalVitoriasEmCasa() {
        return jogos
                .stream()
                .filter(filtro)
                .filter(jogo -> jogo.mandante().equals(jogo.vencedor()))
                .count();
    }

    public Long totalVitoriasForaDeCasa() {
        return jogos
                .stream()
                .filter(filtro)
                .filter(jogo -> jogo.visitante().equals(jogo.vencedor()))
                .count();
    }

    public Long totalEmpates() {
        return jogos
                .stream()
                .filter(filtro)
                .filter(jogo -> jogo.vencedor().nome().equals("-"))
                .count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return jogos
                .stream()
                .filter(filtro)
                .filter(jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar()) < 3)
                .count();
    }

    public Long totalJogosCom3OuMaisGols() {
        return jogos
                .stream()
                .filter(filtro)
                .filter(jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar()) >= 3)
                .count();
    }

    public Map<Resultado, Long> todosOsPlacares() {
        List<Resultado> resultadoStream = jogos.stream()
                .filter(filtro)
                .map(jogo -> new Resultado(jogo.visitantePlacar(), jogo.mandantePlacar()))
                .toList();
        return resultadoStream
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        r -> (long) Collections.frequency(resultadoStream, r),
                        (jogo1, jogo2) -> jogo1
                ));
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        Optional<Map.Entry<Resultado, Long>> maisRepetido = Stream.of(todosOsPlacares())
                .flatMap(resultado -> resultado.entrySet().stream())
                .max(Comparator.comparing(resultado -> resultado.getValue()));

        return maisRepetido.orElseThrow(() -> new RuntimeException("Não existe jogo repetido"));
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        Optional<Map.Entry<Resultado, Long>> menosRepetido = Stream.of(todosOsPlacares())
                .flatMap(resultado -> resultado.entrySet().stream())
                .min(Comparator.comparing(resultado -> resultado.getValue()));

        return menosRepetido.orElseThrow(() -> new RuntimeException("Não existe jogo repetido"));
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

        return Stream.of(mandantes, visitantes)
                .flatMap(times -> times.stream())
                .distinct()
                .toList();
    }

    /**
     * todos os jogos que cada time foi mandante
     *
     * @return Map<Time, List < Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {
        return jogos.stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Jogo::mandante));
    }

    /**
     * todos os jogos que cada time foi visitante
     *
     * @return Map<Time, List < Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {
        return jogos.stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(Jogo::visitante));
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {
        return Stream.of(todosOsJogosPorTimeComoMandantes(), todosOsJogosPorTimeComoVisitante())
                .flatMap(jogos -> jogos.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (jogoDentro, jogoFora) -> {
                            jogoDentro.addAll(jogoFora);
                            return jogoDentro;
                        }
                ));
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {
        return todosOsJogosPorTime()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        map -> map.getValue().stream().collect(Collectors.partitioningBy(
                                jogo -> map.getKey().nome().equals(jogo.mandante())
                        ))

                ));
    }

    public Set<PosicaoTabela> tabela() {
         return todosOsJogosPorTime()
                .entrySet()
                .stream()
                .map(map -> {
                    long vitorias = map.getValue().stream()
                            .filter(jogo -> jogo.vencedor().equals(map.getKey()))
                            .count();
                    long empate = map.getValue().stream()
                            .filter(jogo -> jogo.vencedor().nome().equals("-"))
                            .count();
                    long derrotas = map.getValue().stream()
                            .filter(jogo -> !jogo.vencedor().equals(map.getKey()) && !jogo.vencedor().nome().equals("-"))
                            .count();;

                    long golsPositivosCasa = jogosParticionadosPorMandanteTrueVisitanteFalse()
                            .get(map.getKey()).get(true)
                            .stream()
                            .mapToLong(Jogo::mandantePlacar)
                            .sum();
                    long golsPositivosFora = jogosParticionadosPorMandanteTrueVisitanteFalse()
                            .get(map.getKey()).get(false)
                            .stream()
                            .mapToLong(Jogo::visitantePlacar)
                            .sum();
                    long golsSofridosCasa = jogosParticionadosPorMandanteTrueVisitanteFalse()
                            .get(map.getKey()).get(true)
                            .stream()
                            .mapToLong(Jogo::visitantePlacar)
                            .sum();
                    long golsSofridosFora = jogosParticionadosPorMandanteTrueVisitanteFalse()
                            .get(map.getKey()).get(false)
                            .stream()
                            .mapToLong(Jogo::mandantePlacar)
                            .sum();
                    long golsMarcados = golsPositivosCasa + golsPositivosFora;
                    long golsSofridos = golsSofridosCasa + golsSofridosFora;
                    long saldoGols = golsMarcados - golsSofridos;
                    return new PosicaoTabela(map.getKey(), vitorias, derrotas, empate, golsMarcados, golsSofridos, saldoGols);
                })
                 .sorted(Comparator.comparing(PosicaoTabela::getPontuacaoTotal).reversed())
                 .collect(Collectors.toCollection(LinkedHashSet::new));
    }


    private long derrotas() {
        return todosOsJogosPorTime()
                .entrySet()
                .stream()
                .map(map -> map.getValue().stream().filter(jogo -> !jogo.vencedor().equals(map.getKey()) && !jogo.vencedor().nome().equals("-")))
                .count();
    }



    public List<Jogo> lerArquivo(Path file) throws IOException {
        List<String[]> linhasFile = Files.readAllLines(file).stream()
                .map(linha -> linha.split(";")).toList();


        List<Jogo> jogos = linhasFile.stream()
                .skip(1)
                .map(jogo -> {
                    LocalDate Data = LocalDate.parse(jogo[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalTime horario = null;
                    try {
                        horario = LocalTime.parse((jogo[2].replace("h", ":")));
                    } catch (DateTimeParseException dtpe) {
                        horario = LocalTime.of(16, 00);
                    }
                    DayOfWeek dia = getDayOfWeek(jogo[3]);
                    return new Jogo(Integer.parseInt(jogo[0]),
                            new DataDoJogo(Data, horario, dia),
                            new Time(jogo[4]),
                            new Time(jogo[5]),
                            new Time(jogo[6]),
                            jogo[7],
                            Integer.parseInt(jogo[8]),
                            Integer.parseInt(jogo[9]),
                            jogo[10],
                            jogo[11],
                            jogo[12]);
                }).toList();

        return jogos;
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

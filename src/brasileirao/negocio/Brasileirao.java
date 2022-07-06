package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Brasileirao {
    private final Map<Integer, List<Jogo>> brasileiraoByRound;
    private final List<Jogo> jogos;
    private final Predicate<Jogo> filtro;

    public Brasileirao(Path arquivo, Predicate<Jogo> filtro) throws IOException {
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
        //total de gol, total de jogos, media de gols
       // total de jogos = this.jogos.size(); /// nao, transformar numa Integerstream e egar o intSumStats

        return this.jogos.stream()
                         .mapToInt(jogo -> jogo.getMandantePlacar() + jogo.getVisitantePlacar())
                         .summaryStatistics();
    }

    private List<Jogo> todosOsJogos() {
        return null;
    }

    public Long totalVitoriasEmCasa() {
        return this.jogos.stream().filter(jogo -> jogo.getMandantePlacar() > jogo.getVisitantePlacar()).count();
    }

    public Long totalVitoriasForaDeCasa() {
        return this.jogos.stream().filter(jogo -> jogo.getVisitantePlacar() > jogo.getMandantePlacar()).count();
    }

    public Long totalEmpates() {
        return this.jogos.stream().filter(jogo -> jogo.getVisitantePlacar() == jogo.getMandantePlacar()).count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return this.jogos.stream().filter(jogo -> jogo.getMandantePlacar() + jogo.getVisitantePlacar() < 3).count();
    }

    public Long totalJogosCom3OuMaisGols() {
        return this.jogos.stream().filter(jogo -> jogo.getMandantePlacar() + jogo.getVisitantePlacar() >= 3).count();
    }

    public Map<Resultado, Long> todosOsPlacares() {
        return null;
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        Map.Entry<Resultado, Long> emptyEntry = new AbstractMap.SimpleEntry<>(new Resultado(0, 0), 0L);

         Map<Resultado, Long> resultsByNumberOfOccurrences =  this.jogos.stream().
                                       map(jogo -> new Resultado(jogo.getMandantePlacar(), jogo.getVisitantePlacar())).
                                       collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

         return resultsByNumberOfOccurrences.entrySet().stream().max(Map.Entry.comparingByValue()).get();

//                entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).orElse(emptyEntry);

//                          entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).
//                          orElse(  emptyEntry  )   ;
//                                  ,  Collectors.summingLong());
//                          sorted().
//                          findFirst();
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        Map.Entry<Resultado, Long> emptyEntry = new AbstractMap.SimpleEntry<>(new Resultado(0, 0), 0L);

        Map<Resultado, Long> resultsByNumberOfOccurrences =  this.jogos.stream().
                map(jogo -> new Resultado(jogo.getMandantePlacar(), jogo.getVisitantePlacar())).
                collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return resultsByNumberOfOccurrences.entrySet().stream().max((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())).get();    }

    private List<Time> todosOsTimes() {
        List<Time> mandantes = todosOsJogos().stream()
                                             .map(Jogo::getMandante)
                                             .collect(Collectors.toList());

        List<Time> visitantes = todosOsJogos().stream()
                                              .map(Jogo::getVisitante)
                                              .collect(Collectors.toList());

        return Collections.emptyList();
    }

    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {
        return this.jogos.stream().collect(Collectors.groupingBy(Jogo::getMandante));
    }

    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {
        return this.jogos.stream().collect(Collectors.groupingBy(Jogo::getVisitante));
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {
        return Stream.of(todosOsJogosPorTimeComoMandantes(), todosOsJogosPorTimeComoVisitante()).
                      flatMap(map -> map.entrySet().stream()).
                      collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (list1, list2) -> {
                                                                                            list1.addAll(list2);
                                                                                            return list1;
                                                                                        }));
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {
        Map<Time, Map<Boolean, List<Jogo>>> matchesByHomeTeam =
                                        todosOsJogosPorTimeComoMandantes().
                                        entrySet().
                                        stream().
                                        collect(Collectors.toMap(Map.Entry::getKey,
                                                                 entry -> {
                                                                     Map<Boolean, List<Jogo>> map = new HashMap<>();
                                                                     map.put(true, entry.getValue());
                                                                     return map;
                                                                 }));

        Map<Time, Map<Boolean, List<Jogo>>> matchesByAwayTeam =
                                        todosOsJogosPorTimeComoVisitante().
                                        entrySet().
                                        stream().
                                        collect(Collectors.toMap(Map.Entry::getKey,
                                                                 entry -> {
                                                                     Map<Boolean, List<Jogo>> map = new HashMap<>();
                                                                     map.put(false, entry.getValue());
                                                                     return map;
                                                                 }));

        return Stream.of(matchesByHomeTeam, matchesByAwayTeam).
                      flatMap(map -> map.entrySet().stream()).
                      collect(Collectors.toMap(Map.Entry::getKey,
                                               Entry::getValue,
                                               (map1, map2) -> {
                                                   Map<Boolean, List<Jogo>> map3 = new HashMap<>();
                                                   map3.putAll(map1);
                                                   map3.putAll(map2);
                                                   return map3;
                                               })); //groupingBy(entry -> entry.getKey()));
    }

    public Set<PosicaoTabela> tabela() {

        return null; //talvez seja um groupingby nome do time, somando as coisas
    }

    private List<Jogo> lerArquivo(Path file) throws IOException {
        List<Jogo> matches = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            scanner.nextLine();

            while (scanner.hasNext()) {
                String line = scanner.nextLine();

                String[] matchData = line.split(";");

                String hourString = matchData[2].replace('h', ':');

                if (hourString.isEmpty()) {
                    hourString = "00:00";
                }

                DataDoJogo matchDate = new DataDoJogo(LocalDate.parse(matchData[1], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        LocalTime.parse(hourString, DateTimeFormatter.ofPattern("HH:mm")),
                        getDayOfWeek(matchData[3]));
                // DayOfWeek.valueOf(matchData[3]));

                Jogo match = new Jogo(Integer.valueOf(matchData[0]),  // Integer rodada
                                      matchDate,                      // DataDoJogo data
                                      new Time(matchData[4]),         // Time mandante
                                      new Time(matchData[5]),         // Time visitante
                                      new Time(matchData[6]),         // Time vencedor
                                      matchData[7],                   // String arena
                                      Integer.valueOf(matchData[8]),  // Integer mandante placar
                                      Integer.valueOf(matchData[9]),  // Integer visitante placar
                                      matchData[10],                  // String estado mandante
                                      matchData[11],                  // String estado visitante
                                      matchData[12]);                 // String estado vencedor

                matches.add(match);
            }
        }

        return matches;
    }

    private DayOfWeek getDayOfWeek(String dia) {
        Map<String, DayOfWeek> daysOfWeek = new HashMap<>();

        daysOfWeek.put("Domingo", DayOfWeek.SUNDAY);
        daysOfWeek.put("Segunda-feira", DayOfWeek.MONDAY);
        daysOfWeek.put("Terça-feira", DayOfWeek.TUESDAY);
        daysOfWeek.put("Quarta-feira", DayOfWeek.WEDNESDAY);
        daysOfWeek.put("Quinta-feira", DayOfWeek.THURSDAY);
        daysOfWeek.put("Sexta-feira", DayOfWeek.FRIDAY);
        daysOfWeek.put("Sábado", DayOfWeek.SATURDAY);

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

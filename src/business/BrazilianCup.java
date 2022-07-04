package business;

import domain.*;
import resources.GameBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BrazilianCup {

    private Map<Integer, List<Game>> brasilianCup;
    private List<Game> games;
    private Predicate<Game> filter;

    public BrazilianCup(Path file, Predicate<Game> filter) throws IOException {
        this.games = readFile(file);
        this.filter = filter;
        Map<Integer, List<Game>> map = new HashMap<>();
        for (Game game : games) {
            if (filter.test(game)) {
                Game game1 = game;
                map.computeIfAbsent(game.round(), k -> new ArrayList<>()).add(game1);
            }
        }
        this.brasilianCup = map;
    }

    public Set<TablePosition> table() {
        List<TablePosition> collect = totalGamesPerTeam().keySet().stream().map(
                        team -> new TablePosition(team, totalWinsPerTeam(team), totalDefeatsPerTeam(team),
                                totalDrawsPerTeam(team), totalPositiveGoalsPerTeam(team),
                                totalNegativeGoalsPerTeam(team), totalPositiveGoalsPerTeam(team) - totalNegativeGoalsPerTeam(team)))
                .sorted(Comparator.comparing(TablePosition::getTotalScore).reversed()).toList();
        return new LinkedHashSet<>(collect);
    }

    public List<Game> readFile(Path file) throws IOException {
        List<String> readFile = new ArrayList<>(Files.readAllLines(file));
        readFile.remove(0);
        readFile.replaceAll(line -> line.replace(":", "h"));

        List<String[]> fieldsFile = readFile.stream().map(line -> line.split(";")).toList();
        return fieldsFile.stream().map(
                field -> new GameBuilder().withRound(field[0])
                        .withDate(field[1], defaultTime(field[2]), getDayOfWeek(field[3])).withHome(field[4])
                        .withGuest(field[5]).withWinner(field[6]).withSite(field[7])
                        .withHomeScore(field[8]).withGuestScore(field[9]).withHomeState(field[10])
                        .withGuestState(field[11]).withWinnerState(field[12]).build()).toList();
    }

    private DayOfWeek getDayOfWeek(String day) {
        return Map.of(
                "Segunda-feira", DayOfWeek.MONDAY,
                "Terça-feira", DayOfWeek.TUESDAY,
                "Quarta-feira", DayOfWeek.WEDNESDAY,
                "Quinta-feira", DayOfWeek.THURSDAY,
                "Sexta-feira", DayOfWeek.FRIDAY,
                "Sábado", DayOfWeek.SATURDAY,
                "Domingo", DayOfWeek.SUNDAY
        ).get(day);
    }

    private String defaultTime(String a){
        if (a.isBlank()||a.isEmpty()){
            return "16h00";
        }
        return a;
    }

    public List<Game> totalGames() {
        return this.brasilianCup.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public Map<Result, Long> totalScoreboards() {
        List<Result> results = totalGames().stream()
                .map(game -> new Result(game.homeScore(), game.guestScore())).toList();

        return results.stream()
                .collect(Collectors.toMap(
                        result -> result,
                        result -> (long) Collections.frequency(results, result),
                        (a,b) -> a
                ));
    }

    public Map.Entry<Result, Long> scoreMoreRepeated() {
        Optional<Map.Entry<Result, Long>> max = totalScoreboards().entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue));
        return max.orElseThrow(()-> new RuntimeException("Error"));
    }

    public Map.Entry<Result, Long> scoreMinusRepeated() {
        Optional<Map.Entry<Result, Long>> min = totalScoreboards().entrySet().stream()
                .min(Comparator.comparingLong(Map.Entry::getValue));
        return min.orElseThrow(()-> new RuntimeException("Error"));
    }

    public Long totalWinsAtHome() {
        return totalGames().stream()
                .filter(game -> game.homeScore() > game.guestScore())
                .count();
    }

    public Long totalWinsOutHome() {
        return totalGames().stream()
                .filter(game -> game.guestScore() > game.homeScore())
                .count();
    }

    public Long totalDraws() {
        return totalGames().stream()
                .filter(game -> Objects.equals(game.homeScore(), game.guestScore()))
                .count();
    }

    public Map<Game, Double> averageGoalsPerGame() {
        return totalGames().stream().collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.averagingInt(game -> game.homeScore() + game.guestScore())
        ));
    }

    public IntSummaryStatistics statsPerGame() {

        return totalGames()
                .stream()
                .mapToInt(x -> x.guestScore() + x.homeScore())
                .summaryStatistics();
    }

    public Long totalGamesWithLessThan3Goals() {
        return totalGames().stream()
                .map(game -> game.homeScore() + game.guestScore())
                .filter(goals -> goals < 3)
                .count();
    }

    public Long totalGamesWith3OrMoreGoals() {
        return totalGames().stream()
                .map(game -> game.homeScore() + game.guestScore())
                .filter(goals -> goals >= 3)
                .count();
    }

    private List<Team> allTeams() {
        List<Team> homes = totalGames()
                .stream()
                .map(Game::home)
                .toList();

        List<Team> guests = totalGames()
                .stream()
                .map(Game::guest)
                .toList();

        return Stream.of(homes, guests).flatMap(List::stream).toList();
    }

    public Map<Team, List<Game>> totalGamesPerTeam() {
        return Stream.of(totalGamesForTeamAsHome(), totalGamesForTeamAsGuest())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> {
                            v1.addAll(v2);
                            return v1;
                        }));
    }

    private Map<Team, List<Game>> totalGamesForTeamAsHome() {
        return totalGames().stream().collect(Collectors.groupingBy(Game::home));
    }

    private Map<Team, List<Game>> totalGamesForTeamAsGuest() {
        return totalGames().stream().collect(Collectors.groupingBy(Game::guest));
    }

    public Map<Team, Map<Boolean, List<Game>>> gamesHomeTrueGuestFalse() {
        Map<Team, Map<Boolean, List<Game>>> collect = totalGamesPerTeam()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map -> Map.getValue().stream()
                                        .collect(Collectors.groupingBy(Game -> Game.home().name().equals(Map.getKey().name())))
                        )
                );

        System.out.println("RELAÇÃO DE JOGOS MANDANTE VS VISITANTE");
        collect.entrySet().forEach(System.out::println);

        return collect;
    }

    private Long totalWinsPerTeam(Team team){
        return totalGamesPerTeam().get(team).stream().filter(x -> x.winner().equals(team)).count();
    }

    private Long totalDefeatsPerTeam(Team team){
        return totalGamesPerTeam().get(team).stream().filter(x -> !x.winner().equals(team)).count();
    }

    private Long totalDrawsPerTeam(Team team){
        return totalGamesPerTeam().get(team).stream().filter(x -> x.winnerState().equals("-")).count();
    }

    private long totalPositiveGoalsPerTeam(Team team) {
        return totalGamesPerTeam().get(team).stream().mapToInt(
                x -> {
                    if (x.home().equals(team)){
                        return x.homeScore();
                    } else if (x.guest().equals(team)){
                        return x.guestScore();
                    }
                    return 0;
                }).sum();
    }

    private Long totalNegativeGoalsPerTeam(Team team){
        return (long) totalGamesPerTeam().get(team).stream().mapToInt(
                x -> {
                    if (!x.home().equals(team)){
                        return x.homeScore();
                    } else if (!x.guest().equals(team)){
                        return x.guestScore();
                    }
                    return 0;
                }).sum();
    }

    private Map<Integer, Integer> totalGoalsPerRound() {
        return totalGames().stream().collect(Collectors.toMap(
                Game::round,
                game -> game.homeScore() + game.guestScore()));
    }

    private Map<Integer, Double> averageGoalsPerRound() {
        return totalGames().stream().collect(Collectors.groupingBy(
                Game::round,
                Collectors.averagingInt(jogo -> jogo.homeScore() + jogo.guestScore())));
    }
}
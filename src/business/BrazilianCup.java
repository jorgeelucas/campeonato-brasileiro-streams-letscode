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

public class BrazilianCup {

    private final Predicate<Game> filter;
    private Map<Integer, List<Game>> brazilianCup;
    private List<Game> games;

    public BrazilianCup(Path file, Predicate<Game> filter) throws IOException {
        this.games = readFile(file);
        this.filter = filter;
        this.brazilianCup = games.stream()
                .filter((Predicate<? super Game>) games)
                .collect(Collectors.groupingBy(
                        Game::round,
                        Collectors.mapping(Function.identity(), Collectors.toList())));

    }

    public Map<Game, Double> averageGoalsPerGame() {
        return totalGames().stream().collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.averagingInt(game -> game.homeScore() + game.guestScore())
        ));
    }

    public IntSummaryStatistics statsPerGame() {
        return games.stream().collect(Collectors.summarizingInt(game -> game.homeScore() + game.guestScore()));
    }

    public List<Game> totalGames() {
        return games.stream().filter(filter).toList();
    }

    public Long totalWinsAtHome() {
        return null;
    }

    public Long totalWinsOutHome() {
        return null;
    }

    public Long totalDraws() {
        return null;
    }

    public Long totalGamesWithLessThan3Goals() {
        return null;
    }

    public Long totalGamesWith3OrMoreGoals() {
        return null;
    }

    public Map<Result, Long> allScoreboards() {
        return null;
    }

    public Map.Entry<Result, Long> scoreMoreRepeated() {
        return null;
    }

    public Map.Entry<Result, Long> scoreMinusRepeated() {
        return null;
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

        return null;
    }

    private Map<Team, List<Game>> allGamesForTeamAsHome() {
        return null;
    }

    private Map<Team, List<Game>> allGamesForTeamAsGuest() {
        return null;
    }

    public Map<Team, List<Game>> allGamesForTeam() {
        return null;
    }

    public Map<Team, Map<Boolean, List<Game>>> gamesPerHomeTrueGuestFalse() {
        return null;
    }

    public Set<TablePosition> table() {
        return null;
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

    private Map<Integer, Integer> totalGoalsPerRound() {
        return null;
    }

    private Map<Team, Integer> totalGoalsPerTeam() {
        return null;
    }

    private Map<Integer, Double> averageGoalsPerRound() {
        return null;
    }

    private String defaultTime(String a){
        if (a.isBlank()||a.isEmpty()){
            return "16h00";
        }
        return a;
    }
}
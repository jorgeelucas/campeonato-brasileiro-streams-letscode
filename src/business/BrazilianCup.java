package business;

import domain.*;
import java.io.IOException;
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
                .filter((Predicate<? super Game>) games) //filtrar por ano
                .collect(Collectors.groupingBy(
                        Game::round,
                        Collectors.mapping(Function.identity(), Collectors.toList())));

    }

    public Map<Game, Integer> averageGoalsPerGame() {
        return null;
    }

    public IntSummaryStatistics statsPerGame() {
        return null;
    }

    public List<Game> totalGames() {
        return null;
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
        return null;
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
}
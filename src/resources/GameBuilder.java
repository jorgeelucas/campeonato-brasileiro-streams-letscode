package resources;

import domain.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GameBuilder {

    private Integer round;
    private DateOfGame date;
    private Team home;
    private Team guest;
    private Team winner;
    private String site;
    private Integer homeScore;
    private Integer guestScore;
    private String homeState;
    private String guestState;
    private String winnerState;

    public GameBuilder withRound(String round) {
        this.round = Integer.valueOf(round);
        return this;
    }

    public GameBuilder withDate(String date, String time, DayOfWeek day) {
        LocalDate dateParse = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalTime timeParse = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH'h'mm"));
        this.date = new DateOfGame(dateParse, timeParse, day);
        return this;
    }

    public GameBuilder withHome(String home) {
        this.home = new Team(home);
        return this;
    }

    public GameBuilder withGuest(String guest) {
        this.guest = new Team(guest);
        return this;
    }

    public GameBuilder withWinner(String winner) {
        this.winner = new Team(winner);
        return this;
    }

    public GameBuilder withSite(String site) {
        this.site = site;
        return this;
    }

    public GameBuilder withHomeScore(String homeScore) {
        this.homeScore =  Integer.valueOf(homeScore);
        return this;
    }

    public GameBuilder withGuestScore(String guestScore) {
        this.guestScore =  Integer.valueOf(guestScore);
        return this;
    }

    public GameBuilder withHomeState(String homeState) {
        this.homeState = homeState;
        return this;
    }

    public GameBuilder withGuestState(String guestState) {
        this.guestState = guestState;
        return this;
    }

    public GameBuilder withWinnerState(String winnerState) {
        this.winnerState = winnerState;
        return this;
    }

    public Game build(){
        return new Game(round, date, home, guest, winner, site, homeScore, guestScore, homeState, guestState, winnerState);
    }
}
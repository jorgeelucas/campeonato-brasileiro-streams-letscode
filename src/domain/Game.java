package domain;

public record Game(Integer round,
                   DateOfGame date,
                   Team home,
                   Team guest,
                   Team winner,
                   String site,
                   Integer homeScore,
                   Integer guestScore,
                   String homeState,
                   String guestState,
                   String winnerState){}

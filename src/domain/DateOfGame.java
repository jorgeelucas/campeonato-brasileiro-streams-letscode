package domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record DateOfGame(LocalDate date,
                  LocalTime hour,
                  DayOfWeek day){}
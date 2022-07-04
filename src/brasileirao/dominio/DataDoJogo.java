package brasileirao.dominio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class DataDoJogo {
    private final LocalDate data;
    private final LocalTime horario;
    private final DayOfWeek dia;

    DataDoJogo (LocalDate pData, LocalTime pHorario, DayOfWeek pDia) {
        this.data = pData;
        this.horario = pHorario;
        this.dia = pDia;
    }

    public LocalDate getData() {
        return this.data;
    }
}

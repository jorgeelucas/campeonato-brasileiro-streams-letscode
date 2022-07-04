package brasileirao.recursos;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.Time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class JogoBuilder {

    private Integer rodada;
    private DataDoJogo data;
    private Time mandante;
    private Time visitante;
    private Time vencedor;
    private String arena;
    private Integer mandantePlacar;
    private Integer visitantePlacar;
    private String estadoMandante;
    private String estadoVisitante;
    private String estadoVencedor;

    public JogoBuilder withRodada(String rodada) {
        this.rodada = Integer.valueOf(rodada);
        return this;
    }

    public JogoBuilder withData(String data, String horario, DayOfWeek dia) {
        LocalDate date = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalTime time = LocalTime.parse(horario, DateTimeFormatter.ofPattern("HH'h'mm"));
        this.data = new DataDoJogo(date, time, dia);
        return this;
    }

    public JogoBuilder withMandante(String mandante) {
        this.mandante = new Time(mandante);
        return this;
    }

    public JogoBuilder withVisitante(String visitante) {
        this.visitante = new Time(visitante);
        return this;
    }

    public JogoBuilder withVencedor(String vencedor) {
        this.vencedor = new Time(vencedor);
        return this;
    }

    public JogoBuilder withArena(String arena) {
        this.arena = arena;
        return this;
    }

    public JogoBuilder withMandantePlacar(String mandantePlacar) {
        this.mandantePlacar =  Integer.valueOf(mandantePlacar);
        return this;
    }

    public JogoBuilder withVisitantePlacar(String visitantePlacar) {
        this.visitantePlacar =  Integer.valueOf(visitantePlacar);
        return this;
    }

    public JogoBuilder withEstadoMandante(String estadoMandante) {
        this.estadoMandante = estadoMandante;
        return this;
    }

    public JogoBuilder withEstadoVisitante(String estadoVisitante) {
        this.estadoVisitante = estadoVisitante;
        return this;
    }

    public JogoBuilder withEstadoVencedor(String estadoVencedor) {
        this.estadoVencedor = estadoVencedor;
        return this;
    }

    public Jogo build(){
        return new Jogo(rodada, data, mandante, visitante, vencedor, arena, mandantePlacar, visitantePlacar,
                estadoMandante, estadoVisitante, estadoVencedor);
    }
}

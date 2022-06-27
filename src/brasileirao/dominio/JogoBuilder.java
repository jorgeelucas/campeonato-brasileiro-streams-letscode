package brasileirao.dominio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class JogoBuilder implements IJogoBuilder{

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

    @Override
    public IJogoBuilder setRodada(String rodada) {
        this.rodada = Integer.valueOf(rodada);
        return this;
    }

    @Override
    public IJogoBuilder setDataDoJogo(String data, String horario, DayOfWeek dia) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataDoJogo = LocalDate.parse(data, dateFormatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH'h'mm");
        LocalTime horarioDoJogo;

        if (horario.isEmpty()){
            horarioDoJogo = LocalTime.parse("16h00",timeFormatter);
            this.data = new DataDoJogo(dataDoJogo, horarioDoJogo, dia);
        } else {
            horario = horario.replace(":", "h");
            horarioDoJogo = LocalTime.parse(horario,timeFormatter);
            this.data = new DataDoJogo(dataDoJogo, horarioDoJogo, dia);
        }
        return this;
    }

    @Override
    public IJogoBuilder setMandante(String mandante) {
        this.mandante = new Time(mandante);
        return this;
    }

    @Override
    public IJogoBuilder setVisitante(String visitante) {
        this.visitante = new Time(visitante);
        return this;
    }

    @Override
    public IJogoBuilder setVencedor(String vencedor) {
        this.vencedor = new Time(vencedor);
        return this;
    }

    @Override
    public IJogoBuilder setArena(String arena) {
        this.arena = arena;
        return this;
    }

    @Override
    public IJogoBuilder setMandantePlacar(String mandantePlacar) {
        this.mandantePlacar = Integer.valueOf(mandantePlacar);
        return this;
    }

    @Override
    public IJogoBuilder setVisitantePlacar(String visitantePlacar) {
        this.visitantePlacar = Integer.valueOf(visitantePlacar);
        return this;
    }

    @Override
    public IJogoBuilder setEstadoMandante(String estadoMandante) {
        this.estadoMandante = estadoMandante;
        return this;
    }

    @Override
    public IJogoBuilder setEstadoVisitante(String estadoVisitante) {
        this.estadoVisitante = estadoVisitante;
        return this;
    }

    @Override
    public IJogoBuilder setEstadoVencedor(String estadoVencedor) {
        this.estadoVencedor = estadoVencedor;
        return this;
    }
    @Override
    public Jogo build() {
        return new Jogo(rodada,data,mandante,visitante,vencedor,arena,mandantePlacar,visitantePlacar,estadoMandante,
        estadoVisitante,estadoVencedor);
    }
}

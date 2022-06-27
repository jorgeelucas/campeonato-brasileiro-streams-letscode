package brasileirao.dominio;

import java.time.DayOfWeek;

public interface IJogoBuilder {
    IJogoBuilder setRodada(String rodada);
    IJogoBuilder setDataDoJogo(String data, String horario, DayOfWeek dia);
    IJogoBuilder setMandante(String mandante);
    IJogoBuilder setVisitante(String visitante);
    IJogoBuilder setVencedor(String vencedor);
    IJogoBuilder setArena(String arena);
    IJogoBuilder setMandantePlacar(String mandantePlacar);
    IJogoBuilder setVisitantePlacar(String visitantePlacar);
    IJogoBuilder setEstadoMandante(String estadoMandante);
    IJogoBuilder setEstadoVisitante(String estadoVisitante);
    IJogoBuilder setEstadoVencedor(String estadoVencedor);
    Jogo build();
}

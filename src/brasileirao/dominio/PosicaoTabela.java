package brasileirao.dominio;

import java.util.Objects;

public class PosicaoTabela implements Comparable<PosicaoTabela> {
    private static final byte POINTS_PER_WIN = 3;

    private final Time time;
    private final Long vitorias;
    private final Long derrotas;
    private final Long empates;
    private final Long golsPositivos;
    private final Long golsSofridos;
    private final Long saldoDeGols;

    public PosicaoTabela(Time pTime, Long pVitorias, Long pDerrotas, Long pEmpates,
                         Long pGolsPositivos, Long pGolsSofridos, Long pSaldoDeGols) {
        this.time = pTime;
        this.vitorias = pVitorias;
        this.derrotas = pDerrotas;
        this.empates = pEmpates;
        this.golsPositivos = pGolsPositivos;
        this.golsSofridos = pGolsSofridos;
        this.saldoDeGols = pSaldoDeGols;
    }

    private Long getPontuacaoTotal() {
        return (vitorias * POINTS_PER_WIN) + empates;
    }

    @Override
    public String toString() {
        return  time +
                ", pontos=" + getPontuacaoTotal() +
                ", vitorias=" + vitorias +
                ", derrotas=" + derrotas +
                ", empates=" + empates +
                ", golsPositivos=" + golsPositivos +
                ", golsSofridos=" + golsSofridos +
                ", saldoDeGols=" + saldoDeGols +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PosicaoTabela that = (PosicaoTabela) o;
        return Objects.equals(time, that.time) && Objects.equals(vitorias, that.vitorias) && Objects.equals(derrotas, that.derrotas) && Objects.equals(empates, that.empates) && Objects.equals(golsPositivos, that.golsPositivos) && Objects.equals(golsSofridos, that.golsSofridos) && Objects.equals(saldoDeGols, that.saldoDeGols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, vitorias, derrotas, empates, golsPositivos, golsSofridos, saldoDeGols);
    }

    @Override
    public int compareTo(PosicaoTabela o) {
        if (this.getPontuacaoTotal() > o.getPontuacaoTotal()) {
            return -1;
        } else if (this.getPontuacaoTotal() < o.getPontuacaoTotal()) {
            return 1;
        } else return o.saldoDeGols.compareTo(this.saldoDeGols);
    }
}

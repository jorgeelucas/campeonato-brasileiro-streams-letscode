package brasileirao.dominio;

public class PosicaoTabela {
    private final Time time;
    private final Long vitorias;
    private final Long derrotas;
    private final Long empates;
    private final Long golsPositivos;
    private final Long golsSofridos;
    private final Long saldoDeGols;

    PosicaoTabela(Time pTime, Long pVitorias, Long pDerrotas, Long pEmpates,
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
        return (vitorias * 3) + empates;
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
}

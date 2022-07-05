package brasileirao.dominio;

public record  PosicaoTabela(Time time,
                     Long vitorias,
                     Long derrotas,
                     Long empates,
                     Long golsPositivos,
                     Long golsSofridos,
                     Long saldoDeGols) {
    public Long getPontuacaoTotal() {
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

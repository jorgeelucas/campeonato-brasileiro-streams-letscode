package brasileirao.dominio;

public class Jogo {
    private final Integer rodada;
    private final DataDoJogo data;
    private final Time mandante;
    private final Time visitante;
    private final Time vencedor;
    private final String arena;
    private final Integer mandantePlacar;
    private final Integer visitantePlacar;
    private final String estadoMandante;
    private final String estadoVisitante;
    private final String estadoVencedor;

    public Jogo(Integer pRodada,
                DataDoJogo pData,
                Time pMandante,
                Time pVisitante,
                Time pVencedor,
                String pArena,
                Integer pMandantePlacar,
                Integer pVisitantePlacar,
                String pEstadoMandante,
                String pEstadoVisitante,
                String pEstadoVencedor) {
        this.rodada = pRodada;
        this.data = pData;
        this.mandante = pMandante;
        this.visitante = pVisitante;
        this.vencedor = pVencedor;
        this.arena = pArena;
        this.mandantePlacar = pMandantePlacar;
        this.visitantePlacar = pVisitantePlacar;
        this.estadoMandante = pEstadoMandante;
        this.estadoVisitante = pEstadoVisitante;
        this.estadoVencedor = pEstadoVencedor;
    }

    public Integer getRodada() {
        return this.rodada;
    }

    public Time getMandante() {
        return this.mandante;
    }

    public Time getVisitante() {
        return this.visitante;
    }

    public DataDoJogo getData() {
        return this. data;
    }

    public int getMandantePlacar() {
        return mandantePlacar;
    }

    public int getVisitantePlacar() {
        return visitantePlacar;
    }
}

package brasileirao.dominio;

public class Resultado {
    private final Integer mandante;
    private final Integer visitante;

    Resultado(Integer pMandante, Integer pVisitante) {
        this.mandante = pMandante;
        this.visitante = pVisitante;
    }

    @Override
    public String toString() {
        return mandante + " x " + visitante;
    }
}

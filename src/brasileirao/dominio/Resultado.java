package brasileirao.dominio;

import java.util.Objects;

public class Resultado {
    private final Integer mandante;
    private final Integer visitante;

    public Resultado(Integer pMandante, Integer pVisitante) {
        this.mandante = pMandante;
        this.visitante = pVisitante;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resultado resultado = (Resultado) o;
        return Objects.equals(mandante, resultado.mandante) && Objects.equals(visitante, resultado.visitante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mandante, visitante);
    }

    @Override
    public String toString() {
        return mandante + " x " + visitante;
    }
}

package brasileirao.dominio;

public record Resultado (Integer mandante,
              Integer visitante){
    @Override
    public String toString() {
        return mandante + " x " + visitante;
    }


}

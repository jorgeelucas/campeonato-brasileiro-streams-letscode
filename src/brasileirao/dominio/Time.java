package brasileirao.dominio;

public class Time {
    private final String nome;

    public Time(String pNome){
        this.nome = pNome;
    }

    @Override
    public String toString() {
        return nome;
    }
}

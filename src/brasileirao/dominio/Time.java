package brasileirao.dominio;

import java.util.Objects;

public class Time {
    private final String nome;

    public Time(String pNome){
        this.nome = pNome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return Objects.equals(nome, time.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    @Override
    public String toString() {
        return this.nome;
    }
}

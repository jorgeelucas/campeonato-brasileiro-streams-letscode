package brasileirao;

import brasileirao.dominio.Jogo;
import brasileirao.negocio.Brasileirao;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

public class testemain {
    public static void main(String[] args) throws IOException {
        Path file = Path.of("campeonatos-brasileiro-pontos-corridos.csv");
        Predicate<Jogo> filtro = (jogo) -> jogo.data().data().getYear() == 2014;

        Brasileirao brasileirao = new Brasileirao(file, filtro);

        System.out.println(brasileirao.todosOsTimes());
        System.out.println(brasileirao.totalJogosCom3OuMaisGols());
        System.out.println(brasileirao.totalEmpates());
        System.out.println(brasileirao.placarMaisRepetido());
        System.out.println(brasileirao.placarMenosRepetido());
        System.out.println(brasileirao.todosOsPlacares());
        System.out.println(brasileirao.todosOsJogosPorTime());
    }
}

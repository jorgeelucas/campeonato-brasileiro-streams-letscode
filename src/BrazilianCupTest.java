import domain.*;
import business.BrazilianCup;
import java.io.IOException;
import java.nio.file.Path;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class BrazilianCupTest {
    public static void main(String[] args) throws IOException {

        Path file = Path.of("campeonatos-brasileiro-pontos-corridos.csv");

//        Predicate<Jogo> brasileiraoPorAno = (jogo) -> jogo.data().data().getYear() == 2020;
//        Predicate<Jogo> brasileiraoPorAno2 = (jogo) -> jogo.data().data().getYear() == 2021;
        Predicate<Game> filtre = (game) -> game.date().date().getYear() == 2014;
//        Predicate<Jogo> filtro = brasileiraoPorAno.or(brasileiraoPorAno2);

        BrazilianCup brazilianCup = new BrazilianCup(file, filtre);

        Set<TablePosition> positions = brazilianCup.table();

        printStatistics(brazilianCup);

        printsTable(positions);
    }

    private static void printStatistics(BrazilianCup brazilianCup) {
        IntSummaryStatistics statistics = brazilianCup.statsPerGame();

        System.out.println("Estatisticas (Total de gols) - " + statistics.getSum());
        System.out.println("Estatisticas (Total de jogos) - " + statistics.getCount());
        System.out.println("Estatisticas (Media de gols) - " + statistics.getAverage());

        Map.Entry<Result, Long> scoreMoreRepeated = brazilianCup.scoreMoreRepeated();

        System.out.println("Estatisticas (Placar mais repetido) - "
                + scoreMoreRepeated.getKey() + " (" +scoreMoreRepeated.getValue() + " jogo(s))");

        Map.Entry<Result, Long> scoreMinusRepeated = brazilianCup.scoreMinusRepeated();

        System.out.println("Estatisticas (Placar menos repetido) - "
                + scoreMinusRepeated.getKey() + " (" +scoreMinusRepeated.getValue() + " jogo(s))");

        Long totalGamesWith3OrMoreGoals = brazilianCup.totalGamesWith3OrMoreGoals();
        Long totalGamesWithLessThan3Goals = brazilianCup.totalGamesWithLessThan3Goals();

        System.out.println("Estatisticas (3 ou mais gols) - " + totalGamesWith3OrMoreGoals);
        System.out.println("Estatisticas (-3 gols) - " + totalGamesWithLessThan3Goals);

        Long totalWinsAtHome = brazilianCup.totalWinsAtHome();
        Long totalWinsOutHome = brazilianCup.totalWinsOutHome();
        Long totalDraws = brazilianCup.totalDraws();

        System.out.println("Estatisticas (Vitorias Fora de casa) - " + totalWinsAtHome);
        System.out.println("Estatisticas (Vitorias Em casa) - " + totalWinsOutHome);
        System.out.println("Estatisticas (Empates) - " + totalDraws);
    }

    public static void printsTable(Set<TablePosition> positions) {
        System.out.println();
        System.out.println("## TABELA CAMPEONADO BRASILEIRO: ##");
        int placing = 1;
        for (TablePosition position : positions) {
            System.out.println(placing +". " + position);
            placing++;
        }

        System.out.println();
        System.out.println();
    }
}
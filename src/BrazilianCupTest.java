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
        Predicate<Game> filter = (game) -> game.date().date().getYear() == 2020;
        BrazilianCup brazilianCup = new BrazilianCup(file, filter);
        Set<TablePosition> positions = brazilianCup.table();
        printStatistics(brazilianCup);
        printsTable(positions);
    }

    private static void printStatistics(BrazilianCup brazilianCup) {
        IntSummaryStatistics statistics = brazilianCup.statsPerGame();
        System.out.println();
        System.out.println("ESTATÍSTICAS");
        System.out.println("- Total de Gols: " + statistics.getSum());
        System.out.println("- Total de Jogos: " + statistics.getCount());
        System.out.println("- Média de Gols: " + statistics.getAverage());

        Map.Entry<Result, Long> scoreMoreRepeated = brazilianCup.scoreMoreRepeated();

        System.out.println("- Placar Mais Repetido: "
                + scoreMoreRepeated.getKey() + " (" +scoreMoreRepeated.getValue() + " jogos)");

        Map.Entry<Result, Long> scoreMinusRepeated = brazilianCup.scoreMinusRepeated();

        System.out.println("- Placar Menos Repetido: "
                + scoreMinusRepeated.getKey() + " (" +scoreMinusRepeated.getValue() + " jogo(s))");

        Long totalGamesWith3OrMoreGoals = brazilianCup.totalGamesWith3OrMoreGoals();
        Long totalGamesWithLessThan3Goals = brazilianCup.totalGamesWithLessThan3Goals();

        System.out.println("- Jogos com 3+ Gols: " + totalGamesWith3OrMoreGoals);
        System.out.println("- Jogos com -3 Gols: " + totalGamesWithLessThan3Goals);

        Long totalWinsAtHome = brazilianCup.totalWinsAtHome();
        Long totalWinsOutHome = brazilianCup.totalWinsOutHome();
        Long totalDraws = brazilianCup.totalDraws();

        System.out.println("- Vitórias Fora de Casa: " + totalWinsOutHome);
        System.out.println("- Vitórias em Casa: " + totalWinsAtHome);
        System.out.println("- Empates: " + totalDraws);
    }

    public static void printsTable(Set<TablePosition> positions) {
        System.out.println();
        System.out.println("TABELA CAMPEONADO BRASILEIRO");
        int placing = 1;
        for (TablePosition position : positions) {
            System.out.println(placing +". " + position);
            placing++;
        }
    }
}
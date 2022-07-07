package brasileirao;

import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.negocio.Brasileirao;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class TestandoBrasileirao {
    public static void main(String[] args) throws IOException {
        Path file = Paths.get("campeonatos-brasileiro-pontos-corridos.csv");

//        Predicate<Jogo> brasileiraoPorAno = jogo -> jogo.getData().getData().getYear() == 2022;
//        Predicate<Jogo> brasileiraoPorAno2 = jogo -> jogo.getData().getData().getYear() == 2021;
//        Predicate<Jogo> filtro = brasileiraoPorAno.or(brasileiraoPorAno2);
        Predicate<Jogo> filtro = jogo -> jogo.getData().getData().getYear() == 2014;

        Brasileirao brasileirao = new Brasileirao(file, filtro);

        Set<PosicaoTabela> posicoes = brasileirao.tabela();

        imprimirEstatisticas(brasileirao);

        imprimirTabela(posicoes);
    }

    private static void imprimirEstatisticas(Brasileirao brasileirao) {
        IntSummaryStatistics statistics = brasileirao.estatisticasPorJogo();

        System.out.println("Estatisticas (Total de gols) - " + statistics.getSum());
        System.out.println("Estatisticas (Total de jogos) - " + statistics.getCount());
        System.out.println("Estatisticas (Media de gols) - " + statistics.getAverage());

        Map.Entry<Resultado, Long> placarMaisRepetido = brasileirao.placarMaisRepetido();

        System.out.println("Estatisticas (Placar mais repetido) - " + placarMaisRepetido.getKey() +
                           " (" +placarMaisRepetido.getValue() + " jogo(s))");

        Map.Entry<Resultado, Long> placarMenosRepetido = brasileirao.placarMenosRepetido();

        System.out.println("Estatisticas (Placar menos repetido) - " + placarMenosRepetido.getKey() +
                           " (" +placarMenosRepetido.getValue() + " jogo(s))");

        Long jogosCom3OuMaisGols = brasileirao.totalJogosCom3OuMaisGols();
        Long jogosComMenosDe3Gols = brasileirao.totalJogosComMenosDe3Gols();

        System.out.println("Estatisticas (3 ou mais gols) - " + jogosCom3OuMaisGols);
        System.out.println("Estatisticas (-3 gols) - " + jogosComMenosDe3Gols);

        Long totalVitoriasEmCasa = brasileirao.totalVitoriasEmCasa();
        Long vitoriasForaDeCasa = brasileirao.totalVitoriasForaDeCasa();
        Long empates = brasileirao.totalEmpates();

        System.out.println("Estatisticas (Vitorias Fora de casa) - " + vitoriasForaDeCasa);
        System.out.println("Estatisticas (Vitorias Em casa) - " + totalVitoriasEmCasa);
        System.out.println("Estatisticas (Empates) - " + empates);
    }

    private static void imprimirTabela(Set<PosicaoTabela> posicoes) {
        System.out.println();
        System.out.println("## TABELA CAMPEONATO BRASILEIRO DE FUTEBOL: ##");

        int colocacao = 1;

        for (PosicaoTabela posicao : posicoes) {
            System.out.println(colocacao +". " + posicao);

            colocacao++;
        }

        System.out.println();
        System.out.println();
    }
}

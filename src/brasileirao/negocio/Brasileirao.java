package brasileirao.negocio;

import brasileirao.dados.LeituraDosDados;
import brasileirao.dados.TratamentoDosDados;
import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Brasileirao {

//    private Map<Integer, List<Jogo>> brasileirao;
    private List<Jogo> brasileirao;
    private List<Jogo> jogos;
//    private Predicate<Jogo> filtro;
    private int ano;

 /*   public Brasileirao(Path arquivo, Predicate<Jogo> filtro) throws IOException {
        this.filtro = filtro;
        this.brasileirao = jogos.stream()
                .filter(filtro) //filtrar por ano
                .collect(Collectors.groupingBy(
                        jogo -> jogo.getRodada(),
                        Collectors.mapping(Function.identity(), Collectors.toList())));
    } */

    public Brasileirao(Path arquivo, int ano) throws IOException {
        List<String> listaJogosBrasileirao = LeituraDosDados.lerArquivo(arquivo);
        this.jogos = TratamentoDosDados.tratarDados(listaJogosBrasileirao);
        this.ano = ano;

        //2020 e 2021 foi um único campeonato devido à pandemia
        if (ano == 2020){
            this.brasileirao =  this.jogos.stream()
                    .filter(jogo -> jogo.getData().getData().getYear() == 2021 ||
                            jogo.getData().getData().getYear() == 2020)
                    .collect(toList());
        } else {
            this.brasileirao = this.jogos.stream()
                    .filter(jogo -> jogo.getData().getData().getYear() == ano)
                    .collect(toList());
        }
    }

    public Map<Jogo, Integer> mediaGolsPorJogo() {
        return null;
    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return this.brasileirao.stream()
                .mapToInt(jogo -> jogo.getVisitantePlacar() + jogo.getMandantePlacar())
                .summaryStatistics();
    }

    public List<Jogo> todosOsJogos() {
        return brasileirao;
    }

    public Long totalVitoriasEmCasa() {
       return this.brasileirao.stream()
                .filter(jogo -> jogo.getMandantePlacar() > jogo.getVisitantePlacar())
                .count();
    }

    public Long totalVitoriasForaDeCasa() {
        return this.brasileirao.stream()
                .filter(jogo -> jogo.getMandantePlacar() < jogo.getVisitantePlacar())
                .count();
    }

    public Long totalEmpates() {
        return this.brasileirao.stream()
                .filter(jogo -> jogo.getVencedor().getNome().equals("-"))
                .count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return this.brasileirao.stream()
                .mapToInt(jogo -> jogo.getVisitantePlacar() + jogo.getMandantePlacar())
                .filter(gols -> gols < 3)
                .count();
    }

    public Long totalJogosCom3OuMaisGols() {
        return this.brasileirao.stream()
                .mapToInt(jogo -> jogo.getVisitantePlacar() + jogo.getMandantePlacar())
                .filter(gols -> gols >= 3)
                .count();
    }

    public Map<Resultado, Long> todosOsPlacares() {

        return this.brasileirao.stream()
                .map(resultado -> new Resultado(resultado.getMandantePlacar(), resultado.getVisitantePlacar()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {

        Map<Resultado, Long> placares = todosOsPlacares();

        Optional<Map.Entry<Resultado, Long>> maiorOcorrencia = placares
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        return maiorOcorrencia.orElse(null);

    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        Map<Resultado, Long> placares = todosOsPlacares();

        Optional<Map.Entry<Resultado, Long>> menorOcorrencia = placares
                .entrySet()
                .stream()
                .min(Comparator.comparing(Map.Entry::getValue));

        return menorOcorrencia.orElse(null);

    }

    public List<Time> todosOsTimes() {
        List<Time> mandantes = todosOsJogos()
                .stream()
                .map(Jogo::getVisitante)
                .collect(toList());
               // .toList();

        List<Time> visitantes = todosOsJogos()
                .stream()
                .map(Jogo::getVisitante)
                .collect(toList());
              //  .toList();

        return null;
    }

    /**
     * todos os jogos que cada time foi mandante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {
        return null;
    }

    /**
     * todos os jogos que cada time foi visitante
     * @return Map<Time, List<Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {
        return null;
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {
        return null;
    }

    public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {
        return null;
    }

    public Set<PosicaoTabela> tabela() {
        return null;
    }

    // Criada uma classe estática para este método: LeituraDosDados
   /* public List<Jogo> lerArquivo(Path file) throws IOException {
        return null;
    } */

    // Usado o getDayOfWeek da biblioteca do DayOfWeek, feito na classe TratamentoDosDados
   /* private DayOfWeek getDayOfWeek(String dia) {
        return Map.of(
                "Segunda-feira", DayOfWeek.SUNDAY,
                "Terça-feira", DayOfWeek.SUNDAY,
                "Quarta-feira", DayOfWeek.SUNDAY,
                "Quinta-feira", DayOfWeek.SUNDAY,
                "Sexta-feira", DayOfWeek.SUNDAY,
                "Sábado", DayOfWeek.SUNDAY,
                "Domingo", DayOfWeek.SUNDAY
        ).get(dia);
    } */

    // METODOS EXTRA

    private Map<Integer, Integer> totalGolsPorRodada() {
        return null;
    }

    private Map<Time, Integer> totalDeGolsPorTime() {
        return null;
    }

    private Map<Integer, Double> mediaDeGolsPorRodada() {
        return null;
    }


}

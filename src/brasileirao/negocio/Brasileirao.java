package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Brasileirao {

    private Map<Integer, List<Jogo>> brasileirao;
    private List<Jogo> jogos;
    private Predicate<Jogo> filtro;

    public Brasileirao(Path arquivo, Predicate<Jogo> filtro) throws IOException {
        this.jogos = lerArquivo(arquivo);
        this.filtro = filtro;
        this.brasileirao = jogos.stream()
                .filter(filtro) //filtrar por ano
                .collect(Collectors.groupingBy(
                        Jogo::rodada,
                        Collectors.mapping(Function.identity(), Collectors.toList())));
    }

    public Map<Jogo, Integer> mediaGolsPorJogo() {
        return null;
    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return this.jogos.stream()
                .filter(filtro)
                .collect(Collectors.summarizingInt(jogo->jogo.visitantePlacar()+jogo.mandantePlacar()));
        //return null;
    }

    public List<Jogo> todosOsJogos() {
        return null;
    }

    public Long totalVitoriasEmCasa() {
        return null;
    }

    public Long totalVitoriasForaDeCasa() {
        return null;
    }

    public Long totalEmpates() {
        return null;
    }

    public Long totalJogosComMenosDe3Gols() {
        return null;
    }

    public Long totalJogosCom3OuMaisGols() {
        return null;
    }

    public Map<Resultado, Long> todosOsPlacares() {
        return null;
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        return null;
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        return null;
    }

    private List<Time> todosOsTimes() {
        List<Time> mandantes = todosOsJogos()
                .stream()
                .map(Jogo::mandante)
                .toList();

        List<Time> visitantes = todosOsJogos()
                .stream()
                .map(Jogo::visitante)
                .toList();

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

    public List<Jogo> lerArquivo(Path file) throws IOException {
        try (Stream<String> linhas = Files.lines(file)) {
            return linhas
                    .skip(1)
                    .map(linha ->{
                        String[] conteudoLinha = linha.split(";");
                        String stringRodada = conteudoLinha[0];
                        String stringData = conteudoLinha[1];
                        String stringHorario = conteudoLinha[2].replace("h",":");
                        DayOfWeek dia = getDayOfWeek(conteudoLinha[3]);
                        String stringMandante = conteudoLinha[4];
                        String stringVisitante = conteudoLinha[5];
                        String stringVencedor = conteudoLinha[6];
                        String stringArena = conteudoLinha[7];
                        String stringMandantePlacar = conteudoLinha[8];
                        String stringVisitantePlacar = conteudoLinha[9];
                        String stringEstadoMandante = conteudoLinha[10];
                        String stringEstadoVisitante = conteudoLinha[11];
                        String stringEstadoVencedor = conteudoLinha[12];

                        Integer rodada = Integer.valueOf(stringRodada);

                        DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        DateTimeFormatter formatterHorario = DateTimeFormatter.ofPattern("HH:mm");

                        LocalDate data = LocalDate.parse(stringData,formatterData);
                        LocalTime horario = null;
                        try {
                            LocalTime.parse(stringHorario, formatterHorario);
                        }catch(DateTimeParseException e){
                            horario = LocalTime.of(16, 00);
                        }

                        DataDoJogo dataDoJogo = new DataDoJogo(
                                data,
                                horario,
                                dia
                        );

                        Time mandante = new Time(stringMandante);
                        Time visitante = new Time(stringVisitante);
                        Time vencedor= new Time(stringVencedor);

                        String arena = stringArena;
                        Integer mandantePlacar = Integer.parseInt(stringMandantePlacar);
                        Integer visitantePlacar = Integer.parseInt(stringVisitantePlacar);
                        String estadoMandante = stringEstadoMandante;
                        String estadoVisitante = stringEstadoVisitante;
                        String estadoVencedor = stringEstadoVencedor;

                        Jogo jogo = new Jogo(rodada,dataDoJogo,mandante,visitante,vencedor,arena,mandantePlacar,visitantePlacar,estadoMandante,estadoVisitante,estadoVencedor);
                        return jogo;
                    })
                    .toList();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private DayOfWeek getDayOfWeek(String dia) {
        return Map.of(
                "Segunda-feira", DayOfWeek.SUNDAY,
                "Terça-feira", DayOfWeek.SUNDAY,
                "Quarta-feira", DayOfWeek.SUNDAY,
                "Quinta-feira", DayOfWeek.SUNDAY,
                "Sexta-feira", DayOfWeek.SUNDAY,
                "Sábado", DayOfWeek.SUNDAY,
                "Domingo", DayOfWeek.SUNDAY
        ).get(dia);
    }

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

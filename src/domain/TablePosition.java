package domain;

public record TablePosition(Team team,
                            Long victories,
                            Long defeats,
                            Long draws,
                            Long goalsPositive,
                            Long goalsSuffered,
                            Long balanceOfGoals) {
    public Long getTotalScore() {
        return (victories * 3) + draws;
    }

    @Override
    public String toString() {
        return  team +
                ", Pontos =" + getTotalScore() +
                ", Vitórias =" + victories +
                ", Derrotas =" + defeats +
                ", Empates =" + draws +
                ", Gols Positivos =" + goalsPositive +
                ", Gols Sofridos =" + goalsSuffered +
                ", Saldo De Gols =" + balanceOfGoals +
                '}';
    }
}
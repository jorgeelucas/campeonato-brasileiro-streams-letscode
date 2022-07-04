package domain;

public record Result(Integer home,
              Integer guest){
    @Override
    public String toString() {
        return home + " x " + guest;
    }
}
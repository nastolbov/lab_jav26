package by.bsuir.lab4;

/**
 * Запись о железнодорожной станции.
 *
 * Формат строки в файле (вариант 10):
 *   Станция (строка из 20 символов)
 *   количество пассажирских поездов (целое число)
 *   количество товарных поездов (целое число),
 *   проходящих через станцию за сутки.
 */
public class Station {

    private final String name;
    private final int    passenger;
    private final int    cargo;

    public Station(String name, int passenger, int cargo) {
        this.name      = name;
        this.passenger = passenger;
        this.cargo     = cargo;
    }

    public String getName()     { return name; }
    public int getPassenger()   { return passenger; }
    public int getCargo()       { return cargo; }
    public int getTotal()       { return passenger + cargo; }

    @Override
    public String toString() {
        return String.format("%-20s  пасс=%d  тов=%d", name, passenger, cargo);
    }
}

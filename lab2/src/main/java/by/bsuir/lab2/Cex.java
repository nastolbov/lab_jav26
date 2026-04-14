package by.bsuir.lab2;

import java.util.Objects;

/**
 * Пользовательский класс — ЦЕХ (вариант 10).
 * Поля: имя, начальник, количество работающих.
 * Реализует Comparable, чтобы можно было класть в TreeSet
 * и сортировать естественным порядком (по количеству работающих).
 */
public class Cex implements Comparable<Cex> {

    private final String name;
    private final String chief;
    private final int workersCount;

    public Cex(String name, String chief, int workersCount) {
        this.name = name;
        this.chief = chief;
        this.workersCount = workersCount;
    }

    public String getName()        { return name; }
    public String getChief()       { return chief; }
    public int    getWorkersCount(){ return workersCount; }

    /** Естественный порядок — по количеству работающих, при равенстве — по имени. */
    @Override
    public int compareTo(Cex o) {
        int c = Integer.compare(this.workersCount, o.workersCount);
        if (c != 0) return c;
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cex)) return false;
        Cex cex = (Cex) o;
        return workersCount == cex.workersCount &&
               Objects.equals(name, cex.name) &&
               Objects.equals(chief, cex.chief);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, chief, workersCount);
    }

    @Override
    public String toString() {
        return String.format("Цех{имя='%s', начальник='%s', работающих=%d}",
                name, chief, workersCount);
    }
}

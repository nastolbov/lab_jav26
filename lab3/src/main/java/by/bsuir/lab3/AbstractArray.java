package by.bsuir.lab3;

import java.util.function.DoubleUnaryOperator;

/**
 * Абстрактный базовый класс Array (вариант 10).
 * <p>
 * Хранит массив чисел типа double и объявляет два абстрактных
 * метода:
 * <ul>
 *   <li>{@link #sort()}            — сортировка массива;</li>
 *   <li>{@link #foreach(DoubleUnaryOperator)} — поэлементная обработка
 *       массива заданной функцией.</li>
 * </ul>
 *
 * Производные классы {@link Insert} и {@link Selection} реализуют
 * эти методы по-своему: {@code Insert} использует сортировку
 * включениями и возведение в квадрат, {@code Selection} —
 * сортировку выбором и натуральный логарифм.
 */
public abstract class AbstractArray {

    protected double[] data;

    protected AbstractArray(double[] data) {
        this.data = data.clone();
    }

    /** Сортирует массив (метод определяется в наследнике). */
    public abstract void sort();

    /**
     * Поэлементная обработка массива.
     *
     * @param op функция, применяемая к каждому элементу
     */
    public abstract void foreach(DoubleUnaryOperator op);

    /** Стандартная обработка наследника (квадрат / логарифм). */
    public abstract void foreachDefault();

    public double[] getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(String.format("%.4f", data[i]));
        }
        return sb.append("]").toString();
    }
}

package by.bsuir.lab3;

import java.util.function.DoubleUnaryOperator;

/**
 * Наследник {@link AbstractArray}.
 * Сортировка — методом включения (вставками).
 * Поэлементная обработка по умолчанию — возведение в квадрат.
 */
public class Insert extends AbstractArray {

    public Insert(double[] data) {
        super(data);
    }

    /** Сортировка вставками. */
    @Override
    public void sort() {
        for (int i = 1; i < data.length; i++) {
            double key = data[i];
            int j = i - 1;
            while (j >= 0 && data[j] > key) {
                data[j + 1] = data[j];
                j--;
            }
            data[j + 1] = key;
        }
    }

    @Override
    public void foreach(DoubleUnaryOperator op) {
        for (int i = 0; i < data.length; i++) {
            data[i] = op.applyAsDouble(data[i]);
        }
    }

    /** По умолчанию — возведение в квадрат. */
    @Override
    public void foreachDefault() {
        foreach(x -> x * x);
    }
}

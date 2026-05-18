package by.bsuir.lab3;

import java.util.function.DoubleUnaryOperator;

/**
 * Наследник {@link AbstractArray}.
 * Сортировка — методом выбора.
 * Поэлементная обработка по умолчанию — натуральный логарифм
 * (для отрицательных и нулевых значений Math.log даст NaN /
 * −Infinity, что также демонстрирует возможности).
 */
public class Selection extends AbstractArray {

    public Selection(double[] data) {
        super(data);
    }

    /** Сортировка выбором. */
    @Override
    public void sort() {
        for (int i = 0; i < data.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < data.length; j++) {
                if (data[j] < data[min]) {
                    min = j;
                }
            }
            if (min != i) {
                double tmp = data[i];
                data[i] = data[min];
                data[min] = tmp;
            }
        }
    }

    @Override
    public void foreach(DoubleUnaryOperator op) {
        for (int i = 0; i < data.length; i++) {
            data[i] = op.applyAsDouble(data[i]);
        }
    }

    /** По умолчанию — натуральный логарифм. */
    @Override
    public void foreachDefault() {
        foreach(Math::log);
    }
}

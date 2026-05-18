package by.bsuir.lab3;

import java.util.function.DoubleUnaryOperator;

/** Реализация {@link IArray} — сортировка выбором + логарифм. */
public class SelectionI implements IArray {

    private final double[] data;

    public SelectionI(double[] data) {
        this.data = data.clone();
    }

    @Override
    public void sort() {
        for (int i = 0; i < data.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < data.length; j++) {
                if (data[j] < data[min]) min = j;
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

    @Override
    public void foreachDefault() {
        foreach(Math::log);
    }

    @Override
    public double[] getData() {
        return data;
    }
}

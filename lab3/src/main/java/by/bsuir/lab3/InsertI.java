package by.bsuir.lab3;

import java.util.function.DoubleUnaryOperator;

/** Реализация {@link IArray} — сортировка вставками + квадрат. */
public class InsertI implements IArray {

    private final double[] data;

    public InsertI(double[] data) {
        this.data = data.clone();
    }

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

    @Override
    public void foreachDefault() {
        foreach(x -> x * x);
    }

    @Override
    public double[] getData() {
        return data;
    }
}

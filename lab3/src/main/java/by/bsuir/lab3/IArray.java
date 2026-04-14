package by.bsuir.lab3;

import java.util.function.DoubleUnaryOperator;

/**
 * Тот же контракт, но в виде интерфейса —
 * демонстрирует второй вариант реализации, требуемый заданием
 * («изменить программу, используя в ней в качестве базового
 * класса соответствующий интерфейс»).
 */
public interface IArray {
    void sort();
    void foreach(DoubleUnaryOperator op);
    void foreachDefault();
    double[] getData();
}

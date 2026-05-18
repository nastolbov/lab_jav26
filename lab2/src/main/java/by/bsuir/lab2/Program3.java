package by.bsuir.lab2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Программа №3 — алгоритмы работы с коллекциями.
 *
 * Тип контейнера 1 = Vector, тип 2 = TreeSet, элементы — Cex.
 *
 *  1) Создаём Vector<Cex>.
 *  2) Сортируем по возрастанию (естественный порядок — по числу работающих).
 *  3) Просматриваем.
 *  4) Подходящим алгоритмом ищем элемент по условию (binarySearch).
 *  5) Перемещаем элементы по условию во второй контейнер (TreeSet).
 *  6) Просматриваем второй контейнер.
 *  7) Сортируем оба контейнера по убыванию.
 *  8) Просматриваем оба.
 *  9) Получаем третий контейнер слиянием первых двух.
 * 10) Просматриваем третий контейнер.
 */
public class Program3 {

    public static void run() {
        System.out.println();
        System.out.println("================ ПРОГРАММА №3 (алгоритмы) ================");

        // 1. Контейнер с пользовательским типом
        Vector<Cex> v = new Vector<>();
        v.add(new Cex("Сборочный",  "Иванов",  120));
        v.add(new Cex("Литейный",   "Петров",   80));
        v.add(new Cex("Кузнечный",  "Сидоров", 150));
        v.add(new Cex("Малярный",   "Жуков",    40));
        v.add(new Cex("Покрасочный","Орлов",    65));
        v.add(new Cex("Сварочный",  "Новиков",  95));
        System.out.println("1) Исходный Vector<Cex> создан");

        // 2. Сортировка по возрастанию
        Collections.sort(v);                                   // natural order
        System.out.println("2) Отсортирован по возрастанию (по работающим)");

        // 3. Просмотр
        System.out.println("3) Vector после сортировки:");
        v.forEach(c -> System.out.println("   " + c));

        // 4. Поиск элемента по условию: ищем цех с числом работающих 95
        Cex pattern = new Cex("Сварочный", "Новиков", 95);
        int idx = Collections.binarySearch(v, pattern);
        System.out.println("4) binarySearch(работающих=95) -> индекс " + idx
                + (idx >= 0 ? " : " + v.get(idx) : " (не найден)"));

        // 5. Перемещение элементов с workersCount >= 90 в TreeSet
        TreeSet<Cex> ts = new TreeSet<>();                     // type2
        Iterator<Cex> it = v.iterator();
        while (it.hasNext()) {
            Cex c = it.next();
            if (c.getWorkersCount() >= 90) {
                ts.add(c);
                it.remove();
            }
        }
        System.out.println("5) Перемещены цеха с числом работающих >= 90");

        // 6. Просмотр TreeSet
        System.out.println("6) TreeSet:");
        ts.forEach(c -> System.out.println("   " + c));

        // 7. Сортировка по убыванию.
        //    Vector — Collections.sort с reverseOrder.
        //    TreeSet нельзя пересортировать "на месте", создаём новый
        //    TreeSet с обратным компаратором.
        v.sort(Comparator.reverseOrder());

        TreeSet<Cex> tsDesc = new TreeSet<>(Comparator.reverseOrder());
        tsDesc.addAll(ts);
        ts = tsDesc;

        // 8. Просмотр обоих
        System.out.println("7-8) Vector по убыванию:");
        v.forEach(c -> System.out.println("   " + c));
        System.out.println("7-8) TreeSet по убыванию:");
        ts.forEach(c -> System.out.println("   " + c));

        // 9. Третий контейнер — слияние. Используем ArrayList.
        List<Cex> merged = new ArrayList<>();
        merged.addAll(v);
        merged.addAll(ts);
        merged.sort(Comparator.reverseOrder());

        // 10. Просмотр
        System.out.println("9-10) Третий контейнер (ArrayList, слияние, по убыванию):");
        merged.forEach(c -> System.out.println("   " + c));
    }
}

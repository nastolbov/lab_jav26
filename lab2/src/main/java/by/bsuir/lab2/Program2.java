package by.bsuir.lab2;

import java.util.Iterator;
import java.util.Vector;

/**
 * Программа №2.
 * То же, что и Программа №1, но для пользовательского типа Cex.
 */
public class Program2 {

    public static void run() {
        System.out.println();
        System.out.println("================ ПРОГРАММА №2 (Vector<Cex>) ================");

        // 1. Создание и заполнение
        Vector<Cex> v1 = new Vector<>();
        v1.add(new Cex("Сборочный",  "Иванов",  120));
        v1.add(new Cex("Литейный",   "Петров",   80));
        v1.add(new Cex("Кузнечный",  "Сидоров", 150));
        v1.add(new Cex("Малярный",   "Жуков",    40));
        v1.add(new Cex("Покрасочный","Орлов",    65));
        System.out.println("1) Первый контейнер заполнен (" + v1.size() + " цехов)");

        // 2. Просмотр
        System.out.println("2) Первый контейнер:");
        v1.forEach(c -> System.out.println("   " + c));

        // 3. Изменение: удалить один и заменить другой
        v1.remove(2);                                          // убрали Кузнечный
        v1.set(0, new Cex("Сборочный-1", "Иванов", 130));      // заменили Сборочный
        System.out.println("3) После удаления и замены:");
        v1.forEach(c -> System.out.println("   " + c));

        // 4. Просмотр через итератор
        System.out.println("4) Через итератор:");
        Iterator<Cex> it = v1.iterator();
        while (it.hasNext()) {
            System.out.println("   " + it.next());
        }

        // 5. Второй контейнер
        Vector<Cex> v2 = new Vector<>();
        v2.add(new Cex("Цех №7", "Котов",   55));
        v2.add(new Cex("Цех №8", "Зайцев",  90));
        System.out.println("5) Второй контейнер:");
        v2.forEach(c -> System.out.println("   " + c));

        // 6. Удалить n=1 после индекса 1, добавить все из v2
        int after = 1;
        int n = 1;
        for (int i = 0; i < n && after + 1 < v1.size(); i++) {
            v1.remove(after + 1);
        }
        v1.addAll(v2);

        // 7. Просмотр
        System.out.println("6-7) v1:");
        v1.forEach(c -> System.out.println("   " + c));
        System.out.println("7)   v2:");
        v2.forEach(c -> System.out.println("   " + c));
    }
}

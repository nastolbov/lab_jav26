package by.bsuir.lab2;

import java.util.Iterator;
import java.util.Vector;

/**
 * Программа №1.
 * Контейнер тип1 = Vector, встроенный тип = char.
 *
 * 1) Создаём Vector<Character> и заполняем его.
 * 2) Просматриваем.
 * 3) Изменяем (удаляем одни элементы, заменяем другие).
 * 4) Просматриваем через итератор.
 * 5) Создаём второй Vector<Character>.
 * 6) Из первого удаляем n элементов после заданного и добавляем все
 *    элементы из второго.
 * 7) Просматриваем оба контейнера.
 */
public class Program1 {

    public static void run() {
        System.out.println("================ ПРОГРАММА №1 (Vector<Character>) ================");

        // 1. Создание и заполнение
        Vector<Character> v1 = new Vector<>();
        for (char c : new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}) {
            v1.add(c);
        }
        System.out.println("1) Первый контейнер заполнен");

        // 2. Просмотр
        System.out.println("2) Первый контейнер: " + v1);

        // 3. Изменение: удалить элемент с индексом 2, заменить элементы 0 и 1
        v1.remove(2);                 // удаляем 'c'
        v1.set(0, 'X');               // заменяем 'a' -> 'X'
        v1.set(1, 'Y');               // заменяем 'b' -> 'Y'
        System.out.println("3) После удаления и замены: " + v1);

        // 4. Просмотр через итератор
        System.out.print("4) Через итератор: ");
        Iterator<Character> it = v1.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println();

        // 5. Второй Vector<Character>
        Vector<Character> v2 = new Vector<>();
        for (char c : new char[]{'1', '2', '3', '4'}) {
            v2.add(c);
        }
        System.out.println("5) Второй контейнер: " + v2);

        // 6. Удалить n=2 элементов после индекса 1, потом добавить все из v2
        int after = 1;
        int n = 2;
        for (int i = 0; i < n && after + 1 < v1.size(); i++) {
            v1.remove(after + 1);
        }
        v1.addAll(v2);
        System.out.println("6) Первый после удаления " + n + " после индекса " + after
                + " и добавления второго: " + v1);

        // 7. Просмотр обоих
        System.out.println("7) v1 = " + v1);
        System.out.println("7) v2 = " + v2);
    }
}

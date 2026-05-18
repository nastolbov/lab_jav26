package by.bsuir.lab5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.TreeSet;

/**
 * Сервис работы с целочисленным бинарным файлом
 * (вариант 10: вывести числа, состоящие только из нечётных цифр,
 * по возрастанию, без повторений).
 *
 * Файл создаётся как последовательность 4-байтовых int.
 * Количество чисел случайное из диапазона 1..n,
 * сами числа случайные из диапазона a..b.
 *
 * Согласно заданию, при обработке файл может быть прочитан
 * только один раз; элементы файла нельзя считывать в массив или
 * другую структуру (но можно использовать структуру для других
 * целей — здесь это TreeSet, в который попадают только подходящие
 * числа, без повторений).
 */
public class BinaryFileService {

    /** Создаёт бинарный файл случайных int. */
    public void create(Path file, int maxCount, int a, int b) throws IOException {
        if (maxCount < 1)
            throw new IllegalArgumentException("n должно быть >= 1");
        if (a > b)
            throw new IllegalArgumentException("должно быть a <= b");

        Random rnd  = new Random();
        int    size = 1 + rnd.nextInt(maxCount); // 1..n

        Files.createDirectories(file.toAbsolutePath().getParent());
        try (DataOutputStream out = new DataOutputStream(
                Files.newOutputStream(file))) {
            for (int i = 0; i < size; i++) {
                int v = a + rnd.nextInt(b - a + 1);
                out.writeInt(v);
            }
        }
    }

    /** Читает все int'ы файла (для отображения). */
    public int[] readAll(Path file) throws IOException {
        long bytes = Files.size(file);
        if (bytes % Integer.BYTES != 0) {
            throw new IOException("Размер файла не кратен 4 — это не бинарный int-файл");
        }
        int[] result = new int[(int) (bytes / Integer.BYTES)];
        try (DataInputStream in = new DataInputStream(Files.newInputStream(file))) {
            for (int i = 0; i < result.length; i++) {
                result[i] = in.readInt();
            }
        }
        return result;
    }

    /**
     * Главная обработка для варианта 10.
     * Файл читается один раз, элементы не складываются ни в какой
     * массив; пригодные числа (все цифры нечётны) попадают в TreeSet,
     * который автоматически даёт сортировку и устранение повторов.
     */
    public TreeSet<Integer> oddDigitNumbers(Path file) throws IOException {
        TreeSet<Integer> result = new TreeSet<>();
        try (DataInputStream in = new DataInputStream(Files.newInputStream(file))) {
            while (true) {
                int v;
                try {
                    v = in.readInt();
                } catch (EOFException eof) {
                    break;
                }
                if (allDigitsOdd(v)) {
                    result.add(v);
                }
            }
        }
        return result;
    }

    /** Все ли цифры числа нечётные? Знак минус игнорируется. */
    static boolean allDigitsOdd(int n) {
        if (n == 0) return false;            // 0 — чётная цифра
        long x = Math.abs((long) n);
        while (x > 0) {
            int d = (int) (x % 10);
            if ((d & 1) == 0) return false;  // чётная цифра
            x /= 10;
        }
        return true;
    }
}

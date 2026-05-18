package by.bsuir.lab4;

/**
 * Парсер одной строки файла. Согласно варианту 10:
 *   - название станции — строка из 20 символов
 *   - количество пассажирских поездов — целое
 *   - количество товарных поездов — целое
 *
 * Допустимы два формата:
 *   1) фиксированное поле имени (первые 20 символов),
 *      затем два целых через пробел;
 *   2) разделённый пробелами/табами вариант — последние два
 *      токена считаются числами, всё остальное — название.
 */
public final class StationParser {

    private StationParser() {}

    public static Station parse(String line) throws StationFormatException {
        if (line == null || line.isBlank()) {
            throw new StationFormatException("Пустая строка");
        }

        // Вариант с фиксированной шириной 20 символов
        if (line.length() >= 22) {
            String namePart = line.substring(0, 20).trim();
            String tail     = line.substring(20).trim();
            String[] parts  = tail.split("\\s+");
            if (parts.length == 2 && !namePart.isEmpty()) {
                try {
                    int p = Integer.parseInt(parts[0]);
                    int t = Integer.parseInt(parts[1]);
                    if (p < 0 || t < 0) {
                        throw new StationFormatException(
                                "Количество поездов не может быть отрицательным: " + line);
                    }
                    return new Station(namePart, p, t);
                } catch (NumberFormatException e) {
                    // упадём в общий парсер ниже
                }
            }
        }

        // Гибкий вариант: последние 2 токена — числа
        String[] tokens = line.trim().split("\\s+");
        if (tokens.length < 3) {
            throw new StationFormatException(
                    "Ожидаются: <станция> <пассажирских> <товарных>; получено: " + line);
        }
        try {
            int p = Integer.parseInt(tokens[tokens.length - 2]);
            int t = Integer.parseInt(tokens[tokens.length - 1]);
            if (p < 0 || t < 0) {
                throw new StationFormatException(
                        "Количество поездов не может быть отрицательным: " + line);
            }
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < tokens.length - 2; i++) {
                if (i > 0) name.append(' ');
                name.append(tokens[i]);
            }
            return new Station(name.toString(), p, t);
        } catch (NumberFormatException e) {
            throw new StationFormatException(
                    "Невозможно распознать числа в строке: " + line, e);
        }
    }
}

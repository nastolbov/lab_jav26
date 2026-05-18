package by.bsuir.lab4;

/**
 * Бросается, когда строка файла не соответствует ожидаемому формату
 * для варианта 10 (Железная дорога).
 */
public class StationFormatException extends Exception {
    public StationFormatException(String message) {
        super(message);
    }
    public StationFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}

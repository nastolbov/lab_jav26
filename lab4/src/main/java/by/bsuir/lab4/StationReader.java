package by.bsuir.lab4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Чтение файла данных двумя способами, как требует задание:
 *  - стандартное чтение из потока ({@link FileReader} + {@link BufferedReader});
 *  - чтение в функциональном стиле через {@link Files#lines(Path)}.
 */
public final class StationReader {

    private StationReader() {}

    /** Стандартное чтение из потока. */
    public static List<Station> readClassic(Path path)
            throws IOException, StationFormatException {
        List<Station> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            int    no = 0;
            while ((line = br.readLine()) != null) {
                no++;
                if (line.isBlank()) continue;
                try {
                    result.add(StationParser.parse(line));
                } catch (StationFormatException ex) {
                    throw new StationFormatException(
                            "Строка " + no + ": " + ex.getMessage(), ex);
                }
            }
        }
        return result;
    }

    /** Функциональное чтение через {@link Files#lines(Path)}. */
    public static List<Station> readFunctional(Path path)
            throws IOException, StationFormatException {
        try (Stream<String> lines = Files.lines(path)) {
            try {
                return lines
                        .filter(s -> !s.isBlank())
                        .map(s -> {
                            try {
                                return StationParser.parse(s);
                            } catch (StationFormatException e) {
                                // оборачиваем в RuntimeException и
                                // разворачиваем снаружи
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());
            } catch (RuntimeException re) {
                if (re.getCause() instanceof StationFormatException sfe) {
                    throw sfe;
                }
                throw re;
            }
        }
    }
}

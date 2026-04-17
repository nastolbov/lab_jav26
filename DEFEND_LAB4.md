# Лабораторная работа №4 — Файлы и исключения. Гайд для новичка

## Вариант 10: Чтение станций из текстового файла, обработка исключений

---

## Что делает эта лаба

Программа читает из текстового файла информацию о железнодорожных станциях (название, пассажиропоток, грузооборот), отображает в таблице. Есть два способа чтения файла. Показываются исключения и как с ними работать.

---

## Структура файла `stations.txt`

```
Минск               45 60
Брест               30 90
Гомель              20 75
Витебск             18 40
Гродно              22 55
Могилёв             15 65
Барановичи          10 30
Пинск                8 25
```

Каждая строка — одна станция: название (до 20 символов), затем два числа — пассажирский поток и грузооборот (млн тонн).

---

## Класс `Station.java` — модель данных

```java
public class Station {
    private final String name;
    private final int    passenger;
    private final int    cargo;

    public Station(String name, int passenger, int cargo) {
        this.name = name;
        this.passenger = passenger;
        this.cargo = cargo;
    }

    public String getName()      { return name; }
    public int    getPassenger() { return passenger; }
    public int    getCargo()     { return cargo; }
    public int    getTotal()     { return passenger + cargo; }
}
```

Это **POJO** (Plain Old Java Object) — простой класс-хранилище данных. Поля `final` — после создания объекта их нельзя изменить (неизменяемость). `getTotal()` — вычисляемое поле, не хранится отдельно.

---

## Класс `StationFormatException.java` — своё исключение

```java
public class StationFormatException extends Exception {
    public StationFormatException(String message) {
        super(message);
    }
    public StationFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Зачем создавать своё исключение?**
Стандартные исключения (`IOException`, `NumberFormatException`) говорят ЧТО пошло не так технически, но не ЧТО это значит для нашей задачи. `StationFormatException` чётко говорит: "строка в файле не соответствует ожидаемому формату станции".

`extends Exception` — это **проверяемое исключение** (checked). Java заставит каждого, кто вызывает метод бросающий такое исключение, либо поймать его (`try-catch`) либо объявить (`throws`). Если бы написали `extends RuntimeException` — проверка бы не требовалась.

---

## Класс `StationParser.java` — разбор строки

```java
public class StationParser {

    public static Station parse(String line) throws StationFormatException {
        if (line == null || line.isBlank()) {
            throw new StationFormatException("Пустая строка");
        }

        // Режим 1: фиксированная ширина — имя занимает первые 20 символов
        if (line.length() >= 22) {
            String name = line.substring(0, 20).trim();
            try {
                String[] nums = line.substring(20).trim().split("\\s+");
                int passenger = Integer.parseInt(nums[0]);
                int cargo     = Integer.parseInt(nums[1]);
                return new Station(name, passenger, cargo);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                // не вышло — пробуем второй режим
            }
        }

        // Режим 2: гибкий — последние два токена это числа, остальное — имя
        String[] parts = line.trim().split("\\s+");
        if (parts.length < 3) {
            throw new StationFormatException("Слишком мало полей в строке: \"" + line + "\"");
        }
        try {
            int cargo     = Integer.parseInt(parts[parts.length - 1]);
            int passenger = Integer.parseInt(parts[parts.length - 2]);
            String name = String.join(" ", Arrays.copyOfRange(parts, 0, parts.length - 2));
            return new Station(name, passenger, cargo);
        } catch (NumberFormatException ex) {
            throw new StationFormatException(
                    "Не удалось разобрать числа в строке: \"" + line + "\"", ex);
        }
    }
}
```

**Режим 1 (фиксированная ширина):**
Предполагаем, что имя станции занимает ровно 20 символов (с пробелами). `substring(0, 20).trim()` берёт первые 20 символов и убирает лишние пробелы. Остаток строки — числа.

**Режим 2 (гибкий):**
Разбиваем строку на слова по пробелам. Последнее слово — грузооборот, предпоследнее — пассажиропоток, остальные — название. Это работает для любого формата.

`throw new StationFormatException(...)` — бросаем своё исключение с понятным сообщением.

---

## Класс `StationReader.java` — два способа читать файл

### Способ 1: классический (FileReader + BufferedReader)

```java
public List<Station> readClassic(Path file) throws IOException, StationFormatException {
    List<Station> list = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.isBlank()) {
                list.add(StationParser.parse(line));
            }
        }
    }
    return list;
}
```

`FileReader` — открывает файл как поток символов.
`BufferedReader` — обёртка, добавляет буфер и метод `readLine()` — читает целую строку.
`try-with-resources` — файл закроется автоматически.
`br.readLine()` возвращает `null` когда файл кончился.

### Способ 2: функциональный (Files.lines() + Stream)

```java
public List<Station> readFunctional(Path file) throws IOException, StationFormatException {
    try {
        return Files.lines(file)
                .filter(l -> !l.isBlank())
                .map(line -> {
                    try {
                        return StationParser.parse(line);
                    } catch (StationFormatException e) {
                        throw new RuntimeException(e); // нельзя бросить checked в лямбде
                    }
                })
                .collect(Collectors.toList());
    } catch (RuntimeException e) {
        if (e.getCause() instanceof StationFormatException) {
            throw (StationFormatException) e.getCause(); // достаём обратно
        }
        throw e;
    }
}
```

`Files.lines(file)` — возвращает `Stream<String>`, ленивый поток строк файла.
`.filter(...)` — оставляем только непустые строки.
`.map(...)` — применяем функцию к каждой строке.
`.collect(Collectors.toList())` — собираем результат в список.

**Проблема с checked exceptions в лямбдах:**
Лямбды в `Stream.map()` не могут бросать проверяемые исключения (это ограничение языка). Поэтому мы оборачиваем `StationFormatException` в `RuntimeException`, пробрасываем через Stream, а после `.collect()` разворачиваем обратно.

---

## MainView.java — интерфейс

```java
public class MainView extends HorizontalLayout {
```

`extends HorizontalLayout` — элементы располагаются горизонтально. Слева — вертикальные вкладки и настройки, справа — таблица.

### Tabs (вкладки)

```java
Tabs tabs = new Tabs();
tabs.setOrientation(Tabs.Orientation.VERTICAL);
Tab tabAll    = new Tab("Все станции");
Tab tabSort   = new Tab("По убыванию суммарного");
Tab tabFilter = new Tab("Пассажирский > грузовой");
tabs.add(tabAll, tabSort, tabFilter);
```

`Tab` — одна вкладка. `Tabs` — контейнер для вкладок. `VERTICAL` — вкладки расположены вертикально (справа налево).

```java
tabs.addSelectedChangeListener(e -> refresh());
```

Когда пользователь нажимает другую вкладку — вызывается `refresh()`, который перестраивает таблицу.

### Grid — таблица

```java
Grid<Station> grid = new Grid<>(Station.class, false);
grid.addColumn(Station::getName).setHeader("Станция");
grid.addColumn(Station::getPassenger).setHeader("Пассажирский");
grid.addColumn(Station::getCargo).setHeader("Грузовой");
grid.addColumn(Station::getTotal).setHeader("Суммарный");
```

`Grid<Station>` — таблица, где каждая строка — объект `Station`. `addColumn` добавляет колонку, получая значение через ссылку на метод. `setHeader` — заголовок колонки.

### RadioButtonGroup — выбор способа чтения

```java
RadioButtonGroup<String> readMode = new RadioButtonGroup<>();
readMode.setItems("Классический", "Функциональный");
readMode.setValue("Классический");
```

Переключатель между двумя способами чтения файла.

### Метод refresh():

```java
private void refresh() {
    errorBox.setText("");
    try {
        List<Station> data = readMode.getValue().equals("Классический")
                ? reader.readClassic(filePath)
                : reader.readFunctional(filePath);

        if (tabs.getSelectedTab() == tabSort) {
            data.sort(Comparator.comparingInt(Station::getTotal).reversed());
        } else if (tabs.getSelectedTab() == tabFilter) {
            data = data.stream()
                       .filter(s -> s.getPassenger() > s.getCargo())
                       .collect(Collectors.toList());
        }
        grid.setItems(data);
    } catch (StationFormatException ex) {
        errorBox.setText("Ошибка формата: " + ex.getMessage());
        errorBox.getStyle().set("color", "red");
    } catch (IOException ex) {
        errorBox.setText("Ошибка чтения файла: " + ex.getMessage());
        errorBox.getStyle().set("color", "red");
    }
}
```

`Comparator.comparingInt(Station::getTotal).reversed()` — сортировка по убыванию суммарного.
`.filter(s -> s.getPassenger() > s.getCargo())` — фильтр: оставляем только станции где пассажирский поток больше грузового.

---

## Как показать преподу

1. Открываешь `http://localhost:8084/`
2. Видишь таблицу со всеми станциями (вкладка "Все станции")
3. Переключаешь на "По убыванию суммарного" — таблица сортируется
4. Переключаешь на "Пассажирский > грузовой" — остаются только нужные станции
5. Меняешь "Функциональный" в RadioButton — данные читаются другим способом
6. Можно показать обработку ошибок: указать несуществующий файл

---

## Вопросы препода и ответы

**Q: Что такое исключение? Зачем нужно?**
A: Исключение — ошибка в runtime, которую программа не может или не должна игнорировать. Механизм try-catch позволяет поймать ошибку и обработать её вместо падения программы.

**Q: Разница между checked и unchecked исключениями?**
A: Checked (проверяемые) наследуют `Exception` — Java заставляет обработать. Unchecked наследуют `RuntimeException` — обработка не обязательна. `IOException` — checked, `NullPointerException` — unchecked.

**Q: Зачем создавать своё исключение?**
A: Чтобы дать ошибке понятный смысл на уровне задачи. `StationFormatException` сразу говорит о проблеме с форматом данных станции, а не об абстрактной ошибке I/O.

**Q: Что такое try-with-resources?**
A: Конструкция `try (ресурс = ...)`. После блока try Java автоматически вызывает `close()` — и при успехе, и при исключении. Гарантирует что файл будет закрыт.

**Q: Зачем `BufferedReader` поверх `FileReader`?**
A: `FileReader` читает по одному символу — медленно. `BufferedReader` добавляет буфер (загружает блок сразу) и метод `readLine()` для чтения целой строки.

**Q: Что такое Stream в Java?**
A: Конвейер обработки данных. Не хранит данные, а описывает операции. `Files.lines()` даёт поток строк, `filter` фильтрует, `map` преобразует, `collect` собирает результат.

**Q: Почему нельзя бросить checked exception в лямбде Stream?**
A: Функциональные интерфейсы (`Function`, используемый в `.map()`) не объявляют checked exceptions в своей сигнатуре. Это ограничение языка. Обходим через обёртку в RuntimeException.

**Q: Что такое Grid в Vaadin?**
A: Компонент таблицы. Принимает список объектов и отображает их по колонкам. `addColumn` определяет как получить значение для колонки.

**Q: Что такое Tab/Tabs в Vaadin?**
A: `Tab` — одна вкладка, `Tabs` — контейнер с вкладками. `addSelectedChangeListener` — обработчик смены активной вкладки.

**Q: Зачем два способа чтения файла?**
A: Показать классический (императивный) и функциональный (декларативный) стили программирования в Java. Классический — понятнее для новичка. Функциональный — компактнее, лучше масштабируется.

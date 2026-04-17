# Лабораторная работа №5 — Бинарные файлы. Гайд для новичка

## Вариант 10: Числа с нечётными цифрами в бинарном файле

---

## Что делает эта лаба

Программа создаёт бинарный файл из случайных целых чисел, затем находит среди них числа, у которых **все цифры нечётные** (1, 3, 5, 7, 9), и выводит их по возрастанию без повторений.

---

## Что такое бинарный файл

Обычный текстовый файл хранит символы. Число `42` записывается как два символа: `'4'` и `'2'` — это 2 байта.

Бинарный файл хранит сырые байты. Число `42` типа `int` занимает ровно **4 байта** — будь то `1` или `2000000000`. Компактно и быстро, но нельзя открыть в блокноте и прочитать.

В Java для бинарных int:
- `DataOutputStream.writeInt(x)` — записывает 4 байта
- `DataInputStream.readInt()` — читает 4 байта, возвращает int

---

## `BinaryFileService.java` — вся логика с файлами

### Метод `create(Path file, int maxCount, int a, int b)`

```java
Random rnd  = new Random();
int    size = 1 + rnd.nextInt(maxCount); // случайное количество: 1..n
```

`rnd.nextInt(maxCount)` даёт число от 0 до maxCount-1. Прибавляем 1 → получаем от 1 до n.

```java
Files.createDirectories(file.toAbsolutePath().getParent());
try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
    for (int i = 0; i < size; i++) {
        int v = a + rnd.nextInt(b - a + 1); // случайное число от a до b
        out.writeInt(v);                     // записываем 4 байта
    }
}
```

`b - a + 1` — количество чисел в диапазоне. От 3 до 7: числа 3,4,5,6,7 → 5 штук. 7-3+1=5.

`try (...)` — **try-with-resources**. `out.close()` вызовется автоматически, даже при ошибке.

---

### Метод `readAll(Path file)` — читает все числа для отображения

```java
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
```

`Integer.BYTES` = 4. Если размер файла не делится на 4 — файл повреждён.

Заранее знаем количество чисел (байты / 4) → выделяем массив и читаем ровно столько раз.

---

### Метод `oddDigitNumbers(Path file)` — главная задача варианта 10

```java
public TreeSet<Integer> oddDigitNumbers(Path file) throws IOException {
    TreeSet<Integer> result = new TreeSet<>();
    try (DataInputStream in = new DataInputStream(Files.newInputStream(file))) {
        while (true) {
            int v;
            try {
                v = in.readInt();
            } catch (EOFException eof) {
                break; // конец файла — выходим
            }
            if (allDigitsOdd(v)) {
                result.add(v);
            }
        }
    }
    return result;
}
```

**Почему `while(true)` и `EOFException`?**
Когда `DataInputStream` достигает конца файла и пытается читать ещё — он бросает `EOFException` (End Of File). Это стандартный способ читать бинарный файл до конца.

**Почему `TreeSet<Integer>`, а не массив?**
Задание требует:
1. По возрастанию → `TreeSet` хранит элементы отсортированно автоматически
2. Без повторений → `TreeSet` не добавит дубликат (это свойство Set)
3. Нельзя складывать элементы файла в массив → мы кладём только **подходящие** числа

---

### Метод `allDigitsOdd(int n)` — проверка числа

```java
static boolean allDigitsOdd(int n) {
    if (n == 0) return false;           // 0 — чётная цифра
    long x = Math.abs((long) n);        // берём модуль числа
    while (x > 0) {
        int d = (int) (x % 10);         // последняя цифра
        if ((d & 1) == 0) return false; // если чётная — false
        x /= 10;                        // убираем последнюю цифру
    }
    return true;
}
```

Алгоритм: берём цифры по одной, начиная с последней.
- `x % 10` — последняя цифра: 135 % 10 = 5
- `x / 10` — убираем последнюю: 135 / 10 = 13
- Повторяем пока x > 0

`(d & 1) == 0` — побитовое И. Последний бит = 0 у чётных чисел. Аналог `d % 2 == 0`, но быстрее.

**Почему `(long) n` перед `Math.abs`?**
`Integer.MIN_VALUE` = -2147483648. `Math.abs` от `int` вернёт то же отрицательное число (переполнение). Переводим в `long` до вызова `Math.abs` — проблема решена.

---

## `MainView.java` — интерфейс

```java
@Route("")
public class MainView extends VerticalLayout {
    private final BinaryFileService service = new BinaryFileService();
    private Path currentFile;
    private final Paragraph fileLabel  = new Paragraph("Файл не выбран");
    private final TextArea  contentBox = new TextArea("Содержимое файла");
    private final TextArea  resultBox  = new TextArea("Результат...");
```

`@Route("")` — открывается по адресу `http://localhost:8085/`.

### Три кнопки:

```java
Button btnCreate  = new Button("Создать файл…",  e -> openCreateDialog());
Button btnChoose  = new Button("Выбрать файл…",  e -> openChooseDialog());
Button btnProcess = new Button("Обработать",     e -> process());
```

### Диалог создания файла:

```java
Dialog dlg = new Dialog();
dlg.setHeaderTitle("Создание бинарного файла");

IntegerField n = new IntegerField("n (макс. количество чисел)");
n.setValue(20); n.setMin(1);

IntegerField a = new IntegerField("a (мин. значение)");
a.setValue(1);

IntegerField b = new IntegerField("b (макс. значение)");
b.setValue(99);
```

`Dialog` — всплывающее окно поверх страницы. `IntegerField` принимает только целые числа.

```java
Button ok = new Button("Создать", e -> {
    try {
        Path p = Paths.get(fName.getValue().trim());
        service.create(p, n.getValue() == null ? 0 : n.getValue(), ...);
        currentFile = p;
        showContent();
        dlg.close();
    } catch (IOException ex) {
        Notification.show("Ошибка записи: " + ex.getMessage());
    }
});
```

`n.getValue() == null ? 0 : n.getValue()` — защита от пустого поля (NPE).
`Notification.show(...)` — маленькое временное уведомление внизу экрана.

### Метод process() — запуск обработки:

```java
private void process() {
    if (currentFile == null) {
        Notification.show("Сначала создайте или выберите файл");
        return;
    }
    try {
        TreeSet<Integer> result = service.oddDigitNumbers(currentFile);
        if (result.isEmpty()) {
            resultBox.setValue("Нет чисел, состоящих только из нечётных цифр");
        } else {
            resultBox.setValue(result.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" ")));
        }
    } catch (IOException ex) {
        Notification.show("Ошибка чтения: " + ex.getMessage());
    }
}
```

`result.stream().map(String::valueOf).collect(Collectors.joining(" "))` — берём TreeSet, каждый int превращаем в строку, соединяем пробелами. Результат: `"13 17 31 71 113"`.

---

## Как показать преподу

1. Открываешь `http://localhost:8085/`
2. Жмёшь "Создать файл…" → вводишь n=20, a=1, b=999 → "Создать"
3. В поле "Содержимое файла" появляются все числа из файла
4. Жмёшь "Обработать"
5. В поле "Результат" — числа у которых все цифры нечётные, отсортированные

---

## Вопросы препода и ответы

**Q: Что такое бинарный файл? Чем отличается от текстового?**
A: Бинарный хранит сырые байты. int в бинарном файле всегда 4 байта. Текстовый хранит символы — число 12345 займёт 5 байт, а 1 займёт 1 байт.

**Q: Почему `DataOutputStream`, а не `PrintWriter` или `FileWriter`?**
A: `FileWriter`/`PrintWriter` работают с текстом — записали бы число как символы. `DataOutputStream` записывает int как 4 сырых байта — это и есть бинарный формат.

**Q: Как работает `writeInt` и `readInt`?**
A: `writeInt(42)` записывает 4 байта: `00 00 00 2A`. `readInt()` читает эти 4 байта и возвращает 42. Формат big-endian (старший байт первый).

**Q: Зачем `EOFException`? Почему не проверяем количество заранее?**
A: В `oddDigitNumbers` мы читаем без предварительного знания о количестве — один проход по файлу. `EOFException` — стандартный способ узнать конец бинарного файла при последовательном чтении.

**Q: Почему `TreeSet`, а не обычный массив или ArrayList?**
A: Задание: вывести по возрастанию без повторений. `TreeSet` даёт это автоматически: сортирует и не хранит дубликаты. Альтернатива — ArrayList + sort + дедупликация — три шага против одного.

**Q: Задание говорит не складывать элементы файла в массив. Но TreeSet — это ведь тоже структура?**
A: В `TreeSet` попадают только **подходящие** числа (те, у которых все цифры нечётные), а не все числа из файла. `readAll` читает все в массив, но только для отображения. `oddDigitNumbers` не складывает все числа.

**Q: Как работает `allDigitsOdd`? Объясни алгоритм.**
A: Берём число. `n % 10` даёт последнюю цифру. `n / 10` убирает её. Повторяем в цикле. Если цифра чётная — false. Если прошли все цифры — true.

**Q: Что такое try-with-resources?**
A: `try (ресурс = ...)`. После блока try Java автоматически вызывает `close()` — и при успехе, и при исключении. Файл гарантированно закроется.

**Q: Что такое `Dialog` в Vaadin?**
A: Всплывающее модальное окно. `dlg.open()` открывает, `dlg.close()` закрывает. Блокирует взаимодействие с основной страницей пока открыт.

**Q: Что такое `Notification` в Vaadin?**
A: Маленькое временное уведомление (тост), которое появляется внизу экрана и исчезает само. Для сообщений об ошибках или успехе операций.

**Q: Зачем `Files.createDirectories(file.getParent())`?**
A: Создаём все папки на пути к файлу. Если пользователь ввёл `data/numbers.bin`, а папки `data` нет — без этой строки запись файла упадёт с ошибкой.

**Q: Почему `(d & 1) == 0`, а не `d % 2 == 0`?**
A: Оба варианта правильны. `& 1` — побитовая операция, чуть быстрее. `% 2` — более читаемо. Оба проверяют чётность.

**Q: Что такое `Path` и `Paths.get`?**
A: `Path` — объект пути к файлу. `Paths.get("data/numbers.bin")` создаёт его из строки. Современный API (вместо старого `new File(...)`).

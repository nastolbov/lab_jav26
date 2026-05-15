# Учебник по всем 6 лабам — объяснение с нуля

Читай по порядку. Каждый раздел строится на предыдущем.

---

# ЧАСТЬ 1 — Коллекции (Лаб 2)

## Проблема: зачем вообще нужны коллекции?

Представь что тебе нужно хранить 5 имён студентов:

```java
String s1 = "Иван";
String s2 = "Петр";
String s3 = "Анна";
// ... неудобно
```

Хорошо, используем массив:

```java
String[] names = new String[5];
names[0] = "Иван";
names[1] = "Петр";
```

Но что если студентов станет 6? Массив не растёт — нужно создавать новый большего размера и копировать. Это неудобно.

**Коллекции** решают это: они сами управляют памятью, растут и сжимаются автоматически, и имеют готовые методы для всего.

---

## Vector — динамический массив

```java
Vector<String> names = new Vector<>();  // пустой, размер 0

names.add("Иван");   // добавить в конец    → ["Иван"]
names.add("Петр");   //                     → ["Иван", "Петр"]
names.add("Анна");   //                     → ["Иван", "Петр", "Анна"]

names.get(0);        // получить по индексу → "Иван"
names.set(1, "Олег");// заменить            → ["Иван", "Олег", "Анна"]
names.remove(0);     // удалить по индексу  → ["Олег", "Анна"]
names.size();        // количество          → 2
```

Визуально Vector — это как вагон поезда с нумерованными местами. Места начинаются с 0. Можно добавить новый вагон (расширить), убрать место (удалить).

### Почему `Vector<String>` а не `Vector<string>`?

В Java есть два мира:
- **Примитивы**: `int`, `double`, `char`, `boolean` — хранятся прямо в памяти, быстро
- **Объекты**: `String`, `Integer`, `Character`, `Boolean` — хранятся в куче, медленнее

Коллекции работают **только с объектами**. Для каждого примитива есть класс-обёртка:

```
int    → Integer
double → Double
char   → Character
boolean → Boolean
```

Java сама конвертирует туда-обратно — это называется **autoboxing**:

```java
Vector<Integer> v = new Vector<>();
v.add(42);          // Java сама делает: v.add(Integer.valueOf(42))
int x = v.get(0);   // Java сама делает: v.get(0).intValue()
```

---

## TreeSet — множество без дубликатов, всегда отсортированное

```java
TreeSet<Integer> ts = new TreeSet<>();

ts.add(5);   // {5}
ts.add(2);   // {2, 5}      — автоматически сортирует!
ts.add(8);   // {2, 5, 8}
ts.add(2);   // {2, 5, 8}   — дубликат НЕ добавился!
ts.add(1);   // {1, 2, 5, 8}
```

TreeSet — это как словарь: слова хранятся по алфавиту, и каждое слово только один раз.

**Как он это делает?** Внутри — дерево:
```
        5
       / \
      2   8
     /
    1
```
Чтобы найти место для нового числа — идём по дереву: меньше — влево, больше — вправо. Поэтому поиск и вставка — O(log n), то есть в 1 000 000 элементов нужно максимум 20 шагов.

---

## Comparable — как объекты умеют сравниваться

TreeSet должен знать как сравнивать твои объекты. Для чисел и строк Java знает сама. Но если ты создал свой класс `Cex` — надо объяснить.

```java
public class Cex implements Comparable<Cex> {
    private String name;
    private int workersCount;

    @Override
    public int compareTo(Cex other) {
        // Правило: возвращаем отрицательное если this < other
        //                            0 если equal
        //                    положительное если this > other
        return Integer.compare(this.workersCount, other.workersCount);
    }
}
```

Теперь TreeSet знает: «сравниваю два цеха по количеству работающих».

Шаг за шагом что происходит при `ts.add(новыйЦех)`:
1. TreeSet берёт новый объект
2. Сравнивает его с корнем дерева через `compareTo`
3. Если результат < 0 — идёт влево, если > 0 — вправо
4. Если = 0 — считает дубликатом, не добавляет
5. Находит пустое место и вставляет

---

## Iterator — безопасный обход с удалением

Обычный цикл для обхода:
```java
for (int i = 0; i < v.size(); i++) {
    System.out.println(v.get(i));
}
```

Но если нужно **удалять элементы во время обхода** — это проблема:

```java
// ТАК НЕЛЬЗЯ — упадёт с ConcurrentModificationException:
for (Cex c : v) {
    if (c.getWorkersCount() >= 90) {
        v.remove(c);  // ОШИБКА! Изменяем список пока ходим по нему
    }
}
```

Почему ошибка? Когда ты используешь `for-each`, Java внутри создаёт итератор и запоминает «версию» коллекции. Если коллекция изменилась — версия не совпадает → исключение. Это защита от непредсказуемого поведения.

**Правильный способ:**
```java
Iterator<Cex> it = v.iterator();  // получаем итератор
while (it.hasNext()) {             // пока есть следующий элемент
    Cex c = it.next();             // берём следующий
    if (c.getWorkersCount() >= 90) {
        it.remove();               // итератор сам знает что удаляет
    }
}
```

Представь итератор как палец который ползёт по списку. `it.remove()` — это «убери элемент под пальцем». Палец сам знает где он, поэтому всё безопасно.

---

## equals и hashCode — зачем переопределять?

По умолчанию `equals` сравнивает **ссылки** (адреса в памяти):

```java
Cex c1 = new Cex("Сборочный", "Иванов", 100);
Cex c2 = new Cex("Сборочный", "Иванов", 100);

c1 == c2          // false — разные объекты в памяти
c1.equals(c2)     // false — по умолчанию то же что ==
```

Но мы хотим чтобы два цеха с одинаковыми данными считались равными:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;           // тот же объект — точно равны
    if (!(o instanceof Cex)) return false; // другой тип — не равны
    Cex cex = (Cex) o;
    return workersCount == cex.workersCount
        && name.equals(cex.name)
        && chief.equals(cex.chief);
}
```

**hashCode** — это число-«отпечаток» объекта. Правило Java:
> Если `a.equals(b) == true`, то `a.hashCode() == b.hashCode()`

Зачем? `HashMap` и `HashSet` сначала сравнивают hashCode (быстро), и только если совпал — вызывают `equals`. Если нарушить правило — объекты будут теряться в HashMap.

---

# ЧАСТЬ 2 — Абстрактные классы и интерфейсы (Лаб 3)

## Проблема: дублирование кода

Представь что у тебя два класса сортировки:

```java
class InsertSort {
    double[] data;

    void sort() { /* сортировка вставками */ }
    void printData() {
        for (double d : data) System.out.print(d + " ");
    }
}

class SelectionSort {
    double[] data;  // ДУБЛИКАТ

    void sort() { /* сортировка выбором */ }
    void printData() {  // ДУБЛИКАТ
        for (double d : data) System.out.print(d + " ");
    }
}
```

Общее (`data`, `printData`) дублируется. Если захочешь изменить `printData` — придётся менять в двух местах. Это плохо.

---

## Абстрактный класс — шаблон с общим кодом

```java
public abstract class AbstractArray {
    protected double[] data;  // ОБЩЕЕ поле

    public AbstractArray(double[] input) {
        this.data = input.clone();  // ОБЩИЙ конструктор
    }

    // АБСТРАКТНЫЙ метод — нет тела, наследник ОБЯЗАН реализовать
    public abstract void sort();

    // ОБЫЧНЫЙ метод — есть тело, наследник НАСЛЕДУЕТ его
    public double[] getData() {
        return data.clone();
    }
}
```

`abstract class` — класс-шаблон. Нельзя написать `new AbstractArray(...)` — только наследовать.

`abstract void sort()` — метод без тела. Java заставит каждый наследник написать свой `sort()`.

`protected` — поле доступно внутри класса и во всех наследниках, но не снаружи.

---

## Наследники — заполняют пустые методы

```java
public class Insert extends AbstractArray {

    public Insert(double[] input) {
        super(input);  // вызываем конструктор родителя
    }

    @Override           // говорим: "переопределяю метод родителя"
    public void sort() {
        // реализация сортировки вставками
        for (int i = 1; i < data.length; i++) {
            double key = data[i];
            int j = i - 1;
            while (j >= 0 && data[j] > key) {
                data[j + 1] = data[j];
                j--;
            }
            data[j + 1] = key;
        }
    }
}
```

Шаг за шагом что значит `extends AbstractArray`:
1. `Insert` получает поле `data` от родителя (не нужно объявлять снова)
2. `Insert` получает метод `getData()` от родителя (не нужно писать снова)
3. `Insert` обязан написать `sort()` (иначе ошибка компиляции)

### Как работает сортировка вставками?

Представь что у тебя карты в руке. Берёшь следующую карту и вставляешь её на правильное место среди уже отсортированных:

```
Начало: [5, 3, 1, 4]

i=1: key=3
  data[0]=5 > 3 → сдвигаем 5 вправо: [5, 5, 1, 4]
  вставляем 3 на место j+1=0: [3, 5, 1, 4]

i=2: key=1
  data[1]=5 > 1 → сдвигаем: [3, 5, 5, 4]
  data[0]=3 > 1 → сдвигаем: [3, 3, 5, 4]
  вставляем 1 на место j+1=0: [1, 3, 5, 4]

i=3: key=4
  data[2]=5 > 4 → сдвигаем: [1, 3, 5, 5]
  data[1]=3 < 4 → стоп
  вставляем 4 на место j+1=2: [1, 3, 4, 5]
```

### Как работает сортировка выбором?

Ищем минимум в неотсортированной части и ставим его в начало:

```
Начало: [5, 3, 1, 4]

i=0: ищем минимум в [5,3,1,4] → 1 (индекс 2)
  меняем местами data[0] и data[2]: [1, 3, 5, 4]

i=1: ищем минимум в [3,5,4] → 3 (индекс 1)
  уже на месте: [1, 3, 5, 4]

i=2: ищем минимум в [5,4] → 4 (индекс 3)
  меняем data[2] и data[3]: [1, 3, 4, 5]
```

---

## Полиморфизм — один тип, разное поведение

```java
AbstractArray arr1 = new Insert(data);    // переменная типа "родитель"
AbstractArray arr2 = new Selection(data); // объект типа "ребёнок"

arr1.sort();  // вызовет Insert.sort()    ← Java смотрит на РЕАЛЬНЫЙ тип объекта
arr2.sort();  // вызовет Selection.sort() ← не на тип переменной!
```

Это и есть **полиморфизм** — «много форм». Один вызов `sort()`, но разное поведение в зависимости от реального типа.

Аналогия: у тебя есть переменная `Животное зверь`. Если в неё записать `Собаку` — при вызове `зверь.говори()` услышишь «гав». Если записать `Кошку` — «мяу». Код одинаковый — поведение разное.

---

## Интерфейс — чистый контракт

```java
public interface IArray {
    void sort();              // только объявления
    double[] getData();       // никакого кода внутри
    void foreach(DoubleUnaryOperator op);
}
```

Интерфейс — это как договор: «любой кто реализует меня, обязан иметь эти методы».

**Разница с абстрактным классом:**

| | Абстрактный класс | Интерфейс |
|---|---|---|
| Поля | Да | Нет (только константы) |
| Конструктор | Да | Нет |
| Реализованные методы | Да | Только `default`-методы (Java 8+) |
| Наследование | Только один | Можно реализовать сколько угодно |

```java
class InsertI implements IArray {
    private double[] data;

    public InsertI(double[] input) { this.data = input.clone(); }

    @Override
    public void sort() { /* сортировка вставками */ }

    @Override
    public double[] getData() { return data.clone(); }
}
```

---

## Лямбда — короткая анонимная функция

```java
// Старый способ (до Java 8):
DoubleUnaryOperator squareOp = new DoubleUnaryOperator() {
    @Override
    public double applyAsDouble(double x) {
        return x * x;
    }
};

// Новый способ — лямбда:
DoubleUnaryOperator squareOp = x -> x * x;
```

`x -> x * x` — читается: «взять x, вернуть x в квадрате». Стрелка `->` разделяет аргументы и тело.

`Math::log` — ссылка на метод. Эквивалентно `x -> Math.log(x)`, просто ещё короче.

Используется в методе `foreach`:
```java
public void foreach(DoubleUnaryOperator op) {
    for (int i = 0; i < data.length; i++) {
        data[i] = op.applyAsDouble(data[i]); // применяем функцию к каждому элементу
    }
}

arr.foreach(x -> x * x);      // возведение в квадрат
arr.foreach(Math::log);        // натуральный логарифм
arr.foreach(x -> x + 1);      // прибавить 1 к каждому
```

---

# ЧАСТЬ 3 — Текстовые файлы и исключения (Лаб 4)

## Проблема: что делать когда что-то пошло не так?

```java
String text = file.readLine();
int number = Integer.parseInt(text); // что если text = "привет"???
```

`Integer.parseInt("привет")` упадёт. Программа аварийно завершится. Это плохо.

Java решает это через **исключения** — механизм обработки ошибок.

---

## Что такое исключение?

Исключение — это объект, который создаётся когда что-то пошло не так, и «бросается» вверх по стеку вызовов до тех пор пока кто-нибудь его не «поймает».

```java
void метод3() {
    Integer.parseInt("привет"); // БРОСАЕТ NumberFormatException
}

void метод2() {
    метод3(); // исключение летит сквозь метод2
}

void метод1() {
    try {
        метод2(); // пробуем
    } catch (NumberFormatException e) {
        System.out.println("Неверное число: " + e.getMessage()); // ЛОВИМ
    }
}
```

Визуально: исключение это как огонь. Если не потушить (catch) — сгорит всё вверх по стеку до main и программа упадёт.

---

## try-catch-finally — синтаксис

```java
try {
    // код который может упасть
    String line = br.readLine();
    int n = Integer.parseInt(line);

} catch (NumberFormatException e) {
    // сюда попадаем если строка не число
    System.out.println("Ошибка: " + e.getMessage());

} catch (IOException e) {
    // сюда попадаем если ошибка чтения файла
    System.out.println("Не могу прочитать файл");

} finally {
    // выполнится ВСЕГДА — и при ошибке, и без
    br.close(); // закрываем файл в любом случае
}
```

---

## try-with-resources — автоматическое закрытие

Писать `finally { br.close() }` каждый раз неудобно. Java 7 добавил более простой способ:

```java
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    // работаем с файлом
} // br.close() вызовется АВТОМАТИЧЕСКИ здесь, даже при исключении
```

Работает с любым классом который реализует `Closeable` / `AutoCloseable`.

---

## Читаем файл — шаг за шагом

```
файл.txt (байты) → FileReader (символы) → BufferedReader (строки)
```

```java
// Шаг 1: открываем файл как поток байт → символов
FileReader fr = new FileReader("stations.txt");

// Шаг 2: оборачиваем в буфер — теперь можно читать строками
BufferedReader br = new BufferedReader(fr);

// Шаг 3: читаем строки
String line;
while ((line = br.readLine()) != null) {  // null = конец файла
    System.out.println(line);
}

br.close();
```

**Зачем BufferedReader поверх FileReader?**

`FileReader` читает по одному символу за раз — каждый раз обращается к диску. Диск медленный. `BufferedReader` загружает сразу блок (например 8192 символа) в память — буфер. И уже из буфера отдаёт по символу. Быстрее в 100 раз.

---

## Checked vs Unchecked исключения

Java делит исключения на два типа:

**Checked (проверяемые)** — наследуют `Exception`:
- Java заставляет либо поймать, либо объявить `throws`
- Это "ожидаемые" ошибки: файл не найден, нет сети
- Примеры: `IOException`, `SQLException`, `StationFormatException`

```java
public void readFile() throws IOException {  // объявляем что можем бросить
    FileReader fr = new FileReader("file.txt"); // бросает IOException
}
```

**Unchecked (непроверяемые)** — наследуют `RuntimeException`:
- Java НЕ заставляет их обрабатывать
- Это "программные ошибки": нулевой указатель, выход за границы массива
- Примеры: `NullPointerException`, `ArrayIndexOutOfBoundsException`, `NumberFormatException`

---

## Своё исключение

```java
public class StationFormatException extends Exception {
    public StationFormatException(String message) {
        super(message);  // передаём сообщение родителю
    }
}
```

Зачем? `IOException` говорит «ошибка ввода/вывода» — не понятно что именно. `StationFormatException` говорит «строка файла не соответствует формату станции» — сразу ясно.

---

## Stream API — функциональный стиль чтения

Второй способ читать файл — через потоки (не сетевые, а потоки данных):

```java
List<Station> stations = Files.lines(Paths.get("stations.txt"))
    .filter(line -> !line.isBlank())           // убираем пустые строки
    .map(StationParser::parse)                  // каждую строку → объект Station
    .collect(Collectors.toList());             // собираем в List
```

Это как конвейер на заводе:
```
файл → [убрать пустые] → [преобразовать] → [собрать в список]
```

Разница с классическим способом:
- Классический: ты пишешь КАК делать (цикл, условие, добавить в список)
- Stream: ты говоришь ЧТО сделать (фильтровать, преобразовать, собрать)

---

# ЧАСТЬ 4 — Бинарные файлы (Лаб 5)

## Текстовый файл vs Бинарный файл

**Текстовый файл** хранит символы:
```
Число 12345 → символы '1','2','3','4','5' → 5 байт
Число 9 → символ '9' → 1 байт
```

**Бинарный файл** хранит сырые байты:
```
Число 12345 (тип int) → 4 байта: 00 00 30 39
Число 9 (тип int)     → 4 байта: 00 00 00 09
```

Любой `int` занимает ровно 4 байта. Компактно, но нельзя открыть в блокноте.

---

## Записываем и читаем int из файла

```java
// ЗАПИСЬ
try (DataOutputStream out = new DataOutputStream(
        new FileOutputStream("numbers.bin"))) {

    out.writeInt(42);   // записывает 4 байта: 00 00 00 2A
    out.writeInt(100);  // ещё 4 байта: 00 00 00 64
    out.writeInt(-1);   // ещё 4 байта: FF FF FF FF
}

// ЧТЕНИЕ
try (DataInputStream in = new DataInputStream(
        new FileInputStream("numbers.bin"))) {

    int a = in.readInt(); // читает 4 байта → 42
    int b = in.readInt(); // читает 4 байта → 100
    int c = in.readInt(); // читает 4 байта → -1
}
```

---

## Читаем до конца файла

Мы не знаем заранее сколько чисел в файле. `DataInputStream` бросает `EOFException` (End Of File) когда файл кончился:

```java
TreeSet<Integer> result = new TreeSet<>();

try (DataInputStream in = new DataInputStream(new FileInputStream("numbers.bin"))) {
    while (true) {
        try {
            int v = in.readInt();    // пробуем прочитать
            result.add(v);           // получилось — добавляем
        } catch (EOFException eof) {
            break;                   // файл кончился — выходим
        }
    }
}
```

---

## Алгоритм: все ли цифры нечётные?

```java
static boolean allDigitsOdd(int n) {
    if (n == 0) return false;      // 0 — чётная цифра
    long x = Math.abs((long) n);   // берём по модулю (для отрицательных)

    while (x > 0) {
        int digit = (int)(x % 10); // последняя цифра
        //   135 % 10 = 5
        //    13 % 10 = 3
        //     1 % 10 = 1

        if (digit % 2 == 0) return false; // цифра чётная → false
        x /= 10;  // убираем последнюю цифру: 135 → 13 → 1 → 0
    }

    return true; // все цифры прошли проверку
}
```

Проверим на числе `135`:
```
x = 135
digit = 135 % 10 = 5  → нечётная ✓, x = 13
digit = 13 % 10 = 3   → нечётная ✓, x = 1
digit = 1 % 10 = 1    → нечётная ✓, x = 0
цикл закончился → return true
```

Проверим на числе `132`:
```
x = 132
digit = 132 % 10 = 2  → ЧЁТНАЯ → return false сразу
```

---

# ЧАСТЬ 5 — Сетевой чат (Сокеты и Потоки)

## Как работает сеть на уровне Java?

Представь телефонный звонок:
- Ты набираешь номер — это `new Socket("127.0.0.1", 8088)`
- Другой конец поднимает трубку — это `serverSocket.accept()`
- Теперь у вас есть канал — говори/слушай

В Java:
- **ServerSocket** — «телефонный аппарат», который ждёт звонков
- **Socket** — сам «провод» после соединения
- Через сокет можно читать (`getInputStream`) и писать (`getOutputStream`)

---

## Сервер — шаг за шагом

```java
// Шаг 1: открываем «телефон» на порту 8088
ServerSocket serverSocket = new ServerSocket(8088);

// Шаг 2: ждём звонка (БЛОКИРУЕТ пока кто-то не подключится)
Socket clientSocket = serverSocket.accept();

// Шаг 3: получаем потоки для чтения и записи
BufferedReader in  = new BufferedReader(
    new InputStreamReader(clientSocket.getInputStream()));
PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

// Шаг 4: читаем данные
String message = in.readLine(); // БЛОКИРУЕТ пока клиент не пришлёт строку

// Шаг 5: отвечаем
out.println("Получил: " + message);
```

---

## Проблема: один клиент блокирует сервер

Если сервер обслуживает одного клиента — он не может принять второго:

```java
// БЕЗ ПОТОКОВ — только один клиент за раз:
while (true) {
    Socket client = server.accept();       // ждём клиента
    String msg = client.getInputStream()   // БЛОКИРУЕМ — ждём его сообщения
                       .readLine();        // пока он пишет — второй клиент не войдёт!
    // ...
}
```

**Решение: поток для каждого клиента.**

```java
while (true) {
    Socket client = server.accept();
    // Запускаем ОТДЕЛЬНЫЙ поток — он займётся этим клиентом
    // А главный поток сразу идёт ждать следующего accept()
    new Thread(new ClientHandler(client)).start();
}
```

Теперь:
- Главный поток: только `accept()` — ждёт новых клиентов
- Поток 1: общается с клиентом 1
- Поток 2: общается с клиентом 2
- ...

Всё параллельно!

---

## Thread и Runnable

**Thread** — объект-поток. Запускается через `thread.start()`, после чего метод `run()` выполняется параллельно с остальным кодом.

**Runnable** — интерфейс с одним методом `run()`. Описывает что поток должен делать.

```java
// Способ 1: наследовать Thread
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Я в отдельном потоке!");
    }
}
new MyThread().start();

// Способ 2: реализовать Runnable (предпочтительно)
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Я тоже в отдельном потоке!");
    }
}
new Thread(new MyTask()).start();

// Способ 3: лямбда (самый короткий)
new Thread(() -> System.out.println("И я!")).start();
```

Почему `implements Runnable` лучше `extends Thread`? Java не поддерживает множественное наследование классов. Если наследуешь Thread — больше нельзя наследовать другой класс. С Runnable этого ограничения нет.

---

## CopyOnWriteArrayList — потокобезопасный список

Обычный `ArrayList` ломается при одновременном доступе из нескольких потоков:

```
Поток 1: clients.add(newClient)  ←──┐
Поток 2: clients.remove(old)  ←──┘  │
                                     └─ оба меняют внутренний массив одновременно
                                        → данные повреждены, программа падает
```

`CopyOnWriteArrayList` решает это: при каждом изменении создаёт **копию** массива. Чтение (обход `for-each`) происходит по старой копии — безопасно. Запись — по новой.

---

## Swing и EDT

**Swing** — библиотека графического интерфейса Java. `JFrame` — окно, `JButton` — кнопка, `JTextArea` — текстовое поле.

**EDT (Event Dispatch Thread)** — специальный поток Swing. Только он имеет право изменять компоненты интерфейса.

Проблема: твой сетевой поток получил сообщение и хочет показать его в `JTextArea`. Но сетевой поток ≠ EDT.

```java
// НЕПРАВИЛЬНО — из сетевого потока напрямую:
chatArea.append(message); // гонка данных → непредсказуемое поведение

// ПРАВИЛЬНО — через invokeLater:
SwingUtilities.invokeLater(() -> {
    chatArea.append(message); // выполнится в EDT
});
```

`invokeLater` ставит задачу в очередь EDT. EDT выполнит её когда дойдёт до неё — безопасно.

---

# ЧАСТЬ 6 — База данных SQLite через JDBC

## Что такое реляционная база данных?

База данных — это структурированное хранилище данных. Данные хранятся в **таблицах** (как Excel).

```
Таблица product:
+------------+---------------------+------+--------------+
| article_id | name                | unit | pack_quantity |
+------------+---------------------+------+--------------+
|     1      | Молоко 3.2%         | л    | 1.0          |
|     2      | Творог 9% жирности  | кг   | 0.2          |
+------------+---------------------+------+--------------+

Таблица movement:
+----+------------+----------+----------------+------------+
| id | article_id | store_id | operation_type | pack_count |
+----+------------+----------+----------------+------------+
|  1 |     2      |    3     | Поступление    |    100     |
|  2 |     2      |    3     | Продажа        |     30     |
+----+------------+----------+----------------+------------+
```

`article_id` в `movement` ссылается на `article_id` в `product` — это **внешний ключ (FK)**. Так таблицы связаны между собой.

---

## JDBC — как Java разговаривает с БД

```
Твой Java-код  →  JDBC API  →  Драйвер SQLite  →  Файл .db
```

JDBC — это стандарт (набор интерфейсов). Конкретный драйвер реализует эти интерфейсы для конкретной БД.

Три шага работы:

```java
// 1. Открываем соединение
Connection conn = DriverManager.getConnection("jdbc:sqlite:store.db");

// 2. Создаём запрос и выполняем
Statement st = conn.createStatement();
ResultSet rs = st.executeQuery("SELECT * FROM product");

// 3. Читаем результат
while (rs.next()) {           // переходим к следующей строке
    int id   = rs.getInt("article_id");
    String name = rs.getString("name");
    System.out.println(id + ": " + name);
}

// 4. Закрываем (или используем try-with-resources)
conn.close();
```

---

## SQL — язык запросов

### SELECT — выбрать данные

```sql
SELECT name, unit FROM product
-- Выбрать колонки name и unit из таблицы product

SELECT * FROM product
-- Выбрать все колонки
```

### WHERE — фильтрация

```sql
SELECT * FROM product WHERE unit = 'кг'
-- Только товары в килограммах

SELECT * FROM movement WHERE pack_count > 50
-- Только операции больше 50 упаковок
```

### JOIN — соединить таблицы

```sql
SELECT p.name, m.operation_type, m.pack_count
FROM movement m
JOIN product p ON m.article_id = p.article_id
```

Что происходит:
```
movement строка: article_id=2, operation_type='Продажа', pack_count=30
JOIN product:    находим product где article_id=2 → name='Творог 9%'
Результат строка: name='Творог 9%', operation_type='Продажа', pack_count=30
```

### SUM, GROUP BY — агрегация

```sql
SELECT SUM(pack_count) FROM movement WHERE operation_type = 'Поступление'
-- Сумма всех поступлений
```

### CASE WHEN — условие внутри запроса

```sql
SELECT
    SUM(CASE WHEN operation_type = 'Поступление' THEN pack_count ELSE 0 END) AS пришло,
    SUM(CASE WHEN operation_type = 'Продажа'     THEN pack_count ELSE 0 END) AS ушло
FROM movement
```

Для каждой строки: если операция = поступление — берём pack_count, иначе 0. Потом суммируем.

---

## ResultSet — чтение результата

`ResultSet` — курсор. Представь его как палец который ползёт по строкам результата:

```
До rs.next():  [курсор перед первой строкой]

rs.next() → true:
  строка 1: id=1, name="Молоко"
  rs.getInt("id")     → 1
  rs.getString("name") → "Молоко"

rs.next() → true:
  строка 2: id=2, name="Творог"
  ...

rs.next() → false:  строк больше нет
```

```java
while (rs.next()) {      // пока есть строки
    String name = rs.getString("name");
    int count = rs.getInt("pack_count");
    System.out.println(name + ": " + count);
}
```

---

# ИТОГОВАЯ ШПАРГАЛКА — ключевые концепты

| Концепт | Одна строка |
|---------|-------------|
| `Vector` | Динамический массив — растёт при добавлении, потокобезопасен |
| `TreeSet` | Множество без дубликатов, всегда отсортированное (красно-чёрное дерево) |
| `Iterator` | Безопасный обход с возможностью удалять через `it.remove()` |
| `Comparable` | Интерфейс «я умею сравнивать себя с другим» — нужен для TreeSet и sort |
| `equals/hashCode` | Сравнение по содержимому; hashCode нужен для HashMap/HashSet |
| `abstract class` | Шаблон с общим кодом; нельзя создать объект напрямую |
| `interface` | Чистый контракт; класс может реализовать несколько |
| Полиморфизм | Переменная типа-родителя хранит объект-наследника; метод вызывается реальный |
| Лямбда | `x -> x*x` — короткая анонимная функция |
| `try-catch` | Поймать исключение и обработать вместо падения программы |
| `try-with-resources` | Автоматическое `close()` — гарантированно закрывает файлы |
| Checked exception | Обязателен к обработке; наследует `Exception` |
| `BufferedReader` | Читает файл строками, с буфером для скорости |
| `DataInputStream` | Читает бинарный файл — сырые байты как `int`, `double` и т.д. |
| `EOFException` | Сигнал что бинарный файл кончился |
| `Socket` | Двухсторонний канал между двумя программами по сети |
| `ServerSocket` | «Ухо сервера» — ждёт входящих подключений |
| `Thread/Runnable` | Поток — параллельное выполнение кода |
| EDT | Event Dispatch Thread — только он трогает Swing-компоненты |
| `invokeLater` | Поставить задачу в очередь EDT из другого потока |
| JDBC | Java API для работы с любыми базами данных |
| `Connection` | Соединение с БД |
| `ResultSet` | Курсор на результат SQL-запроса; `rs.next()` + `rs.getString()` |
| SQL JOIN | Соединить строки двух таблиц по совпадающему ключу |
| `CASE WHEN` | Условие внутри SQL-запроса — аналог if-else |

---

# Как учить эффективно

1. **По одной теме за раз.** Не пытайся понять всё сразу.

2. **Запускай код.** Понимание приходит когда видишь результат.

3. **Меняй код.** Понял как работает `TreeSet`? Попробуй добавить дубликат и убедись что он не добавился.

4. **Объясни вслух.** Если можешь объяснить своими словами — понял.

5. **Задавай вопросы.** Если что-то непонятно — спрашивай конкретно: «не понимаю что делает `rs.next()`» лучше чем «не понимаю JDBC».

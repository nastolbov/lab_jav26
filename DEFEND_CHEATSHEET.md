# Шпаргалка для защиты лаб

## Навигация

| Лаба | Тема | Перейти |
|------|------|---------|
| Лаба 2 | Коллекции (Vector, TreeSet) | [→](#лаба-2--коллекции) |
| Лаба 3 | Абстрактные классы / Интерфейсы | [→](#лаба-3--абстрактные-классы) |
| Лаба 4 | Текстовые файлы / Исключения | [→](#лаба-4--текстовые-файлы) |
| Лаба 5 | Бинарные файлы | [→](#лаба-5--бинарные-файлы) |

---

# Лаба 2 — Коллекции

**Навигация:** [Суть](#суть-1) · [Запуск](#запуск-1) · [Программа 1](#программа-1-vectorcharacter) · [Программа 2](#программа-2-vectorcex) · [Программа 3](#программа-3-алгоритмы) · [Класс Cex](#класс-cex) · [Вопросы](#вопросы-1) · [↑ Наверх](#навигация)

---

## Суть-1

**Для чего лаба:** научиться работать с коллекциями Java — контейнерами которые хранят группы объектов. В отличие от массива — коллекция сама меняет размер, умеет искать, сортировать, удалять.

**Вариант 10:** два контейнера — `Vector` и `TreeSet`, встроенный тип `char`, пользовательский класс `Cex` (цех: имя, начальник, количество работающих).

**Три программы:**
- `Program1` — Vector с символами `char`
- `Program2` — Vector с объектами `Cex`
- `Program3` — алгоритмы: сортировка, поиск, перемещение, слияние

---

## Запуск-1

```bash
cd ~/Desktop/lab_jav26/lab2
mvn -q exec:java
```

---

## Программа 1 (Vector\<Character\>)

**Файл:** `Program1.java`

### Шаг 1 — создание и заполнение (строки 24-28)

```java
Vector<Character> v1 = new Vector<>();
for (char c : new char[]{'a','b','c','d','e','f','g','h'}) {
    v1.add(c);
}
```

Создаём вектор из 8 символов. `Vector` — это список который автоматически растёт. Добавляем символы по одному через `add()`.

Вывод: `1) Первый контейнер заполнен`

### Шаг 2 — просмотр (строка 31)

```java
System.out.println("2) Первый контейнер: " + v1);
```

`v1.toString()` выводит `[a, b, c, d, e, f, g, h]` — Vector сам знает как себя выводить.

### Шаг 3 — изменение: удаление и замена (строки 34-37)

```java
v1.remove(2);       // удаляем элемент с индексом 2 ('c'), остальные сдвигаются
v1.set(0, 'X');     // заменяем элемент на позиции 0: 'a' → 'X'
v1.set(1, 'Y');     // заменяем элемент на позиции 1: 'b' → 'Y'
```

После: `[X, Y, d, e, f, g, h]`

- `remove(2)` — удалить по **индексу**, не по значению
- `set(0, 'X')` — заменить, не добавить

### Шаг 4 — просмотр через Iterator (строки 40-45)

```java
Iterator<Character> it = v1.iterator();
while (it.hasNext()) {
    System.out.print(it.next() + " ");
}
```

`iterator()` — объект для обхода. `hasNext()` — есть ли ещё элемент. `next()` — взять следующий. Это более низкоуровневый способ чем for-each, но позволяет безопасно удалять во время обхода.

### Шаг 5 — второй Vector (строки 48-51)

```java
Vector<Character> v2 = new Vector<>();
for (char c : new char[]{'1','2','3','4'}) v2.add(c);
```

Второй вектор из 4 символов: `[1, 2, 3, 4]`

### Шаг 6 — удалить n=2 после индекса 1, добавить v2 (строки 54-59)

```java
int after = 1;
int n = 2;
for (int i = 0; i < n && after + 1 < v1.size(); i++) {
    v1.remove(after + 1);  // удаляем элемент сразу после позиции 1
}
v1.addAll(v2);  // добавляем все элементы из v2 в конец v1
```

Было: `[X, Y, d, e, f, g, h]` → убрали 2 после индекса 1 → `[X, Y, f, g, h]` → добавили v2 → `[X, Y, f, g, h, 1, 2, 3, 4]`

`addAll()` — добавить все элементы другой коллекции сразу.

### Шаг 7 — просмотр обоих (строки 61-63)

```java
System.out.println("7) v1 = " + v1);
System.out.println("7) v2 = " + v2);
```

v2 не изменился — `addAll` копирует ссылки, не перемещает.

---

## Программа 2 (Vector\<Cex\>)

**Файл:** `Program2.java` — то же самое что Program1, но вместо `char` используется наш класс `Cex`.

### Шаг 1 — создание (строки 17-23)

```java
Vector<Cex> v1 = new Vector<>();
v1.add(new Cex("Сборочный",  "Иванов",  120));
v1.add(new Cex("Литейный",   "Петров",   80));
v1.add(new Cex("Кузнечный",  "Сидоров", 150));
v1.add(new Cex("Малярный",   "Жуков",    40));
v1.add(new Cex("Покрасочный","Орлов",    65));
```

5 цехов. Каждый — объект с тремя полями.

### Шаг 2 — просмотр (строки 26-27)

```java
v1.forEach(c -> System.out.println("   " + c));
```

`forEach` с лямбдой — короче чем Iterator. Для каждого элемента вызывает `toString()` из `Cex`.

### Шаг 3 — изменение (строки 30-33)

```java
v1.remove(2);  // убрали "Кузнечный" (индекс 2)
v1.set(0, new Cex("Сборочный-1", "Иванов", 130));  // заменили "Сборочный"
```

Было 5 цехов → стало 4.

### Шаг 4 — Iterator (строки 36-40)

```java
Iterator<Cex> it = v1.iterator();
while (it.hasNext()) {
    System.out.println("   " + it.next());
}
```

То же что в Program1, но с объектами Cex.

### Шаги 5-7 — второй вектор, удаление, слияние (строки 43-57)

Аналогично Program1 — создаём v2 с двумя цехами, удаляем 1 после индекса 1 из v1, добавляем v2.

---

## Программа 3 (Алгоритмы)

**Файл:** `Program3.java` — здесь показываем сортировку, поиск, перемещение между контейнерами, слияние.

### Шаг 1 — создание Vector (строки 35-42)

6 цехов в Vector.

### Шаг 2 — сортировка по возрастанию (строка 45)

```java
Collections.sort(v);  // сортирует по compareTo() из Cex
```

`Collections.sort()` — стандартный алгоритм сортировки. Использует `compareTo()` из `Cex` — сортирует по числу работающих.

После: `[Малярный(40), Покрасочный(65), Литейный(80), Сварочный(95), Сборочный(120), Кузнечный(150)]`

### Шаг 3 — просмотр (строки 49-50)

Вывод отсортированного вектора.

### Шаг 4 — поиск (строки 53-56)

```java
Cex pattern = new Cex("Сварочный", "Новиков", 95);
int idx = Collections.binarySearch(v, pattern);
```

`binarySearch` — быстрый поиск (делит пополам). Требует **отсортированного** контейнера. Возвращает индекс если нашёл, отрицательное число если нет. Использует `compareTo()` для сравнения.

### Шаг 5 — перемещение в TreeSet (строки 60-68)

```java
TreeSet<Cex> ts = new TreeSet<>();
Iterator<Cex> it = v.iterator();
while (it.hasNext()) {
    Cex c = it.next();
    if (c.getWorkersCount() >= 90) {
        ts.add(c);
        it.remove();  // удаляем из Vector безопасно через итератор
    }
}
```

Цехи с >= 90 работниками **перемещаются** (удаляются из Vector, добавляются в TreeSet). `it.remove()` — безопасное удаление во время обхода.

TreeSet автоматически отсортировал по `compareTo()`: `[Сварочный(95), Сборочный(120), Кузнечный(150)]`

### Шаг 6 — просмотр TreeSet (строки 71-72)

### Шаг 7 — сортировка по убыванию (строки 77-83)

```java
v.sort(Comparator.reverseOrder());  // Vector сортируем обратно

TreeSet<Cex> tsDesc = new TreeSet<>(Comparator.reverseOrder());
tsDesc.addAll(ts);
ts = tsDesc;  // TreeSet нельзя пересортировать — создаём новый с обратным компаратором
```

Почему новый TreeSet для убывания? TreeSet хранит порядок сортировки заданный при создании. Чтобы изменить — создаём новый с `Comparator.reverseOrder()`.

### Шаг 8 — просмотр обоих по убыванию (строки 86-90)

### Шаги 9-10 — слияние в третий контейнер (строки 93-98)

```java
List<Cex> merged = new ArrayList<>();
merged.addAll(v);      // добавили всё из Vector
merged.addAll(ts);     // добавили всё из TreeSet
merged.sort(Comparator.reverseOrder());  // отсортировали по убыванию
```

Третий контейнер — `ArrayList` (не Vector, не TreeSet). Содержит все 6 цехов по убыванию.

---

## Класс Cex

**Файл:** `Cex.java`

### Поля (строки 13-15)

```java
private final String name;         // имя цеха
private final String chief;        // начальник
private final int workersCount;    // количество работающих
```

`final` — нельзя изменить после создания объекта.

### compareTo() — строки 29-33

```java
public int compareTo(Cex o) {
    int c = Integer.compare(this.workersCount, o.workersCount);
    if (c != 0) return c;
    return this.name.compareTo(o.name);
}
```

Нужен для `TreeSet` и `Collections.sort()`. Сортируем по числу работающих. Если одинаково — по имени (чтобы два цеха с одинаковым числом работников не считались дубликатом в TreeSet).

Возвращает: отрицательное (меньше), 0 (одинаковые → в TreeSet дубликат!), положительное (больше).

### equals() — строки 36-43

```java
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Cex)) return false;
    Cex cex = (Cex) o;
    return workersCount == cex.workersCount &&
           Objects.equals(name, cex.name) &&
           Objects.equals(chief, cex.chief);
}
```

Без этого два объекта `new Cex("Сборочный", "Иванов", 120)` были бы разными — Java сравнивала бы адреса в памяти. С переопределением — сравниваются все три поля.

Нужен для `vector.contains(cex)`, `vector.remove(cex)` (по значению, не по индексу).

### hashCode() — строки 46-48

```java
public int hashCode() {
    return Objects.hash(name, chief, workersCount);
}
```

Обязательная пара к `equals()`. Нужен для `HashSet` и `HashMap`. Правило: если `equals()` true — `hashCode()` одинаковый.

---

## Вопросы-1

**Что такое Vector?** Список с динамическим размером. Потокобезопасен (все методы synchronized). Хранит элементы в порядке добавления, допускает дубликаты.

**Чем Vector отличается от ArrayList?** Vector потокобезопасен, ArrayList нет. ArrayList быстрее в однопоточных задачах.

**Что такое TreeSet?** Множество без дубликатов с автоматической сортировкой. Использует `compareTo()` для сортировки и проверки дубликатов.

**Зачем Iterator?** Для безопасного удаления во время обхода — `it.remove()`. При удалении через for-each — `ConcurrentModificationException`.

**Зачем compareTo?** TreeSet и Collections.sort() не знают как сравнивать объекты Cex. compareTo объясняет — по числу работающих.

**Зачем equals и hashCode?** equals — сравнение по данным, не по адресу. hashCode — обязательная пара, для корректной работы в HashSet/HashMap.

**Что такое binarySearch?** Быстрый поиск делением пополам. Требует отсортированную коллекцию. Использует compareTo.

---

# Лаба 3 — Абстрактные классы

**Навигация:** [Суть](#суть-2) · [Запуск](#запуск-2) · [AbstractArray](#abstractarray) · [InsertSelection](#insert--selection) · [IArray](#iarray) · [InsertISelectionI](#inserti--selectioni) · [MainView](#mainview-лаба-3) · [Вопросы](#вопросы-2) · [↑ Наверх](#навигация)

---

## Суть-2

**Для чего лаба:** показать два способа организации кода через ООП — абстрактный класс и интерфейс. Оба способа решают одну задачу: сортировка массива двумя алгоритмами + поэлементная обработка функцией.

**Вариант 10:** сортировка вставками (Insert) и выбором (Selection), функция — квадрат для Insert, логарифм для Selection.

**Два дерева классов:**
```
AbstractArray (abstract)       IArray (interface)
├── Insert                     ├── InsertI
└── Selection                  └── SelectionI
```

---

## Запуск-2

```bash
cd ~/Desktop/lab_jav26/lab3
mvn spring-boot:run
# открыть http://localhost:8083
```

---

## AbstractArray

**Файл:** `AbstractArray.java`

**Строки 22-44 — что хранит и что требует:**

```java
public abstract class AbstractArray {
    protected double[] data;   // массив — доступен подклассам

    protected AbstractArray(double[] data) {
        this.data = data.clone();  // копия — не трогаем оригинал
    }

    public abstract void sort();                          // ОБЯЗАН реализовать подкласс
    public abstract void foreach(DoubleUnaryOperator op); // ОБЯЗАН реализовать подкласс
    public abstract void foreachDefault();                // ОБЯЗАН реализовать подкласс

    public double[] getData() { return data; }  // готовый метод — достаётся бесплатно
}
```

`abstract` у метода = нет тела, только подпись. Подкласс **обязан** написать реализацию. Если не напишет — ошибка компиляции.

`toString()` — строки 46-54 — тоже готовый, наследуется автоматически.

---

## Insert + Selection

**Файлы:** `Insert.java`, `Selection.java`

Оба делают `extends AbstractArray` — наследуют поле `data`, метод `getData()`, `toString()`. Обязаны реализовать `sort()`, `foreach()`, `foreachDefault()`.

**Insert.java:14-26 — сортировка вставками:**
Каждый элемент ставится на нужное место среди уже отсортированных.

**Insert.java:28-32 — foreach с лямбдой:**
```java
public void foreach(DoubleUnaryOperator op) {
    for (int i = 0; i < data.length; i++) {
        data[i] = op.applyAsDouble(data[i]);  // применяем функцию к каждому элементу
    }
}
```

**Insert.java:34-37 — foreachDefault (квадрат):**
```java
public void foreachDefault() {
    foreach(x -> x * x);  // лямбда: умножить на себя
}
```

**Selection** — то же самое, но `foreachDefault` применяет `Math::log` (логарифм).

---

## IArray

**Файл:** `IArray.java`, строки 11-16

```java
public interface IArray {
    void sort();
    void foreach(DoubleUnaryOperator op);
    void foreachDefault();
    double[] getData();
}
```

Только контракт — никакого кода, никаких полей. Класс реализующий интерфейс обязан написать ВСЕ методы.

**Главное отличие от AbstractArray:** нет поля `data`, нет `toString()`, нет готовых методов. Каждый класс пишет всё сам.

---

## InsertI + SelectionI

**Файлы:** `InsertI.java`, `SelectionI.java`

`implements IArray` — обязаны реализовать все 4 метода.

**InsertI.java:8 — поле сами объявляем:**
```java
private final double[] data;  // не достаётся из IArray — пишем сами
```

Алгоритм sort() и foreach() — **идентичен** Insert.java. Это и показывает разницу: у AbstractArray общий код в родителе, у IArray каждый класс пишет всё сам.

**toString()** у InsertI **нет** — IArray его не объявляет. В MainView для вывода используется метод `format(double[])` — см. ниже.

---

## MainView (Лаба 3)

**Файл:** `MainView.java`

**Строки 62-88 — основная логика:**

```java
// Вариант через абстрактный класс
Insert ins = new Insert(arr);
ins.sort();
// ins.getData() — получить результат

// Вариант через интерфейс
IArray iIns = new InsertI(arr);
iIns.sort();
// iIns.getData()
```

**Строки 104-111 — метод format() для вывода:**
```java
private static String format(double[] a) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < a.length; i++) {
        if (i > 0) sb.append(", ");
        sb.append(String.format("%.4f", a[i]));
    }
    return sb.append("]").toString();
}
```

Используется вместо `toString()` и для AbstractArray-классов и для IArray-классов — единообразно.

---

## Вопросы-2

**Что такое абстрактный класс?** Класс с `abstract`. Нельзя создать объект напрямую. Содержит абстрактные методы — подклассы обязаны реализовать.

**Что такое интерфейс?** Только контракт. Никакого кода, никаких полей. Класс может реализовывать много интерфейсов, но наследовать только один класс.

**Что такое полиморфизм?** `AbstractArray a = new Insert(arr)` — переменная типа AbstractArray, внутри Insert. `a.sort()` вызывает Insert.sort(). Один тип — разное поведение.

**Зачем @Override?** Защита от опечаток. Если написал неверное имя — компилятор сообщит.

**Зачем DoubleUnaryOperator?** Функциональный интерфейс — принимает double, возвращает double. Позволяет передавать функции как параметры (`x -> x*x`, `Math::log`).

**Почему у InsertI нет toString()?** IArray не объявляет toString(). MainView использует свой format() для единообразия.

---

# Лаба 4 — Текстовые файлы

**Навигация:** [Суть](#суть-3) · [Запуск](#запуск-3) · [Что показывать](#что-показывать-3) · [Station](#station) · [StationFormatException](#stationformatexception) · [StationParser](#stationparser) · [StationReader](#stationreader) · [MainView](#mainview-лаба-4) · [Вопросы](#вопросы-3) · [↑ Наверх](#навигация)

---

## Суть-3

**Для чего лаба:** научиться читать текстовые файлы и правильно обрабатывать ошибки. Если файл не найден или данные в нём кривые — программа не должна падать, она должна показать понятное сообщение об ошибке.

**Вариант 10:** Железная дорога. Читаем файл со станциями (название, пассажирские поезда, товарные поезда). Находим самую загруженную станцию. Сравниваем две станции.

**Два требования задания:**
1. Реализовать чтение **двумя способами** (классический и функциональный)
2. Обрабатывать ошибки через **исключения** (своё + стандартные)

---

## Запуск-3

```bash
cd ~/Desktop/lab_jav26/lab4
mvn spring-boot:run
# открыть http://localhost:8084
```

---

## Что показывать-3

1. "Загрузить" → таблица заполнилась
2. Вкладка "Самая загруженная" → "Найти"
3. Вкладка "Сравнение" → две станции → "Сравнить"
4. Переключи RadioButton на "Files.lines()" → снова загрузи → тот же результат
5. Напиши несуществующий файл → красная надпись (показываешь обработку ошибок)

---

## Station

**Файл:** `Station.java`

```java
private final String name;      // название станции
private final int    passenger; // пассажирских поездов в сутки
private final int    cargo;     // товарных поездов в сутки
```

`final` — поля нельзя изменить после создания. Это модель данных — просто хранит информацию об одной станции.

`getTotal()` — строка 26 — возвращает `passenger + cargo`, вычисляется на ходу, не хранится.

---

## StationFormatException

**Файл:** `StationFormatException.java`

```java
public class StationFormatException extends Exception {
    public StationFormatException(String message) { super(message); }
    public StationFormatException(String message, Throwable cause) { super(message, cause); }
}
```

**Зачем своё исключение:** стандартный `NumberFormatException` скажет только "не число". Наш `StationFormatException` скажет "строка 3 в файле stations.txt — не удалось разобрать числа". Конкретнее и понятнее.

**extends Exception = checked** — Java заставит всех кто вызывает методы которые бросают это исключение, либо поймать (`catch`), либо объявить (`throws`). Нельзя проигнорировать.

**Два конструктора:**
- Первый — просто сообщение
- Второй — сообщение + причина (`Throwable cause`). Сохраняем оригинальное исключение — в стектрейсе будет видна вся цепочка

---

## StationParser

**Файл:** `StationParser.java`

Разбирает **одну строку** из файла и возвращает объект `Station`. Поддерживает два формата строки.

### Формат 1 — фиксированная ширина (строки 26-39)

Строка в файле: `"Минск               45 60"`

```java
if (line.length() >= 22) {
    String namePart = line.substring(0, 20).trim();  // первые 20 символов = имя
    String tail     = line.substring(20).trim();      // остаток = числа
    String[] parts  = tail.split("\\s+");             // ["45", "60"]
    int p = Integer.parseInt(parts[0]);               // 45
    int t = Integer.parseInt(parts[1]);               // 60
    return new Station(namePart, p, t);
}
```

Пошагово:
```
"Минск               45 60"
 substring(0,20) = "Минск               " → trim() = "Минск"
 substring(20)   = "45 60"               → split  = ["45","60"]
```

Проверка `>= 22`: нужно минимум 20 символов имени + пробел + хоть одна цифра.

### Формат 2 — гибкий (строки 42-60)

Строка: `"Барановичи 10 30"`

```java
String[] tokens = line.trim().split("\\s+");
// ["Барановичи", "10", "30"]
int p = Integer.parseInt(tokens[tokens.length - 2]);  // предпоследний = 10
int t = Integer.parseInt(tokens[tokens.length - 1]);  // последний = 30
// имя = все токены кроме последних двух = "Барановичи"
```

Почему через if: парсер **сам** пробует первый формат, если не вышло — второй. Вызывающий код просто передаёт строку и получает Station. Логика выбора формата спрятана внутри.

### Обработка ошибок в парсере

- Пустая строка → `StationFormatException("Пустая строка")` — строка 20
- Меньше 3 токенов → `StationFormatException("Ожидаются: ...")` — строка 44
- Не число → ловим `NumberFormatException`, бросаем `StationFormatException` с причиной — строки 57-59
- Отрицательное число → `StationFormatException("не может быть отрицательным")` — строки 33-35

---

## StationReader

**Файл:** `StationReader.java`

Читает **весь файл** и возвращает `List<Station>`.

### Способ 1 — классический (строки 23-41)

```java
try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
    String line;
    int no = 0;
    while ((line = br.readLine()) != null) {  // null = файл кончился
        no++;
        if (line.isBlank()) continue;
        try {
            result.add(StationParser.parse(line));
        } catch (StationFormatException ex) {
            throw new StationFormatException("Строка " + no + ": " + ex.getMessage(), ex);
        }
    }
}
```

- **Строка 26** — три обёртки: `FileReader` (байты→символы) → `BufferedReader` (буфер 8192 символа + readLine)
- **Строка 26** — `try-with-resources`: файл закроется автоматически даже при ошибке
- **Строка 29** — `readLine()` возвращает `null` когда файл кончился
- **Строки 34-37** — добавляем номер строки к сообщению об ошибке, сохраняем причину через второй конструктор

### Способ 2 — функциональный (строки 44-67)

```java
try (Stream<String> lines = Files.lines(path)) {
    return lines
        .filter(s -> !s.isBlank())       // убрать пустые
        .map(s -> StationParser.parse(s)) // строку → Station
        .collect(Collectors.toList());    // собрать в список
}
```

**Конвейер:** каждая строка проходит filter → map → попадает в список.

**Ленивый:** `Files.lines()` не читает весь файл сразу — читает строку только когда она нужна конвейеру.

**Проблема checked в лямбде (строки 50-57):**
```java
.map(s -> {
    try {
        return StationParser.parse(s);
    } catch (StationFormatException e) {
        throw new RuntimeException(e);  // оборачиваем — лямбда не может checked
    }
})
```
Функциональный интерфейс `Function` не объявляет `throws` — checked нельзя бросить из лямбды. Оборачиваем в `RuntimeException` (unchecked), пробрасываем, ловим снаружи (строки 60-65), достаём через `getCause()`.

**Разница двух способов:**

| | Способ 1 (BufferedReader) | Способ 2 (Files.lines) |
|--|--|--|
| Стиль | Пошаговый цикл | Конвейер |
| Читает | Блоками в буфер | Лениво — по одной строке |
| Огромный файл | Может не хватить памяти | Экономит память |

---

## MainView (Лаба 4)

**Файл:** `MainView.java`

**Метод load() — обработка ошибок:**
```java
try {
    data = StationReader.readClassic(path);    // или readFunctional
    grid.setItems(data);
} catch (StationFormatException ex) {
    errorBox.setText("Ошибка формата файла: " + ex.getMessage());
} catch (IOException ex) {
    errorBox.setText("Ошибка чтения файла: " + ex.getMessage());
}
```

Два catch — две разные ошибки:
- `IOException` — файл не найден, нет прав
- `StationFormatException` — файл есть, но данные кривые

Программа не падает — показывает красный текст пользователю.

---

## Вопросы-3

**Что такое исключение?** Объект-ошибка который "летит" по стеку вызовов пока его не поймает catch.

**Чем checked отличается от unchecked?** Checked (extends Exception) — Java заставляет обработать. Unchecked (extends RuntimeException) — не обязательно. IOException — checked, NullPointerException — unchecked.

**Зачем своё исключение?** Стандартные описывают техническую проблему. Своё даёт смысл задачи — "неверный формат станции".

**Что такое try-with-resources?** `try (ресурс = ...)` — Java автоматически вызывает close() после блока.

**Зачем BufferedReader поверх FileReader?** FileReader читает посимвольно — медленно. BufferedReader добавляет буфер (загружает 8192 символа разом) и метод readLine().

**Что возвращает readLine() в конце файла?** null.

**Почему нельзя checked в лямбде?** Function не объявляет throws. Обходим: оборачиваем в RuntimeException.

**Разница двух способов чтения?** BufferedReader — классический цикл. Files.lines() — функциональный конвейер, ленивый.

---

# Лаба 5 — Бинарные файлы

**Навигация:** [Суть](#суть-4) · [Запуск](#запуск-4) · [Что показывать](#что-показывать-4) · [BinaryFileService](#binaryfileservice) · [Проверка требований](#проверка-требований) · [MainView](#mainview-лаба-5) · [Вопросы](#вопросы-4) · [↑ Наверх](#навигация)

---

## Суть-4

**Для чего лаба:** научиться работать с бинарными файлами — файлами где данные хранятся не как текст, а как байты напрямую. Число `45` в текстовом файле = два символа `'4'` и `'5'`. В бинарном = четыре байта `00 00 00 2D`.

**Вариант 10:** создать бинарный файл со случайными числами. Найти числа у которых **все цифры нечётные** (1,3,5,7,9). Вывести по возрастанию без повторений.

---

## Запуск-4

```bash
cd ~/Desktop/lab_jav26/lab5
mvn spring-boot:run
# открыть http://localhost:8085
```

---

## Что показывать-4

1. "Создать файл…" → n=20, a=1, b=99 → "Создать" → видишь все числа
2. "Обработать" → видишь только числа с нечётными цифрами
3. Объясняешь: 13 ✅ (1 и 3 нечётные), 24 ❌ (2 и 4 чётные)
4. Создаёшь снова → числа другие (Random), результат меняется
5. Результат всегда отсортирован и без дубликатов

---

## BinaryFileService

**Файл:** `BinaryFileService.java`

### create() — строки 30-47

```java
Random rnd  = new Random();
int    size = 1 + rnd.nextInt(maxCount);  // случайное количество 1..n

try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
    for (int i = 0; i < size; i++) {
        int v = a + rnd.nextInt(b - a + 1);  // случайное число a..b
        out.writeInt(v);                      // записывает ровно 4 байта
    }
}
```

`DataOutputStream.writeInt(45)` записывает `00 00 00 2D` — ровно 4 байта в двоичном представлении.

`Files.createDirectories()` — строка 39 — создаёт папку `data/` если её нет.

### readAll() — строки 50-62

```java
long bytes = Files.size(file);
int[] result = new int[(int)(bytes / Integer.BYTES)];  // сколько чисел = байты/4
try (DataInputStream in = new DataInputStream(Files.newInputStream(file))) {
    for (int i = 0; i < result.length; i++) {
        result[i] = in.readInt();  // читает 4 байта → int
    }
}
```

Этот метод — **только для отображения** содержимого в UI. Задание не нарушает.

### oddDigitNumbers() — строки 70-86

**Это главный метод — реализует задание варианта 10.**

```java
TreeSet<Integer> result = new TreeSet<>();
try (DataInputStream in = new DataInputStream(Files.newInputStream(file))) {
    while (true) {
        int v;
        try {
            v = in.readInt();         // читаем одно число
        } catch (EOFException eof) {
            break;                    // файл кончился — нормально
        }
        if (allDigitsOdd(v)) {
            result.add(v);            // только подходящие в TreeSet
        }
    }
}
```

`EOFException` — `readInt()` ждёт 4 байта, файл кончился — нормальный способ узнать конец.

`while(true)` — нет метода "проверить остались ли байты" без реального чтения.

### allDigitsOdd() — строки 89-98

```java
static boolean allDigitsOdd(int n) {
    if (n == 0) return false;
    long x = Math.abs((long) n);
    while (x > 0) {
        int d = (int)(x % 10);           // последняя цифра: 135 % 10 = 5
        if ((d & 1) == 0) return false;  // чётная → сразу false
        x /= 10;                         // убираем цифру: 135 / 10 = 13
    }
    return true;
}
```

Пример для 135: `5 нечётная → 3 нечётная → 1 нечётная → true ✅`
Пример для 124: `4 чётная → false ❌` (сразу)

`(d & 1) == 0` — побитовая проверка: у чётных чисел последний бит = 0.

---

## Проверка требований

### ✅ Создание бинарного файла со случайными числами
`BinaryFileService:30-47` — `DataOutputStream.writeInt()`, количество `1..n`, значения `a..b`.

### ✅ Файл читается только один раз
`BinaryFileService:72` — один цикл `while(true)`, один поток `DataInputStream`, один проход.

### ✅ Элементы НЕ считываются в массив
`BinaryFileService:70-86` — числа читаются по одному через `readInt()`. В `TreeSet` попадают **только подходящие**. Весь файл в структуру не загружается.

⚠️ `readAll()` (строки 50-62) читает в `int[]` — но это отдельный метод **только для UI**, не для обработки. Если спросят — объяснить это.

### ✅ Сортировка по возрастанию
`BinaryFileService:71` — `TreeSet<Integer>` сортирует автоматически при каждом `add()`.

### ✅ Без повторений
`TreeSet` — это Set, дубликаты не добавляются по определению.

### ✅ Только числа с нечётными цифрами
`BinaryFileService:89-98` — метод `allDigitsOdd()`, проверяет каждую цифру через `% 10`.

### ✅ Обработка ошибок
`MainView:106-112` — три catch: `IllegalArgumentException` (неверные параметры), `IOException` (ошибка файла), `Exception` (всё остальное).

---

## MainView (Лаба 5)

**Файл:** `MainView.java`

**Строки 63-65 — три кнопки:**
```java
Button btnCreate  = new Button("Создать файл…",  e -> openCreateDialog());
Button btnChoose  = new Button("Выбрать файл…",  e -> openChooseDialog());
Button btnProcess = new Button("Обработать",     e -> process());
```

**openCreateDialog() — строки 75-121:**
`Dialog` — всплывающее окно. `IntegerField` — поле только для целых чисел. Вызывает `service.create()`.

**process() — строки 164-183:**
```java
TreeSet<Integer> result = service.oddDigitNumbers(currentFile);
resultBox.setValue(result.stream()
    .map(String::valueOf)
    .collect(Collectors.joining(" ")));  // [13, 15, 31] → "13 15 31"
```

---

## Вопросы-4

**Чем бинарный файл отличается от текстового?** В текстовом числа как символы ("45" = 2 байта). В бинарном — двоичное представление (45 = 4 байта). Быстрее, фиксированный размер.

**Что такое DataOutputStream/DataInputStream?** Обёртки над байтовым потоком. writeInt() записывает 4 байта, readInt() читает 4 байта.

**Что такое EOFException?** Конец файла при попытке readInt(). Нормальный способ узнать конец бинарного файла.

**Зачем TreeSet?** Задание требует сортировки и без повторений. TreeSet даёт оба автоматически.

**Как работает allDigitsOdd?** `% 10` — последняя цифра. `/ 10` — убираем цифру. Повторяем до нуля.

**Нарушает ли readAll() требование задания?** Нет — readAll() только для отображения в UI. Обработка (поиск чисел с нечётными цифрами) выполняется в oddDigitNumbers() без массива.

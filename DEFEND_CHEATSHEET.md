# Шпаргалка для защиты лаб

## Навигация по лабам

| Лаба | Тема | Быстрый переход |
|------|------|-----------------|
| Лаба 2 | Коллекции (Vector, TreeSet) | [→ перейти](#лаба-2--коллекции) |
| Лаба 3 | Абстрактные классы / Интерфейсы | [→ перейти](#лаба-3--абстрактные-классы) |
| Лаба 4 | Текстовые файлы / Исключения | [→ перейти](#лаба-4--текстовые-файлы) |
| Лаба 5 | Бинарные файлы | [→ перейти](#лаба-5--бинарные-файлы) |
| Чат | Сокеты / Потоки / Swing | [→ перейти](#лаба-чат--сокеты) |
| Магазин | JDBC / SQLite | [→ перейти](#лаба-магазин--jdbc) |

---

# Лаба 2 — Коллекции

**Навигация:** [Запуск](#запуск-1) · [Что показывать](#что-показывать-1) · [Внутрянка](#внутрянка-1) · [Вопросы](#вопросы-1) · [↑ Наверх](#навигация-по-лабам)

---

## Запуск-1

```bash
cd ~/Desktop/lab_jav26/lab2
mvn -q exec:java
```

---

## Что показывать-1

1. Запустил — вывод в консоли, три блока
2. **Блок Vector:** показываешь добавление, вывод в порядке добавления, удаление по индексу
3. **Блок TreeSet:** показываешь что числа отсортированы и нет дубликатов
4. **Блок сравнения:** показываешь Iterator — обход коллекции

---

## Внутрянка-1

### Vector — зачем он

```java
Vector<Integer> vector = new Vector<>();
vector.add(5);
vector.add(2);
vector.add(5); // дубликат — добавится!
// [5, 2, 5] — порядок добавления сохранён, дубликаты разрешены
```

**Главная особенность Vector:** все методы синхронизированы — можно безопасно использовать из нескольких потоков. За это платим скоростью. ArrayList быстрее но не потокобезопасен.

Внутри Vector — обычный массив. Когда заполняется — создаётся новый массив вдвое больше, данные копируются. Это называется динамический массив.

### TreeSet — зачем он

```java
TreeSet<Integer> set = new TreeSet<>();
set.add(5);
set.add(2);
set.add(5); // дубликат — ПРОИГНОРИРУЕТСЯ
set.add(1);
// [1, 2, 5] — отсортировано, без дубликатов
```

Внутри TreeSet — красно-чёрное дерево. При каждом `add()` число встаёт на нужное место. Операции работают за O(log n) — быстро даже для миллиона элементов.

### Comparable — как TreeSet знает как сортировать

Чтобы TreeSet мог сортировать твои объекты, класс должен реализовать `Comparable`:

```java
public class Student implements Comparable<Student> {
    String name;
    int grade;

    @Override
    public int compareTo(Student other) {
        return this.grade - other.grade; // сортировка по оценке
        // отрицательное = this меньше other
        // 0 = равны (считаются одинаковыми в TreeSet!)
        // положительное = this больше other
    }
}
```

### Iterator — зачем не просто for-each

```java
Iterator<Integer> it = vector.iterator();
while (it.hasNext()) {
    int val = it.next();
    if (val < 0) {
        it.remove(); // БЕЗОПАСНО удаляем во время обхода
    }
}
```

Если использовать обычный for-each и удалять элементы — `ConcurrentModificationException`. Iterator позволяет безопасно удалять через `it.remove()`.

### equals() и hashCode() — зачем вместе

```java
// Два объекта "равны" по смыслу:
Student a = new Student("Никита", 9);
Student b = new Student("Никита", 9);

a.equals(b); // без @Override → false (сравниваются ССЫЛКИ, не данные)
             // с @Override    → true (сравниваются поля)
```

Правило: если `equals()` возвращает true — `hashCode()` должен быть одинаковым. Нарушишь — HashMap и HashSet сломаются.

---

## Вопросы-1

**Что такое коллекция?**
Контейнер для хранения группы объектов. В отличие от массива — динамически меняет размер, есть методы поиска/сортировки/удаления.

**Чем Vector отличается от ArrayList?**
Vector потокобезопасен (все методы synchronized), ArrayList нет. Vector медленнее. Сегодня если нужна потокобезопасность — используют `Collections.synchronizedList()` или `CopyOnWriteArrayList`.

**Что такое TreeSet?**
Множество без дубликатов с автоматической сортировкой. Внутри — красно-чёрное дерево. add/remove/contains работают за O(log n).

**Что такое Iterator?**
Объект для обхода коллекции. `hasNext()` — есть ли следующий, `next()` — взять следующий. Позволяет безопасно удалять во время обхода через `iterator.remove()`.

**Зачем Comparable?**
Говорит как сравнивать два объекта. TreeSet использует `compareTo()` для сортировки и проверки дубликатов. Если `compareTo()` вернул 0 — элементы считаются одинаковыми.

**Чем TreeSet отличается от HashSet?**
TreeSet сортирует (O(log n)). HashSet не сортирует но быстрее (O(1) для add/contains). HashSet работает через hashCode().

**Что такое autoboxing?**
Автопреобразование примитива в объект: `int → Integer`. При добавлении `int` в коллекцию Java сама оборачивает в `Integer`.

**Что происходит если не переопределить equals()?**
Сравниваются ссылки на объекты (адреса в памяти), а не данные. Два объекта с одинаковыми полями будут "разными".

**Почему нельзя удалять из коллекции в for-each?**
for-each использует Iterator внутри. Изменение коллекции во время итерации → `ConcurrentModificationException`. Решение: Iterator с `it.remove()`.

**Что такое generics (`<Integer>`)?**
Параметр типа. `Vector<Integer>` — Vector который хранит только Integer. Без generics можно было класть объекты любого типа и получать ClassCastException в рантайме.

---

# Лаба 3 — Абстрактные классы

**Навигация:** [Запуск](#запуск-2) · [Что показывать](#что-показывать-2) · [Внутрянка](#внутрянка-2) · [Вопросы](#вопросы-2) · [↑ Наверх](#навигация-по-лабам)

---

## Запуск-2

```bash
cd ~/Desktop/lab_jav26/lab3
mvn spring-boot:run
# открыть http://localhost:8083
```

---

## Что показывать-2

1. Вводишь массив чисел в поле
2. Нажимаешь "Сортировать Insert" → результат
3. Нажимаешь "Сортировать Selection" → тот же результат другим алгоритмом
4. Переключаешь на вкладку "Интерфейс" — то же самое через интерфейс
5. Объясняешь: один и тот же результат, разная организация кода

---

## Внутрянка-2

### Иерархия классов — два параллельных дерева

```
AbstractArray (abstract)          IArray (interface)
├── InsertSort                    ├── InsertSortI
└── SelectionSort                 └── SelectionSortI
```

В лабе специально два дерева — чтобы показать оба механизма.

### AbstractArray — что внутри

```java
public abstract class AbstractArray {
    protected int[] arr;  // поле — доступно подклассам

    public AbstractArray(int[] arr) {
        this.arr = arr.clone(); // копия — не портим оригинал
    }

    public abstract void sort(); // ОБЯЗАН переопределить подкласс

    public int[] getArray() { return arr; } // готовый метод — достаётся бесплатно
}
```

`abstract void sort()` — это контракт. InsertSort и SelectionSort обязаны написать свою реализацию. Если не напишут — ошибка компиляции.

`protected` — поле видно в подклассах, но не снаружи. Подкласс работает с `arr` напрямую.

### InsertSort — как реализует

```java
public class InsertSort extends AbstractArray {

    public InsertSort(int[] arr) {
        super(arr); // вызываем конструктор родителя
    }

    @Override
    public void sort() {
        // алгоритм сортировки вставками
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}
```

`@Override` — говорит компилятору "это переопределение метода из родителя". Если напишешь имя с опечаткой — компилятор предупредит.

### Полиморфизм — главная идея

```java
// Переменная типа AbstractArray — но внутри разные объекты
AbstractArray sorter1 = new InsertSort(arr);
AbstractArray sorter2 = new SelectionSort(arr);

sorter1.sort(); // вызовется InsertSort.sort()
sorter2.sort(); // вызовется SelectionSort.sort()
```

Один тип переменной — разное поведение. Это и есть полиморфизм. Код который работает с `AbstractArray` не знает что внутри InsertSort или SelectionSort — и не должен знать.

### IArray — интерфейс вместо класса

```java
public interface IArray {
    void sort();    // абстрактный по умолчанию
    int[] getArray();
}
```

Отличие от абстрактного класса: нет полей (только константы), нет конструктора, нет `protected`. Только контракт.

```java
public class InsertSortI implements IArray {
    private int[] arr; // поле объявляем сами — не достаётся из интерфейса

    @Override
    public void sort() { /* алгоритм */ }

    @Override
    public int[] getArray() { return arr; }
}
```

### Главная разница abstract vs interface

```java
// Абстрактный класс — один родитель:
class Foo extends AbstractArray { }          // ✅
class Foo extends AbstractArray, Bar { }     // ❌ нельзя

// Интерфейс — любое количество:
class Foo implements IArray { }              // ✅
class Foo implements IArray, Comparable, Runnable { } // ✅ можно много
```

### DoubleUnaryOperator — лямбда в действии

```java
DoubleUnaryOperator f = Math::log; // ссылка на метод = лямбда x -> Math.log(x)
double result = f.applyAsDouble(10.0); // = Math.log(10.0)
```

`DoubleUnaryOperator` — функциональный интерфейс: принимает double, возвращает double. Позволяет передавать функции как параметры.

---

## Вопросы-2

**Что такое абстрактный класс?**
Класс с `abstract`. Нельзя создать объект напрямую. Может иметь абстрактные методы (без тела) — подклассы обязаны их реализовать.

**Что такое абстрактный метод?**
Метод без тела: `abstract void sort();`. Подкласс обязан написать реализацию с `@Override`. Это контракт.

**Что такое интерфейс?**
Чистый контракт поведения. Список методов которые класс обязан реализовать. Нет полей, нет готового кода (до Java 8).

**Чем абстрактный класс отличается от интерфейса?**
Абстрактный класс может иметь поля и готовые методы. Интерфейс — только контракт. Класс наследует один класс но реализует много интерфейсов.

**Что такое полиморфизм?**
Одна переменная — разные реализации. `AbstractArray a = new InsertSort(...)` — вызов `a.sort()` запустит код InsertSort. Код не знает что внутри.

**Зачем @Override?**
Говорит компилятору что метод переопределяет родительский. Защита от опечаток: если написал неверное имя — ошибка компиляции.

**Почему нельзя наследовать два класса?**
Diamond problem — неоднозначность какой метод вызвать если оба родителя его определяют. Интерфейсы решают это — можно реализовывать много.

**Что такое super()?**
Вызов конструктора или метода родительского класса. `super(arr)` в InsertSort вызывает конструктор AbstractArray.

**Зачем protected у поля arr?**
private — не видно в подклассах. public — видно всем. protected — видно только в подклассах и пакете. Подклассы работают с arr напрямую, снаружи не трогают.

**Что такое ссылка на метод (`Math::log`)?**
Короткая запись лямбды `x -> Math.log(x)`. Используется там где ожидается функциональный интерфейс с подходящей сигнатурой.

---

# Лаба 4 — Текстовые файлы

**Навигация:** [Запуск](#запуск-3) · [Что показывать](#что-показывать-3) · [Внутрянка](#внутрянка-3) · [Вопросы](#вопросы-3) · [↑ Наверх](#навигация-по-лабам)

---

## Запуск-3

```bash
cd ~/Desktop/lab_jav26/lab4
mvn spring-boot:run
# открыть http://localhost:8084
```

---

## Что показывать-3

1. Открываешь страницу — таблица пустая
2. Нажимаешь "Загрузить" (файл `data/stations.txt` уже вписан) → таблица заполнилась
3. Вкладка "Самая загруженная" → "Найти" → показывает станцию
4. Вкладка "Сравнение" → вводишь две станции → "Сравнить"
5. Меняешь RadioButton на "Files.lines()" → снова загружаешь → результат тот же
6. **Демонстрация ошибок:** пишешь несуществующий файл → красная надпись

---

## Внутрянка-3

### Цепочка классов — как данные проходят

```
stations.txt
    ↓
StationReader.readClassic()  или  StationReader.readFunctional()
    ↓ (для каждой строки)
StationParser.parse(line)
    ↓
new Station(name, passenger, cargo)
    ↓
List<Station>
    ↓
grid.setItems(data) → таблица в браузере
```

### Почему три обёртки при чтении файла

```java
new BufferedReader(new FileReader(path.toFile()))
```

```
Файл на диске → байты
    FileReader          байты → символы (учитывает кодировку UTF-8)
        BufferedReader  символы → строки + буфер 8192 символа
            .readLine() → "Минск               45 60"
```

Без BufferedReader каждый символ = обращение к диску. С буфером — загружается блок сразу, потом берём из памяти. Быстро.

### try-with-resources — почему важно

```java
// БЕЗ — опасно:
BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
// ... если здесь исключение — br.close() никогда не вызовется
br.close(); // файл навсегда занят

// С try-with-resources — всегда закроется:
try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
    // ... даже если исключение — close() вызовется автоматически
}
```

### StationParser — два режима разбора строки

```
"Минск               45 60"
```

**Режим 1 (фиксированная ширина):** первые 20 символов = название, остаток = числа

```java
String name = line.substring(0, 20).trim(); // "Минск"
String[] nums = line.substring(20).trim().split("\\s+"); // ["45", "60"]
int p = Integer.parseInt(nums[0]); // 45
int t = Integer.parseInt(nums[1]); // 60
```

**Режим 2 (гибкий):** последние два токена = числа, остальное = название

```java
String[] tokens = line.trim().split("\\s+");
// ["Минск", "45", "60"]
int t = Integer.parseInt(tokens[tokens.length - 1]); // последний = 60
int p = Integer.parseInt(tokens[tokens.length - 2]); // предпоследний = 45
// name = всё остальное = "Минск"
```

### StationFormatException — зачем своё исключение

```java
// Без своего исключения — непонятно что случилось:
throw new Exception("abc");

// Со своим — сразу ясно: проблема с форматом данных станции:
throw new StationFormatException("Строка 3: не удалось разобрать числа: 'Минск abc xyz'");
```

Extends Exception = checked. Java заставит всех кто вызывает StationParser.parse() либо поймать, либо объявить throws.

### Два способа чтения — в чём разница

**BufferedReader (классический):**
```java
while ((line = br.readLine()) != null) {
    result.add(StationParser.parse(line));
}
```
Читаем строку, обрабатываем, следующая строка. Понятно, линейно.

**Files.lines() (функциональный):**
```java
Files.lines(path)
    .filter(s -> !s.isBlank())           // убрать пустые строки
    .map(s -> StationParser.parse(s))    // каждую строку → Station
    .collect(Collectors.toList());       // собрать в список
```
Конвейер. Ленивый — читает строку только когда нужна. При огромных файлах не загружает всё в память сразу.

### Проблема checked exception в лямбде

```java
.map(s -> {
    try {
        return StationParser.parse(s); // бросает StationFormatException (checked)
    } catch (StationFormatException e) {
        throw new RuntimeException(e); // оборачиваем в unchecked — можно бросить
    }
})
```

Лямбда в `.map()` не может бросать checked exception — функциональный интерфейс Function не объявляет throws. Обходим: оборачиваем → пробрасываем → разворачиваем снаружи.

---

## Вопросы-3

**Что такое исключение?**
Объект-ошибка который "летит" по стеку вызовов пока его не поймает catch. Позволяет обработать ошибку в нужном месте.

**Чем checked отличается от unchecked?**
Checked (extends Exception) — Java заставляет поймать или объявить throws. Unchecked (extends RuntimeException) — не обязательно. IOException — checked, NullPointerException — unchecked.

**Зачем своё исключение?**
Стандартные исключения описывают технические проблемы. Своё даёт смысл задачи — "неверный формат станции". По имени класса сразу понятно что случилось.

**Что такое try-with-resources?**
`try (ресурс = ...)` — Java автоматически вызывает close() после блока, даже при исключении. Гарантирует закрытие файла.

**Зачем BufferedReader поверх FileReader?**
FileReader читает посимвольно — медленно. BufferedReader добавляет буфер и readLine(). Без буфера каждый символ = обращение к диску.

**Что возвращает readLine() когда файл кончился?**
null. Поэтому: `while ((line = br.readLine()) != null)`.

**Что такое Stream?**
Конвейер обработки данных. filter → map → collect. Ленивый — обрабатывает данные только при терминальной операции (collect).

**Почему нельзя бросить checked в лямбде?**
Function не объявляет throws. Обходим: оборачиваем в RuntimeException → пробрасываем → разворачиваем после collect.

**В чём реальная разница двух способов чтения?**
BufferedReader — загружает весь файл, классический стиль. Files.lines() — ленивый, при огромных файлах экономит память. Результат одинаковый.

**Что такое split("\\s+")?**
Разрезать строку по одному или нескольким пробельным символам (пробел, таб). `"A  B C".split("\\s+")` → `["A", "B", "C"]`.

---

# Лаба 5 — Бинарные файлы

**Навигация:** [Запуск](#запуск-4) · [Что показывать](#что-показывать-4) · [Внутрянка](#внутрянка-4) · [Вопросы](#вопросы-4) · [↑ Наверх](#навигация-по-лабам)

---

## Запуск-4

```bash
cd ~/Desktop/lab_jav26/lab5
mvn spring-boot:run
# открыть http://localhost:8085
```

---

## Что показывать-4

1. "Создать файл…" → n=20, a=1, b=99 → "Создать"
2. Видишь все случайные числа в поле содержимого
3. "Обработать" → видишь только числа где все цифры нечётные
4. Объясняешь: 13 ✅ (цифры 1,3 нечётные), 24 ❌ (2,4 чётные), 15 ✅
5. Создаёшь файл ещё раз → числа другие (Random), результат пересчитывается
6. Показываешь что результат всегда отсортирован и без дубликатов (TreeSet)

---

## Внутрянка-4

### Текстовый vs Бинарный — главная разница

```
Текстовый файл:  число 45 хранится как символы '4' и '5' = 2 байта
Бинарный файл:   число 45 хранится как int = 4 байта (00 00 00 2D)
```

В блокноте текстовый читается нормально. Бинарный — мусор. Зато бинарный быстрее и фиксированного размера.

### DataOutputStream — запись в файл

```java
try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
    out.writeInt(45);  // записывает ровно 4 байта: 00 00 00 2D
    out.writeInt(13);  // ещё 4 байта: 00 00 00 0D
}
// Файл: [00 00 00 2D][00 00 00 0D] = 8 байт
```

Цепочка: `Files.newOutputStream` → байтовый поток в файл. `DataOutputStream` → добавляет writeInt(), writeDouble() и т.д.

### DataInputStream + EOFException — чтение

```java
try (DataInputStream in = new DataInputStream(Files.newInputStream(file))) {
    while (true) {
        int v;
        try {
            v = in.readInt();       // читает ровно 4 байта → int
        } catch (EOFException eof) {
            break;                  // файл кончился — нормально, выходим
        }
        if (allDigitsOdd(v)) result.add(v);
    }
}
```

`readInt()` ждёт 4 байта. Когда файл заканчивается — бросает `EOFException`. Это нормальный способ узнать конец бинарного файла (нет метода "проверить есть ли ещё байты" без реального чтения).

### allDigitsOdd() — алгоритм проверки цифр

```java
static boolean allDigitsOdd(int n) {
    if (n == 0) return false;         // 0 — чётная цифра
    long x = Math.abs((long) n);      // берём абсолют (для отрицательных)

    while (x > 0) {
        int d = (int)(x % 10);        // последняя цифра: 135 % 10 = 5
        if ((d & 1) == 0) return false; // чётная цифра — сразу false
        x /= 10;                      // убираем цифру: 135 / 10 = 13
    }
    return true;
}
```

Пример для 135:
```
x=135: d = 135%10 = 5 → нечётная, x = 13
x=13:  d = 13%10  = 3 → нечётная, x = 1
x=1:   d = 1%10   = 1 → нечётная, x = 0
Цикл: x=0, конец → true ✅
```

`d & 1` — побитовая проверка чётности. Последний бит: у нечётных = 1, у чётных = 0. Быстрее чем `d % 2`.

### TreeSet — почему не ArrayList

По заданию: **по возрастанию + без дубликатов**. TreeSet даёт это бесплатно:

```java
TreeSet<Integer> set = new TreeSet<>();
set.add(31); set.add(13); set.add(31); // дубликат!
// Результат: [13, 31] — отсортировано, дубликат выброшен
```

С ArrayList пришлось бы: `Collections.sort(list)` + ручное удаление дубликатов.

### Как узнать сколько чисел в файле

```java
long bytes = Files.size(file);       // размер в байтах
int count = (int)(bytes / 4);        // каждый int = 4 байта
// Integer.BYTES = 4 — константа в Java
```

Проверяем `bytes % 4 != 0` — если не кратно 4, файл битый.

---

## Вопросы-4

**Чем бинарный файл отличается от текстового?**
В текстовом числа как символы ("45" = 2 байта). В бинарном — двоичное представление (45 = 4 байта). Бинарный быстрее, фиксированный размер, нечитаем в блокноте.

**Что такое DataOutputStream/DataInputStream?**
Обёртки над байтовым потоком. Добавляют writeInt(), readInt(), writeDouble() — работают с примитивами Java напрямую.

**Что делает writeInt()?**
Записывает int как ровно 4 байта. readInt() — читает 4 байта и возвращает int.

**Что такое EOFException?**
End Of File — readInt() ждёт 4 байта, а файл кончился. Нормальный способ определить конец бинарного файла. Ловим в catch и выходим из цикла.

**Зачем TreeSet а не ArrayList?**
Задание требует сортировки и отсутствия дубликатов. TreeSet даёт оба автоматически.

**Как работает allDigitsOdd()?**
Берём цифры через `% 10` (остаток = последняя цифра). Если чётная — false. `/ 10` убирает цифру. Повторяем пока число не станет 0.

**Что значит `d & 1 == 0`?**
Побитовое И. Последний бит: нечётные = 1, чётные = 0. Быстрая проверка чётности.

**Зачем Files.createDirectories()?**
Создаёт папку data/ если её нет. Без этого запись файла упадёт — папки нет.

**Как посчитать сколько int в файле?**
`Files.size(file) / 4`. Каждый int = 4 байта. Проверяем что размер кратен 4.

**Что такое Dialog в Vaadin?**
Модальное всплывающее окно. Блокирует основную страницу. Закрывается через `dlg.close()`.

---

# Лаба Чат — Сокеты

**Навигация:** [Запуск](#запуск-5) · [Что показывать](#что-показывать-5) · [Внутрянка](#внутрянка-5) · [Вопросы](#вопросы-5) · [↑ Наверх](#навигация-по-лабам)

---

## Запуск-5

```bash
cd ~/Desktop/lab_jav26/4lab_java
javac *.java
```

Нужно 3 терминала (в VS Code: кнопка `+` рядом с названием терминала):

```bash
# Терминал 1 — ПЕРВЫМ
java ChatServer

# Терминал 2
java ChatClientGUI

# Терминал 3
java ChatClient
```

---

## Что показывать-5

1. Терминал 1: сервер запустился — "Чат-сервер запущен на порту 8088"
2. Терминал 2: GUI окно — вводишь имя "Никита"
3. Терминал 3: консоль — вводишь имя "Иван"
4. Пишешь из GUI → сообщение появляется у "Ивана" в консоли
5. Пишешь из консоли → сообщение появляется в GUI
6. Закрываешь GUI → в консоли "Никита покинул чат"

---

## Внутрянка-5

### Что такое сокет — аналогия

Сокет = телефонная трубка. Сервер поднял трубку и ждёт звонка. Клиент звонит. Соединение установлено — оба могут говорить.

```
ChatClientGUI ←——сокет——→ ChatServer ←——сокет——→ ChatClient
порт случайный            порт 8088              порт случайный
```

### Как устанавливается соединение

```java
// Сервер:
ServerSocket ss = new ServerSocket(8088); // слушаем порт 8088
Socket clientSocket = ss.accept();        // БЛОКИРУЕТСЯ — ждёт клиента

// Клиент:
Socket socket = new Socket("127.0.0.1", 8088); // подключаемся
// ← accept() разблокируется, соединение установлено
```

`127.0.0.1` = localhost = "я сам". Порт 8088 = произвольный номер (как квартира в доме).

### Как данные передаются через сокет

```java
// Сокет — это два потока: входящий и исходящий
InputStream  raw_in  = socket.getInputStream();   // байты от собеседника
OutputStream raw_out = socket.getOutputStream();  // байты к собеседнику

// Оборачиваем для удобной работы со строками:
BufferedReader in  = new BufferedReader(new InputStreamReader(raw_in));
PrintWriter    out = new PrintWriter(raw_out, true); // true = autoFlush

out.println("Привет");       // отправляем строку
String msg = in.readLine();  // получаем строку (БЛОКИРУЕТСЯ пока не придёт)
```

`autoFlush = true` — после каждого println данные сразу отправляются. Без этого могут зависнуть в буфере.

### Почему нужны потоки (Thread) на сервере

```java
while (true) {
    Socket clientSocket = serverSocket.accept(); // ждём нового клиента
    ClientHandler handler = new ClientHandler(clientSocket);
    clients.add(handler);
    new Thread(handler).start(); // новый поток для этого клиента
    // главный поток сразу возвращается к accept() — готов принять следующего
}
```

`readLine()` блокирует — поток спит пока клиент молчит. Если бы все клиенты в одном потоке — пока ждём от первого, второй не может подключиться. Каждый клиент в своём потоке — параллельно.

### ClientHandler.run() — жизнь клиента на сервере

```java
@Override
public void run() {
    try {
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        username = in.readLine(); // первая строка = имя
        broadcastMessage("📢 " + username + " присоединился!", this);

        String message;
        while ((message = in.readLine()) != null) { // ждём сообщений
            if (message.equals("/exit")) break;
            broadcastMessage("[" + username + "]: " + message, this);
        }
    } finally {
        socket.close();
        removeClient(this);
        broadcastMessage("📢 " + username + " покинул чат.", this);
    }
}
```

`finally` — выполняется **всегда**: и при нормальном выходе и при аварийном отключении (IOException). Гарантирует что клиент всегда удалится из списка.

### broadcastMessage — рассылка всем

```java
public static void broadcastMessage(String message, ClientHandler sender) {
    for (ClientHandler client : clients) {
        if (client != sender) {     // не отправляем себе
            client.sendMessage(message);
        }
    }
}
```

`client != sender` — сравниваем **ссылки**, не equals(). Важно что это буквально тот же объект, а не "равный по данным".

### CopyOnWriteArrayList — почему не ArrayList

```java
private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
```

Несколько потоков (по одному на клиента) одновременно могут вызывать `clients.add()` и `clients.remove()`. Обычный ArrayList не защищён — данные могут перемешаться. CopyOnWriteArrayList при каждом изменении создаёт копию массива — безопасно.

### Почему два потока в клиенте

```java
// Поток 1 — читает сообщения от сервера
Thread readThread = new Thread(() -> {
    while ((msg = in.readLine()) != null) {
        System.out.println(msg); // или chatArea.append(msg)
    }
});
readThread.start();

// Главный поток — ждёт ввода пользователя
while (true) {
    String input = scanner.nextLine(); // БЛОКИРУЕТ
    out.println(input);
}
```

`scanner.nextLine()` блокирует — ждёт пока пользователь напишет. `in.readLine()` тоже блокирует — ждёт сообщения. Один поток не может ждать двух вещей одновременно.

### SwingUtilities.invokeLater — почему в GUI клиенте

```java
private void appendMessage(String msg) {
    SwingUtilities.invokeLater(() -> {
        chatArea.append(msg + "\n"); // изменение UI
    });
}
```

Правило Swing: UI менять только в Event Dispatch Thread (EDT). Сетевой поток — другой поток. Прямой вызов `chatArea.append()` из сетевого потока = гонка данных = зависание. `invokeLater` ставит задачу в очередь EDT — безопасно.

---

## Вопросы-5

**Что такое сокет?**
Двухсторонний канал связи между программами по сети. ServerSocket слушает порт, Socket — сам канал. Данные через InputStream/OutputStream.

**Что такое порт?**
Число 0-65535. Как номер квартиры — один компьютер, много программ, у каждой свой порт.

**Зачем многопоточность на сервере?**
readLine() блокирует поток. Без потоков — сервер обслуживает одного клиента и не принимает других. Каждый клиент в своём потоке — работают параллельно.

**Что такое CopyOnWriteArrayList?**
Потокобезопасный список. При изменении создаёт копию массива. Нужен когда несколько потоков одновременно добавляют/удаляют клиентов.

**Что такое Runnable?**
Интерфейс с методом run(). Описывает задачу для потока. Лучше extends Thread — позволяет дополнительно наследовать другой класс.

**Что происходит при аварийном отключении клиента?**
readLine() бросает IOException. Блок finally выполняется всегда — закрывает сокет, удаляет из clients, уведомляет остальных.

**Зачем autoFlush в PrintWriter?**
`new PrintWriter(out, true)` — после каждого println данные сразу отправляются. Без этого могут зависнуть в буфере и не дойти.

**Что такое EDT в Swing?**
Event Dispatch Thread — единственный поток с правом изменять UI компоненты. invokeLater() ставит задачу в очередь EDT — безопасно из любого потока.

**Зачем два потока в клиенте?**
scanner.nextLine() и in.readLine() оба блокируют. Один поток не делает два ожидания одновременно — нужны два потока.

**Почему client != sender а не client.equals(sender)?**
Нам важно что это буквально тот же объект в памяти (та же ссылка), а не "равный по данным". != сравнивает адреса, equals() — данные.

---

# Лаба Магазин — JDBC

**Навигация:** [Запуск](#запуск-6) · [Что показывать](#что-показывать-6) · [Внутрянка](#внутрянка-6) · [Вопросы](#вопросы-6) · [↑ Наверх](#навигация-по-лабам)

---

## Запуск-6

```bash
cd ~/Desktop/lab_jav26/JavaStoreProject
mvn compile exec:java -Dexec.mainClass="com.example.App"
```

> Если путь к БД не работает — открой `App.java` и замени строку url на:
> `String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/store.db";`

---

## Что показывать-6

1. Программа запустилась, вывела одно число
2. Объясняешь: программа подключилась к SQLite базе данных
3. Выполнила SQL-запрос — соединила три таблицы через JOIN
4. Посчитала поступления минус продажи
5. Перевела упаковки в килограммы

---

## Внутрянка-6

### Архитектура JDBC

```
Java-код → JDBC API → Драйвер SQLite → Файл store.db
```

JDBC — прослойка. Один и тот же Java-код работает с любой базой данных. Меняешь строку подключения и jar-файл драйвера — и работаешь с MySQL вместо SQLite.

### Подключение к базе

```java
String url = "jdbc:sqlite:/путь/к/store.db";
//            ^^^^^^^^^^^
//            формат: jdbc:<тип_бд>:<параметры>

try (Connection conn = DriverManager.getConnection(url)) {
    // работаем с базой
} // conn.close() вызовется автоматически
```

`DriverManager` находит нужный драйвер по префиксу `jdbc:sqlite:` и открывает соединение. try-with-resources — соединение всегда закроется.

### Структура базы данных

```
product                 movement                store
--------                --------                --------
article_id (PK) ←───── article_id (FK)         store_id (PK)
name                    store_id   (FK) ──────► district
unit                    operation_type
pack_quantity           pack_count
```

Три таблицы. movement связывает product и store через внешние ключи (FK).

### SQL-запрос — разбор по частям

```sql
SELECT
  SUM(CASE WHEN m.operation_type = 'Поступление' THEN m.pack_count ELSE 0 END) AS in_packs,
  SUM(CASE WHEN m.operation_type = 'Продажа'     THEN m.pack_count ELSE 0 END) AS out_packs,
  p.unit,
  p.pack_quantity
FROM movement m
JOIN product p ON m.article_id = p.article_id   -- присоединяем товары
JOIN store   s ON m.store_id   = s.store_id     -- присоединяем магазины
WHERE p.name      = 'Творог 9% жирности'
  AND s.district  = 'Заречный';
```

**JOIN** — объединяем строки из двух таблиц где совпадают ключи. Одна строка `movement` получает данные и из `product` и из `store`.

**CASE WHEN** — условная сумма. Для каждой строки: если это поступление → берём pack_count, иначе → 0. SUM суммирует всё. Получаем итог по поступлениям и продажам в одном запросе.

```
Строка 1: Поступление, 100 упак  → CASE = 100, 0
Строка 2: Продажа,      30 упак  → CASE = 0,  30
Строка 3: Поступление,  50 упак  → CASE = 50,   0
-----
SUM:                             → in=150, out=30
```

### Чтение результата

```java
try (Statement st = conn.createStatement();
     ResultSet rs = st.executeQuery(sql)) {

    if (rs.next()) { // переходим к первой строке (наш запрос вернёт одну)
        inPacks  = rs.getInt("in_packs");
        outPacks = rs.getInt("out_packs");
        unit     = rs.getString("unit");
    }
}
```

`ResultSet` — курсор. Изначально стоит **перед** первой строкой. `rs.next()` двигает на следующую и возвращает true если она есть.

### Вычисление результата

```java
int netPacks = inPacks - outPacks; // прирост в упаковках

double packKg;
if ("кг".equals(unit)) {
    packKg = packQty;           // упаковка уже в кг
} else if ("г".equals(unit)) {
    packKg = packQty / 1000.0;  // граммы → килограммы
}

double netKg = netPacks * packKg; // прирост в килограммах
```

`"кг".equals(unit)` а не `unit.equals("кг")` — защита от NPE. Если unit == null, второй вариант упадёт, первый вернёт false.

---

## Вопросы-6

**Что такое JDBC?**
Java Database Connectivity — стандартный API для баз данных. Один код работает с MySQL, PostgreSQL, SQLite — меняешь только драйвер и строку подключения.

**Что такое SQLite?**
База данных в одном файле без отдельного сервера. Идеально для учебных задач.

**Три главных класса JDBC?**
Connection — соединение. Statement — выполнение SQL. ResultSet — результат (строки таблицы).

**Зачем try-with-resources для Connection?**
Незакрытое соединение = утечка ресурсов. try-with-resources гарантирует close() даже при исключении.

**Что такое JOIN?**
Соединение таблиц по ключу. Строки объединяются там где совпадают значения ключей. Результат содержит столбцы из обеих таблиц.

**Что делает CASE WHEN в SQL?**
Условное выражение: если условие → одно значение, иначе → другое. Вместе с SUM = условная агрегация.

**Что такое ResultSet?**
Курсор на результат запроса. rs.next() переходит к следующей строке. rs.getInt() / rs.getString() читают значения.

**Чем PreparedStatement отличается от Statement?**
Statement — статический запрос. PreparedStatement — с параметрами (`WHERE name = ?`). Защита от SQL-инъекций и быстрее при повторном выполнении.

**Что такое SQL-инъекция?**
Если вставить пользовательский ввод в SQL-строку, злоумышленник может ввести `'; DROP TABLE product; --` и удалить таблицу. PreparedStatement параметры не интерпретирует как SQL.

**Почему `"кг".equals(unit)` а не наоборот?**
Если unit == null → `unit.equals("кг")` бросит NullPointerException. `"кг".equals(null)` просто вернёт false.

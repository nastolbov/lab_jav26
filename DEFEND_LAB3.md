# Лабораторная работа №3 — Абстрактные классы и интерфейсы. Гайд для новичка

## Вариант 10: Сортировка массива двумя способами (вставками и выбором)

---

## Что делает эта лаба

Ты вводишь числа через веб-интерфейс (Vaadin), программа сортирует их двумя алгоритмами — вставками и выбором — и показывает результаты. Параллельно демонстрируется использование **абстрактных классов** и **интерфейсов** — ключевые концепции ООП.

---

## Главная идея: абстрактный класс

Представь, что ты создаёшь шаблон для разных видов сортировки. Все сортировки делают одно и то же с данными, но по-разному. Логично вынести общее в один класс.

```java
public abstract class AbstractArray {
    protected double[] data;

    public AbstractArray(double[] input) {
        this.data = input.clone(); // копируем, чтобы не менять оригинал
    }

    public abstract void sort();
    public abstract void foreach(DoubleUnaryOperator op);
    public abstract void foreachDefault();

    public double[] getData() { return data.clone(); }
}
```

**Ключевые слова:**

- `abstract class` — класс, от которого нельзя создать объект напрямую. Только наследники.
- `abstract void sort()` — метод без тела. Наследники **обязаны** его реализовать.
- `protected double[] data` — поле доступно внутри класса и всем наследникам (но не извне).
- `input.clone()` — создаём копию массива. Без этого два объекта сортировки работали бы с одним массивом и мешали друг другу.

---

## Класс Insert (сортировка вставками)

```java
public class Insert extends AbstractArray {

    public Insert(double[] input) {
        super(input); // вызываем конструктор родителя
    }

    @Override
    public void sort() {
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

    @Override
    public void foreach(DoubleUnaryOperator op) {
        for (int i = 0; i < data.length; i++) {
            data[i] = op.applyAsDouble(data[i]);
        }
    }

    @Override
    public void foreachDefault() {
        foreach(x -> x * x); // применяем операцию x² к каждому элементу
    }
}
```

**Алгоритм вставками:** берём i-й элемент (key) и вставляем его на правильное место среди уже отсортированных элементов слева. Сдвигаем вправо всё, что больше key.

Пример: `[5, 3, 1]`
- i=1: key=3, сдвигаем 5 вправо → `[3, 5, 1]`
- i=2: key=1, сдвигаем 5 и 3 вправо → `[1, 3, 5]`

**`@Override`** — аннотация. Говорит компилятору: "этот метод переопределяет метод родителя". Если ты написал имя с опечаткой, компилятор скажет об ошибке.

**`DoubleUnaryOperator`** — функциональный интерфейс из Java. Принимает один `double` и возвращает `double`. Позволяет передавать функцию как параметр.

`x -> x * x` — лямбда-выражение. Это анонимная функция: берёт x, возвращает x в квадрате.

---

## Класс Selection (сортировка выбором)

```java
public class Selection extends AbstractArray {

    public Selection(double[] input) {
        super(input);
    }

    @Override
    public void sort() {
        for (int i = 0; i < data.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < data.length; j++) {
                if (data[j] < data[minIdx]) minIdx = j;
            }
            double tmp = data[i];
            data[i] = data[minIdx];
            data[minIdx] = tmp;
        }
    }

    @Override
    public void foreach(DoubleUnaryOperator op) {
        for (int i = 0; i < data.length; i++) {
            data[i] = op.applyAsDouble(data[i]);
        }
    }

    @Override
    public void foreachDefault() {
        foreach(Math::log); // применяем натуральный логарифм к каждому элементу
    }
}
```

**Алгоритм выбором:** находим минимальный элемент в неотсортированной части и ставим его на место i-го.

Пример: `[5, 3, 1]`
- i=0: минимум=1 (индекс 2), меняем местами с индексом 0 → `[1, 3, 5]`
- i=1: минимум=3 (индекс 1), уже на месте → `[1, 3, 5]`

`Math::log` — ссылка на метод. Эквивалентно лямбде `x -> Math.log(x)`.

---

## Интерфейс IArray и классы InsertI, SelectionI

```java
public interface IArray {
    void sort();
    void foreach(DoubleUnaryOperator op);
    void foreachDefault();
    double[] getData();
}
```

**Интерфейс** — чистый контракт. Только объявления методов, без реализации (в классическом виде). Класс, реализующий интерфейс, обязан реализовать все его методы.

`implements IArray` вместо `extends AbstractArray` — разные механизмы, один результат: код, работающий с интерфейсом, не знает какой именно класс за ним стоит.

**Разница абстрактного класса и интерфейса:**
- Абстрактный класс может содержать **готовый код** (поля, конструкторы, реализованные методы)
- Интерфейс — только объявления (методов, констант)
- Класс может `extends` только **один** абстрактный класс, но `implements` **много** интерфейсов

---

## MainView.java — интерфейс пользователя

```java
@Route("")
public class MainView extends VerticalLayout {

    private final TextField input = new TextField("Введите числа через пробел");
    private final Paragraph p1 = new Paragraph(), p2 = new Paragraph(),
                            p3 = new Paragraph(), p4 = new Paragraph(),
                            p5 = new Paragraph(), p6 = new Paragraph();

    public MainView() {
        Button btn = new Button("Выполнить", e -> run());
        add(input, btn, p1, p2, p3, p4, p5, p6);
    }
```

`@Route("")` — открывается по адресу `http://localhost:8083/`.

`extends VerticalLayout` — все элементы, добавленные через `add(...)`, располагаются вертикально.

#### Метод run():

```java
private void run() {
    double[] arr = Arrays.stream(input.getValue().trim().split("\\s+"))
                         .mapToDouble(Double::parseDouble).toArray();

    AbstractArray ins = new Insert(arr);
    AbstractArray sel = new Selection(arr);

    ins.sort();
    sel.sort();

    p1.setText("Insert sorted: " + Arrays.toString(ins.getData()));
    p2.setText("Selection sorted: " + Arrays.toString(sel.getData()));

    AbstractArray ins2 = new Insert(arr);
    ins2.foreachDefault();
    p3.setText("Insert foreachDefault (x²): " + Arrays.toString(ins2.getData()));

    AbstractArray sel2 = new Selection(arr);
    sel2.foreachDefault();
    p4.setText("Selection foreachDefault (ln): " + Arrays.toString(sel2.getData()));

    IArray insI = new InsertI(arr);
    IArray selI = new SelectionI(arr);
    insI.sort();
    selI.sort();
    p5.setText("InsertI sorted: " + Arrays.toString(insI.getData()));
    p6.setText("SelectionI sorted: " + Arrays.toString(selI.getData()));
}
```

`Arrays.stream(...).mapToDouble(...).toArray()` — разбиваем строку на части по пробелам, каждую часть парсим как double, собираем в массив.

**Полиморфизм в действии:**
```java
AbstractArray ins = new Insert(arr);
ins.sort(); // вызывается Sort из Insert, а не AbstractArray
```
Переменная типа `AbstractArray`, но объект `Insert`. Когда вызываем `sort()`, Java смотрит на реальный тип объекта и вызывает метод из `Insert`. Это и есть полиморфизм — один интерфейс, разное поведение.

---

## Как показать преподу

1. Открываешь `http://localhost:8083/`
2. Вводишь числа: например `5 3 8 1 9 2`
3. Жмёшь "Выполнить"
4. Видишь 6 строк результатов:
   - Отсортированный массив методом вставок
   - Отсортированный методом выбора
   - Массив после x² (Insert)
   - Массив после ln(x) (Selection)
   - То же через интерфейс InsertI
   - То же через интерфейс SelectionI

---

## Вопросы препода и ответы

**Q: Что такое абстрактный класс? Зачем он нужен?**
A: Абстрактный класс — шаблон, от которого нельзя создать объект. Он содержит общую логику (поля, конструктор) и объявляет методы, которые каждый наследник обязан реализовать. Нужен чтобы не дублировать код: общее — в родителе, различное — в потомках.

**Q: Чем отличается `abstract class` от `interface`?**
A: Абстрактный класс может иметь поля, конструктор и реализованные методы. Интерфейс — только объявления. Класс наследует один абстрактный класс, но реализует много интерфейсов.

**Q: Что такое полиморфизм?**
A: Переменная типа "родитель" может хранить объект "потомка". При вызове метода Java выбирает нужную реализацию по реальному типу объекта в runtime. Пример: `AbstractArray x = new Insert(arr); x.sort()` — вызовется `Insert.sort()`.

**Q: Что такое `@Override`?**
A: Аннотация, сообщающая компилятору что метод переопределяет метод родителя. Компилятор проверит что такой метод действительно есть в родителе — защита от опечаток.

**Q: Что такое `DoubleUnaryOperator`?**
A: Функциональный интерфейс из Java (пакет `java.util.function`). Принимает `double`, возвращает `double`. Позволяет передавать функцию как аргумент метода.

**Q: Что такое лямбда?**
A: Анонимная функция. `x -> x * x` означает "взять x, вернуть x в квадрате". Короткая запись для реализации функционального интерфейса.

**Q: Зачем `input.clone()` в конструкторе?**
A: Чтобы каждый объект сортировки работал со своей независимой копией данных. Иначе `Insert` и `Selection` изменяли бы один и тот же массив и мешали друг другу.

**Q: Зачем `Math::log` вместо `x -> Math.log(x)`?**
A: Это одно и то же — ссылка на метод. Более краткая и читаемая запись. Java сама подставляет аргумент.

**Q: Сравни алгоритмы сортировки вставками и выбором.**
A: Оба O(n²) по времени. Вставками: сдвигаем элементы вправо, вставляем текущий на нужное место. Выбором: ищем минимум в оставшейся части, меняем с i-м. Вставками быстрее на почти отсортированных данных.

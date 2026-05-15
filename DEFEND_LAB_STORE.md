# Лаба: Работа с базой данных SQLite через JDBC

## Что делает эта лаба

Программа подключается к базе данных SQLite (`store.db`), выполняет SQL-запрос и подсчитывает **чистое увеличение запаса** товара «Творог 9% жирности» в магазинах Заречного района:
- суммирует поступления и продажи из таблицы движения товаров
- переводит результат из упаковок в килограммы

---

## Что такое JDBC?

**JDBC (Java Database Connectivity)** — стандартный API Java для работы с любыми базами данных. Принцип: один и тот же Java-код работает с MySQL, PostgreSQL, SQLite, Oracle и т.д. — нужно только поменять **драйвер** (jar-файл) и строку подключения (URL).

```
Java-код → JDBC API → Драйвер SQLite → Файл store.db
```

Три главных класса JDBC:
| Класс | Назначение |
|-------|------------|
| `Connection` | Соединение с БД |
| `Statement` | Выполнение SQL-запроса |
| `ResultSet` | Результат запроса (строки таблицы) |

---

## Строка подключения (URL)

```java
String url = "jdbc:sqlite:/Users/nikita/Desktop/Java_LAb3/JavaStoreProject/store.db";
```

Формат: `jdbc:<тип_бд>:<параметры>`

- `jdbc:` — всегда
- `sqlite:` — тип драйвера
- `/path/to/store.db` — путь к файлу базы данных

SQLite — это база данных в одном файле. Не нужен отдельный сервер — просто файл `.db` на диске.

---

## Подключение к БД

```java
try (Connection conn = DriverManager.getConnection(url)) {
    // работаем с базой
}
```

`DriverManager.getConnection(url)` — открывает соединение. Находит нужный драйвер автоматически по префиксу `jdbc:sqlite:`.

`try-with-resources` — соединение закроется автоматически. **Важно:** незакрытые соединения — утечка ресурсов. БД имеет ограниченный пул соединений.

---

## SQL-запрос — самая важная часть

```java
String sql =
    "SELECT " +
    "  SUM(CASE WHEN m.operation_type = 'Поступление' THEN m.pack_count ELSE 0 END) AS in_packs, " +
    "  SUM(CASE WHEN m.operation_type = 'Продажа'     THEN m.pack_count ELSE 0 END) AS out_packs, " +
    "  p.unit, " +
    "  p.pack_quantity " +
    "FROM movement m " +
    "JOIN product p ON m.article_id = p.article_id " +
    "JOIN store   s ON m.store_id   = s.store_id " +
    "WHERE p.name = 'Творог 9% жирности' " +
    "  AND s.district = 'Заречный';";
```

Разберём по частям.

### Структура БД (схема таблиц)

```
product              movement              store
---------            ----------            ---------
article_id (PK) ←── article_id (FK)       store_id (PK)
name                 store_id   (FK) ──→   district
unit                 operation_type
pack_quantity        pack_count
```

Три таблицы:
- `product` — справочник товаров (название, единица измерения, количество в упаковке)
- `store` — справочник магазинов (район)
- `movement` — движения товаров: каждая строка — одна операция (поступление или продажа)

### JOIN — соединение таблиц

```sql
FROM movement m
JOIN product p ON m.article_id = p.article_id
JOIN store   s ON m.store_id   = s.store_id
```

`JOIN` = «объедини строки из двух таблиц где совпадают ключи». Без JOIN у нас были бы три отдельные таблицы — после JOIN каждая строка содержит данные из всех трёх.

Схема работы `JOIN`:
```
movement (строка):  article_id=5, store_id=3, operation_type='Продажа', pack_count=10
product  (строка):  article_id=5, name='Творог 9%', unit='кг', pack_quantity=0.5
store    (строка):  store_id=3, district='Заречный'

После JOIN → одна строка: article_id=5, name='Творог 9%', unit='кг', pack_quantity=0.5, district='Заречный', operation_type='Продажа', pack_count=10
```

### WHERE — фильтрация

```sql
WHERE p.name = 'Творог 9% жирности'
  AND s.district = 'Заречный'
```

Оставляем только строки где имя товара и район совпадают.

### CASE WHEN — условная агрегация

```sql
SUM(CASE WHEN m.operation_type = 'Поступление' THEN m.pack_count ELSE 0 END) AS in_packs
```

Это «умная» сумма: складываем `pack_count` только для поступлений, для всего остального берём 0.

Шаг за шагом, представим 4 строки:
```
operation_type='Поступление', pack_count=100  → CASE = 100
operation_type='Продажа',     pack_count=30   → CASE = 0
operation_type='Поступление', pack_count=50   → CASE = 50
operation_type='Продажа',     pack_count=20   → CASE = 0
```
`SUM(...) = 100 + 0 + 50 + 0 = 150` — всего поступило 150 упаковок.

Аналогично для продаж (in_packs) — суммируем только Продажи.

### AS — псевдоним колонки

`AS in_packs` — называем результирующую колонку `in_packs`. Потом в Java используем это имя: `rs.getInt("in_packs")`.

---

## Выполнение запроса

```java
try (Statement st = conn.createStatement();
     ResultSet rs = st.executeQuery(sql)) {

    if (rs.next()) { // переходим к первой строке результата
        inPacks  = rs.getInt("in_packs");
        outPacks = rs.getInt("out_packs");
        unit     = rs.getString("unit");
        // ...
    }
}
```

`conn.createStatement()` — создаёт объект для выполнения запросов.
`st.executeQuery(sql)` — выполняет SELECT, возвращает `ResultSet`.

`ResultSet` — это курсор на результат запроса. Изначально стоит **перед** первой строкой.
- `rs.next()` — переходит к следующей строке, возвращает `true` если строка есть
- `rs.getInt("колонка")` — читает значение из текущей строки
- `rs.getString("колонка")` — читает строковое значение

Наш запрос с `SUM` всегда возвращает **одну строку** (даже если ничего нет — тогда NULL). Поэтому один вызов `rs.next()` достаточен.

---

## Вычисление результата

```java
int netPacks = inPacks - outPacks; // сколько упаковок прибавилось в запасе

// Переводим из упаковок в килограммы
if ("кг".equals(unit)) {
    packKg = packQty;           // упаковка = packQty кг
} else if ("г".equals(unit)) {
    packKg = packQty / 1000.0;  // переводим граммы в кило
}

netKg = netPacks * packKg;
```

Формула: `(поступило - продано) × вес одной упаковки = прирост запаса в кг`.

### Почему `"кг".equals(unit)` а не `unit.equals("кг")`?

Если `unit == null` и написать `unit.equals("кг")` — `NullPointerException`. А `"кг".equals(null)` просто вернёт `false`. Это стандартная Java-защита от NPE.

---

## Обработка числа из строки

```java
String packQtyStr = rs.getString("pack_quantity");
if (packQtyStr != null) {
    packQtyStr = packQtyStr.replace(',', '.');   // "0,5" → "0.5"
    packQty = Double.parseDouble(packQtyStr);
}
```

В базе число может быть записано с запятой (`0,5`) — в европейском формате. `Double.parseDouble` понимает только точку. Поэтому заменяем запятую.

---

## Вывод результата

```java
System.out.printf("Увеличение запаса Творог 9%% жирности в Заречном районе (кг): %.1f%n", netKg);
```

`%%` — экранированный символ `%`. В строках формата `printf` один `%` начинает спецификатор формата, поэтому чтобы вывести буквальный `%` нужно два.
`%.1f` — число с плавающей точкой, 1 знак после запятой.

---

## Как запустить

```bash
cd JavaStoreProject
mvn compile exec:java    # если в pom.xml настроен exec-maven-plugin
# или
javac -cp sqlite-jdbc.jar src/main/java/com/example/App.java
java  -cp sqlite-jdbc.jar:. com.example.App
```

В `pom.xml` должна быть зависимость на SQLite-драйвер:
```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.43.0.0</version>
</dependency>
```

---

## Вопросы препода и ответы

**Q: Что такое JDBC?**
A: Java Database Connectivity — стандартный API для работы с реляционными базами данных. Позволяет писать один и тот же код для разных СУБД, меняя только драйвер и строку подключения.

**Q: Что такое SQLite? Зачем он, а не MySQL/PostgreSQL?**
A: SQLite — встраиваемая база данных в одном файле, без отдельного сервера. Идеально для учебных задач, мобильных приложений, локального хранилища. MySQL/PostgreSQL требуют установки и запуска сервера.

**Q: Зачем try-with-resources для Connection?**
A: `Connection` — ресурс, который нужно закрыть после работы. `try-with-resources` гарантирует вызов `close()` даже при исключении. Незакрытые соединения — утечка ресурсов, база данных имеет лимит подключений.

**Q: Чем отличается Statement от PreparedStatement?**
A: `Statement` — статический запрос. `PreparedStatement` — запрос с параметрами: `WHERE name = ?`. Параметры передаются отдельно, БД **заранее компилирует** запрос — это быстрее при многократном выполнении. И главное: защита от SQL-инъекций. В нашем случае параметры захардкожены в строку, поэтому `Statement` достаточен.

**Q: Что такое SQL-инъекция?**
A: Если подставлять пользовательский ввод прямо в SQL-строку: `WHERE name = '" + userInput + "'"`, злоумышленник может ввести `'; DROP TABLE product; --` и удалить таблицу. `PreparedStatement` предотвращает это — параметры передаются отдельно и не интерпретируются как SQL.

**Q: Что такое JOIN?**
A: Операция соединения таблиц по ключу. `JOIN A ON A.id = B.a_id` объединяет строки таблиц, у которых совпадают значения ключей. Результат — строки со столбцами из обеих таблиц.

**Q: Что делает CASE WHEN в SQL?**
A: Условное выражение: если условие истинно — возвращает одно значение, иначе — другое. Аналог `if-else`. Вместе с `SUM` даёт «условную агрегацию» — складываем только строки удовлетворяющие условию.

**Q: Что такое ResultSet и как с ним работать?**
A: Курсор на результат SQL-запроса. Изначально стоит перед первой строкой. `rs.next()` переходит к следующей строке (возвращает true если есть). `rs.getInt()`, `rs.getString()` — читают значения из текущей строки по имени или номеру колонки.

**Q: Почему hasData, а не просто проверить что inPacks > 0?**
A: Если данных нет, `rs.next()` вернёт false и переменные останутся в нулях (inPacks=0, outPacks=0). Результат выглядел бы как «поступило 0, продано 0» — это неинформативно. Флаг `hasData` позволяет различить «нулевой результат» от «данных нет вообще».

**Q: Что означает `%.1f` в printf?**
A: Спецификатор формата: `%f` — число с плавающей точкой, `.1` — один знак после запятой. `%%` — экранированный знак процента (один `%` начинает спецификатор, два `%%` выводят буквальный `%`).

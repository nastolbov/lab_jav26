# Как запустить все лабы на macOS в VS Code

---

## Шаг 0 — Установить всё необходимое (один раз)

### 1. Java 17

Открой Терминал (Cmd+Space → «Терминал») и проверь:

```bash
java -version
```

Если вывод `java version "17..."` или `openjdk version "17..."` — всё хорошо, пропускай.

Если ошибка или версия ниже 17 — установи:

```bash
# Установи Homebrew если нет:
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Установи Java 17:
brew install openjdk@17

# Добавь в PATH (вставь в терминал и нажми Enter):
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Проверь:
java -version
```

### 2. Maven

```bash
# Проверь:
mvn -version

# Если нет — установи:
brew install maven

# Проверь ещё раз:
mvn -version
```

### 3. VS Code + расширения

Скачай VS Code с сайта code.visualstudio.com.

Открой VS Code → нажми `Cmd+Shift+X` (Extensions) → установи:

| Расширение | Зачем |
|-----------|-------|
| **Extension Pack for Java** (Microsoft) | Подсветка, автодополнение, запуск Java |
| **Spring Boot Extension Pack** (VMware) | Запуск Spring Boot / Vaadin лаб |

### 4. Скачай репозиторий

```bash
cd ~/Desktop
git clone https://github.com/nastolbov/lab_jav26.git
cd lab_jav26
git checkout claude/complete-four-labs-v1cRt
```

---

## Открыть папку в VS Code

Есть два способа:

**Способ 1 — через терминал:**
```bash
code ~/Desktop/lab_jav26
```

**Способ 2 — через меню:**
VS Code → File → Open Folder → выбери папку `lab_jav26`

Терминал в VS Code открывается через **Ctrl+`** (тильда под Esc).

---

## Лаба 2 — Коллекции (консольная программа)

**Тип:** обычный Maven-проект, без веба.

```bash
# Открой терминал в VS Code (Ctrl+`)
cd ~/Desktop/lab_jav26/lab2

# Скомпилируй и запусти одной командой:
mvn -q exec:java
```

Должен появиться вывод трёх программ с операциями над Vector и TreeSet.

**Если ошибка `Plugin not found`:**
```bash
mvn compile exec:java -Dexec.mainClass="by.bsuir.lab2.Main"
```

---

## Лаба 3 — Абстрактные классы (Vaadin веб-приложение)

**Тип:** Spring Boot + Vaadin. Запускает локальный веб-сервер.

```bash
cd ~/Desktop/lab_jav26/lab3

# Первый запуск — скачает зависимости (несколько минут, нормально):
mvn spring-boot:run
```

Подожди пока в терминале появится строка вида:
```
Started Application in 12.3 seconds
```

Затем открой браузер и перейди по адресу:
```
http://localhost:8083
```

Чтобы остановить — нажми `Ctrl+C` в терминале.

---

## Лаба 4 — Текстовые файлы (Vaadin веб-приложение)

```bash
cd ~/Desktop/lab_jav26/lab4
mvn spring-boot:run
```

Когда в терминале появится `Started Application` — открой браузер:
```
http://localhost:8084
```

---

## Лаба 5 — Бинарные файлы (Vaadin веб-приложение)

```bash
cd ~/Desktop/lab_jav26/lab5
mvn spring-boot:run
```

Когда в терминале появится `Started Application` — открой браузер:
```
http://localhost:8085
```

---

## 4lab_java — Чат (сервер + клиент)

**Тип:** обычные `.java` файлы, без Maven. Нужно три отдельных окна терминала.

```bash
# Перейди в папку
cd ~/Desktop/lab_jav26/4lab_java

# Скомпилируй все файлы:
javac *.java
```

Теперь нужно открыть **3 окна терминала**. В VS Code: Terminal → New Terminal (или `Cmd+Shift+5` чтобы разделить).

**Терминал 1 — сервер (запускай первым):**
```bash
cd ~/Desktop/lab_jav26/4lab_java
java ChatServer
```
Должно появиться: `✅ Чат-сервер запущен на порту 8088`

**Терминал 2 — GUI клиент:**
```bash
cd ~/Desktop/lab_jav26/4lab_java
java ChatClientGUI
```
Откроется окно Swing — введи имя и чати.

**Терминал 3 — консольный клиент (второй пользователь):**
```bash
cd ~/Desktop/lab_jav26/4lab_java
java ChatClient
```
Введи имя, пиши сообщения — они появятся у GUI клиента.

Чтобы выйти из консольного клиента — напиши `/exit`.

---

## JavaStoreProject — Магазин (SQLite + JDBC)

**Важно:** в файле `App.java` прописан захардкоженный путь к базе данных:
```java
String url = "jdbc:sqlite:/Users/nikita/Desktop/Java_LAb3/JavaStoreProject/store.db";
```

Это путь к компьютеру другого человека — на твоём Mac такой папки нет. Нужно исправить.

### Шаг 1 — Исправь путь в App.java

Открой файл `JavaStoreProject/src/main/java/com/example/App.java` в VS Code.

Найди строку:
```java
String url = "jdbc:sqlite:/Users/nikita/Desktop/Java_LAb3/JavaStoreProject/store.db";
```

Замени на (используй абсолютный путь к твоей папке):
```java
String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/store.db";
```

Это автоматически подставит текущую директорию, откуда запускаешь программу.

### Шаг 2 — Скомпилируй и запусти

```bash
cd ~/Desktop/lab_jav26/JavaStoreProject

# Первый раз скачает sqlite-jdbc драйвер:
mvn compile

# Запусти:
mvn exec:java -Dexec.mainClass="com.example.App"
```

Должен появиться вывод с количеством упаковок творога.

---

## Быстрая справка по командам

| Команда | Что делает |
|---------|-----------|
| `mvn -q exec:java` | Скомпилировать и запустить консольный Maven-проект |
| `mvn spring-boot:run` | Запустить Spring Boot / Vaadin приложение |
| `mvn compile` | Только скомпилировать (без запуска) |
| `mvn clean` | Удалить скомпилированные файлы (если что-то сломалось) |
| `mvn clean spring-boot:run` | Пересобрать с нуля и запустить |
| `javac *.java` | Скомпилировать все .java файлы в папке |
| `java ИмяКласса` | Запустить скомпилированный класс |
| `Ctrl+C` | Остановить запущенную программу |

---

## Типичные проблемы и решения

### «Port already in use»
Уже запущено одно Vaadin-приложение на этом порту. Останови его `Ctrl+C` или:
```bash
# Найти что занимает нужный порт (замени 8084 на нужный) и убить:
lsof -i :8084 | grep LISTEN
kill -9 <PID из вывода>
```

### «Cannot find symbol» / ошибка компиляции
```bash
mvn clean compile  # удали старые файлы и скомпилируй заново
```

### Maven скачивает зависимости очень долго
Это нормально при первом запуске — скачиваются библиотеки. Подожди. При следующем запуске всё будет в кеше.

### VS Code не видит Java / красные подчёркивания везде
1. `Cmd+Shift+P` → напиши `Java: Configure Java Runtime`
2. Убедись что Java 17 выбрана
3. `Cmd+Shift+P` → `Java: Clean Language Server Workspace` → перезапусти VS Code

### Чат: «Не удалось подключиться к серверу»
Сначала запусти `ChatServer`, только потом клиент. Если сервер не запущен — клиент сразу падает.

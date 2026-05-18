# Лабораторные работы по Java (вариант 10)

В репозитории четыре независимых Maven-проекта:

| Папка  | Тема                                            | Запуск                       |
|--------|-------------------------------------------------|------------------------------|
| `lab2` | Коллекции (Vector + TreeSet, char, ЦЕХ)          | `mvn -q exec:java`           |
| `lab3` | Абстрактные классы / интерфейсы (Vaadin)         | `mvn spring-boot:run` → http://localhost:8083 |
| `lab4` | Текстовые файлы и исключения (Vaadin)            | `mvn spring-boot:run` → http://localhost:8084 |
| `lab5` | Бинарные файлы (Vaadin)                          | `mvn spring-boot:run` → http://localhost:8085 |

## Требования

- **JDK 17** или новее (проверено на 17/21).
- **Maven 3.9+**.
- На macOS установить можно через Homebrew:
  ```bash
  brew install openjdk@17 maven
  ```
- В **VS Code** установить расширения:
  - *Extension Pack for Java* (Microsoft);
  - *Spring Boot Extension Pack* (Microsoft) — нужно для лаб 3, 4, 5.

## Как открыть в VS Code

1. `File → Open Folder…` и выбрать папку нужной лабы (`lab2`, `lab3`, `lab4` или
   `lab5`). Каждая лаба — отдельный проект с собственным `pom.xml`.
2. VS Code сам определит Maven-проект, попросит «Import Java Projects» — согласиться.
3. Дождаться, пока загрузятся зависимости (для лаб с Vaadin первый запуск
   качает frontend-ресурсы — это может занять несколько минут).

## Запуск

### Лаба 2 (консольное приложение)
```bash
cd lab2
mvn -q exec:java
```
Либо из VS Code запустить класс `by.bsuir.lab2.Main` (Run → Run Java).

### Лабы 3, 4, 5 (Vaadin)
```bash
cd lab3   # или lab4, lab5
mvn spring-boot:run
```
После старта открыть в браузере:
- Лаба 3 — http://localhost:8083
- Лаба 4 — http://localhost:8084
- Лаба 5 — http://localhost:8085

В VS Code можно также нажать «Run» над методом `main` в `Application.java`.

## Документация по каждой работе

См. файл `DOCS.md` — там по каждой лабе описано:

- условие варианта 10;
- что делает программа и какими средствами;
- ключевые классы и их назначение;
- возможные вопросы преподавателя и краткие ответы.

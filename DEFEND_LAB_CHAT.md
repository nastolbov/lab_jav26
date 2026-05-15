# Лаба: Сетевой чат на Java (Сокеты + Потоки + Swing)

## Что делает эта лаба

Многопользовательский чат:
- `ChatServer.java` — сервер, принимает подключения, рассылает сообщения всем
- `ChatClient.java` — консольный клиент
- `ChatClientGUI.java` — клиент с графическим интерфейсом (Swing)

---

## Главная идея: как работает сеть в Java

### Что такое сокет?

Сокет — это «труба» между двумя программами по сети (или на одной машине). Данные входят с одного конца, выходят с другого.

```
[ChatClientGUI] ←——сокет——→ [ChatServer] ←——сокет——→ [ChatClient]
  порт рандомный              порт 8088               порт рандомный
```

Java предоставляет два класса:
- `ServerSocket` — «ухо сервера», ждёт входящих подключений
- `Socket` — сам «провод», двухсторонний канал

### Как устанавливается соединение?

```
Сервер: ServerSocket ss = new ServerSocket(8088);  // начинает слушать порт
Сервер: Socket client = ss.accept();               // БЛОКИРУЕТСЯ — ждёт клиента

Клиент: Socket socket = new Socket("127.0.0.1", 8088); // подключается
                                                   // ← accept() возвращается ↑
```

`127.0.0.1` — это всегда «я сам» (localhost). Клиент подключается к серверу на той же машине.

---

## ChatServer.java — разбор по частям

### Поле clients

```java
private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
```

`CopyOnWriteArrayList` — особый список: при каждом изменении (add/remove) создаёт **копию** массива внутри. Зачем? Потому что несколько потоков (по одному на клиента) могут одновременно добавлять/удалять клиентов. Обычный `ArrayList` сломается при одновременном доступе из нескольких потоков.

### Главный цикл

```java
try (ServerSocket serverSocket = new ServerSocket(PORT)) {
    while (true) {
        Socket clientSocket = serverSocket.accept(); // ждём нового клиента
        ClientHandler handler = new ClientHandler(clientSocket);
        clients.add(handler);
        new Thread(handler).start(); // запускаем отдельный поток для клиента
    }
}
```

**Почему `new Thread(handler).start()`?**

`accept()` возвращает управление только когда подключился клиент. Если мы не запустим поток, сервер будет обслуживать одного клиента и не сможет принять следующего. Каждый клиент живёт в своём потоке — они работают параллельно.

### broadcastMessage — рассылка всем

```java
public static void broadcastMessage(String message, ClientHandler sender) {
    for (ClientHandler client : clients) {
        if (client != sender) {    // не отправляем автору сообщения
            client.sendMessage(message);
        }
    }
}
```

Проходим по всем подключённым клиентам и отправляем каждому. Проверка `client != sender` — сравнение **ссылок** (не equals), нам важно что это буквально тот же объект.

### ClientHandler — внутренний класс

```java
static class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
```

`implements Runnable` — говорит Java: этот класс можно запустить в потоке (`new Thread(handler)`). Метод `run()` будет выполняться в отдельном потоке.

`BufferedReader in` — читаем текстовые строки от клиента.
`PrintWriter out` — пишем текстовые строки клиенту.

### Чтение/запись через сокет

```java
in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
out = new PrintWriter(socket.getOutputStream(), true);
```

Сокет — это байтовый поток (`InputStream`/`OutputStream`). Мы оборачиваем:
- `InputStreamReader` — байты → символы (с учётом кодировки)
- `BufferedReader` — символы → строки (метод `readLine()`)
- `PrintWriter(stream, true)` — `true` означает **autoFlush**: каждый `println` сразу отправляется, не ждёт буфера

### Протокол (соглашение клиент-сервер)

```
Клиент отправляет первой строкой: имя пользователя
Потом в цикле: текст сообщения
Команды: /exit или /quit — выйти
```

Сервер читает первую строку как имя, остальные — как сообщения.

### run() — жизнь клиента на сервере

```java
@Override
public void run() {
    try {
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        username = in.readLine(); // первое сообщение = имя
        broadcastMessage("📢 " + username + " присоединился!", this);

        String message;
        while ((message = in.readLine()) != null) { // цикл чтения
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

`in.readLine()` **блокируется** — поток «засыпает» и ждёт новой строки от клиента. Как только клиент отправил сообщение — поток «просыпается», обрабатывает, снова засыпает. Это называется **блокирующий I/O**.

`finally` — выполнится **всегда**, даже если клиент аварийно отключился (выбросил исключение). Это гарантирует что мы всегда уберём клиента из списка и закроем сокет.

---

## ChatClient.java — консольный клиент

```java
try (Socket socket = new Socket(SERVER_IP, PORT)) {
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    Scanner scanner = new Scanner(System.in);

    out.println(username); // отправляем имя серверу

    // Поток для чтения входящих сообщений
    Thread readThread = new Thread(() -> {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) { ... }
    });
    readThread.start();

    // Главный поток — отправка сообщений
    while (true) {
        String input = scanner.nextLine(); // ждём ввода пользователя
        if ("/exit".equals(input)) break;
        out.println(input);
    }
}
```

**Почему два потока?**

Клиент должен одновременно:
1. Ждать ввода от пользователя (`scanner.nextLine()` — блокирует)
2. Получать сообщения от сервера (`in.readLine()` — блокирует)

Один поток не может делать два дела одновременно. Поэтому:
- **Поток читающий** (`readThread`) — слушает сервер и печатает в консоль
- **Главный поток** — ждёт ввода и отправляет на сервер

---

## ChatClientGUI.java — Swing-клиент

```java
public class ChatClientGUI extends JFrame {
    private JTextArea chatArea;     // область с историей чата
    private JTextField messageField; // поле ввода
    private PrintWriter out;         // отправка на сервер
    private BufferedReader in;       // получение от сервера
    private Socket socket;
```

`JFrame` — главное окно приложения.
`JTextArea` — многострочное текстовое поле (нередактируемое — только чтение).
`JTextField` — однострочное поле ввода.

### Компоновка окна

```java
add(new JScrollPane(chatArea), BorderLayout.CENTER); // чат в центре
add(inputPanel, BorderLayout.SOUTH);                  // поле ввода снизу
```

`BorderLayout` — раскладка: CENTER (заполняет всё свободное место), NORTH/SOUTH/EAST/WEST (фиксированная полоса по краю). Самая частая раскладка в Swing.

`JScrollPane(chatArea)` — оборачивает `JTextArea` в прокручиваемую область. Когда сообщений много — появляется полоса прокрутки.

### Почему DO_NOTHING_ON_CLOSE?

```java
setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
addWindowListener(new WindowAdapter() {
    @Override
    public void windowClosing(WindowEvent e) {
        exitGracefully(); // наш метод — сначала /exit, потом System.exit
    }
});
```

Если бы стояло `EXIT_ON_CLOSE` — программа закрылась бы немедленно без отправки `/exit` серверу. Сервер не узнал бы что клиент ушёл (пока не получит IOException). С `DO_NOTHING_ON_CLOSE` мы сами контролируем закрытие: сначала сообщаем серверу, потом закрываем.

### Важнейшая строка — SwingUtilities.invokeLater

```java
private void appendMessage(String msg) {
    SwingUtilities.invokeLater(() -> {
        chatArea.append(msg + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    });
}
```

**Правило Swing:** все изменения UI должны происходить **только в Event Dispatch Thread (EDT)** — специальном потоке Swing. Сетевой поток (где читаем сообщения) — это другой поток. Прямой вызов `chatArea.append()` из чужого потока — это **гонка данных**, программа может зависнуть или упасть.

`SwingUtilities.invokeLater(задача)` — ставит задачу в очередь EDT. EDT выполнит её в свою очередь, безопасно.

`setCaretPosition(document.getLength())` — прокручивает чат вниз (к последнему сообщению).

---

## Как запустить

```bash
# Терминал 1 — сервер
cd 4lab_java
javac ChatServer.java
java ChatServer

# Терминал 2 — GUI клиент
javac ChatClientGUI.java
java ChatClientGUI

# Терминал 3 — ещё один клиент (консольный)
javac ChatClient.java
java ChatClient
```

---

## Вопросы препода и ответы

**Q: Что такое сокет?**
A: Двухсторонний канал связи между двумя программами через сеть. В Java — класс `Socket`. Сервер создаёт `ServerSocket` и ждёт через `accept()`. Клиент создаёт `Socket` и указывает IP и порт сервера.

**Q: Зачем многопоточность на сервере?**
A: Каждый `in.readLine()` блокирует выполнение пока клиент не пришлёт строку. Если обслуживать клиентов последовательно — пока ждём от первого, второй не может подключиться. Каждый клиент получает свой поток — они работают параллельно.

**Q: Что такое CopyOnWriteArrayList и зачем он здесь?**
A: Потокобезопасный список. При добавлении/удалении создаёт копию внутреннего массива. Нужен потому что несколько потоков (ClientHandler'ов) одновременно могут вызывать `clients.add()` и `clients.remove()`. Обычный ArrayList не защищён от этого.

**Q: Что такое Runnable? Зачем implements Runnable вместо extends Thread?**
A: `Runnable` — интерфейс с одним методом `run()`. Класс, реализующий его, описывает задачу для потока. Предпочтительнее `extends Thread` — позволяет дополнительно наследовать другой класс (Java не поддерживает множественное наследование классов).

**Q: Почему BufferedReader поверх InputStreamReader?**
A: `socket.getInputStream()` — байтовый поток. `InputStreamReader` переводит байты в символы. `BufferedReader` добавляет буфер и метод `readLine()` для чтения целой строки. Без буфера `readLine()` делал бы системный вызов на каждый символ — медленно.

**Q: Что такое EDT в Swing? Зачем invokeLater?**
A: Event Dispatch Thread — единственный поток, который имеет право изменять компоненты Swing. Если изменять UI из другого потока — гонка данных, непредсказуемое поведение. `invokeLater` ставит задачу в очередь EDT, гарантируя безопасность.

**Q: Что происходит если клиент аварийно отключится (не отправит /exit)?**
A: `in.readLine()` бросит `IOException`. Блок `finally` выполнится в любом случае — закроет сокет, удалит из `clients`, разошлёт уведомление.

**Q: Что значит autoFlush в PrintWriter?**
A: `new PrintWriter(out, true)` — второй аргумент `true` означает что после каждого `println` данные немедленно отправляются, не копятся в буфере. Без этого сообщения могут зависнуть в буфере и не дойти до получателя.

**Q: Почему клиент запускает отдельный поток для чтения?**
A: `scanner.nextLine()` блокирует главный поток ожидая ввода пользователя. Одновременно нужно читать сообщения от сервера (`in.readLine()` тоже блокирует). Один поток не может делать два блокирующих ожидания одновременно — значит нужен второй поток.

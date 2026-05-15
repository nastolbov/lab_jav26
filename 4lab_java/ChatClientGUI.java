import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatClientGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String username;

    public ChatClientGUI() {
        // Шаг 1: ввод имени
        username = JOptionPane.showInputDialog(this, "Введите ваше имя:", "Регистрация", JOptionPane.QUESTION_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            username = "Гость";
        }

        // Шаг 2: настройка окна
        setTitle("💬 Чат — " + username);
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // важно! не EXIT_ON_CLOSE пока не отключились
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Menlo", Font.PLAIN, 13));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        messageField = new JTextField();
        JButton sendBtn = new JButton("➤");
        sendBtn.setPreferredSize(new Dimension(60, 30));
        sendBtn.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Шаг 3: подключение
        connectToServer();

        // Закрытие по крестику — корректное
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGracefully();
            }
        });

        setVisible(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 8088);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Отправляем имя
            out.println(username);

            // Запускаем поток ПРОСЛУШКИ входящих сообщений (главное!)
            Thread readerThread = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        // Любое сообщение — просто выводим
                        appendMessage(msg);
                    }
                } catch (IOException e) {
                    appendMessage("⚠️ Соединение с сервером потеряно.");
                }
            });
            readerThread.start();

            appendMessage("✅ Подключено к серверу. Добро пожаловать, " + username + "!");
            appendMessage("📢 Вы вошли в чат.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "❌ Не удалось подключиться к серверу.\nУбедитесь, что сервер запущен.",
                "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            // Отправляем на сервер
            out.println(text);
            // Локально показываем своё сообщение
            appendMessage("[" + username + "]: " + text);
            messageField.setText("");
            messageField.requestFocus();
        }
    }

    private void exitGracefully() {
        try {
            if (out != null) out.println("/exit");
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        System.exit(0);
    }

    private void appendMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientGUI());
    }
}
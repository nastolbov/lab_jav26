import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private static final int PORT = 8088;
    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ Чат-сервер запущен на порту " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("👤 Новый клиент подключился: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Ошибка сервера: " + e.getMessage());
        }
    }

    // Рассылка сообщения всем клиентам
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) { // Не отправляем отправителю
                client.sendMessage(message);
            }
        }
    }

    // Удаление клиента из списка
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("🚪 Клиент отключился: " + client.getUsername());
    }

    // Получить список пользователей
    public static String getClientsList() {
        StringBuilder sb = new StringBuilder();
        sb.append("👥 Онлайн: ");
        for (ClientHandler client : clients) {
            sb.append(client.getUsername()).append(", ");
        }
        return sb.length() > 3 ? sb.substring(0, sb.length() - 2) : "Никто не в сети";
    }

    // Внутренний класс для обработки каждого клиента
    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Первое сообщение — имя пользователя
                username = in.readLine();
                if (username == null || username.trim().isEmpty()) {
                    username = "Аноним" + System.currentTimeMillis() % 1000;
                }
                System.out.println("📝 Зарегистрирован: " + username);

                // Уведомление всем о новом пользователе
                broadcastMessage("📢 " + username + " присоединился к чату!", this);

                // Главный цикл обработки сообщений
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equals("/exit") || message.equals("/quit")) {
                        break;
                    }
                    String fullMessage = "[" + username + "]: " + message;
                    System.out.println(fullMessage);
                    broadcastMessage(fullMessage, this);
                }

            } catch (IOException e) {
                System.err.println("⚠️ Ошибка при обработке клиента: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                removeClient(this);
                broadcastMessage("📢 " + username + " покинул чат.", this);
            }
        }

        public String getUsername() {
            return username;
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
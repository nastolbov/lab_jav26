import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_IP = "localhost"; // Можно заменить на IP сервера
    private static final int PORT = 8088;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT)) {
            System.out.println("✅ Подключение к серверу...");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            // Регистрация
            System.out.print("Введите ваше имя: ");
            String username = scanner.nextLine();
            out.println(username); // Отправляем имя серверу

            // Поток для чтения сообщений от сервера
            Thread readThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.err.println("❌ Соединение с сервером разорвано.");
                }
            });
            readThread.start();

            // Цикл отправки сообщений
            System.out.println("💬 Вы можете писать сообщения. Для выхода введите /exit или /quit");
            while (true) {
                String input = scanner.nextLine();
                if ("/exit".equals(input) || "/quit".equals(input)) {
                    break;
                }
                out.println(input);
            }

        } catch (IOException e) {
            System.err.println("❌ Не удалось подключиться к серверу: " + e.getMessage());
        }
    }
}
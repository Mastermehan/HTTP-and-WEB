package Server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class ServerRunnable implements Runnable {

    private final Socket socket;
    private final List<String> validPaths;

    public ServerRunnable(Socket socket, List<String> validPaths) {
        this.socket = socket;
        this.validPaths = validPaths;
    }

    @Override
    public void run() {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1

            final String requestLine = in.readLine();  //метод считывания строк
            final String[] parts = requestLine.split(" "); //разбиваем строку по пробелу- получаем массив

            if (parts.length != 3) {
                // just close socket
                socket.close();
                return;
            }

            final String path = parts[1];
            if (!validPaths.contains(path)) {
                out.write((  //метод для записи одиночных байтов или массива байтов (отвечают за вывод данных).
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes()); // кодирует данную строку в последовательность байтов,сохраняет
                // результат в новый массив байтов.
                out.flush();//используется, чтобы принудительно записать в целевой поток данные, которые могут
                // кэшироваться в текущем потоке. Актуально при использовании буферизации и/или нескольких
                // объектах потоков, организованных в цепочку.
                socket.close(); //закрываем
                return;
            }

            final Path filePath = Path.of(".", "public", path); //путь файла
            final String mimeType = Files.probeContentType(filePath); //(тип)метод probeContentType (Path),
            // который «проверяет тип содержимого файла» посредством использования «установленных
            // реализаций FileTypeDetector»


            if (path.equals("/classic.html")) {
                final String template = Files.readString(filePath);
                final byte[] content = template.replace( //метод замены метки time(сами придумали) на текущее время
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
                socket.close();
                return;
            }

            final long length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

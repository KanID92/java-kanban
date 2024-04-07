package server;

import adapter.DateTimeFormatterAdapter;
import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handler.*;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    HttpServer httpServer;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(DateTimeFormatter.class, new DateTimeFormatterAdapter())
                .create();
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.createContext("/tasks", new TaskHandler(manager, gson, DEFAULT_CHARSET));
        httpServer.createContext("/epics", new EpicHandler(manager, gson, DEFAULT_CHARSET));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson, DEFAULT_CHARSET));
        httpServer.createContext("/history", new HistoryHandler(manager, gson, DEFAULT_CHARSET));
        httpServer.createContext("/prioritized", new PriorityHandler(manager, gson, DEFAULT_CHARSET));
    }

    public static void main(String[] args) {
        HttpTaskServer httpServer = new HttpTaskServer(Managers.getDefault());
        httpServer.manager.createTask(new Task(/* №1 */
                "Тестовый таск №1",
                "Описание тестового таска №1",
                "2024-04-01T20:00:00",
                60));
        httpServer.manager.createTask(new Task(/* №2 */
                "Тестовый таск №2",
                "Описание тестового таска №12",
                "2024-05-01T21:00:00",
                800));

        httpServer.start();
    }

    public void start() {
        httpServer.start();
        System.out.println("Запущен сервер на порту: " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Остановлен сервер на порту: " + PORT);
    }


}

package server;

import adapter.DateTimeFormatterAdapter;
import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    HttpTaskServer httpServer;
    TaskManager manager;
    Gson gson;

    @BeforeEach
    void init() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
        httpServer = new HttpTaskServer(manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(DateTimeFormatter.class, new DateTimeFormatterAdapter())
                .create();

        Task testTask1 = new Task( /* №1 */
                "Тестовый таск ID№2",
                "Описание тестового таска №1",
                "2024-05-01T20:00:00",
                60);
        manager.createTask(testTask1);

        Epic testEpic2 = new Epic( /* №2 */
                "Тестовый epic. ID№2",
                "Описание тестового эпика №2");
        manager.createEpic(testEpic2);

        SubTask subTask3 = new SubTask( /* №3 */
                "Тестовая подзадача. ID№3",
                "Описание тестовой подзадачи №3", testEpic2.getId(), "2024-10-02T01:00:00", 600);
        manager.createSubtask(subTask3);

        SubTask subTask4 = new SubTask( /* №4 */
                "Тестовая подзадача. ID№4",
                "Описание тестовой подзадачи №4", testEpic2.getId(), "2024-07-05T14:00:00", 130);
        manager.createSubtask(subTask4);

        manager.getTaskByID(1);
        manager.getSubtaskByID(4);

        httpServer.start();

    }

    @AfterEach
    void stopServer() {
        httpServer.stop();
    }

    @Test
    void shouldGetTaskList() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Task> taskList = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertNotNull(taskList);
        assertEquals(manager.getTaskList().size(), taskList.size(), "Проверка размера листа");
        Task task1 = taskList.get(0);
        assertEquals(manager.getTaskByID(1), task1, "Проверка идентичности таска");
    }

    @Test
    void shouldGetTask() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final Task task = gson.fromJson(response.body(), Task.class);

        assertNotNull(task);
        assertEquals(manager.getTaskByID(1), task, "Проверка идентичности таска");
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        assertNull(manager.getTaskByID(1));
    }

    @Test
    void shouldGetEpicList() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Epic> epicList = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
        }.getType());

        assertNotNull(epicList);
        assertEquals(manager.getEpicList().size(), epicList.size(), "Проверка размера листа");
        Epic epic2 = epicList.get(0);
        assertEquals(manager.getEpicByID(2), epic2, "Проверка идентичности эпика");
    }

    @Test
    void shouldGetEpic() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final Epic epic = gson.fromJson(response.body(), Epic.class);

        assertNotNull(epic);
        assertEquals(manager.getEpicByID(2), epic, "Проверка идентичности таска");
    }

    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        assertNull(manager.getEpicByID(2));
        assertNull(manager.getSubtaskByID(3));
        assertNull(manager.getSubtaskByID(4));

    }

    @Test
    void shouldGetSubtaskList() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<SubTask> subTaskList = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
        }.getType());

        assertNotNull(subTaskList);
        assertEquals(manager.getSubtaskList().size(), subTaskList.size(), "Проверка размера листа");
        SubTask subtask3 = subTaskList.get(0);
        SubTask subtask4 = subTaskList.get(1);
        assertEquals(manager.getSubtaskByID(3), subtask3, "Проверка идентичности сабтаска");
        assertEquals(manager.getSubtaskByID(4), subtask4, "Проверка идентичности сабтаска");
    }

    @Test
    void shouldGetSubTask() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final SubTask subTask = gson.fromJson(response.body(), SubTask.class);

        assertNotNull(subTask);
        assertEquals(manager.getSubtaskByID(3), subTask, "Проверка идентичности сабтаска");
    }

    @Test
    void shouldDeleteSubtask() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        assertNull(manager.getSubtaskByID(3));
    }

    @Test
    void shouldGetHistoryList() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Task> historyList = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertNotNull(historyList);
        assertEquals(manager.getHistory().size(), historyList.size(), "Проверка размера листа");
        Task task = historyList.get(0);
        assertEquals(manager.getTaskByID(1), task, "Проверка идентичности таска");
    }

    @Test
    void shouldGetPrioritizeList() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Task> prioritizedList = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertNotNull(prioritizedList);
        assertEquals(manager.getPrioritizedTasks().size(), prioritizedList.size(), "Проверка размера листа");
        Task task1 = prioritizedList.get(0);
        assertEquals(manager.getTaskByID(1), task1, "Проверка идентичности таска");
    }


}

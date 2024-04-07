package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TaskHandler implements HttpHandler {

    final Gson gson;
    final TaskManager manager;
    final Charset charset;
    final ExceptionHandler exceptionHandler;

    public TaskHandler(TaskManager manager, Gson gson, Charset charset) {
        this.manager = manager;
        this.gson = gson;
        this.charset = charset;
        this.exceptionHandler = new ExceptionHandler(this.gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            String method = httpExchange.getRequestMethod();
            String[] pathSplit = httpExchange.getRequestURI().getPath().split("/");
            int id = -1;
            if (pathSplit.length > 2) {
                id = Integer.parseInt(pathSplit[2]);
            }

            switch (method) {
                case "GET":
                    if (id == -1) {
                        String response = gson.toJson(manager.getTaskList());
                        writeGetResponse(httpExchange, response);
                        break;
                    }
                    Task taskToGet = manager.getTaskByID((id));
                    if (taskToGet != null) {
                        String response = gson.toJson(taskToGet);
                        writeGetResponse(httpExchange, response);
                    } else {
                        throw new NotFoundException("Задача с id:" + id + " не найдена");
                    }
                    break;
                case "POST":
                    if (httpExchange.getRequestBody().available() != 0) {
                        String taskStr = new String(
                            httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task postTask = gson.fromJson(taskStr, Task.class);
                        if (postTask.getId() == 0) {
                            manager.createTask(postTask);
                            httpExchange.sendResponseHeaders(201, 0);
                        } else {
                            manager.updateTask(postTask);
                            httpExchange.sendResponseHeaders(201, 0);
                        }
                    } else {
                        throw new BadRequestException("Пустое тело запроса");
                    }
                    break;
                case "DELETE":
                    if (id == -1) {
                        manager.deleteAllTasks();
                        httpExchange.sendResponseHeaders(204, 0);
                    } else {
                        manager.deleteTaskByID(id);
                        httpExchange.sendResponseHeaders(204, 0);
                    }
                    break;
                default:
                    System.out.println("Неверный формат запроса");
                    httpExchange.sendResponseHeaders(400, 0);

            }
        } catch (Exception exception) {
            exceptionHandler.handle(httpExchange, exception);
        }
    }

    protected void writeGetResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, 0);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    }
}

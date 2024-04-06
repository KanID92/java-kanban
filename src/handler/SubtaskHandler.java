package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.SubTask;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends TaskHandler implements HttpHandler {

    public SubtaskHandler(TaskManager manager, Gson gson, Charset charset) {
        super(manager, gson, charset);
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
                        String response = gson.toJson(manager.getSubtaskList());
                        writeGetResponse(httpExchange, response);
                    } else {
                        String response = gson.toJson(manager.getSubtaskByID(id));
                        writeGetResponse(httpExchange, response);
                    }
                    break;
                case "POST":
                    String subtaskStr = new String(
                            httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    if (id == -1) {
                        manager.createSubtask(gson.fromJson(subtaskStr, SubTask.class));
                        System.out.println("Подзадача добавлена");
                        httpExchange.sendResponseHeaders(201, 0);
                    } else {
                        manager.updateSubtask(gson.fromJson(subtaskStr, SubTask.class));
                        System.out.println("Задача обновлена");
                        httpExchange.sendResponseHeaders(201, 0);
                    }
                    break;
                case "DELETE":
                    if (id == -1) {
                        manager.deleteAllSubtasks();
                        System.out.println("Все подзадачи удалены");
                        httpExchange.sendResponseHeaders(204, 0);
                    } else {
                        manager.deleteSubtaskByID(id);
                        System.out.println("Удалена подзадача с id: " + id);
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

}

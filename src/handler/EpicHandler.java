package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends TaskHandler implements HttpHandler {

    public EpicHandler(TaskManager manager, Gson gson, Charset charset) {
        super(manager, gson, charset);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            String method = httpExchange.getRequestMethod();
            String[] pathSplit = httpExchange.getRequestURI().getPath().split("/");
            int id = -1;
            boolean isEpicSubtasksNeed = false;
            if (pathSplit.length > 2) {
                id = Integer.parseInt(pathSplit[2]);
            }
            if (pathSplit.length == 4) {
                isEpicSubtasksNeed = true;
            }

            switch (method) {
                case "GET":
                    if (id == -1) {
                        String response = gson.toJson(manager.getEpicList());
                        writeGetResponse(httpExchange, response);
                        break;
                    }

                    Epic epicToGet = manager.getEpicByID((id));
                    if (epicToGet != null) {
                        if (!isEpicSubtasksNeed) {
                            String response = gson.toJson(epicToGet);
                            writeGetResponse(httpExchange, response);
                        } else {
                            String response = gson.toJson(manager.getEpicSubtasks((id)));
                            writeGetResponse(httpExchange, response);
                        }
                    } else {
                        throw new NotFoundException("Эпик с id:" + id + " не найден");
                    }
                    break;
                case "POST":
                    if (httpExchange.getRequestBody().available() != 0) {
                        String epicStr = new String(
                                httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic postEpic = gson.fromJson(epicStr, Epic.class);
                        if (postEpic.getId() == 0) {
                            manager.createEpic(postEpic);
                            httpExchange.sendResponseHeaders(201, 0);
                        } else {
                            manager.updateEpic(postEpic);
                            httpExchange.sendResponseHeaders(201, 0);
                        }
                    } else {
                        throw new BadRequestException("Пустое тело запроса");
                    }
                    break;
                case "DELETE":
                    if (id == -1) {
                        manager.deleteAllEpics();
                        httpExchange.sendResponseHeaders(204, 0);
                    } else {
                        manager.deleteEpicByID(id);
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

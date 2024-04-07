package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.BadRequestException;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import exceptions.ValidationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ExceptionHandler { //Обрабатывает исключения в зависимости от их типа
    final Gson gson;

    public ExceptionHandler(Gson gson) {
        this.gson = gson;
    }

    public void handle(HttpExchange exchange, ManagerSaveException e) throws IOException {
        System.out.println("Ошибка при создании или сохранения файла данных");
        writeResponse(exchange, gson.toJson(e), 400);
    }

    public void handle(HttpExchange exchange, ValidationException e) throws IOException {
        System.out.println("Время выполнения создаваемой или обновляемой задачи пересекается с существующей");
        writeResponse(exchange, gson.toJson(e), 406);
    }

    public void handle(HttpExchange exchange, NumberFormatException e) throws IOException {
        System.out.println("Получен неверный формат идентификатора.");
        writeResponse(exchange, gson.toJson(e), 400);
    }

    public void handle(HttpExchange exchange, NotFoundException e) throws IOException {
        System.out.println("Задача не найдена.");
        writeResponse(exchange, gson.toJson(e), 404);
    }

    public void handle(HttpExchange exchange, BadRequestException e) throws IOException {
        System.out.println("Пустое тело запроса");
        writeResponse(exchange, gson.toJson(e), 400);
    }

    public void handle(HttpExchange exchange, Exception e) throws IOException {
        System.out.println("Неизвестная ошибка.");
        writeResponse(exchange, gson.toJson(e), 500);

    }

    protected void writeResponse(HttpExchange exchange, String response, int responseCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(responseCode, 0);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    }
}

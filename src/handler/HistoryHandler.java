package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;

public class HistoryHandler extends TaskHandler implements HttpHandler {

    public HistoryHandler(TaskManager manager, Gson gson, Charset charset) {
        super(manager, gson, charset);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            String method = httpExchange.getRequestMethod();
            if (method.equals("GET")) {
                String response = gson.toJson(manager.getHistory());
                writeGetResponse(httpExchange, response);
            } else {
                System.out.println("Неверный формат запроса");
                httpExchange.sendResponseHeaders(400, 0);
            }

        } catch (Exception exception) {
            exceptionHandler.handle(httpExchange, exception);
        }
    }
}

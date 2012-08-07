package ru.concerteza.util.handlers;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import static ru.concerteza.util.string.CtzConstants.UTF8;

/**
 * User: alexey
 * Date: 8/8/12
 */
public abstract class JsonHandler<T> implements RequestHandler {
    private final Gson gson;

    public JsonHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Map<String, String> urlParams) throws Exception {
        Reader reader = new InputStreamReader(request.getInputStream(), UTF8);
        T input = gson.fromJson(reader, paramsClass());
        handle(input, response);
    }

    protected abstract void handle(T input, HttpServletResponse response) throws Exception;

    protected abstract Class<T> paramsClass();
}

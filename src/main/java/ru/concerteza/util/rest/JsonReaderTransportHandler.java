package ru.concerteza.util.rest;

import com.alexkasko.rest.handlers.TransportHandler;
import com.google.gson.Gson;
import org.springframework.beans.factory.BeanFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * User: alexkasko
 * Date: 7/13/13
 */
public class JsonReaderTransportHandler implements TransportHandler<JsonReaderHandler> {

    private final BeanFactory bf;
    private final Gson gson;

    public JsonReaderTransportHandler(BeanFactory bf, Gson gson) {
        this.bf = bf;
        this.gson = gson;
    }

    @Override
    public void handle(Class<? extends JsonReaderHandler> handlerClass, HttpServletRequest request, HttpServletResponse response, Map<String, String> urlParams) throws Exception {
        response.setContentType("application/json");
        JsonReaderHandler ha = bf.getBean(handlerClass);
        // fire handler
        Object res = ha.handle(request.getReader());
        // write not null results to client
        if(null != res) gson.toJson(res, response.getWriter());
    }
}

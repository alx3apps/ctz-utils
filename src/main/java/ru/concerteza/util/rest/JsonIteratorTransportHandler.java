package ru.concerteza.util.rest;

import com.alexkasko.rest.handlers.TransportHandler;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.springframework.beans.factory.BeanFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.concerteza.util.json.JsonParseIterator.jsonParseIterator;

/**
 * User: alexkasko
 * Date: 7/12/13
 */
public class JsonIteratorTransportHandler implements TransportHandler<JsonIteratorHandler> {

    private final BeanFactory bf;
    private final Gson gson;

    public JsonIteratorTransportHandler(BeanFactory bf, Gson gson) {
        checkNotNull(bf);
        checkNotNull(gson);
        this.bf = bf;
        this.gson = gson;
    }

    /**
     * Streaming input JSON array as iterator to handler
     *
     * @param handlerClass application handler class
     * @param request request
     * @param response response
     * @param urlParams named parameters from request path
     * @throws Exception on any app exception
     */
    @SuppressWarnings("unchecked") // handler input class
    @Override
    public void handle(Class<? extends JsonIteratorHandler> handlerClass, HttpServletRequest request,
                       HttpServletResponse response, Map<String, String> urlParams) throws Exception {
        response.setContentType("application/json");
        JsonIteratorHandler ha = bf.getBean(handlerClass);
        Iterator<?> iter = jsonParseIterator(gson, request.getReader(), ha.inputClass());
        Object res = ha.handle(iter);
        if(null != res) gson.toJson(res, response.getWriter());
    }
}

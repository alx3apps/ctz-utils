package ru.concerteza.util.rest;

import com.alexkasko.rest.handlers.TransportHandler;
import com.google.gson.Gson;
import org.springframework.beans.factory.BeanFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Generic JSON tarnsport handler
 *
 * @author alexkasko
 * Date: 5/22/13
 */

public class JsonTransportHandler implements TransportHandler<JsonHandler> {

    private final BeanFactory bf;
    private final Gson gson;

    public JsonTransportHandler(BeanFactory bf, Gson gson) {
        checkNotNull(bf);
        checkNotNull(gson);
        this.bf = bf;
        this.gson = gson;
    }

    /**
     * Instantiate handler class, got input class form it, parses input object from request,
     * gives it to handlers, writes results to response as JSON
     *
     * @param handlerClass application handler class
     * @param request request
     * @param response response
     * @param urlParams named parameters from request path
     * @throws Exception on any app exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public void handle(Class<? extends JsonHandler> handlerClass, HttpServletRequest request,
                       HttpServletResponse response, Map<String, String> urlParams) throws Exception {
        response.setContentType("application/json");
        // obtain handler instance, may be singleton, from DI etc
        JsonHandler ha = bf.getBean(handlerClass);
        // parse input object from request body
        Class<?> clazz = ha.inputClass();
        final Object in = Void.class == clazz ? null : gson.fromJson(request.getReader(), clazz);
        // fire handler
        Object res = ha.handle(in);
        // write not null results to client
        if(null != res) gson.toJson(res, response.getWriter());
    }
}


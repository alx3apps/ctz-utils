package ru.concerteza.util.handlers;

import org.apache.commons.lang.UnhandledException;
import ru.concerteza.util.namedregex.NamedMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * User: alexey
 * Date: 4/4/12
 */
public class HandlersDispatcher {

    private final HandlersProvider provider;
    private final List<HandlersMappingEntry> handlers;
    private final boolean sendExceptionsToClient;


    public HandlersDispatcher(HandlersProvider provider, boolean sendExceptionsToClient, List<HandlersMappingEntry> handlers) {
        this.sendExceptionsToClient = sendExceptionsToClient;
        this.handlers = handlers;
        this.provider = provider;
    }

    public void dispatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            dispatchInternal(req, resp);
        } catch(Exception e) {
            if(sendExceptionsToClient) throw new UnhandledException(e);
            else sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void dispatchInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        for(HandlersMappingEntry en : handlers) {
            NamedMatcher matcher = en.matcher(req.getPathInfo());
            if(matcher.matches()) {
                RequestHandler ha = provider.get(en.handlerClass());
                ha.handleRequest(req, resp, matcher.namedGroups());
                return;
            }
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void sendError(HttpServletResponse resp, int code) {
        try {
            resp.sendError(code);
        } catch(IOException e) {
            throw new UnhandledException(e);
        }
    }
}

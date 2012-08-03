package ru.concerteza.util.handlers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.UnhandledException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

/**
 * User: alexey
 * Date: 4/4/12
 */
public class HandlersDispatcher {

    private final List<RequestHandler> handlers;
    private boolean sendExceptionsToClient = true;

    public HandlersDispatcher(RequestHandler... handlers) {
        this.handlers = ImmutableList.copyOf(handlers);
    }

    public HandlersDispatcher(List<RequestHandler> handlers) {
        this.handlers = ImmutableList.copyOf(handlers);
    }

    public HandlersDispatcher setSendExceptionsToClient(boolean sendExceptionsToClient) {
        this.sendExceptionsToClient = sendExceptionsToClient;
        return this;
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
        for(RequestHandler ha : handlers) {
            Matcher matcher = ha.pattern().matcher(req.getPathInfo());
            if(matcher.matches()) {
                ha.handleRequest(req, resp);
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

//    @SuppressWarnings("unchecked")
//    private Map<String, String> extractParams(HttpServletRequest req, NamedMatcher matcher) {
//        Map<String, String> res = new HashMap<String, String>();
//        // extract get and multipart post params
//        Enumeration<String> names = req.getParameterNames();
//        while(names.hasMoreElements()) {
//            String na = names.nextElement();
//            checkArgument(!res.containsKey(na), "Duplicate param: '%s'", na);
//            res.put(na, req.getParameter(na));
//        }
//        // extract path params
//        for(Map.Entry<String, String> en : matcher.namedGroups().entrySet()) {
//            String name = en.getKey();
//            checkArgument(!res.containsKey(name), "Duplicate param: '%s'", name);
//            res.put(name, en.getValue());
//        }
//        return res;
//    }
//
//    private Object createPojo(RequestHandler ha, Map<String, String> params) throws IllegalAccessException {
//        Class<?> clazz = ha.paramsClass();
//        // instantiate
//        final Object res = CtzReflectionUtils.callDefaultConstructor(clazz, ha);
//        // set fields
//        List<Field> fields = collectFields(clazz, fieldsPredicate);
//        checkArgument(fields.size() == params.size(), "Input params: '%s' doesn't match object fields: '%S'", params, fields);
//        for(Field fi : fields) {
//            String value = params.get(fi.getName());
//            checkArgument(null != value, "No param found for field: '%s', params: '%s'", fi.getName(), params);
//            Object parsed = gson.fromJson(value, fi.getType());
//            if(!fi.isAccessible()) fi.setAccessible(true);
//            fi.set(res, parsed);
//        }
//        return res;
//    }
//
//    private class FieldsPredicate implements Predicate<Field> {
//        @Override
//        public boolean apply(Field input) {
//            int modifiers = input.getModifiers();
//            return !Modifier.isStatic(modifiers) &&
//                    !Modifier.isFinal(modifiers) &&
//                    !input.isSynthetic();
//        }
//    }
}

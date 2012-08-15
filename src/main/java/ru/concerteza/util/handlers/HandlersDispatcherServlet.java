package ru.concerteza.util.handlers;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;
import ru.concerteza.util.string.CtzConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static ru.concerteza.util.string.CtzConstants.UTF8;

/**
 * User: alexey
 * Date: 3/18/12
 */
public class HandlersDispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 7041995029366447857L;
    private String dispatcherBeanName;

    @Override
    public void init() throws ServletException {
        this.dispatcherBeanName = this.getServletConfig().getInitParameter("dispatcherBeanName");
    }

    @Override
    public void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    @Override
    public void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    private void dispatch(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding(UTF8);
        resp.setCharacterEncoding(UTF8);
        dispatcher().dispatch(req, resp);
    }

    private HandlersDispatcher dispatcher() {
        BeanFactory ctx = (BeanFactory) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        final HandlersDispatcher res;
        if(null != dispatcherBeanName) {
            res = ctx.getBean(dispatcherBeanName, HandlersDispatcher.class);
        } else {
            res = ctx.getBean(HandlersDispatcher.class);
        }
        return res;
    }
}

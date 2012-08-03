package ru.concerteza.util.handlers;

import org.springframework.beans.factory.BeanFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        dispatch(req, res);
    }

    public void doPost (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        dispatch(req, res);
    }

    private void dispatch(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // dispatch
        HandlersDispatcher dispatcher = obtainDispatcher();
        dispatcher.dispatch(req, res);
    }

    private HandlersDispatcher obtainDispatcher() {
//      WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE replaced by literal to remove spring-web dependency
        BeanFactory ctx = (BeanFactory) getServletContext().getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
        final HandlersDispatcher res;
        if(null != dispatcherBeanName) {
            res = ctx.getBean(dispatcherBeanName, HandlersDispatcher.class);
        } else {
            res = ctx.getBean(HandlersDispatcher.class);
        }
        return res;
    }
}

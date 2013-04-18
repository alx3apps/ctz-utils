package ru.concerteza.util.net.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: alexkasko
 * Date: 4/18/13
 */
public class ExceptionLoggingHandler extends SimpleChannelHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String moduleName;

    public ExceptionLoggingHandler(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("Error in module: [" + moduleName + "]", e.getCause());
    }
}

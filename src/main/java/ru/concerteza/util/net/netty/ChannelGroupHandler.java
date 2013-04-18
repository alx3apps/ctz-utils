package ru.concerteza.util.net.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;

/**
 * Handler implementation, that keeps all open channel in channel group for proper shutdown
 *
 * @author alexkasko
 *         Date: 4/18/13
 */
public class ChannelGroupHandler extends SimpleChannelUpstreamHandler {
    private final ChannelGroup group;

    public ChannelGroupHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        group.add(e.getChannel());
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        group.remove(e.getChannel());
        super.channelClosed(ctx, e);
    }
}

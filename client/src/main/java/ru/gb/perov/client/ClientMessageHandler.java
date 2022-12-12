package ru.gb.perov.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.perov.server.AbstractMessage;

public class ClientMessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {
    private final OnMessageReceived callback;
    public ClientMessageHandler(OnMessageReceived callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage abstractMessage) throws Exception {
        callback.OnReceive(abstractMessage);

    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {

    }
}

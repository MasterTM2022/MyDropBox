package ru.gb.perov.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage abstractMessage) throws Exception {
        log.debug("Received: " + abstractMessage);
        ctx.writeAndFlush(abstractMessage);
    }
}

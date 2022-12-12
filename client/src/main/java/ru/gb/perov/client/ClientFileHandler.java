package ru.gb.perov.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.gb.perov.server.FileUploadFile;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ClientFileHandler extends SimpleChannelInboundHandler<Object> {
    private int start = 0;
    private static final List<Channel> channels = new ArrayList<>();
    private static int newClientIndex;
    private String clientName;



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected...");
        channels.add(ctx.channel());
        clientName = "Client #" + newClientIndex++;

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client disconnected...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileUploadFile ef) {
            byte[] bytes = ef.getBytes();
            int byteRead = ef.getEndPos();
            String md5 = ef.getFile_md5();//имя файла
//            String path = file_dir + File.separator + md5;
//            File file = new File(path);
//            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
//            randomAccessFile.seek(start);
//            randomAccessFile.write(bytes);
//            start = start + byteRead;
//            if (byteRead > 0) {
//                ctx.writeAndFlush(start);
//            } else {
//                randomAccessFile.close();
//                ctx.close();
//            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("Received: " + o);
        String out = String.format("[%s]: %s\n", clientName, o);
        for (Channel c : channels) {
            c.writeAndFlush(out);
        }


        ByteBuf buf = (ByteBuf) o;
        while (buf.readableBytes() > 0) {
            System.out.print((char)buf.readByte());
        }
        buf.release();
    }

    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
package ru.gb.perov.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class ServerApp {
    private static final String HOST = "localhost";
    private static final int PORT = 8190;

    public static void main(String[] args) {
        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelFuture future = bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new MessageHandler(),
                                    new FileHandler()
                            );
                        }
                    }).bind(PORT).sync();
            log.debug("Server started...");

            Path clientShare = Path.of("files" + File.separator + "Dir_For_Clients" + File.separator); //выделяем на сервере папку для клиента
            if (Files.notExists(clientShare)) {                                                             //зашита одна папка, функционал выделения
                new File(clientShare.toString()).mkdirs();                                                  //папок для каждого клиента не предусмотрен
            }
            future.channel().closeFuture().sync(); //block

        } catch (Exception e) {
            log.error("e=", e);
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
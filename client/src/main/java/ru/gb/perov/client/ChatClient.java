package ru.gb.perov.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;

@Slf4j
class ChatClient {
    private SocketChannel channel;
    private int byteRead;
    private volatile int start = 0;
    private volatile int lastLength = 0;
    private final static int PORT = 8190;
    private final static String HOST = "localhost";
    public RandomAccessFile randomAccessFile;
    private FileUploadFile fileUploadFile;


    public class FileUploadClient {
        public void connect(int port, String host, final FileUploadFile fileUploadFile) throws Exception {
            new Thread(() -> {

                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    channel = socketChannel;
                                    socketChannel.pipeline().addLast(new ObjectEncoder());
                                    socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                                    socketChannel.pipeline().addLast(new FileUploadClientHandler(fileUploadFile));
                                }
                            });
                    ChannelFuture future = bootstrap.connect(host, port).sync();
                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    log.debug("Exception...");
                } finally {
                    group.shutdownGracefully();
                }
            }).start();
        }

        public void sendMessage(String msg) {
            channel.writeAndFlush(msg);
        }

        //    private final ChatController controller;
        private volatile static boolean flagAuth = false;
        private final int PAUSE_TO_SLEEP_SEC = 20;
        private final int FPS = 2;

//    public ChatClient(ChatController controller) {
//        this.controller = controller;
//    }

//    public void openConnection() throws IOException {
//        socket = new Socket("localhost", 8190);
//        in = new DataInputStream(socket.getInputStream());
//        out = new DataOutputStream(socket.getOutputStream());
//        new Thread(() -> {
//            try {
//                if (waitAuth()) {
//                    readMessages();
//                }
//            } catch (IOException e) {
//                System.out.println("Клиент был закрыт по тайм-ауту...");
////                e.printStackTrace();
//            } finally {
//                closeConnection();
//                System.exit(0);
//            }
//        }).start();
//    }

//    private boolean waitAuth() throws IOException {
//        new Thread(() -> {
//            int timer = PAUSE_TO_SLEEP_SEC * FPS;
//            while (timer >= 0) {
//                try {
//                    Thread.sleep(1000/FPS);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                double var = 1.0 * timer / PAUSE_TO_SLEEP_SEC / FPS;
//                controller.setProgress(var);
//                timer -= 1;
//            }
//
//            if (!flagAuth) {
//                try {
//                    Platform.runLater(() -> controller.showError("Слишком долго вспоминатете параметры входа...\nСейчас клиент будет закрыт"));
//                    Thread.sleep(3_000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                sendMessage(END);
//                System.exit(0);
//            }
//        }).start();
//
//        while (true) {
//            final String message = in.readUTF();
//            final Command command = Command.getCommand(message);
//            final String[] params = command.parse(message);
//            if (command == AUTHOK) { //   /authok nick1
//                flagAuth = true;
//                final String nick = params[0];
//                controller.setNickOnForm(nick);
//                controller.setAuth(true);
//                controller.addMessage("Успешная авторизация под ником " + nick);
//                return true;
//            }
//
//            if (command == ERROR) {
//                Platform.runLater(() -> controller.showError(params[0]));
//                continue;
//            }
//
//            controller.setAuth(false);
//        }
//    }

//    private void closeConnection() {
//        if (in != null) {
//            try {
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (out != null) {
//            try {
//                out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (socket != null) {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private void readMessages() throws IOException {
//        while (true) {
//            final String message = in.readUTF();
//            final Command command = Command.getCommand(message);
//
//            if (END == command) {
//                controller.setAuth(false);
//                break;
//            }
//
//            String[] params = command.parse(message);
//
//            if (ERROR == command) {
//                String messageError = params[0];
//                Platform.runLater(() -> controller.showError(messageError));
//                continue;
//            }
//            if (MESSAGE == command) {
//                controller.addMessage(params[0]);
//                continue;
//            }
//
//            if (command == APROOVE_CHANGE_NICK) {
//                controller.setNewNickOnForm();
//                continue;
//            }
//
//            if (command == REFUSE_CHANGE_NICK) {
//                controller.setOldNickOnForm();
//                continue;
//            }
//
//            if (CLIENTS == command || CHANGE_NICK == command) {
////                Arrays.stream(params).forEach(System.out::println);
//                Platform.runLater(() -> controller.updateClientList(params));
//                continue;
//            }
//        }
//    }

//    private void sendMessage(String message) {
//        try {
//            out.writeUTF(message);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public void sendMessage(Command command, String... params) {
//        sendMessage(command.collectMessage(params));
//    }
    }
}

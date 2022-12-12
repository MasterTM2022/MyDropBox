package ru.gb.perov.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.nio.file.Files.*;

public class NioServer {
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final ByteBuffer buf;

    private String currentDir;

    public NioServer(int port) throws IOException {

        buf = ByteBuffer.allocate(5);
        currentDir = String.valueOf(Paths.get(".").toAbsolutePath().normalize());
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT, "Hallo world!");

        while (serverChannel.isOpen()) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            try {
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        handleAccept();
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    iterator.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        StringBuilder msg = new StringBuilder();
        while (true) {
            int read = channel.read(buf);
            if (read == -1) {
                channel.close();
                return;
            }
            if (read == 0) {
                break;
            }
            buf.flip();
            while (buf.hasRemaining()) {
                msg.append((char) buf.get());
            }
            buf.clear();
        }
//        String response = "Hallo " + msg + key.attachment();
//        channel.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));

        processMessage(channel, msg.toString());
    }


    private void processMessage(SocketChannel channel, String msg) throws IOException {

        String command = msg.trim().substring(0, (msg.trim().indexOf(' ') == -1 ? msg.trim().length() : msg.trim().indexOf(' ')));
        String argument = msg.trim().indexOf(' ') == -1 ? "" : msg.trim().substring(msg.trim().indexOf(' ') + 1);
        switch (command) {
            case "ls":
                Path dir = Path.of(currentDir);
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                    for (Path file : stream) {
                        if (Files.isDirectory(file)) {
                            responce(channel, "[" + file.getFileName() + "]\n\r", false);
                        } else {
                            responce(channel, file.getFileName() + "\n\r", false);
                        }
                    }
                    responce(channel, "\n\r", true);
                } catch (IOException | DirectoryIteratorException e) {
                    e.printStackTrace();
                }
                break;
            case "cat":
                if (Files.exists(Path.of(currentDir + "\\" + argument))) {
                    Path path = Path.of(currentDir + "\\" + argument);
                    List<String> list = Files.readAllLines(path);
                    for (String str : list)
                        responce(channel, str + "\n\r", false);
                    responce(channel, "\n\r", false);
                } else {
                    responce(channel, "File " + argument + " doesn't exist \n\r", true);
                }
                responce(channel, "\n\r", true);
                break;
            case "cd":
                if (argument.lastIndexOf(":") > -1) {
                    if (Files.exists(Path.of(argument))) {
                        currentDir = argument;
                        responce(channel, "Current dir is [" + currentDir + "]\n\r", true);
                    }
                } else if (Files.exists(Path.of(currentDir + "\\" + argument))) {
                    currentDir = currentDir + "\\" + argument;
                    responce(channel, "Current dir is [" + currentDir + "]\n\r", true);
                } else {
                    responce(channel, "Dir [" + argument + "] doesn't exist\n\r", true);
                }
                break;
            case "touch":
                if (exists(Path.of(currentDir + "\\" + argument))) {
                    responce(channel, "Fle (or dir) with name " + argument + " already exists. Create new filename!\n\r", false);
                    responce(channel, "", true);
                } else {
                    try {
                        createFile(Path.of(currentDir + "\\" + argument));
                        responce(channel, "File " + argument + " was created successfully!\n\r", false);
                        responce(channel, "", true);
                    } catch (Exception e) {
                        responce(channel, "Name of new file contains illegal character. File was not created. Check it!!!\n\r", false);
                        responce(channel, "", true);
                    }
                }
                break;
            case "mkdir":
                if (exists(Path.of(currentDir + "\\" + argument + "\\"))) {
                    responce(channel, "Dir (or file) with name " + argument + " already exists. Create new dirname!\n\r", false);
                    responce(channel, "", true);
                } else {
                    try {
                        createDirectory(Path.of(currentDir + "\\" + argument + "\\"));
                        responce(channel, "Dir " + argument + " was created successfully!\n\r", false);
                        responce(channel, "", true);
                    } catch (Exception e) {
                        responce(channel, "Name of new dir contains illegal character. Dir was not created. Check it!!!\n\r", false);
                        responce(channel, "", true);
                        e.printStackTrace();
                    }
                }
                break;
            case "..":
                if (currentDir.lastIndexOf(":") + 1 != currentDir.length()) {
                    currentDir = currentDir.substring(0, currentDir.lastIndexOf("\\"));
                    responce(channel, "Current dir is [" + currentDir + "]\n\r", true);
                } else {
                    responce(channel, "Current dir is top-level [" + currentDir + "]\n\r", true);
                }
                break;
            default:
                responce(channel, "Unknown command\n\r", true);
        }

    }

    private void responce(SocketChannel channel, String answer, boolean prefix) {
        try {
            if (answer.endsWith(":]\n\r")) {
                channel.write(ByteBuffer.wrap(answer.getBytes(StandardCharsets.UTF_8)));
                channel.write(ByteBuffer.wrap((answer.substring(answer.lastIndexOf('[') + 1, answer.lastIndexOf(']')) + "> ").getBytes(StandardCharsets.UTF_8)));
            } else {
                channel.write(ByteBuffer.wrap((answer + (prefix ? " " + currentDir.substring(currentDir.lastIndexOf("\\")) + "> " : "")).getBytes(StandardCharsets.UTF_8)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAccept() throws IOException {
        System.out.println("Client accepted...");
        SocketChannel socketChannel;
        socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, "Hallo world!\n");
    }

    public static void main(String[] args) {
        try {
            new NioServer(8189);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
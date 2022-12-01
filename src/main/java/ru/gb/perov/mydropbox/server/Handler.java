package ru.gb.perov.mydropbox.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Handler implements Runnable {
    private boolean running;
    private final Socket socket;
    private final byte[] buf;
    private final InputStream is;
    private final OutputStream os;
    private final static String pathStorage = "files/server/";


    public Handler(Socket socket) throws IOException {
        running = true;
        buf = new byte[8192];
        this.socket = socket;
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                int read = is.read(buf);
                String message = new String(buf, 0, read).trim();
                if (message.equals("quit")) {
                    String disconnectionMessage = "Client disconnected\n";
                    os.write(disconnectionMessage.getBytes(StandardCharsets.UTF_8));
                    System.out.println(disconnectionMessage);
                    close();
                    break;
                }
                System.out.println("Received: " + message);
                os.write((message + "\n").getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

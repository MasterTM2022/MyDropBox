package ru.gb.perov.mydropbox.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.time.Instant;

public class IoNet implements Closeable {
    private final Callback callback;
    private final Socket socket;
    private final InputStream is;
    private final OutputStream os;
    private final byte[] buf;


    public IoNet(Callback callback, Socket socket) throws IOException {
        this.callback = callback;
        this.socket = socket;
        is = socket.getInputStream();
        os = socket.getOutputStream();
        buf = new byte[8192];
        Thread readTread = new Thread(this::readMessages);
        readTread.setDaemon(true);
        readTread.start();
    }

    public void sendMsg(String msg) throws IOException {
            os.write(msg.getBytes(StandardCharsets.UTF_8));
            os.flush();
    }

    private void readMessages() {
        try {
            while (true) {
                int read;
                read = is.read(buf);
                String msg = new String(buf, 0, read).trim();
                callback.onReceive(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File file) {
        System.out.println(file.getAbsolutePath());
        byte[] buf = new byte[8192];
//        long start = Instant.now().getEpochSecond();
        try (FileInputStream is = new FileInputStream(file)) {
//            int read;
//            try (FileOutputStream os = new FileOutputStream(file.getName())) {
//                while ((read = is.read(buf)) != -1) {
//                    os.write(buf, 0, read);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        long end = Instant.now().getEpochSecond();
//        System.out.println("Time: " + (end- start) + " ms.");
    }

    private void receiveFile() {

    }

    @Override
    public void close() {
        try {
            sendMsg("quit");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

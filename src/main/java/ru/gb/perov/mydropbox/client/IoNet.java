package ru.gb.perov.mydropbox.client;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IoNet implements Closeable {
    private final Callback callback;
    private final Socket socket;
    private final InputStream is;
    private final OutputStream os;
    //    private final DataInputStream dis;
//    private final DataOutputStream dos;
    private final byte[] buf;


    public IoNet(Callback callback, Socket socket) throws IOException {
        this.callback = callback;
        this.socket = socket;
        is = socket.getInputStream();
        os = socket.getOutputStream();
//        dis = (DataInputStream) socket.getInputStream();
//        dos = (DataOutputStream) socket.getOutputStream();
        buf = new byte[8192];
        Thread readTread = new Thread(this::readMessages);
        readTread.setDaemon(true);
        readTread.start();
    }

    public void sendMsg(String msg) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF("~msg");

        os.write(msg.getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    private void readMessages() {
        try {
            while (true) {
                int read;
                read = is.read(buf);
                String msg = new String(buf, 0, read).trim();
                if (!msg.equals("~msg") && !msg.equals("~file")) {
                    callback.onReceive(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File file) {
        System.out.println("Start sending " + file.getAbsolutePath());
        int read;
        try (FileInputStream fis = new FileInputStream(file)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF("~file");
//            dos.flush();

            dos.writeLong(file.length());
//            dos.flush();

            dos.writeUTF(file.getName());
//            dos.flush();
            System.out.printf("Sending file: %s (%s bytes)%n", file.getName(), String.format("%,d", file.length()));

            byte[] buf = new byte[8192];
            while ((read = fis.read(buf)) != -1) {
                os.write(buf, 0, read);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

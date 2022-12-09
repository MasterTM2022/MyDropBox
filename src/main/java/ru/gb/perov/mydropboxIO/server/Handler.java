package ru.gb.perov.mydropboxIO.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Handler implements Runnable {
    private boolean running;
    private final Socket socket;
    private final byte[] buf;
    private final InputStream is;
    private final OutputStream os;

    private final static String pathStorage = "files/server/"; //папка должна быть создана заранее, проверка на ее существование не предусмотрена


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
                try {
                    DataInputStream dis = null;
                    dis = new DataInputStream(socket.getInputStream());
                    String typeCommand = dis.readUTF();
                    System.out.println(typeCommand);
                    if (typeCommand.equals("~msg")) {

                        int read = is.read(buf);
                        String message = new String(buf, 0, read).trim();
                        if (message.equalsIgnoreCase("/quit")) {
                            String disconnectionMessage = "Client disconnected\n";
                            os.write(disconnectionMessage.getBytes(StandardCharsets.UTF_8));
                            System.out.println(disconnectionMessage);
                            close();
                            break;
                        }
                        System.out.println("Received: " + message);
                        os.write((message + "\n").getBytes(StandardCharsets.UTF_8));
                    }
                    if (typeCommand.equals("~file")) {
                        dis = new DataInputStream(socket.getInputStream());
                        long size = dis.readLong();
                        String fileName = dis.readUTF();
                        System.out.printf("Receiving file: %s (%s bytes)%n", fileName, String.format("%,d", size));

                        fileName = checkDuplicates(fileName);

                        try (FileOutputStream fos = new FileOutputStream(pathStorage + fileName, true)) {
                            int read = 0;
                            byte[] buf = new byte[8192];
                            while (size > 0 && (read = is.read(buf, 0, (int) Math.min(buf.length, size))) != -1) {
                                fos.write(buf, 0, read);
                                size -= read;
                            }
                            fos.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
}

    private String checkDuplicates(String fileName) {
        File file = new File(pathStorage + fileName);
        int index = 2;
        String bodyName = "";
        String extension = "";

        while (file.exists()) {
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                bodyName = fileName.substring(0, i);
                extension = fileName.substring(i + 1);
            }
            System.out.println("File with name «" + file.getName() + "» already exists.");
            fileName = bodyName + " (" + index++ + ")." + extension;
            file = new File(pathStorage + fileName);
            System.out.println("File with new name «" + fileName + "» will be created");
        }
        return fileName;
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

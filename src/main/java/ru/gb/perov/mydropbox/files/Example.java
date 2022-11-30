package ru.gb.perov.mydropbox.files;

import java.io.*;
import java.time.Instant;

public class Example {
    public static void main(String[] args) {
        File file = new File("files/car.jpg");
        byte[] buf = new byte[8192];
        long start = Instant.now().getEpochSecond();
        try (FileInputStream is = new FileInputStream(file)) {
            int read;
            try (FileOutputStream os = new FileOutputStream("files/copy.jpg")) {
                while ((read = is.read(buf)) != -1) {
                    os.write(buf, 0, read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = Instant.now().getEpochSecond();
        System.out.println("Time: " + (end- start) + " ms.");
    }
}

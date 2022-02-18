package test;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Receiver {

    public static void main(String[] args) {
        /*Transmitter transmitter = new Transmitter();

        transmitter.messageReceive((msg) -> {
            System.out.println(msg);
        });*/

        Path currentPath = Paths.get(System.getProperty("user.home"));
        Path fileName = currentPath.resolve("kunce-v-tehnologiya-soloda-i-piva_f62d96649ae.pdf");
        if (Files.exists(fileName))
            System.out.println(fileName);
        sendFile(fileName);
    }

    public static void sendFile(Path fileName) {
        final int BUFFER_SIZE = 8192;
        FileOutputStream fos = null;
        try (FileInputStream fin = new FileInputStream(fileName.toString())){
            fos = new FileOutputStream("kunce_copy.pdf");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] bytes = new byte[BUFFER_SIZE];
            long size = Files.size(fileName);
//            int sizePart = (int) size / BUFFER_SIZE;
//            if (size % BUFFER_SIZE != 0) sizePart++;
            while (size > 0) {
                int i = fin.read(bytes);
                bos.write(bytes, 0, i);
                size -= i;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

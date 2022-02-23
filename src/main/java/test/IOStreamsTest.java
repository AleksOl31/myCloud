package test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class IOStreamsTest {

    public static void main(String[] args) {
//        copyFile();
        testIO();
    }

    private static void testIO() {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream("test.dat")));
            for (int i = 0; i < 100; i++) {
                out.writeLong(i);
            }
            out.close();
            DataInputStream in = new DataInputStream(new BufferedInputStream(
                    new FileInputStream("test.dat")
            ));
            byte[] bytes = new byte[8];
           /* for (int i = 0; i < 100; i++) {
                System.out.print(in.read(bytes));
                System.out.print(" ");
                System.out.println(Arrays.toString(bytes));
            }*/

            while (in.available() != 0) {
                System.out.print(in.read(bytes));
                System.out.print(" ");
                System.out.println(bytes[7]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } /*finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

    }

    private static final void copyFile() {
        Path currentPath = Paths.get(System.getProperty("user.home"));
        Path fileName = currentPath.resolve("Bryus_Ekkel_-_Filosofia_Java_4-e_polnoe_izdanie.pdf");
        if (Files.exists(fileName)) {
            System.out.println(fileName);
            sendFile(fileName);
        }
    }

    public static void sendFile(Path fileName) {
        final int BUFFER_SIZE = 8192;
        BufferedOutputStream bos = null;
        try (FileInputStream fin = new FileInputStream(fileName.toFile())) {

            bos = new BufferedOutputStream(new FileOutputStream("kunce_copy.pdf"));
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
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

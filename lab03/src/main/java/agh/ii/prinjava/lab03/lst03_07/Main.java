package agh.ii.prinjava.lab03.lst03_07;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static agh.ii.prinjava.lab03.lst03_06.Main.textLines;

/**
 * <ul>
 *     <li>The abstract {@link InputStream} is the root class for reading binary data</li>
 *     <li>The abstract {@link OutputStream} is the root class for writing binary data</li>
 * </ul>
 * <p>
 * {@link java.io.FileInputStream} / {@link FileOutputStream} are for reading/writing bytes from/to files.
 * All the methods in these classes are inherited from {@link InputStream} and {@link OutputStream}.
 *
 * <p>The {@link InputStream} and {@link OutputStream} classes implement the {@link AutoCloseable} interface.
 * The AutoClosable interface defines the close() method that closes a resource. Any object of the {@code AutoClosable}
 * type can be used with the try-with-resources syntax for automatic closing.
 */
public class Main {



    private static final String COUNTER = "count.txt";

    private static int reading() {
        try (var in = new BufferedReader(new FileReader(COUNTER))) {
            String line = in.readLine();
            if (line != null) {
                return Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            // ignore and return 0
        }
        return 0;
    }

    private static int writting(int count) {

        //try (var fout = new BufferedWriter(new FileWriter(demoFileName1, StandardCharsets.UTF_8))) {
        try (var fout = Files.newBufferedWriter(Path.of(COUNTER), StandardCharsets.UTF_8)) {

            for (var line : textLines) {
                fout.write(line);
            }

            fout.flush();

            System.out.println("Writing to " + COUNTER + " completed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /*private static void writting(int count) {
        try (var out = new BufferedWriter(new FileWriter(COUNTER))) {
            out.write(Integer.toString(count));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    private static final double[] dbls = {1.1, 2.2, 3.3, 4.4, 5.5};
    private static final String demoFileName = "dbls.dat";

    private static void deleteDemoFile() {
        try {
            Files.deleteIfExists(Path.of(demoFileName));
            System.out.println("\nDemo file deleted");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Low-level solution as the write methods of an {@link OutputStream}
     * can write ONLY individual bytes and byte arrays
     */
    private static void demo1() {
        System.out.println("demo1...");
        // Prepare the data to be sent to the stream
        ByteBuffer bb = ByteBuffer.allocate(dbls.length * Double.BYTES);
        for (double d : dbls) {
            bb.putDouble(d);
        }

        try (var fout = Files.newOutputStream(Path.of(demoFileName))) {
            fout.write(bb.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Low-level solution as the read methods of a {@link FileInputStream}
     * can read ONLY individual bytes and byte arrays
     */
    private static void demo2() {
        System.out.println("\ndemo2...");
        try (var fin = Files.newInputStream(Path.of(demoFileName))) {
            // Again, this low-level stuff...
            byte[] data = fin.readAllBytes();
            double[] ldbls = new double[data.length / Double.BYTES];
            ByteBuffer.wrap(data).asDoubleBuffer().get(ldbls);

            for (double d : ldbls) {
                System.out.println(d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@link DataOutputStream} to the rescue (compare to {@link #demo1}).
     * It enables you to write primitive data-type values and strings into an output stream
     */
    private static void demo3() {
        System.out.println("\ndemo3...");
        try (var out = new DataOutputStream(Files.newOutputStream(Path.of(demoFileName)))) {
            for (double d : dbls) {
                out.writeDouble(d); // instead of byte-level writes, DataOutputStream introduces data awarness
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@link DataInputStream} to the rescue (compare to {@link #demo2}).
     * It converts an input stream of bytes into primitive data-type values and strings
     */
    private static void demo4() {
        System.out.println("\ndemo4...");
        try (var in = new DataInputStream(Files.newInputStream(Path.of(demoFileName)))) {
            for (int i = 0; i < dbls.length; i++) {
                System.out.println(in.readDouble());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // (write + read) + delete, byte-oriented
        demo1();
        demo2();
        deleteDemoFile();

        // (write + read) + delete, data type aware (but only for predefined types)
        demo3();
        demo4();
        deleteDemoFile();

        // But what about user-defined types? Object serialisation is (or rather used to be) the answer :)

        int count = reading();
        count++;
        writting(count);
        System.out.println("This program has been launched " + count + " times.");
    }
}

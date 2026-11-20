import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

/**
 * LineCounter instruments and measures unbuffered vs buffered I/O execution.
 */
public class LineCounter {

    /**
     * Counts lines reading ONE BYTE at a time via unbuffered FileInputStream.
     * Triggers one native read call per byte.
     */
    public static long countUnbuffered(Path path) throws IOException {
        long lines = 0;
        try (FileInputStream in = new FileInputStream(path.toFile())) {
            int b;
            while ((b = in.read()) != -1) {
                if (b == '\n') {
                    lines++;
                }
            }
        }
        return lines;
    }

    /**
     * Counts lines using BufferedInputStream (8 KB in-memory buffer).
     */
    public static long countBufferedStream(Path path) throws IOException {
        long lines = 0;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path.toFile()))) {
            int b;
            while ((b = bis.read()) != -1) {
                if (b == '\n') {
                    lines++;
                }
            }
        }
        return lines;
    }

    /**
     * Counts lines using BufferedReader.readLine().
     */
    public static long countBufferedReader(Path path) throws IOException {
        long lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            while (reader.readLine() != null) {
                lines++;
            }
        }
        return lines;
    }
}

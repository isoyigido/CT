package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataLoader {
//    Reads the CSV file into a String and then returns it as a custom Data object
    public static Data loadCSV(String pathname) {
        try {
            return new Data(Files.readString(Paths.get(pathname)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

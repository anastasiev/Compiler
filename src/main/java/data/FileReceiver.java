package data;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by dmytro on 14.03.16.
 */
public class FileReceiver implements Receiver {
    private BufferedReader reader;

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public String getString() throws IOException {
        return reader.readLine();
    }
}

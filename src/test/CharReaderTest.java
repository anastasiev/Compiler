import data.CharReader;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * Created by dmytro on 10.04.16.
 */
public class CharReaderTest {
    @Test
    public void readerTest() throws Exception{
        InputStreamReader inp = new InputStreamReader(
                new FileInputStream("/home/dmytro/IdeaProjects/LexAnalyserServer/src/test1.sgn"));
        CharReader reader = new CharReader(inp);
        while(!reader.isEmpty())
            System.out.println(reader.remove());
    }

}
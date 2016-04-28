import analizer.LexAnalyser;
import data.CharReader;
import data.DataReceiver;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * Created by dmytro on 15.03.16.
 */
public class LexAnalyserTest {

    @Test
    public void testAnalyse() throws Exception {
        LexAnalyser analizer = new LexAnalyser();
//        DataReceiver receiver = new DataReceiver("/home/dmytro/IdeaProjects/LexAnalyserServer/src/test.sgn");
//        receiver.open();
//        analizer.analyse(receiver);
//        receiver.close();
        InputStreamReader inp = new InputStreamReader(
                new FileInputStream("/home/dmytro/IdeaProjects/LexAnalyserServer/src/test.sgn"));
        CharReader reader = new CharReader(inp);
        analizer.analyse(reader);
        System.out.println(analizer.getLexems());

        System.out.println(analizer.getConstantTable());
        System.out.println(analizer.getIdentTable());
        System.out.println(analizer.getKeyWordTable());
        System.out.println(analizer.getSingleSymTable());
        System.out.println(analizer.getDoubleSymTable());
    }


    @Test
    public void readerTest() throws Exception{
        // -32
        System.out.println((int)'a');
        System.out.println((int)'z');
        DataReceiver receiver = new DataReceiver("/home/dmytro/IdeaProjects/LexAnalyserServer/src/test1.sgn");
        receiver.open();
        System.out.println(receiver.read());
        System.out.println(receiver.read());
        receiver.close();
    }
}
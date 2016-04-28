package data;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by dmytro on 06.03.16.
 */
public class DataReceiver implements Receiver {
    private String fileName;
    BufferedReader bufferedReader;

    public DataReceiver(String fileName){
        this.fileName = fileName;
    }

    public void open(){
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        if(bufferedReader != null)
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public ArrayList<String> getStringLines() throws IOException {
        ArrayList<String> res = new ArrayList<String>();
        String str = bufferedReader.readLine();
        while(str != null){
            res.add(str);
            str = bufferedReader.readLine();
        }
        return res;
    }

    @Override
    public String getString() throws IOException {
        return bufferedReader.readLine();
    }
    public int read() throws IOException {
        return bufferedReader.read();
    }
}


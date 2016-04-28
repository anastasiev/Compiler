package data;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by dmytro on 10.04.16.
 */

public class CharReader {
    private InputStreamReader reader;
    private ArrayList<Character> buf = new ArrayList<>();

    public CharReader(InputStream inputStream){
        reader = new InputStreamReader(inputStream);
    }
    public CharReader(InputStreamReader inputStreamReader){
        reader = inputStreamReader;
    }

    public void setReader(InputStreamReader reader) {
        this.reader = reader;
    }

    private boolean isLowLet(int c){
        if(c >= 94 && c<=122)
            return true;
        return false;
    }

    private int read(){
        int val = 0;
        try {
            val = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(isLowLet(val))
            val-=32;
        return val;
    }

    public boolean isEmpty(){
        if(!buf.isEmpty())
            return false;
        int val = read();
        if(val == -1)
            return true;
        buf.add((char)val);

        return false;
    }

    public char get(){
        if(!buf.isEmpty())
            return buf.get(0);
        int val = read();
        buf.add((char)val);
        return (char)val;
    }
    public void add(char c){
        buf.add(0, c);
    }
    public char remove(){
        if(!buf.isEmpty())
            return buf.remove(0);
        return (char)read();
    }

    public void close(){
        if(reader != null)
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}

package data;

/**
 * Created by dmytro on 26.05.16.
 */
public class LabelGenerator {
    static private int labelNumber = 0;
    public String generate(){
        return "?L" + labelNumber++;
    }
}

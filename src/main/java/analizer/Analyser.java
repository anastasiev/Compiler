package analizer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dmytro on 16.04.16.
 */
public class Analyser {
    protected HashMap<String, Integer> keyWordTable;
    protected HashMap<String, Integer> constantTable;
    protected HashMap<String, Integer> identTable;
    protected HashMap<String, Integer> singleSymTable;
    protected HashMap<String, Integer> doubleSymTable;
    protected ArrayList<Lexem> lexems;

    public ArrayList<Lexem> getLexems() {
        return lexems;
    }

    public HashMap<String, Integer> getKeyWordTable() {
        return keyWordTable;
    }

    public HashMap<String, Integer> getConstantTable() {
        return constantTable;
    }

    public HashMap<String, Integer> getIdentTable() {
        return identTable;
    }

    public HashMap<String, Integer> getSingleSymTable() {
        return singleSymTable;
    }

    public HashMap<String, Integer> getDoubleSymTable() {
        return doubleSymTable;
    }

    public void setLexems(ArrayList<Lexem> lexems) {
        this.lexems = lexems;
    }

    public void setKeyWordTable(HashMap<String, Integer> keyWordTable) {
        this.keyWordTable = keyWordTable;
    }

    public void setConstantTable(HashMap<String, Integer> constantTable) {
        this.constantTable = constantTable;
    }

    public void setIdentTable(HashMap<String, Integer> identTable) {
        this.identTable = identTable;
    }

    public void setSingleSymTable(HashMap<String, Integer> singleSymTable) {
        this.singleSymTable = singleSymTable;
    }

    public void setDoubleSymTable(HashMap<String, Integer> doubleSymTable) {
        this.doubleSymTable = doubleSymTable;
    }
}

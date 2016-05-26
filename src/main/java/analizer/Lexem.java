package analizer;

/**
 * Created by dmytro on 06.03.16.
 */
public class Lexem {
    private String name;
    private int code;
    private int lineNum;

    public Lexem(String name, int code, int lineNum) {
        this.name = name;
        this.code = code;
        this.lineNum = lineNum;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String toString(){
        return name + ": " + code;
    }

    public int getLineNum() {
        return lineNum;
    }
}

package analizer;

import constants.ComentSymbols;
import constants.Delimeters;
import constants.KeyWords;
import data.CharReader;
import exceptions.InvalidIDException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by dmytro on 06.03.16.
 */

//Tests!!!!!!!
@Component
public class LexAnalyser extends Analyser {
    private int lastConstantCode = 500;
    private int lastIdentCode = 1000;

    private int lastKeyWordCode = 400;
    private int lastDoubleSymCode = 300;

    private boolean commentFlag = false;

    public LexAnalyser(){
        keyWordTable = new HashMap<>();
        constantTable = new HashMap<>();
        identTable = new HashMap<>();
        singleSymTable = new HashMap<>();
        doubleSymTable = new HashMap<>();
        lexems = new ArrayList<>();

        keyWordTable.put(KeyWords.BEGIN, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.END, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.VAR, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.PROGRAM, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.CONST, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.SIGNAL, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.COMPLEX, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.INTEGER, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.FLOAT, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.BLOCKFLOAT, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.EXT, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.LOOP, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.ENDLOOP, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.FOR, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.ENDFOR, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.CASE, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.OF, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.ENDCASE, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.TO, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.DO, ++lastKeyWordCode);
        keyWordTable.put(KeyWords.MOD, ++lastKeyWordCode);

        doubleSymTable.put(Delimeters.APPROPRIATE, ++lastDoubleSymCode);
        doubleSymTable.put(Delimeters.GREATER_OR_EQUAL, ++lastDoubleSymCode);
        doubleSymTable.put(Delimeters.LESS_OR_EQUAL, ++lastDoubleSymCode);
        doubleSymTable.put(Delimeters.RANGE, ++lastDoubleSymCode);

        singleSymTable.put(Delimeters.LESS, Integer.valueOf(Delimeters.LESS.charAt(0)));
        singleSymTable.put(Delimeters.GREATER, Integer.valueOf(Delimeters.GREATER.charAt(0)));
        singleSymTable.put(Delimeters.ADD, Integer.valueOf(Delimeters.ADD.charAt(0)));
        singleSymTable.put(Delimeters.COLON, Integer.valueOf(Delimeters.COLON.charAt(0)));
        singleSymTable.put(Delimeters.DIV, Integer.valueOf(Delimeters.DIV.charAt(0)));
        singleSymTable.put(Delimeters.EQUAL, Integer.valueOf(Delimeters.EQUAL.charAt(0)));
        singleSymTable.put(Delimeters.MULTIPLY, Integer.valueOf(Delimeters.MULTIPLY.charAt(0)));
        singleSymTable.put(Delimeters.STOP_LINE, Integer.valueOf(Delimeters.STOP_LINE.charAt(0)));
        singleSymTable.put(Delimeters.SUB, Integer.valueOf(Delimeters.SUB.charAt(0)));
        singleSymTable.put(Delimeters.COMA, Integer.valueOf(Delimeters.COMA.charAt(0)));
        singleSymTable.put(Delimeters.EXCLAMATION, Integer.valueOf(Delimeters.EXCLAMATION.charAt(0)));
        singleSymTable.put(Delimeters.AMPERSAND, Integer.valueOf(Delimeters.AMPERSAND.charAt(0)));
        singleSymTable.put(Delimeters.LBRACKET, Integer.valueOf(Delimeters.LBRACKET.charAt(0)));
        singleSymTable.put(Delimeters.RBRACKET, Integer.valueOf(Delimeters.RBRACKET.charAt(0)));
        singleSymTable.put(Delimeters.POWER, Integer.valueOf(Delimeters.POWER.charAt(0)));
        singleSymTable.put(Delimeters.GRID, Integer.valueOf(Delimeters.GRID.charAt(0)));
        singleSymTable.put(Delimeters.SQUARE_BRACKET_B, Integer.valueOf(Delimeters.SQUARE_BRACKET_B.charAt(0)));
        singleSymTable.put(Delimeters.SQUARE_BRACKET_E, Integer.valueOf(Delimeters.SQUARE_BRACKET_E.charAt(0)));
        singleSymTable.put(Delimeters.FULL_STOP, Integer.valueOf(Delimeters.FULL_STOP.charAt(0)));
    }
    //
    private boolean isLet(char c){
        if(c >= 65 && c<=90)
            return true;
        return false;
    }

    //
    private boolean isNum(char c){
        if(c >= 48 && c <=57)
            return true;
        return false;
    }
    //
    private boolean isSpaces(char c){
        if(c == 32 || c == 9 || c == 11 || c == 10 || c == 13)
            return true;
        return false;
    }

    private boolean isSingleDelimiter(String del){
        return singleSymTable.containsKey(del);
    }

    private boolean isDoubleDelimiter(String del){
        return doubleSymTable.containsKey(del);
    }

    //
    private String makeWord(CharReader line){
        StringBuilder builder = new StringBuilder();

        while(!line.isEmpty() && (isLet(line.get()) || isNum(line.get())))
            builder.append(line.remove());


        return builder.toString();
    }

    //
    private String makeNumber(CharReader line) throws InvalidIDException {
        String res = makeWord(line);
        for(int i = 0; i<res.length(); i++)
            if(isLet(res.charAt(i))){
                InvalidIDException ex = new InvalidIDException(res + " is invalid identificator");
                ex.setInvalidId(res);
                throw ex;
            }

        return res;
    }
    //////
    public String makeExpNumber(CharReader line) throws InvalidIDException {
        StringBuilder res = new StringBuilder();
        res.append(makeNumber(line));
        if(!line.isEmpty() && line.get() == Delimeters.GRID.charAt(0)){
            res.append(line.remove());
            if(!line.isEmpty() && (line.get() == Delimeters.ADD.charAt(0) || line.get() == Delimeters.SUB.charAt(0))) {
                res.append(line.remove() );
                String str = makeWord(line);
                if(!str.isEmpty()) {
                    res.append(str);
                    boolean isOk = true;
                    for (int i = 0; i < str.length(); i++) {
                        if (isLet(str.charAt(i))) {
                            isOk = false;
                            break;
                        }
                    }
                    if (isOk)
                        return res.toString();
                }
            }
        }else{
            return res.toString();
        }
        for(int i = res.length() - 1; i>=0; i--){
            line.add(res.charAt(i));
        }

        return null;
    }
    //
    private String makeDelimiter(CharReader line){
        if(line.isEmpty())return null;

        StringBuilder builder = new StringBuilder();
        builder.append(line.remove());
        if (!line.isEmpty()){
            builder.append(line.remove());
            if (isDoubleDelimiter(builder.toString())) return builder.toString();
            line.add(builder.charAt(1));
            builder.deleteCharAt(1);
        }

        if (isSingleDelimiter(builder.toString())) return builder.toString();
        line.add(builder.charAt(0));
        return null;
    }
    //
    private boolean isCommentSymbol(CharReader line, String symbol){
        StringBuilder builder = new StringBuilder();
        builder.append(line.remove());
        if (!line.isEmpty()){
            builder.append(line.remove());
            if(symbol.equalsIgnoreCase(builder.toString()))
                return true;
            line.add(builder.charAt(1));
            line.add(builder.charAt(0));
            return false;
        }
        line.add(builder.charAt(0));
        return false;
    }
    //
    private void deleteComment(CharReader line){
        boolean endCom = false;
        while (!line.isEmpty()){
            if(isCommentSymbol(line, ComentSymbols.ECOM)){
                endCom = true;
                break;
            }
            line.remove();
        }
        if(endCom)commentFlag = false;
    }


    public void analyse(CharReader lineChar){
//        ArrayList<Character> lineChar = new ArrayList<>();
//
//        for(Character c: line.toCharArray())
//            lineChar.add(c);

        if(commentFlag)deleteComment(lineChar);

        while (!lineChar.isEmpty()){
            //seperate words
            if(isLet(lineChar.get())){
                String word = makeWord(lineChar);
                if(keyWordTable.containsKey(word))
                    lexems.add(new Lexem(word, keyWordTable.get(word)));
                else{
                    if(!identTable.containsKey(word))
                        identTable.put(word, ++lastIdentCode);
                    lexems.add(new Lexem(word, identTable.get(word)));
                }
                continue;
            }

            //seperate exp number
            if(isNum(lineChar.get())){
                String number = null;
                try {
                    number = makeExpNumber(lineChar);
                    if(number != null) {
                        if (!constantTable.containsKey(number))
                            constantTable.put(number, ++lastConstantCode);
                        lexems.add(new Lexem(number, constantTable.get(number)));
                    }

                } catch (InvalidIDException e) {
                    lexems.add(new Lexem(e.getInvalidId(), -1));
                }finally {
                    if(number != null)
                        continue;
                }
            }

            //seperate constants
            if(isNum(lineChar.get())){
                try {
                    String number = makeNumber(lineChar);
                    if(!constantTable.containsKey(number))
                        constantTable.put(number, ++lastConstantCode);
                    lexems.add(new Lexem(number, constantTable.get(number)));
                } catch (InvalidIDException e) {
                    lexems.add(new Lexem(e.getInvalidId(), -1));
                }finally {
                    continue;
                }
            }
            //seperate spaces
            if(isSpaces(lineChar.get())){
                lineChar.remove();
                continue;
            }

            //search comment
            commentFlag = isCommentSymbol(lineChar, ComentSymbols.BCOM);
            if(commentFlag){
                deleteComment(lineChar);
                continue;
            }


            //seperate delimiters
            String delim = makeDelimiter(lineChar);
            if(delim != null) {
                if (delim.length() == 2)
                    lexems.add(new Lexem(delim, doubleSymTable.get(delim)));
                else
                    lexems.add(new Lexem(delim, singleSymTable.get(delim)));
                continue;
            }

            //unknown symbol

            lexems.add(new Lexem(String.valueOf(lineChar.remove()) , -1));

        }
        if(commentFlag)
            lexems.add(new Lexem(KeyWords.ERROR, -1));
    }




}

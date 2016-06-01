package analizer;

import constants.KeyWords;
import data.LabelGenerator;
import tree.Node;

import javax.smartcardio.ATR;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

/**
 * Created by dmytro on 26.05.16.
 */
public class CodeGenerator {
    private Node blockRoot;

    private Node currentNode;

    private ArrayList<String> outLines = new ArrayList<>();

    private LabelGenerator labelGenerator = new LabelGenerator();


    public CodeGenerator(Node blockRoot) {
        this.blockRoot = blockRoot;
        currentNode = blockRoot;
    }

    public void makeDataSegment(){
        outLines.add("Data SEGMENT");
        HashMap<String, String> res = new HashMap<>();
        currentNode = blockRoot.getChildren().get(0).getChildren().get(0).getChildren().get(1);
        makeConstants(currentNode, res);
        Set<String> keys = res.keySet();
        for(String key: keys){
            outLines.add(key + " EQU " + res.get(key));
        }

        res.clear();
        currentNode = blockRoot.getChildren().get(0).getChildren().get(1).getChildren().get(1);
        makeVar(currentNode, res);
        keys = res.keySet();
        for(String key: keys){
            if(KeyWords.SIGNAL.equals(res.get(key))) {
                outLines.add(key + " DB " + "?");
            }else{
                outLines.add(key + " DD " + "?");
            }
        }
        outLines.add("end SEGMENT");
    }

    private void makeConstants(Node constantDeclList, HashMap<String, String> res){
        res.put(getInd(constantDeclList), getConst(constantDeclList));
        if(constantDeclList.getChildren().size() == 2)
            makeConstants(constantDeclList.getChildren().get(1), res);
    }

    private void makeVar(Node varDeclList, HashMap<String, String> res){
        String ind = getInd(varDeclList);
        String attr = getAttribute(varDeclList);
        if(attr.equals("["))
            attr = "dup(?)";
        res.put(ind, attr);
        if(varDeclList.getChildren().size() == 2)
            makeVar(varDeclList.getChildren().get(1), res);
    }

    private String getInd(Node constantDeclList){

        Node constDect = constantDeclList.getChildren().get(0);
        String res = constDect.getChildren().get(0).getChildren().get(0).getChildren().get(0).getInfo();
        return res;
    }
    private String getConst(Node constantDeclList){

        Node constDect = constantDeclList.getChildren().get(0);
        String res = constDect.getChildren().get(2).getChildren().get(0).getInfo();
        return res;
    }
    private String getAttribute(Node constantDeclList){

        Node constDect = constantDeclList.getChildren().get(0);
        String res = constDect.getChildren().get(3).getChildren().get(0).getInfo();
        return res;
    }

    public void makeCodeSegment(){
        outLines.add("Code SEGMENT");
        currentNode = blockRoot.getChildren().get(2);
        parseStatementList(currentNode);
        outLines.add("End SEGMENT");

    }

    private void parseStatementList(Node statementList){
        if(statementList.getChildren().isEmpty())
            return;
        String typeStatement = statementList.getChildren().get(0).getChildren().get(0).getInfo();
        if(typeStatement.equals(KeyWords.LOOP)){
            makeLoop(statementList.getChildren().get(0));
        }else if(typeStatement.equals(KeyWords.FOR)){
            makeFor(statementList.getChildren().get(0));
        }else{
            makeCase(statementList.getChildren().get(0));
        }
        parseStatementList(statementList.getChildren().get(1));
    }

    private void makeLoop(Node statement){
        String label = labelGenerator.generate();
        outLines.add(label + ":");
        parseStatementList(statement.getChildren().get(1));
        outLines.add("JMP " + label);
    }

    private void makeFor(Node statement){
        getLowLoop(statement);
        String loopVarName = getLoopVarName(statement);
        outLines.add("MOV " + loopVarName + ", AX");
        getHighLoop(statement);
        outLines.add("CMP AX, " + loopVarName);
        String endLabel = labelGenerator.generate();
        outLines.add("JL " + endLabel);
        String loopLabel = labelGenerator.generate();
        outLines.add(loopLabel + ":");
        parseStatementList(statement.getChildren().get(3).getChildren().get(4));
        outLines.add("INC " + loopVarName);
        outLines.add("CMP AX, " + loopVarName);
        outLines.add("JGE " + loopLabel);
        outLines.add(endLabel + ":");
    }

    private void makeCase(Node statement){
        String caseName = getCaseName(statement);
        String endCaseLabel = labelGenerator.generate();
        outLines.add("MOV AX, " + caseName);
        ArrayList<String> altList = new ArrayList<>();
        fillAltList(statement.getChildren().get(3), altList);
        for(String alt: altList){
            outLines.add("CMP AX, " + alt);
            outLines.add("JE ?" + alt);
        }
        parseCaseAlternatives(statement.getChildren().get(3), altList, endCaseLabel);
        outLines.add(endCaseLabel + ":");
    }

    private void parseCaseAlternatives(Node alternativeList, ArrayList<String> altList, String endLab){
        if(alternativeList.getChildren().isEmpty())
            return;
        outLines.add("?" + altList.remove(0) + ":");
        parseStatementList(alternativeList.getChildren().get(0).getChildren().get(3));
        outLines.add("JMP " + endLab);
        parseCaseAlternatives(alternativeList.getChildren().get(1), altList, endLab);
    }

    private void fillAltList(Node alternativeList, ArrayList<String> list){
        if(alternativeList.getChildren().isEmpty())
            return;
        String alt = alternativeList.getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0).getInfo();
        list.add(alt);
        fillAltList(alternativeList.getChildren().get(1), list);
    }

    private String getCaseName(Node statement){
        return statement.getChildren().get(1)
                        .getChildren().get(0)
                        .getChildren().get(0)
                        .getChildren().get(0)
                        .getChildren().get(0).getInfo();
    }

    private void getLowLoop(Node statement){
        Node multList = statement.getChildren().get(3)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(1);

        String lowVal = statement.getChildren().get(3)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0).getInfo();

        String loopVal = getLoopVarName(statement);

        if(!multList.getChildren().isEmpty()){
            String instr = multList.getChildren().get(0)
                    .getChildren().get(0).getInfo();
            if(instr.equals("/")){
                String divVal = multList.getChildren().get(1).getChildren().get(0).getInfo();
                outLines.add("MOV AX, " + lowVal);
                outLines.add("DIV " + divVal);
            }else if(instr.equals("MOD")){
                String divVal = multList.getChildren().get(1).getChildren().get(0).getInfo();
                outLines.add("MOV AX, " + lowVal);
                outLines.add("DIV " + divVal);
                outLines.add("MOV AL, AH");
                outLines.add("XOR AH, AH");
            }else if(instr.equals("*")){
                String divVal = multList.getChildren().get(1).getChildren().get(0).getInfo();
                outLines.add("MOV AX, " + lowVal);
                outLines.add("MULT AX, " + divVal);
            }

        }else {
            outLines.add("MOV AX, " + lowVal);
        }
    }
    private void getHighLoop(Node statement){
        String highVal =  statement.getChildren().get(3)
                .getChildren().get(2)
                .getChildren().get(0)
                .getChildren().get(0)
                .getChildren().get(0).getInfo();
        Node multList = statement.getChildren().get(3)
                .getChildren().get(2)
                .getChildren().get(0)
                .getChildren().get(1);
        String loopVal = getLoopVarName(statement);

        if(!multList.getChildren().isEmpty()){
            String instr = multList.getChildren().get(0)
                    .getChildren().get(0).getInfo();
            if(instr.equals("/")){
                String divVal = multList.getChildren().get(1).getChildren().get(0).getInfo();
                outLines.add("MOV AX, " + highVal);
                outLines.add("DIV " + divVal);
            }else if(instr.equals("MOD")){
                String divVal = multList.getChildren().get(1).getChildren().get(0).getInfo();
                outLines.add("MOV AX, " + highVal);
                outLines.add("DIV " + divVal);
                outLines.add("MOV AH, AL");
                outLines.add("XOR AH, AH");
            }else if(instr.equals("*")){
                String divVal = multList.getChildren().get(1).getChildren().get(0).getInfo();
                outLines.add("MOV AX, " + highVal);
                outLines.add("MULT AX, " + divVal );
            }

        }else {
            outLines.add("MOV AX, " + highVal);
        }


    }
    private String getLoopVarName(Node statement){
        return statement.getChildren().get(1).getInfo();
    }


    public void generateCode(){
        makeDataSegment();
        outLines.add("\n");
        makeCodeSegment();
    }
    public void createAsmFile(){
        File file = new File("test.asm");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            for (String line: outLines){
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

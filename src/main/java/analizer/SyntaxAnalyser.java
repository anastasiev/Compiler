package analizer;

import constants.Delimeters;
import constants.KeyWords;
import exceptions.SAException;
import org.springframework.stereotype.Component;
import tree.Node;
import tree.ParsingTree;

import java.util.*;

/**
 * Created by dmytro on 16.04.16.
 */
public class SyntaxAnalyser extends Analyser{

    private ParsingTree tree = new ParsingTree("program");

    public SyntaxAnalyser(ArrayList<Lexem> lexems){
        this.lexems = lexems;
    }

    private Set<String> constIdentSet = new HashSet<>();
    private Set<String> varIdentSet = new HashSet<>();

    public ParsingTree getTree() {
        return tree;
    }


    public void analyse()throws SAException, NoSuchElementException{
        ListIterator<Lexem> li = lexems.listIterator();
        try {
            program(li);
            System.out.println("All right!!!");
        } catch (SAException e) {
            System.out.println("Error: " + e.toString());
            throw e;
        } catch (NoSuchElementException e1){
            System.out.println("Error: . excepted");
            throw e1;
        }
    }

    private boolean isIdentifier(int code){
        if(code >= 1001)
            return true;
        return false;
    }

    private boolean isConstant(int code){
        if(code >= 501 && code <= 1000)
            return true;
        return false;
    }

    private void makeError(String message, ListIterator<Lexem> it) throws SAException {
        if(it.hasPrevious())
            it.previous();
        int lineNum = 1;
        if(it.hasPrevious())
            lineNum = it.previous().getLineNum();
        it.next();
        it.next();
        SAException ex = new SAException(message + " in line " + lineNum);
        ex.setError(true);
        throw ex;
    }

    private void program(ListIterator<Lexem> it) throws SAException, NoSuchElementException {
        if(KeyWords.PROGRAM.equals(it.next().getName())){
            tree.add("PROGRAM");

            procIdentifier(it, tree.add("procIdentifier"));
            if(Delimeters.STOP_LINE.equals(it.next().getName())){
                tree.add(";");

                block(it, tree.add("block"));
                if(!Delimeters.FULL_STOP.equals(it.next().getName())){
                    makeError(". excepted", it);
                }
                tree.add(".");
            }else{
                makeError("; excepted", it);
            }
        }else{
            makeError("PROGRAM excepted", it);
        }

    }
    private void procIdentifier(ListIterator<Lexem> it, Node node)throws SAException {
        tree.next(node);

        identifier(it, tree.add("identifier"));
        tree.previous();
    }
    private void block(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        declarations(it, tree.add("declarations"));
        if(KeyWords.BEGIN.equals(it.next().getName())){
            tree.add("BEGIN");

            statementList(it, tree.add("statementList"));
            if(!KeyWords.END.equals(it.next().getName())){
                makeError("END excepted", it);
            }
            tree.add("END");
        }
        tree.previous();
    }

    private void identifier(ListIterator<Lexem> it, Node node)throws SAException {
        tree.next(node);
        if(it.hasNext()) {
            Lexem lex =  it.next();
            if (!isIdentifier(lex.getCode())) {
                throw new SAException(lex.getName() + " is not identifier");
            }
            tree.add(lex.getName());
        }else{
            makeError("identifier excepted", it);
        }
        tree.previous();
    }

    private void constant(ListIterator<Lexem> it, Node node)throws SAException {
        tree.next(node);
        if(it.hasNext()) {
            Lexem lex =  it.next();
            if (!isConstant(lex.getCode())) {
                if(!(it.hasNext() && Delimeters.SUB.equals(lex.getName()) && isConstant(it.next().getCode()))){
                    makeError("constant excepted", it);
                }
            }
            tree.add(lex.getName());
        }else{
            makeError("constant excepted", it);
        }
        tree.previous();
    }

    private void declarations(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);

//        labelDeclarations(it,  tree.add("labelDeclarations"));

        constDeclarations(it, tree.add("constDeclarations"));


        varDeclarations(it,  tree.add("varDeclarations"));


        tree.previous();
    }
    private void labelDeclarations(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException {
        tree.next(node);
        if("LABEL".equals(it.next().getName())) {
            tree.add("LABEL");
            labelList(it, tree.add("labelList"));

            if(!Delimeters.STOP_LINE.equals(it.next().getName())) {
                makeError("; excepted", it);
            }
            tree.add(";");
        }else{
            makeError("LABEL excepted", it);
        }

        tree.previous();
    }
    private void labelList(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException {
        tree.next(node);
        label(it, tree.add("label"));
        if(Delimeters.COMA.equals(it.next().getName())){
            tree.add(",");
            labelList(it, tree.add("labelList"));
        }else{
            it.previous();
        }
        tree.previous();
    }
    private void label(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException {
        tree.next(node);
        constant(it, tree.add("constant"));
        tree.previous();
    }


    private void statementList(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        try{
            statement(it, tree.add("statement"));
            statementList(it, tree.add("statementList"));
        }catch (SAException ex){
            if(!ex.isError()) {
                it.previous();
                tree.removeChain(node);
            }else
                throw ex;
        }
        tree.previous();
    }
    private void statement(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        Lexem lex = it.next();

        if(KeyWords.LOOP.equals(lex.getName())){
            tree.add(lex.getName());
            statementList(it, tree.add("statementList"));
            if(!KeyWords.ENDLOOP.equals(it.next().getName())){
                makeError("ENDLOOP excepted", it);
            }
            tree.add("ENDLOOP");
        }else if(KeyWords.FOR.equals(lex.getName())){
            tree.add(lex.getName());
            Lexem forIndex = it.next();
            if(!varIdentSet.contains(forIndex.getName())){
                makeError(forIndex.getName() + " is not variable", it);
            }
            tree.add(forIndex.getName());
            if(Delimeters.APPROPRIATE.equals(it.next().getName())){
                tree.add(":=");
                loopDeclarations(it, tree.add("loopDeclarations"));
                if(!KeyWords.ENDFOR.equals(it.next().getName())){
                    makeError("ENDFOR excepted", it);
                }
                tree.add("ENDFOR");
            }else{
                makeError(":= excepted", it);
            }

        }else if(KeyWords.CASE.equals(lex.getName())){
            tree.add(lex.getName());
            expression(it, tree.add("expression"));
            if(KeyWords.OF.equals(it.next().getName())){
                tree.add("OF");
                alternativeList(it, tree.add("alternativeList"));
                if(!KeyWords.ENDCASE.equals(it.next().getName())){
                    makeError("ENDCASE excepted", it);
                }
                tree.add("ENDCASE");
            }else{
                makeError("OF excepted", it);
            }
        }else{
            throw new SAException("unknown statement");
        }
        if(!Delimeters.STOP_LINE.equals(it.next().getName())){
            makeError("; excepted", it);
        }
        tree.add(";");
        tree.previous();
    }

    private void alternativeList(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        try {
            alternative(it, tree.add("alternative"));
            alternativeList(it, tree.add("alternativeList"));
        }catch (SAException ex){
            //Сделай тут костыль с ENDCASE
            Lexem lexem = it.previous();
            if(KeyWords.ENDCASE.equals(lexem.getName())){
                tree.removeChain(node);
            }else{
                throw ex;
            }
        }
        tree.previous();
    }

    private void alternative(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        expression(it, tree.add("expression"));
        if(Delimeters.COLON.equals(it.next().getName())){
            tree.add(":");
            if(Delimeters.DIV.equals(it.next().getName())){
                tree.add("/");
                statementList(it, tree.add("statementList"));
                if(!Delimeters.BACK_SLESH.equals(it.next().getName())){
                    makeError("\\ excepted", it);
                }
                tree.add("\\");
            }else{
                makeError("/ excepted", it);
            }
        }else{
            makeError(": excepted", it);
        }
        tree.previous();
    }

    private void loopDeclarations(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        expression(it, tree.add("expression"));
        if(KeyWords.TO.equals(it.next().getName())){
            tree.add("TO");
            expression(it, tree.add("expression"));
            if(KeyWords.DO.equals(it.next().getName())){
                tree.add("DO");
                statementList(it, tree.add("statementList"));
            }else{
                makeError("DO excepted", it);
            }
        }else{
            makeError("TO excepted", it);
        }
        tree.previous();
    }

    private void expression(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        try {
            if (Delimeters.SUB.equals(it.next().getName())) {
                tree.add("-");
                summand(it, tree.add("summand"));
                summandList(it, tree.add("summandList"));
            } else {
                it.previous();
                summand(it, tree.add("summand"));
                summandList(it, tree.add("summandList"));
            }
        }catch (SAException ex){
            if(!ex.isError()) {
                it.previous();
                tree.removeChain(node);
            }else
                throw ex;
        }
        tree.previous();
    }

    private void summand(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        multiplier(it, tree.add("multiplier"));
        multipliersList(it, tree.add("multipliersList"));
        tree.previous();
    }

    private void multipliersList(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        try {
            multiplicationInstraction(it, tree.add("multiplicationInstraction"));
            multiplier(it, tree.add("multiplier"));
            multipliersList(it, tree.add("multipliersList"));
        }catch (SAException ex){
            if(!ex.isError()) {
                it.previous();
                tree.removeChain(node);
            }else
                throw ex;
        }
        tree.previous();
    }

    private void multiplicationInstraction(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        Lexem lex = it.next();
        if(lex.getName().equals(Delimeters.MULTIPLY) || lex.getName().equals(Delimeters.DIV)||
                lex.getName().equals(Delimeters.AMPERSAND) || lex.getName().equals(KeyWords.MOD) ){
            tree.add(lex.getName());
        }else{
            throw new SAException("unknow instraction");
        }
        tree.previous();
    }

    private void multiplier(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        Lexem lex = it.next();
        if(isConstant(lex.getCode())){
            tree.add(lex.getName());
        }else  if(constIdentSet.contains(lex.getName())){
            tree.add(lex.getName());
        }else if(Delimeters.LBRACKET.equals(lex.getName())){
            tree.add("(");
            expression(it, tree.add("expression"));
            if(!Delimeters.RBRACKET.equals(it.next().getName())){
                makeError(") excepted", it);
            }
            tree.add(")");
        }else if(Delimeters.SUB.equals(lex.getName())){
            tree.add("-");
            multiplier(it, tree.add("multiplier"));
        }else if(Delimeters.POWER.equals(lex.getName())){
            tree.add("^");
            multiplier(it, tree.add("multiplier"));
        }else{
            it.previous();
            variable(it, tree.add("variable"));
        }
        tree.previous();
    }

    private void variable(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        Lexem lexem = it.next();
        if (varIdentSet.contains( lexem.getName())){
            tree.add(lexem.getName());
            dimension(it, tree.add("dimension"));
        }else{
            makeError("unknown multiplier", it);
        }
        tree.previous();
    }

    private void dimension(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException {
        tree.next(node);
        if (Delimeters.SQUARE_BRACKET_B.equals(it.next().getName())) {
            tree.add("[");
            expression(it, tree.add("expression"));
            expressionsList(it, tree.add("expressionsList"));
            if (!Delimeters.SQUARE_BRACKET_E.equals(it.next().getName())) {
                makeError("] excepted", it);
            }
            tree.add("]");
        }else{
            it.previous();
            tree.removeChain(node);
        }
        tree.previous();
    }

    private void expressionsList(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        if (Delimeters.COMA.equals(it.next().getName())) {
            tree.add(",");
            expression(it, tree.add("expression"));
            expressionsList(it, tree.add("expressionsList"));
        }else{
            it.previous();
            tree.removeChain(node);
        }
        tree.previous();
    }

    private void summandList(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        addInstractions(it, tree.add("addInstractions"));
        summand(it, tree.add("summand"));
        summandList(it, tree.add("summandList"));
        tree.previous();
    }

    private void addInstractions(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        Lexem lex = it.next();
        if(lex.getName().equals(Delimeters.ADD) || lex.getName().equals(Delimeters.SUB)||
                lex.getName().equals(Delimeters.EXCLAMATION)){
            tree.add(lex.getName());
        }else{
            throw new SAException("unknow instraction");
        }
        tree.previous();
    }


    private void constDeclarations(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        if(KeyWords.CONST.equals(it.next().getName())){
            tree.add("CONST");
            try {
                constDeclarationsList(it,tree.add("constDeclarationsList"));
            }catch (SAException ex){
                if(!ex.isError()) {
                    it.previous();
                    tree.removeChain(node);
                }else
                    throw ex;
            }
        }else{
            throw new SAException("CONST excepted");
        }
        tree.previous();
    }

    private void constDeclaration(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);

        constIdentifier(it, tree.add("constIdentifier"));

        if(Delimeters.EQUAL.equals(it.next().getName())){
            tree.add("=");

            constant(it, tree.add("constant"));
            if(!Delimeters.STOP_LINE.equals(it.next().getName())){
                makeError("; excepted", it);
            }
            tree.add(";");
        }else{
            makeError("= excepted", it);

        }
        tree.previous();
    }

    private void constDeclarationsList(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
            tree.next(node);

            constDeclaration(it,tree.add("constDeclaration"));

            constDeclarationsList(it,tree.add("constDeclarationsList"));
            tree.previous();
    }

    private void constIdentifier(ListIterator<Lexem> it, Node node)throws SAException {
        tree.next(node);

        identifier(it, tree.add("identifier"));
        Lexem lex = it.previous();
        if(constIdentSet.contains(lex.getName()) || varIdentSet.contains(lex.getName())){
            makeError("identifier " + lex.getName() + " already exist", it);
        }
        constIdentSet.add(lex.getName());
        it.next();
        tree.previous();
    }

    private void varDeclarations(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        if(KeyWords.VAR.equals(it.next().getName())){
            tree.add("VAR");
            try {
                declarationsList(it, tree.add("declarationsList"));
            }catch (SAException ex){
                if(!ex.isError()) {
                    it.previous();
                    tree.removeChain(node);
                }else
                    throw ex;
            }
        }else{
            throw new SAException("VAR excepted");
        }
        tree.previous();
    }

    private void declarationsList(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);

        declaration(it, tree.add("declaration"));

        declarationsList(it, tree.add("declarationsList"));
        tree.previous();
    }

    private void declaration(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);

        variableIdentifier(it, tree.add("variableIdentifier"));

        identifiersList(it, tree.add("identifiersList"));
        if(Delimeters.COLON.equals(it.next().getName())){
            tree.add(":");
            attribute(it, tree.add("attribute"));
            if(!Delimeters.STOP_LINE.equals(it.next().getName())){
                makeError("; excepted", it);
            }
            tree.add(";");
        }else{
            makeError(": excepted", it);
        }
        tree.previous();
    }

    private void variableIdentifier(ListIterator<Lexem> it, Node node)throws SAException {
        tree.next(node);

        identifier(it,  tree.add("identifier"));
        Lexem lex = it.previous();
        if(constIdentSet.contains(lex.getName()) || varIdentSet.contains(lex.getName())){
            makeError("identifier " + lex.getName() + " already exist", it);
        }
        varIdentSet.add(lex.getName());
        it.next();
        tree.previous();
    }

    private void identifiersList(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        if(Delimeters.COMA.equals(it.next().getName())){
            tree.add(",");
            variableIdentifier(it, tree.add("variableIdentifier"));
            identifiersList(it, tree.add("identifiersList"));
        }else{
            it.previous();
            tree.removeChain(node);
        }
        tree.previous();
    }

    private void attribute(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        Lexem lex = it.next();
        if(KeyWords.SIGNAL.equals(lex.getName())){
            tree.add("SIGNAL");
        }else if(KeyWords.COMPLEX.equals(lex.getName())){
            tree.add("COMPLEX");
        }else if(KeyWords.INTEGER.equals(lex.getName())){
            tree.add("INTEGER");
        }else if(KeyWords.FLOAT.equals(lex.getName())){
            tree.add("FLOAT");
        }else if(KeyWords.BLOCKFLOAT.equals(lex.getName())){
            tree.add("BLOCKFLOAT");
        }else if(KeyWords.EXT.equals(lex.getName())){
            tree.add("EXT");
        }else{
            if(Delimeters.SQUARE_BRACKET_B.equals(lex.getName())){
                tree.add("[");
                range(it, tree.add("range"));
                rangesList(it, tree.add("rangesList"));
                if(!Delimeters.SQUARE_BRACKET_E.equals(it.next().getName())){
                    makeError("] excepted", it);
                }
                tree.add("]");
            }else{
                makeError(lex.getName() + " unknown attribute", it);
            }
        }
        tree.previous();
    }


    private void range(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException {
        tree.next(node);
        unsignedInteger(it, tree.add("unsignedInteger"));
        if(Delimeters.RANGE.equals(it.next().getName())){
            tree.add("..");
            unsignedInteger(it, tree.add("unsignedInteger"));
        }else{
            makeError(".. excepted", it);
        }
        tree.previous();
    }

    private void rangesList(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        if(Delimeters.COMA.equals(it.next().getName())){
            tree.add(",");
            range(it, tree.add("range"));
            rangesList(it, tree.add("rangesList"));
        }else{
            it.previous();
            tree.removeChain(node);
        }
        tree.previous();
    }

    //change it in future
    private void unsignedInteger(ListIterator<Lexem> it, Node node)throws SAException, NoSuchElementException{
        tree.next(node);
        Lexem lex = it.next();
        if(!isConstant(lex.getCode())){
            makeError(lex.getName() + " is not constant", it);
        }
        tree.add(lex.getName());
        tree.previous();
    }


}

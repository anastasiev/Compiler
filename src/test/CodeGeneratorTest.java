import analizer.CodeGenerator;
import analizer.LexAnalyser;
import analizer.SyntaxAnalyser;
import data.CharReader;
import org.junit.Test;
import tree.Node;
import view.DotTreeViewer;
import view.TreeViewer;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * Created by dmytro on 26.05.16.
 */
public class CodeGeneratorTest {

    @Test
    public void testMakeConst() throws Exception {
        LexAnalyser analyser = new LexAnalyser();
        InputStreamReader inp = new InputStreamReader(
                new FileInputStream("/home/dmytro/IdeaProjects/Compiler/src/main/resources/test.sgn"));
        CharReader reader = new CharReader(inp);
        analyser.analyse(reader);
        reader.close();

        SyntaxAnalyser syntaxAnalyser = new SyntaxAnalyser(analyser.getLexems());
        try {
            syntaxAnalyser.analyse();
            TreeViewer viewer = new DotTreeViewer("test.gv", syntaxAnalyser.getTree().getRoot());
            viewer.parseTree();
            Node root = syntaxAnalyser.getTree().getRoot();
            root = root.getChildren().get(3); //get block node
            CodeGenerator generator = new CodeGenerator(root);

            generator.generateCode();
            generator.createAsmFile();
        }catch (Exception ex){
            System.out.println("Something was wrong");
        }
    }
}
package view;

import tree.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by dmytro on 29.04.16.
 */
public class DotTreeViewer extends TreeViewer {
    private final String ARROW = " -> ";
    private final String STOP_LINE = ";";
    private int nodeId = 0;

    public DotTreeViewer(String fileName, Node root) throws IOException {
        super(fileName, root);
    }


    private void addLabel(BufferedWriter bw, Node node, int id) throws IOException {
        if(node.getInfo().equals("\\"))
            node.setInfo("\\\\");
        bw.write(id+"[label=\""+node.getInfo()+"\"];");
        bw.newLine();
    }
    private void prepareTerminal(BufferedWriter bw, int id, Node node)throws IOException {
        if(node.getInfo().equals("\\"))
            node.setInfo("\\\\");
        bw.write(id+"[style=filled,color=\".7 .3 1.0\",label=\""+node.getInfo()+"\"];");
        bw.newLine();
    }
    private void draw(BufferedWriter bw, int currentId, int childId) throws IOException {
        bw.write(currentId + ARROW + childId + ";");
        bw.newLine();
    }
    private void drawNode(BufferedWriter bw, Node node) throws IOException {
        int currentId = nodeId;
        for(Node child:node.getChildren()){
            nodeId++;
            if(child.getChildren().isEmpty()){
                prepareTerminal(bw, nodeId, child);
                draw(bw, currentId, nodeId);
            }else{
                addLabel(bw, node, currentId);
                draw(bw, currentId, nodeId);
                drawNode(bw, child);
            }
        }
    }
    @Override
    public void parseTree() throws IOException {
        FileWriter fw =new FileWriter(file.getAbsoluteFile());

        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("digraph G {\n" +
                "node [shape=box];");
        bw.newLine();
        drawNode(bw, root);
        bw.write("}");

        bw.close();


    }
}

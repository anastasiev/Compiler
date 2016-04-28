package view;

import tree.Node;

import java.io.File;
import java.io.IOException;

/**
 * Created by dmytro on 29.04.16.
 */
public abstract class TreeViewer {
    protected String fileName;
    protected Node root;
    protected File file;
    public TreeViewer(String fileName, Node root) throws IOException {
        this.fileName = fileName;
        this.root = root;
        file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
    public abstract void parseTree() throws IOException;


}

package tree;

import java.util.ArrayList;

/**
 * Created by dmytro on 26.04.16.
 */
public class Node {
    private String info;
    private Node parent;
    private int number;
    private ArrayList<Node> children = new ArrayList<>();

    public Node(String info, int number) {
        this.info = info;
        this.number = number;
    }

    public void incNumber(){number++;}

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void addChild(Node node){
        node.setParent(this);
        children.add(node);
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return info;
    }
}

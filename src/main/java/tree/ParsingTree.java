package tree;

import java.util.ArrayList;

/**
 * Created by dmytro on 26.04.16.
 */
public class ParsingTree {
    private Node root;

    public Node getRoot() {
        return root;
    }

    private Node currentNode;

    private int nodesCount = 0;

    public ParsingTree(String rootName) {
        this.root = new Node(rootName, nodesCount);
        currentNode = root;
    }

    public Node add(String nodeName){
        Node node = new Node(nodeName, nodesCount);
        currentNode.addChild(node);
        return node;
    }

    public void next(Node nextNode){
        nodesCount++;
        ArrayList<Node> children = currentNode.getChildren();
        currentNode = children.get(children.size() - 1);
    }

    public void previous(){
        if(currentNode.getParent() != null) {
            currentNode = currentNode.getParent();
            nodesCount--;
        }
    }

    public void removeChain(Node node){
        int removeCount = nodesCount - node.getNumber() - 1;
        for(int i = 0; i<removeCount; i++) {
            Node tmp = currentNode;
            previous();
            if (currentNode != root && tmp.getChildren().isEmpty())
                currentNode.getChildren().remove(tmp);
        }
    }

}

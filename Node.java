package com.company;
import java.util.ArrayList;
import java.util.List;
public class Node {
    public List<Node> children = new ArrayList<Node>();
    public Node parent;
    public String data;
    public int attIndex = 10 ;

    public Node(String  data, int attIndex) {
            this.data = data;
            this.attIndex = attIndex;
        }
    public Node(String  data) {
        this.data = data;
        }
    public Node(String data, int attIndex, Node parent) {
            this.data = data;
            this.parent = parent;
            this.attIndex= attIndex;
        }
        public List<Node> getChildren() {
            return children;
        }

        public void setParent(Node parent) {
            parent.addChild(this);
            this.parent = parent;
        }

        public void addChild(String data, int attIndex) {
            Node child = new Node(data, attIndex);
            child.setParent(this);
            this.children.add(child);
        }

        public void addChild(Node child) {
            child.setParent(this);
            this.children.add(child);
        }

        public String getData() {
            return this.data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public boolean isRoot() {
            return (this.parent == null);
        }

        public boolean isLeaf() {
            return this.children.size() == 0;
        }

        public void removeParent() {
            this.parent = null;
        }
    }

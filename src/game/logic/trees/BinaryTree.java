package game.logic.trees;


import game.entities.Dragon;

public class BinaryTree {

    private TreeNode root;

    public BinaryTree(){

    }

    public boolean contains (double element){
        return this.contains(element, this.root);
    }

    private boolean contains (double element, TreeNode node){
        if (node == null){
            return false;
        } else {
            if (element < node.element){
                return contains(element, node.left);
            } else if (element > node.element){
                return contains(element, node.right);
            }
            else{
                return true;
            }
        }
    }

    public TreeNode findMin(){
        if (root == null){
            return null;
        }
        else{
            return findMin(this.root);
        }
    }

    private TreeNode findMin (TreeNode node){
        if (node == null){
            return null;
        }else if(node.left == null){
            return node;
        }else{
            return findMin(node.left);
        }
    }

    public TreeNode findMax(){
        if (root == null){
            return null;
        }
        else{
            return findMax(this.root);
        }
    }

    private TreeNode findMax(TreeNode node){
        if (node == null){
            return null;
        }else if(node.right == null){
            return node;
        }else{
            return findMin(node.right);
        }
    }

    private TreeNode addRecursive(TreeNode current, double value) {
        if (current == null) {
            return new TreeNode(value);
        }
        if (value < current.element) {
            current.left = addRecursive(current.left, value);
        } else if (value > current.element) {
            current.right = addRecursive(current.right, value);
        } else {
            // value already exists
            return current;
        }
        return current;
    }

    private TreeNode addRecursiveDragon(TreeNode current, double value, Dragon dragon) {
        if (current == null) {
            return new TreeNode(value, dragon);
        }
        if (value < current.element) {
            current.left = addRecursiveDragon(current.left, value, dragon);
        } else if (value > current.element) {
            current.right = addRecursiveDragon(current.right, value, dragon);
        } else {
            // value already exists
            return current;
        }
        return current;
    }

    public void add (double value){
        root = addRecursive(root, value);
    }

    private void addDragon (double value, Dragon dragon){
        root = addRecursiveDragon(root, value, dragon);
    }

    public void addDragon (Dragon dragon){
        addDragon(dragon.getAge(), dragon);
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }
}


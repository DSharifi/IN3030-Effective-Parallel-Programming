class Tree {
    Node root;    

    class Node {
        int data;
        Node left = null;
        Node right = null;

        Node(int data) {
            this.data = data;
        }
    }

    Tree(int data) {
        this.root = new Node(data);
    }


    Node getParent(String path) {
        Node parent = root;
        char direction;

        for (int i = 0; i < path.length() - 1; i++) {
            direction = path.charAt(i);

            if (direction == 'r')
                parent = parent.right;
            else
                parent = parent.left;
        }

        return parent;
    }


    public void add(int data, String path) {
        Node parent = getParent(path);

        char direction = path.charAt(path.length() - 1);

        if (direction == 'r')
            parent.right = new Node(data);
        else
            parent.left = new Node(data);

    }

    public void addLine(IntList data, String path) {
        if (data.size() == 0)
            return;

        Node parent = getParent(path);
        
        // char direction = path.charAt(path.length() - 1);
        // Node starter = new Node().

        for (int i = 0; i < data.len; i++) {
            parent.left = new Node(data.get(i));
            parent = parent.left;
        }
    }

    public void printContent() {
        traversePrint(root);
    }

    private String printContent(Node node, String content) {
        if (node == null) {
            return "";
        } else {
            content += printContent(node.right, content);
            System.out.println(node.data);
            content += printContent(node.left, content);
            content += node.data;
        }

        return content;
    }

    private void traversePrint(Node node) {
        if (node == null)
            return;

        traversePrint(node.left);
        System.out.println(node.data);
        traversePrint(node.right);
    }

}
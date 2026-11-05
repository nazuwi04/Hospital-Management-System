package Project;

public class PatientRecords {

    private class Node {
        Patient patient;
        Node left, right;
        int height = 1;

        Node(Patient patient) {
            this.patient = patient;
        }
    }

    private Node root;
    private int count;

    // ---------- AVL helpers ----------

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int balanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private Node rotateRight(Node y) {

        Node x = y.left;
        Node moved = x.right;

        x.right = y;
        y.left = moved;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private Node rotateLeft(Node x) {

        Node y = x.right;
        Node moved = y.left;

        y.left = x;
        x.right = moved;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    private Node rebalance(Node node) {

        updateHeight(node);

        int balance = balanceFactor(node);

        if(balance > 1) {

            if(balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }

            return rotateRight(node);
        }

        if(balance < -1) {

            if(balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }

            return rotateLeft(node);
        }

        return node;
    }

    // ---------- Insert ----------

    // Insert patient
    public boolean insert(Patient patient) {
        if(search(patient.getId()) != null) {
            return false;
        }

        root = insertRecursive(root, patient);
        count++;
        return true;
    }

    private Node insertRecursive(Node current, Patient patient) {

        if(current == null) {
            return new Node(patient);
        }

        if(patient.getId() < current.patient.getId()) {
            current.left = insertRecursive(current.left, patient);
        } else {
            current.right = insertRecursive(current.right, patient);
        }

        return rebalance(current);
    }

    // ---------- Search ----------

    // Search patient
    public Patient search(int id) {

        Node result = searchRecursive(root, id);

        return result == null ? null : result.patient;
    }

    private Node searchRecursive(Node current, int id) {

        if(current == null || current.patient.getId() == id) {
            return current;
        }

        if(id < current.patient.getId()) {
            return searchRecursive(current.left, id);
        }

        return searchRecursive(current.right, id);
    }

    // ---------- Duplicate check ----------

    // Find an existing patient with the same name and age or null.
    public Patient findSimilar(String name, int age) {

        return findSimilarRecursive(root, name, age);
    }

    private Patient findSimilarRecursive(Node current, String name, int age) {

        if(current == null) {
            return null;
        }

        if(current.patient.getAge() == age && current.patient.getName().equalsIgnoreCase(name)) {
            return current.patient;
        }

        Patient found = findSimilarRecursive(current.left, name, age);

        return found != null ? found : findSimilarRecursive(current.right, name, age);
    }

    // ---------- Display ----------

    // Display all records
    public void displayRecords() {

        if(root == null) {
            System.out.println("No patient records found.");
            return;
        }

        System.out.println("\n===== PATIENT RECORDS =====");

        inorder(root);

        System.out.println("Total patients: " + count + " | Tree height: " + height(root));
    }

    // Inorder traversal
    private void inorder(Node current) {

        if(current == null) {
            return;
        }

        inorder(current.left);

        current.patient.display();

        System.out.println("----------------------");

        inorder(current.right);
    }
}
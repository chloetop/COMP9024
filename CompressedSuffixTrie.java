import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jigneshkakkad on 4/06/2016.
 *
 *
 * Alright don't have much time (and also because of missed lectures), I am not going to submit the full assignment.!!!
 * So in this submission, I am doing first two task which is to write constucture and find Stirng function.
 *
 * As typical style of mine, have created a NODE class which holds the information related to tree (Charactor and Map)
 * Format
 *
 * Constucture reads the input file and calls the helper functions to create tree.
 *
 * The detail description of each functions as below
 *
 *
 */
public class CompressedSuffixTrie {
    Node rootNode = null;


    String fileData = null;
    private static final String END_STRING = "$";
    private static final int DEFAULT_INDEX = -1;

    /**
     *
     * This is the helper method to read the file - I have used the same method in the other assignment.
     * Not sure if I needed to give any other explaination.!!!!!!!!
     *
     * @param fileNameWithFullPath
     * @return
     * @throws Exception
     */
    private String readFileContent(String fileNameWithFullPath) throws Exception{
        StringBuilder sb = new StringBuilder();
        File sourceFile = new File(fileNameWithFullPath);

        if(!sourceFile.exists()){
            throw new Exception("File Doesn't exist");
        }

        FileInputStream fis = new FileInputStream(sourceFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    /**
     * Simple getter and setter method to set the fle data value.
     *
     * @param s
     */
    private void setFileData(String s){
        this.fileData = s;
    }
    private String getFileData(){
        return this.fileData;
    }


    /**
     *
     * The first task  - to write a constructor which reads the data from the input and create a compressed tree
     *
     * As usual, it calls the read content file and star processing the data.
     *
     * It calls the create and createHelper method which basically traverse through the tree
     * and reads the edge.
     *
     * As per my finding
     *
     * Traversing a suffix tree would take = O(n * log n)
     * and reading edges would take m = | edge |
     *
     * So total time complexicy of this would be O(mn * log n)
     *
     * @param file
     */
    public CompressedSuffixTrie(String file){
        StringBuilder sb = new StringBuilder();
        try{
            String fileContent = this.readFileContent(file);
            if(fileContent != null && fileContent.length() > 0){
                this.setFileData(fileContent);
                sb.append(fileContent).append(END_STRING);
                this.createHelper(sb.toString());
            }
        }catch(Exception exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    private boolean isCorrectBranch(Node node, String string){
        return (!(node.getEdge().length() == 0 || string.length() == 0)) && node.getEdge().charAt(0) == string.charAt(0);
    }

    private String getSubString(String string, int index){
        if(index == -1){
            return string;
        }else{
            return string.substring(index);
        }
    }

    private Node createNode(String string, int index){
        Node node = new Node();
        node.setEdge(string);
        node.setStart(index);
        return node;
    }

    private void create(Node node, String string, int index, int differentInIndex, boolean needToCreateABranch){
        if(node.getChildren().size() != 0){
            boolean isMatchFound = false;
            for(Node childNode : node.getChildren().values()){
                if(this.isCorrectBranch(childNode, string)){
                    int findTheNeededIndex = -1;
                    for(int innerIndex = 1; innerIndex <= childNode.getEdge().length(); innerIndex++){
                        try {
                            if (childNode.getEdge().charAt(innerIndex) != string.charAt(innerIndex)) {
                                findTheNeededIndex = innerIndex;
                                break;
                            }
                        } catch (Exception e) {
                            findTheNeededIndex = innerIndex;
                        }
                    }
                    string = this.getSubString(string, findTheNeededIndex);

                    if(string.length() == 0) continue;

                    isMatchFound = true;
                    needToCreateABranch = childNode.getChildren().size() != 0 && findTheNeededIndex < childNode.getEdge().length();
                    this.create(childNode, string, index, findTheNeededIndex, needToCreateABranch);
                    node.setStart(index);
                    break;
                }
            }
            if (!isMatchFound && string.length() >= 1) {
                node.addChild(string.charAt(0), this.createNode(string, index));
                node.setStart(index);
            }
        }else{
            if(needToCreateABranch){
                String edge = node.getEdge();

                String updatedEdge = edge.substring(0, differentInIndex);
                String newEdge = edge.substring(differentInIndex);

                Node anotherChild = this.createNode(newEdge, node.getStart());
                anotherChild.clone(node);
                node.createBranch(newEdge.charAt(0), anotherChild);
                Node child2 = this.createNode(string, index);
                node.addChild(string.charAt(0), child2);
                node.setEdge(updatedEdge);
                node.setStart(index);
            }else{
                if (node == this.rootNode) {
                    node.addChild(string.charAt(0), this.createNode(string, index));
                } else {
                    String edge = node.getEdge();
                    String updatedEdge = edge.substring(0, differentInIndex);;
                    String newEdge = edge.substring(differentInIndex);
                    node.addChild(newEdge.charAt(0), this.createNode(newEdge, node.getStart()));
                    node.addChild(string.charAt(0), this.createNode(string,index));
                    node.setEdge(updatedEdge);
                    node.setStart(index);
                }
            }
        }
    }

    private void createHelper(String fileData){
        rootNode = new Node();
        rootNode.setStart(DEFAULT_INDEX);
        for(int index = fileData.length() - 1 ; index >= 0 ; index--){
            String stringToBeProcessed = fileData.substring(index);

            if(stringToBeProcessed.length() == 0){
                continue;
            }else{
                this.create(rootNode, stringToBeProcessed, index, 0, false);
            }

        }
    }

    /**
     * As per my finding
     *
     * Traversing a suffix tree would take = O(n * log n) However, with the search pattern it would be O(m)
     * where m is |s|
     *
     * so the overall time complexity would be O(m)
     *
     */

    public int findString(String s){
        int returnIndex = DEFAULT_INDEX;
        Node currentNode = this.rootNode;
        boolean inEdge = false;
        int edgeIndex = 0;

        if (s.length() == this.getFileData().length())
            return 0;

        for (int i = 0; i < s.length(); i++) {

            if (inEdge) {
                try {
                    if (s.charAt(i) == currentNode.getEdge().charAt(edgeIndex)) {
                        edgeIndex++;
                        continue;
                    } else {
                        return DEFAULT_INDEX;
                    }
                } catch (Exception e) {
                    inEdge = false;
                    i--;
                }
            } else {
                try {
                    currentNode = currentNode.getChildren().get(s.charAt(i));
                    if (currentNode.getEdge().length() > 1) {
                        inEdge = true;
                        edgeIndex = 1;
                    }

                    returnIndex = currentNode.getStart();
                } catch (Exception e) {
                    // If there is no match found
                    return -1;
                }
            }
        }

        return returnIndex;

    }

    public static float similarityAnalyser(String f1, String f2, String f3){
        return 0f;
    }


    public static void main(String[] args) {
        CompressedSuffixTrie trie1 = new CompressedSuffixTrie("//Users//jigneshkakkad//Development//UniAssignment//Sem2Assignment//file1.txt");

        System.out.println("ACTTCGTAAG is at: " + trie1.findString("ACTTCGTAAG"));

        System.out.println("AAAACAACTTCG is at: " + trie1.findString("AAAACAACTTCG"));

        System.out.println("ACTTCGTAAGGTT : " + trie1.findString("ACTTCGTAAGGTT"));

        System.out.println(CompressedSuffixTrie.similarityAnalyser("file2.txt", "file3.txt", "file4.txt"));

    }
}
class Node{
    // fields of Node class
    private Map<Character, Node> children;
    private String edge;
    private int start;

    public Node(){
        children = new HashMap<Character, Node>();
        edge = "";
    }

    public Map<Character, Node> getChildren() {
        return children;
    }

    public void setChildren(Map<Character, Node> children) {
        this.children = children;
    }

    public String getEdge() {
        return edge;
    }

    public void setEdge(String edge) {
        this.edge = edge;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void addChild(Character key, Node child) {
        this.children.put(key, child);
    }

    public void createBranch(Character key, Node child) {
        this.children.clear();
        this.children.put(key, child);
    }

    public void clone(Node cloneNode) {
        for (Node child : cloneNode.getChildren().values()) {
            this.children.put(child.getEdge().charAt(0), child);
        }
    }
}
package ass1;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by jigneshkakkad on 30/03/2016.
 */
public class MyDlist extends DList {

    private static final String STDIN_NAME = "stdin";
    private static final String FILE_WORD_SPLITER = " ";

    /**
     * This method is to check the input string. As per the requirement of a single parameter
     * contructure (that it will be used for two different purposes), we need to use the same
     * for creating list either from console or a file
     *
     * This is a helper method to check if the input stirng is 'stdin' or a file name
     *
     *
     * @param nameToBeChecked
     * @return
     */
    private final boolean isItToReadFromConsole(String nameToBeChecked){
        return STDIN_NAME.equalsIgnoreCase(nameToBeChecked);
    }

    /**
     *
     * The default constructor. It calls parent default constructor which creates an empty list
     * and initialize the requires nodes (i.e. header, tailer)
     *
     */

    public MyDlist(){
        super();
    }

    /**
     *
     * This is another helper method to read the file content if the input string a single parameter
     * constructor is not a 'stdin'
     *
     * It is better to implement a piece of code in a method so that it can be reused. I didn't add this
     * as part of constructor otherwise, it would have been a long, messy code.
     *
     * This method accepts a string argument which is a full path, for example, 'c:/blah/blah/myfile.txt'
     * equivalent in MAC OS. It doesn't check anything else in terms of file existence. It relies on Java IO
     * API and throws an IOException in case if there isn't file or issues while reading the file.
     *
     * It returns an Array List which contains String (which is lines in the input file).
     *
     * @param fileNameWithFullPath
     * @return
     * @throws IOException
     */

    /*private ArrayList<String> readFileContent(String fileNameWithFullPath) throws IOException{
        ArrayList fileContent = new ArrayList();
        File file = new File(fileNameWithFullPath);
        FileInputStream fis = new FileInputStream(file);

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        while ((line = br.readLine()) != null) {
            fileContent.add(line);
        }

        br.close();

        return fileContent;
    }*/

    /**
     *
     * Second required constructor as per the specifiication. The input string 'f' serves two purpose here
     * It can be either 'stdin' or a full path of a file. In the first case, the application should
     * accept values from the console. It should stop accepting when the input value is an empty string.
     *
     * In the second case, it should reads the file. It uses the helper method to read the file.
     *
     * In both cases, it then creates a list with items either entered by the user or read from the file.
     *
     * Extra note - the code uses array list and string split function to get a list of items (i.e. words)
     * and create object of DNode and stores that in the list object.
     *
     *
     * @param f
     */

    public MyDlist(String f){
        this();

        if(this.isItToReadFromConsole(f)){
            //READ from Console
            Scanner scanner = new Scanner(System.in);
            String theEnteredLine = null;

            while(true){
                theEnteredLine = scanner.nextLine();
                if(theEnteredLine.length() == 0){
                    break;
                }
                DNode nodeToBeAdded = new DNode(theEnteredLine, null, null);
                if(isEmpty()){
                    this.addFirst(nodeToBeAdded);
                }else{
                    DNode theNode = this.getLast();
                    this.addAfter(theNode, nodeToBeAdded);
                }

            }
        }else{
            try {
                ArrayList<String> fileContent = null;//this.readFileContent(f);
                for(String lineInTheFile : fileContent){
                    String[] words = lineInTheFile.split(FILE_WORD_SPLITER);
                    for(String aWord : words){
                        DNode nodeToBeAdded = new DNode(aWord, null, null);
                        if(this.isEmpty()){
                            this.addFirst(nodeToBeAdded);
                        }else{
                            DNode theNode = this.getLast();
                            this.addAfter(theNode, nodeToBeAdded);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception while reading the file");
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * Another method as per the specification of this assignment. This method needs to clone the input list
     * and returns the cloned list.
     *
     * Implementation - it get the first node of the input list and traverse through that list. Before entering in the
     * loop, it creates an empty list which is the cloned list.
     *
     * Note on time complexcity
     *
     * Ignoring the object creations as it takes constant time.
     *
     * 'firstList.getFirst();' this statement gets the pointer to the first element. - > time complexity O(1)
     * the first if condition to check whether the returned list is empty of not -> O(1)
     * the else part of the implementation which adds the node after the first node (assuming the addition takes
     * constant time) -> O(1)
     *
     * If and else is part of a loop, which will be executed n times (n being the number of elements in put
     * list).
     *
     * so If part will be execute only once and the else bit will be executed n-1 so the total
     * complexity of this function would be O(n)
     *
     * @param firstList
     * @return
     */

    public static MyDlist cloneList(MyDlist firstList){
        MyDlist clonedList = new MyDlist();
        DNode firstListNode = firstList.getFirst();
        do{
            DNode clonedNode = new DNode(firstListNode.getElement(), null, null);
            if(clonedList.isEmpty()){
                clonedList.addFirst(clonedNode);
            }else{
                clonedList.addAfter(clonedList.getLast(), clonedNode);
            }
            firstListNode = firstList.getNext(firstListNode);
        }while(firstList.hasNext(firstListNode));
        return clonedList;
    }

    /**
     * Method as per the specification. This method is to print items. It doesn't need any input parameters.
     * The object on which this method gets called, the items of that list, will be printer.
     * i.e. from the main method if firstList calls this method, then it will print the items
     * of the first list.
     *
     */

    public void printList(){
        DNode node = this.getFirst();
        do{
            System.out.println(node.getElement());
            node = this.getNext(node);
        }while(this.hasNext(node));
    }

    /**
     *
     * This is another helper method to check if the list object who is calling this method contains the input (argument)
     * node.
     *
     * Of course, it uses node.getElement value to compare the values of this list and the input node.
     *
     * This is being used as part of union and intersection methods.
     *
     * Note on time complexity
     *
     * As per the previous comments, assuming getFirst funtion take only constant time. O(1)
     * and the check if condition takes O(1).
     *
     * As soon as it finds the match it breaks the loop. However, in worst case, the if condition
     * will be executed n times (n being the size of the list).
     *
     * time complexity of this function would be O(n)
     *
     *
     * @param checkTheNode
     * @return
     */

    private boolean contains(DNode checkTheNode){
        boolean contains = false;
        DNode node = this.getFirst();
        do{
            if(node.getElement().equals(checkTheNode.getElement())){
                contains = true;
                break;
            }
            node = this.getNext(node);
        }while(this.hasNext(node));
        return contains;
    }

    /**
     *
     * This another requested method as per the specification. It unions two lists, as per the definition of
     * union function, it stores unique values from two lists (if two lists have a list of common values then
     * it stores only one copy in the returned list).
     *
     * Note of time complexity
     *
     * As we figured out in the previous comment
     *
     * cloneList method time complexity - > O(n)
     *
     * contains method time complexity is -> O(n)
     *
     * and the if block is part of a loop which traverses the second list so the time complexity of that block
     * of code is O(n seq 2)
     *
     * so total time complexity would be O(n) + O(n seq 2)
     * dropping the O(n) as per the big Oh rules. So the time complexity would be O(n seq 2)
     *
     * @param u
     * @param v
     * @return
     */

    public static MyDlist union(MyDlist u, MyDlist v){
        MyDlist returnList = cloneList(u);

        DNode node = v.getFirst();
        do{
            if(!returnList.contains(node)){
                returnList.addAfter(returnList.getLast(), new DNode(node.getElement(), null, null));
            }
            node = v.getNext(node);
        }while(v.hasNext(node));

        return returnList;
    }


    /**
     *
     * This is the final method as per the specification which returns a list which contains a list of common items
     * the items which apprear in both input lists.
     *
     * Time complexity
     *
     * As per the previous comment,
     *
     * contains  -> O(n)
     *
     * As per the previous assumption, addAfter and addFirst takes constant time. However, these two blocks and contains
     * method will be executed n times.
     *
     * So the time complexity of this method would be O(n seq 2).
     *
     * @param u
     * @param v
     * @return
     */


    public static MyDlist intersection(MyDlist u, MyDlist v){
        MyDlist returnList = new MyDlist();

        DNode node = u.getFirst();
        do{
            if(v.contains(node)){
                if(returnList.isEmpty()){
                    returnList.addFirst(new DNode(node.getElement(), null, null));
                }else{
                    returnList.addAfter(returnList.getLast(), new DNode(node.getElement(), null, null));
                }
            }
            node = u.getNext(node);
        }while(u.hasNext(node));
        return returnList;
    }

    public static void main(String[] args) {

        System.out.println("please type some strings, one string each line and an empty line for the end of input:");
        /** Create the first doubly linked list
         by reading all the strings from the standard input. */
        MyDlist firstList = new MyDlist("stdin");

        /** Print all elememts in firstList */
//        firstList.printList();

        /** Create the second doubly linked list
         by reading all the strings from the file myfile that contains some strings. */

        /** Replace the argument by the full path name of the text file */

        MyDlist secondList = new MyDlist("/Users/jigneshkakkad/Development/UniAssignment/Sem2/src/main/resources/myfile.txt");

        /** Print all elememts in secondList */
        secondList.printList();

        /** Clone firstList */
        MyDlist thirdList = cloneList(firstList);

        /** Print all elements in thirdList. */
        thirdList.printList();
//
        /** Clone secondList */
        MyDlist fourthList = cloneList(secondList);
//
        /** Print all elements in fourthList. */
        fourthList.printList();
//
        /** Compute the union of firstList and secondList */
        MyDlist fifthList = union(firstList, secondList);
//
//        /** Print all elements in thirdList. */
        fifthList.printList();
//
//        /** Compute the intersection of thirdList and fourthList */
        MyDlist sixthList = intersection(thirdList, fourthList);
//
//        /** Print all elements in fourthList. */
        sixthList.printList();
    }
}

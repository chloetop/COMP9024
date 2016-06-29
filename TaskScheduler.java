import net.datastructures.HeapPriorityQueue;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Jiggy on 10/05/2016.
 *
 * Right so this is the third assignment and hopefully the final one. I might not be able to write much about here
 * because I have missed these lectures when this assingment was release. So just following what I have read from
 * the specifiation file.
 *
 * We will need to write a program to schedule a list of tasks for an embeded system with X number of cores.
 * As suggested in the specificiaton document that we should be used EDF (Early Deadline First) strategy using
 * heap based priority queue.
 *
 * I must say I had google a bit about EDF and the heap based priority queue but fortunately I managed to figure out
 * what was required to complete this task.
 *
 * As usual, I have created some class which either helpers or beans
 *
 * Also, I have a list of helper methods to do the Job.
 *
 * Each method's description / explanation is mentioned
 *
 * Before going into the details of my class, I must say that I might have violated the space complexity
 * requirement. But it wasn't critical enough so didn't bother to refactor my code. I have used
 * array list at more placed than I should have.!!!!!
 *
 *
 */
public class TaskScheduler {

    /**
     *
     * An enum to store the default / static messages.
     *
     * It has a getter method to get a static message.
     *
     */
    public enum OUTPUT_ERROR_MESSAGE{
        FILE_NOT_EXIST("file1 does not exist"),
        UNKNOWN_ERROR("Not sure what happened!!!!!!"),
        NO_FEASIBLE_SCHEDULE_EXISTS("No feasible schedule exists."),
        INVALID_FORMAT("input error when reading the attributes of the task %s");

        private String s;
        OUTPUT_ERROR_MESSAGE(String s){
            this.s  = s;
        }
        public String getMessage(){
            return this.s;
        }

    }

    /**
     *
     * A helper method to read the file content.
     *
     * It also checks if the file exist or not, if there isn't any input file or system can't find any
     * input file, in that case, it throws an exception  - File Doesn't exist
     *
     * Or any other file operation error, it throws an IO exception
     *
     * It returns an array list of string. each record in the list is one line from the input file
     *
     * @param fileNameWithFullPath
     * @return
     * @throws FileDoesntExistException
     * @throws IOException
     */
    private ArrayList<String> readFileContent(String fileNameWithFullPath) throws FileDoesntExistException, IOException {
        ArrayList<String> fileContent = new ArrayList<String>();
        File sourceFile = new File(fileNameWithFullPath);

        if(!sourceFile.exists()){
            throw new FileDoesntExistException(OUTPUT_ERROR_MESSAGE.FILE_NOT_EXIST.getMessage());
        }

        FileInputStream fis = new FileInputStream(sourceFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        while ((line = br.readLine()) != null) {
            fileContent.add(line);
        }
        br.close();
        return fileContent;
    }

    /**
     *
     * Another helper method to write the content of input Array List of String into the
     * input file name.
     *
     * As per the specification, we are require to check if the file exist or not. It does
     * all these necessary checks and writes the content to the file.
     *
     * Any file IO issues, it simply write something on the console.
     *
     *
     * @param fileNameWithFullPath
     * @param data
     */
    private void writeFileContent(String fileNameWithFullPath, ArrayList<String> data){
        File destinationFile ;
        FileOutputStream fos = null;
        try{
            destinationFile  = new File(fileNameWithFullPath);
            fos = new FileOutputStream(destinationFile);
            for(String line : data){
                fos.write(line.getBytes());
                fos.write(" ".getBytes());
            }
        }catch(Exception exception){
            System.out.println("Some issues - > "+ exception.getMessage());
        }finally{
            try {
                if(fos != null)
                    fos.close();
            }catch (Exception innerException){

            }
        }
    }

    /**
     *
     * Right something !!!! ;)
     *
     * @param args
     */
    public static void main(String[] args) {
        scheduler("test1.txt", "test2.txt",6);
    }

    /**
     * Not using this method!!!!
     * This is a helper method to convert an array list into the Heap based priority queue.
     * //TODO refactor this one!! - Time complexity is more than the required one.
     * @param list
     * @return
     */
    private HeapPriorityQueue<Integer, TaskBean> getPriorityQueue(ArrayList<TaskBean> list){
        HeapPriorityQueue<Integer, TaskBean> returnList = new HeapPriorityQueue<Integer, TaskBean>();
        for(TaskBean task : list){
            returnList.insert(task.getReleaseTime(), task);
        }
        return returnList;
    }

    /**
     *
     * This method does the most of the core work in terms of processing the input list of tasks
     * and scheduling based on the number of cores and EDF method.
     *
     * It has mainly 3 loops. The most outer loop is for the time unit which basically depends
     * on the number of tasks in the input queue.
     *
     * Second two loops are used to schedule the task.
     *
     * The main time complexity of this we need to consider is
     *
     * 1. Remove min  which I believe O(n)
     * 2. Insert which I believe O(n log n)
     *
     * So the high level time complexity analysis would be
     *
     * O(n) + O(n log n) + O(n)
     *
     * Adding an item in the array list would be constant time (as I'm not maintaing the order).
     *
     * So this method would take O(n log n)  - ignoring the other bits as per the Big O rules.
     *
     * @param queue
     * @param numberOfCores
     * @return
     */
    private ArrayList<String>
        getSortedPriorityQueueBasedOnEDF(HeapPriorityQueue<Integer, TaskBean> queue,
                                         int numberOfCores){

        ArrayList<String> returnList = new ArrayList<String>();
        HeapPriorityQueue<Integer, TaskBean> processingQueue = new HeapPriorityQueue<Integer, TaskBean>();

        for(int timeUnit = 0; !queue.isEmpty(); ++timeUnit){
            while(!queue.isEmpty() && queue.min().getValue().getReleaseTime() == timeUnit){
                TaskBean taskBean = queue.min().getValue();
                queue.removeMin();
                processingQueue.insert(taskBean.getDeadline(), taskBean);
            }
            int coreCounter = 1;
            while(!processingQueue.isEmpty() && coreCounter <= numberOfCores){
                if(processingQueue.min().getKey() <= timeUnit){
                    //TODO return from here!!
                    //returnList.add(OUTPUT_ERROR_MESSAGE.NO_FEASIBLE_SCHEDULE_EXISTS.getMessage());
                    System.out.println(OUTPUT_ERROR_MESSAGE.NO_FEASIBLE_SCHEDULE_EXISTS.getMessage());
                    return returnList;
                }else{
                    TaskBean taskBean = processingQueue.min().getValue();
                    returnList.add(taskBean.getTaskName() + " "+timeUnit);
                    processingQueue.removeMin();
                    coreCounter += 1;
                }
            }
        }

        return returnList;
    }

    /**
     *
     * This is the required method as per the specification. However, this method is calling all the
     * helper methods. Time complexity analysis for this method would be to add the time complexity
     * of all other helper methods.
     *
     * The convert method would take O(n log n) - mainly because it is just to insert the value.
     *
     * getSortedPriorityQueueBasedOnEDF - as discussed before this method takes O(n log n).
     *
     * @param file1
     * @param file2
     * @param m
     */
    static void scheduler(String file1, String file2, int m) {
        TaskScheduler scheduler = new TaskScheduler();
        ArrayList<String> fileContent;
        try{

            fileContent = scheduler.readFileContent(file1);
            //new ContentPrinter(fileContent).forFile(file1).print();
            DataValidatorAndConvertor dataValidatorAndConvertor = new DataValidatorAndConvertor(fileContent);
            HeapPriorityQueue<Integer, TaskBean> listOfTasks = dataValidatorAndConvertor.convert();  // O(n log n)
            ArrayList<String> theFinalSortedList = scheduler.getSortedPriorityQueueBasedOnEDF(listOfTasks, m);
            scheduler.writeFileContent(file2, theFinalSortedList);
        }catch(FileDoesntExistException e){
            fileContent = new ArrayList<String>();
            fileContent.add(e.getMessage());
            scheduler.writeFileContent(file2, fileContent);
        }catch(IOException ioException){
            fileContent = new ArrayList<String>();
            fileContent.add(OUTPUT_ERROR_MESSAGE.UNKNOWN_ERROR.getMessage());
            scheduler.writeFileContent(file2, fileContent);
        }catch(ValidationError validationError){
            fileContent = new ArrayList<String>();
            fileContent.add(validationError.getMessage());
            scheduler.writeFileContent(file2, fileContent);
        }

    }
}
class TaskBean{

    private String taskName;
    private int releaseTime;
    private int deadline;

    public TaskBean(){

    }
    public TaskBean(String taskName, int releaseTime, int deadline){
        this.taskName = taskName;
        this.releaseTime = releaseTime;
        this.deadline = deadline;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(int releaseTime) {
        this.releaseTime = releaseTime;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }




}
class FileDoesntExistException extends Exception{
    public FileDoesntExistException(String message){
        super(message);
    }
}
class ValidationError extends Exception{
    public ValidationError(String message){
        super(message);
    }
}

/**
 *
 * As name suggested this class is to parse the input file content and convert
 * the content into heap based priority queue.
 *
 * It also checks the input format if there is any issues with the format
 * then the convert method throws an exception.
 *
 */

class DataValidatorAndConvertor {

    private final String TASK_NAME_REGEX = "^[a-zA-Z]";
    private final String INTEGER_REGEX = "^[0-9]\\d*$";

    ArrayList<String> content;

    public DataValidatorAndConvertor(ArrayList<String> content){
        this.content = content;
    }

    private boolean validate(String s, String pattern){
        return Pattern.compile(pattern).matcher(s).find();
    }

    /**
     *
     * Alright this is helper method to parse the input strings from the array list and create
     * a heap priority queue.
     *
     * I am also using the reg ex and string builder append method - I am not sure the time complexity of
     * the reg ex. hmm !!
     *
     * So the insert operation for heap based priority queue would take O(n log n). However, it parses m number
     * of lines from the input file.
     *
     * the total time complexity would be O(n log n) * m
     *
     * Assuming that the input file would contain only one line in that case m = 1 and hence the complexity
     * would be O(n log n).
     *
     * Improvement - I could put the logic of this method at the time of reading the file.
     *
     *
     * @return
     * @throws ValidationError
     */
    public HeapPriorityQueue<Integer, TaskBean> convert() throws ValidationError{
        HeapPriorityQueue<Integer, TaskBean> returnList = new HeapPriorityQueue<Integer, TaskBean>();
        StringBuilder sb = new StringBuilder();
        if(content == null){

        }else{
            for(String line : content){
                sb.append(line);
                sb.append(" ");
            }
            String tempString = sb.toString();
            String[] tokens = tempString.replaceAll(" +", " ").split(" ");
            int index = 0;
            while(index < tokens.length){
                TaskBean bean = new TaskBean();
                String taskName = tokens[index];
                if(this.validate(taskName, TASK_NAME_REGEX)){
                    index += 1;
                    bean.setTaskName(taskName);
                }else{
                    System.out.println("Error");
                    throw new ValidationError(
                            String.format(TaskScheduler.OUTPUT_ERROR_MESSAGE.INVALID_FORMAT.getMessage(), taskName));
                }
                String releaseNumber = tokens[index];
                if(this.validate(releaseNumber, INTEGER_REGEX)){
                    index += 1;
                    bean.setReleaseTime(Integer.parseInt(releaseNumber));
                }else{
                    System.out.println("Error");
                    throw new ValidationError(
                            String.format(TaskScheduler.OUTPUT_ERROR_MESSAGE.INVALID_FORMAT.getMessage(), taskName));
                }
                String deadLine = tokens[index];
                if(this.validate(deadLine, INTEGER_REGEX)){
                    index += 1;
                    bean.setDeadline(Integer.parseInt(deadLine));
                }else{
                    System.out.println("Error");
                    throw new ValidationError(
                            String.format(TaskScheduler.OUTPUT_ERROR_MESSAGE.INVALID_FORMAT.getMessage(), taskName));
                }
                returnList.insert(bean.getReleaseTime(), bean);
            }

        }
        return returnList;
    }
}

/***
 *
 * Just the helper class to print values! Nothing else.
 *
 */
class ContentPrinter{
    ArrayList<String> content;
    String fileName;
    public ContentPrinter(ArrayList<String> content){
        this.content = content;
    }

    public ContentPrinter forFile(String fileName){
        this.fileName = fileName;
        return this;
    }
    public void print(){
        if(content == null){
            System.out.println("Nothing to print for file name "+fileName);
        }else{
            System.out.println("Number of lines "+content.size());
            System.out.println("Printing the content for "+fileName);

            for(String line : content){
                System.out.println(line);
            }
        }
    }
}
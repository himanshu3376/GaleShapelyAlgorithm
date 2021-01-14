/********************************* Student Information ********************************* 
 * Name: Himanshu Sehgal
 * Student Number: 8688440
 * Course: CSI 2110
 * Programming Assignment P1
****************************************************************************************/

import java.util.*;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class GaleShapley {
    public static void main(String[] args) throws Exception {
        //Exceptions are used in all methods to catch any null variance in the inputs
        //The file name containing the information required to apply Gale Shapley's algorithm is collected

        System.out.println("Please enter the filename with the extension (i.e 'filename.txt): '");
        Scanner sc = new Scanner(System.in);
        String filename = sc.nextLine();
        
        //An object of the non-static method is created here
        GaleShapley initial = new GaleShapley();

        //Filename given by the user is passed as an argument into the initialize method
        initial.initialize(filename);
    }

    // Global declaration of the Stack, and Priorty Queue to make them availale for all methods

    Stack<Integer> Sue = new Stack<Integer>();
    ArrayList<PriorityQueue<Pair>> PQArr = new ArrayList<PriorityQueue<Pair>>();
    PriorityQueue<Pair> PQ = new PriorityQueue<Pair>();
    PriorityQueue<Pair> PQ1 = new PriorityQueue<Pair>();
    PriorityQueue<Pair> PQ2 = new PriorityQueue<Pair>();

    void initialize(String filename) throws Exception {

        //The file is read and each line is placed into a String array
        ArrayList<String> fileElements = new ArrayList<>(Files.readAllLines(Paths.get(filename)));

        //A string array list is created to store the string names for the employers and students
        //these values are used to represent the output
        ArrayList<String> studentNames = new ArrayList<>();
        ArrayList<String> employerNames = new ArrayList<>();

        // First element is parsed an int to to get size of list
        int listSize = Integer.parseInt(fileElements.get(0));
        
        //Declaration of all Arrays and ArrayLists
        int A[][] = new int[listSize][listSize];
        int eRating[][] = new int[listSize][listSize];
        int employers[] = new int[listSize];
        int students[] = new int[listSize];
        ArrayList<Integer> studentRating1DArr = new ArrayList<>();
        ArrayList<Integer> employerRating1DArr = new ArrayList<>();

        //Fill all the empty arrays with the -1 value that is used in the execution method
        Arrays.fill(employers, -1);
        Arrays.fill(students, -1);
        Arrays.stream(A).forEach(a -> Arrays.fill(a, -1));

        // Push all unmatched employers into stack by index (0,1,2, ... , (Number of employers))
        //Number of employers = Number of Students

        for (int i = 0; i < listSize; i++) {
            Sue.push(i);
        }

        //Fill the string array list with the names of employers and students
        for(int i = 0; i < listSize; i++){
            studentNames.add(fileElements.get(listSize + i + 1));
        }
        for(int i = 0; i < listSize; i++){
            employerNames.add(fileElements.get( i + 1));
        }

        // Populate the employers and students rankings, combine all input pairs into one string array
        ArrayList<String> employerRatings = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            employerRatings.add(fileElements.get((listSize * 2) + 1 + i));
        }
        StringBuilder employerString = new StringBuilder();
        for (String s : employerRatings) {
            employerString.append(s);
            employerString.append(" ");
        }

        //Split the String array obtained above and use "." " " as delimitters to parse each value as an integer
        //Then copy all integer values into an array
        String[] values = employerString.toString().split("\\s+|,\\s*|\\.\\s*");
        Integer[] eResults = new Integer[values.length];
        for (int i = 0; i < values.length; i++) {
            eResults[i] = Integer.parseInt(values[i]);
        }

        //Generate student rating array
        for (int i = 1; i < eResults.length; i = i + 2) {
            studentRating1DArr.add(eResults[i]);
        }
        int index = 0;
        for (int i = 0; i < listSize; i++) {
            for (int j = 0; j < listSize; j++) {
                // 1D Array to 2D array
                A[i][j] = studentRating1DArr.get(index);
                index++;
            }
        }

        //Generate employer ranking array 
        for (int i = 0; i < eResults.length; i = i + 2) {
            employerRating1DArr.add(eResults[i]);
        }
        int index2 = 0;
        for (int i = 0; i < listSize; i++) {
            for (int j = 0; j < listSize; j++) {
                // 1D Array to 2D array
                eRating[i][j] = employerRating1DArr.get(index2);
                index2++;
            }
        }
        
        //Add pair elements into a priority queue and implement the Pair class, order the priority queue 
        //from lowers to highest ranking
        int index3 = 0;
        for (int s = 0; s < listSize-2; s++) {
            for (int e = 0; e < listSize; e++) {
                PQ.add(new Pair(eRating[s][e], index3));
                index3++;
                if (PQ.size() == 3) {
                    PQArr.add(PQ);
                } else {
                    continue;
                }
            }
        }
        
        int index4 = 0;
        for (int s = 1; s < listSize-1; s++) {
            for (int e = 0; e < listSize; e++) {
                PQ1.add(new Pair(eRating[s][e], index4));
                index4++;
                if (PQ1.size() == 3) {
                    PQArr.add(PQ1);
                } else {
                    continue;
                }
            }
        }

        int index5 = 0;
        for (int s = 2; s < listSize; s++) {
            for (int e = 0; e < listSize; e++) {
                PQ2.add(new Pair(eRating[s][e], index5));
                index5++;
                if (PQ2.size() == 3) {
                    PQArr.add(PQ2);
                } else {
                    continue;
                }
            }
        }

        /********System prints for testing*********/
        // System.out.println("");
        // System.out.println(Arrays.toString(eResults));
        // System.out.println("");
        // System.out.println(PQArr.toString());
        // System.out.println(studentNames);
        // System.out.println(employerNames);

        //Pass the arguments required to apply the Gale Shapley algorithm
        execute(students, employers, A, studentNames, employerNames, filename, listSize);
    }

    //The following will apply the Gale Shapley algorithm to pair each employer to a student
    void execute(int[] students, int[] employers, int[][] A, ArrayList<String> studentNames, ArrayList<String> employerNames, String filename, int listSize) throws Exception {
        
        while(!Sue.empty()){

            //All variables declared and initialized
            int employer = -1;
            int stu;
            int pEmployer;

            //The following follows the pseudocode implementation provided
            employer = Sue.pop();
            stu = PQArr.get(employer).poll().student;
            pEmployer = students[stu];

            if (students[stu]==-1){
                students[stu] = employer;
                employers[employer] = stu;
            }else if (A[stu][employer] < A[stu][pEmployer]){
                students[stu] = employer;
                employers[employer] = stu;
                employers[pEmployer] = -1;
                Sue.push(pEmployer);
            }else {
                Sue.push(employer);
            }
        }

        /********System prints for testing*********/
        // System.out.println(Arrays.toString(students));
        // System.out.println(Arrays.toString(employers));

        //The employers[] and students[] arrays are updated above and now their index values will be matched to the respective 
        //string values from the arrays declared in the initialize method.
        //The following will generate an array of the results but also print to console for the user

        String[] output = new String[listSize];
        for(int i = 0; i < listSize; i++){
            output[i] = "Match "+i+" : " + employerNames.get(i).toString() + " - " + studentNames.get(students[i]).toString();
            System.out.println(output[i]);
        }

        //The save metod is called and the respective variables are passed
        save(filename, output, listSize);
    }


    //The sole purpose of this method is to take the output obtained by the execute method in its string form 
    //and save it to a .txt file with the respective naming convention required
    void save(String filename, String[] output, int listSize) throws Exception {
        //An exception is placed if there is an error in the name of the output file
        try (PrintWriter out = new PrintWriter("match_"+filename+".txt")) {
            for(int i = 0; i < listSize; i++ ){
                out.println(output[i]);
                out.flush();
            }
        }catch (Exception e){
        } 
    }
}
/********************************* Student Information ********************************* 
 * Name: Himanshu Sehgal
 * Student Number: 8688440
 * Course: CSI 2110
 * Programming Assignment P1
****************************************************************************************/

class Pair implements Comparable<Pair> {
    //Key and value or employers and students
    Integer employer;
    Integer student;

    public Pair(Integer employer, Integer student) {
        this.employer = employer;
        this.student = student;
    }

    @Override
    public int compareTo(Pair obj) {
        return student - obj.student;
    }

    //Overriden toString to obtain a pair output
    @Override
    public String toString()
    {
        return "(" + employer + ", " + student + ")";
    }
}
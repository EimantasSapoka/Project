/*
 * AssignedAppInfo.java
 *
 * Created on 03 March 2007, 17:26
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

/** class to represent an applicant assigned to a particular post 
 *  for use in the list of assigned applicants associated with a post */
public class AssignedAppInfo implements Comparable<AssignedAppInfo> {
    
    private Applicant a;
    private int rank;
    
    
    /** Creates a new instance of AssignedAppInfo */
    public AssignedAppInfo(Applicant a, int r) {
        this.a = a;
        rank = r;
    }
    
    public Applicant getApplicant(){
        return a;
    }
    
    public void setApplicant(Applicant a){
        this.a = a;
    }
    
    public int getRank(){
        return rank;
    }
    
    public void setRank(int r){
        rank = r;
    }
    
    public int compareTo(AssignedAppInfo ass){
       if (a.getIndex() < ass.getApplicant().getIndex())
          return -1;
       else if (a.getIndex() > ass.getApplicant().getIndex())
          return 1;
       else
          return 0;
    }
}

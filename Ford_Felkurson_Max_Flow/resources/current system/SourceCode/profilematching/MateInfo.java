/*
 * MateInfo.java
 *
 * Created on 01 March 2007, 16:45
 *
 */

package profilematching;

/**
 *
 * @author Rob Info
 */
public class MateInfo {
    
            
    private Applicant a;
    private int rank;
    
    
    /** Creates a new instance of MateInfo */
    public MateInfo(Applicant a, int r) {
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
}

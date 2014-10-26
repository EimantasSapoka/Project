/*
 * Pair.java
 *
 * Created on 26 February 2007, 17:31
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

/** class to represent an (applicant,post) pair */
public class Pair {
    
    private Applicant a;
    private Post p;
    
    /** Creates a new instance of Pair */
    public Pair(Applicant a, Post p) {
        this.a = a;
        this.p = p;
    }
    
    public Applicant getApplicant(){
        return a;
    }
    
    public void setApplicant(Applicant a){
        this.a = a;
    }
    
    public Post getPost(){
        return p;
    }
    
    public void setPost(Post p){
        this.p = p;
    }
}

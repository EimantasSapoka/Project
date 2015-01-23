/*
 * PrefListEntry.java
 *
 * Created on 03 March 2007, 17:33
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

/** class to represent an entry in an applicant's preference list */
public class PrefListEntry {
    
    private Post p;
    private int rank;  // value >= 1 for a genuine rank
    
    /** Creates a new instance of PrefListEntry */
    public PrefListEntry(Post p, int r) {
        this.p = p;
        rank = r;
    }
    
    public Post getPost(){
        return p;
    }
    
    public void setPost(Post p){
        this.p = p;
    }
    
    public int getRank(){
        return rank;
    }
    
    public void setRank(int r){
        rank = r;
    }
}

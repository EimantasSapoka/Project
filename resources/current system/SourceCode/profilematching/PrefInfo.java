/*
 * PrefInfo.java
 *
 * Created on 26 February 2007, 17:07
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

/* class to represent information for a preference list entry - post and rank */
public class PrefInfo {
    
    private Post p;
    private int rank;  // value >= 1 for a genuine rank
    
    /** Creates a new instance of PrefInfo */
    public PrefInfo(Post p, int r) {
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

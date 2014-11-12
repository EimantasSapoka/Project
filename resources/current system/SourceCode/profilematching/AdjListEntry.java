/*
 * AdjListEntry.java
 *
 * Created on 03 March 2007, 17:19
 *
 */

package profilematching;

/**
 *
 * @author Rob Irving
 */
    
  public class AdjListEntry {
    
    private PostVertex pv;
    private int rank;
    
    /** Creates a new instance of Edge */
    public AdjListEntry(PostVertex pv, int r) {
        this.pv = pv;
        rank = r;
    }
    
    public int getRank(){
        return rank;
    }
    
    public void setRank(int r){
        rank = r;
    }
    
    public PostVertex getPostVertex(){
        return pv;
    }
    
    public void setPostVertex(PostVertex pv){
        this.pv = pv;
    }
}

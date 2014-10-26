/*
 * Edge.java
 *
 * Created on 01 March 2007, 15:51
 *
 */

package profilematching;

/**
 *
 * @author Rob Irving
 */
public class Edge {
    
    private PostVertex pv;
    private int rank;
    
    /** Creates a new instance of Edge */
    public Edge(PostVertex pv, int r) {
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

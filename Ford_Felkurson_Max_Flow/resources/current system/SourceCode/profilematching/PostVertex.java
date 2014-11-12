/*
 * PostVertex.java
 *
 * Created on 28 February 2007, 14:47
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

/* class to represent a post vertex in the graph of the instance */
public class PostVertex {
   
   /* a large constant to use as the value for unvisited post vertices */
   private static final int infinity = Integer.MAX_VALUE;
   
   private Post p;                 // the corresponding post
   private ApplicantVertex pred;   // predecessor vertex in an augmenting path
   private int predRank;           // rank in predecessor's perference list'
   private int updated;            // updated at previous iteration?
   private Profile value;          // vertex's 'value' for the greedy and generous algorithms
   private int mcvalue = infinity; // vertex's 'value' for the mincost algorithm'
   
   /** Creates a new instance of PostVertex */
   public PostVertex(Post p, int n) {
      this.p = p;
      value = new Profile(n);
   }
   
   public void reset(){
      pred = null;
      predRank = 0;
      updated = 0;
      mcvalue = infinity;
      resetValue();
   }
   
   public Post getPost(){
      return p;
   }
   
   public void setPred(ApplicantVertex v){
      pred = v;
   }
   
   public ApplicantVertex getPred(){
      return pred;
   }
   
   public void setPredRank(int r){
      predRank = r;
   }
   
   public int getPredRank(){
      return predRank;
   }
   
   public void setUpdated(int i){
      updated = i;
   }
   
   public int getUpdated(){
      return updated;
   }
   
   public void setValue(Profile p){
      value = p;
   }
   
   public Profile getValue(){
      return value;
   }
   
   public void setmcValue(int v){
      mcvalue = v;
   }
   
   public int getmcValue(){
      return mcvalue;
   }
   
   public void resetValue(){
      value.reset();
   }
   
   public boolean isEqual(PostVertex pv){
      return this.p.isEqual(pv.p);
   }
}

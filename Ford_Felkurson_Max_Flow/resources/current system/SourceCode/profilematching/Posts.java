/*
 * Posts.java
 *
 * Created on 23 February 2007, 11:23
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */
public class Posts {
    
    private Post[] jobs;
    private int count;
    
    /** Creates a new instance of Posts */
    public Posts(int n) {
        jobs = new Post[n];
        count = 0;
    }
    
    public void reset(){
       for (Post p : jobs)
          p.reset();
    }
        
    public int getCount(){
        return count;
    }
    
    public void setCount(int n){
        count = n;
    }
    
    public void addPost(Post p){
        jobs[count++] = p;
    }
    
    public Post getPost(int i){
        return jobs[i];
    }
    
    public Post[] getJobs(){
        return jobs;
    }
}

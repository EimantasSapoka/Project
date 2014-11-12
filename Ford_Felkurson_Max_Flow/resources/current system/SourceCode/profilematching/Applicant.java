/*
 * Applicant.java
 *
 * Created on 22 February 2007, 16:44
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */
import java.util.*;

public class Applicant {
    
    private int index;                 // a unique identifier in the range >= 1
    private Post assignedPost;
    private int assignedRank;          // ranks are in the range >= 1
    private ApplicantVertex av;        // the corresponding vertex in the graph of the instance
    private LinkedList<PrefListEntry> prefList;  // the applicant's preference list
    
    public Applicant(int i) {
        index = i;
        prefList = new LinkedList<PrefListEntry>();
    }
    
    public void reset(){
        assignedPost = null;
        assignedRank = 0;
    }
    
    public int getIndex(){
        return index;
    }
    
    public void setIndex(int i){
        index = i;
    }
    
    public Post getAssignedPost(){
        return assignedPost;
    }
    
    public void setAssignedPost(Post p){
        assignedPost = p;
    }
    
    public int getAssignedRank(){
        return assignedRank;
    }
    
    public void setAssignedRank(int i){
        assignedRank = i;
    }
    
    public ApplicantVertex getVertex(){
        return av;
    }
    
    public void setVertex(ApplicantVertex av){
        this.av = av;
    }
    
    public int getPrefListLength(){
        return prefList.size();
    }
    
    public LinkedList<PrefListEntry> getPrefList(){
        return prefList;
    }
    
    public void appendToPrefList(Post p, int r){
        PrefListEntry x = new PrefListEntry(p, r);
        prefList.addLast(x);
    }
    
    public boolean isAssigned(){
        return assignedPost != null;
    }
    
    public boolean isEqual(Applicant a){
        return this.index == a.index;
    }
    
    public String displayAssignment(){
       String s = String.format("%4d%14d%11d",index, assignedPost.getIndex(),assignedRank);
       return s;
    }
    
    public String displayPrefList(){
        String s;
        s = index + ":";
        for (PrefListEntry p : prefList){
            s = s + String.format(" %4d",p.getPost().getIndex());
        }
        return s;
    }
}

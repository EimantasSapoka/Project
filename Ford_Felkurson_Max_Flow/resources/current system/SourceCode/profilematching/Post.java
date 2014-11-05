/*
 * Post.java
 *
 * Created on 22 February 2007, 16:47
 *
 */

package profilematching;

/**
 *
 * @author Rob Irving
 */

import java.util.*;

public class Post {
   
   private int index;           // a unique identifier in range >= 1
   private int capacity;        // number of places available in this post
   private PostVertex pv;       // the corresponding vertex in the graph of the instance
   private TreeSet<AssignedAppInfo> assignedApplicants;  // applicants assigned to this post
   
   public Post(int i) {
      index = i;
      assignedApplicants = new TreeSet<AssignedAppInfo>();
   }
   
   public void reset(){
      assignedApplicants =  new TreeSet<AssignedAppInfo>();
   }
   
   public int getIndex(){
      return index;
   }
   
   public void setIndex(int i){
      index = i;
   }
   
   public int getCapacity(){
      return capacity;
   }
   
   public void setCapacity(int c){
      capacity = c;
   }
   
   public PostVertex getVertex(){
      return pv;
   }
   
   public void setVertex(PostVertex pv){
      this.pv = pv;
   }
   
   public void addToAssignedApplicants(Applicant a, int r){
      AssignedAppInfo ai = new AssignedAppInfo(a, r);
      assignedApplicants.add(ai);
   }
   
   public void removeFromAssignedApplicants(Applicant a){
      Iterator<AssignedAppInfo> Iter = assignedApplicants.iterator();
      while (Iter.hasNext()){
         AssignedAppInfo nextAssignee = Iter.next();
         if (nextAssignee.getApplicant().isEqual(a)){
            Iter.remove();
            break;
         }
      }
   }
   
   public int getNumAssignedApplicants(){
      return assignedApplicants.size();
   }
   
   public TreeSet<AssignedAppInfo> getAssignedApplicants(){
      return assignedApplicants;
   }
   
   public boolean isUndersubscribed(){
      return getNumAssignedApplicants() < capacity;
   }
   
   public boolean isEqual(Post p){
      return this.index == p.index;
   }
   
   public String display(){
      String s = String.format("%3d%12d%10d",index, getNumAssignedApplicants(),capacity);
      return s;
   }
   
   public String displayAssignment(){
      String s;
      s = "      ";
      for (AssignedAppInfo mi : assignedApplicants){
         s = s + String.format(" %5d", mi.getApplicant().getIndex());
      }
      return (String) s;
   }
}

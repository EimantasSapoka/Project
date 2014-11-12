/*
 * Profile.java
 *
 * Created on 26 February 2007, 17:20
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

/** class to represent the profile of a matching, and also the 'value' of a
*   post vertex when searching for an optimal augmenting path */
public class Profile {
   
   // value[i] gives the number of applicants matched to a post of rank i+1
   private int[] value;
   // value[degree-1] is the last non-zero element, so degree is the largest
   // rank represented in the profile , and so is 0 if the matching is empty
   private int degree;
   
   /** Creates a new instance of Profile */
   public Profile(int n) {
      value = new int[n];
      degree = 0;
      for (int i = 0; i < n; i++)
         value[i] = 0;
   }
   
   public void reset(){
      degree = 0;
      for (int i = 0; i < value.length; i++)
         value[i] = 0;
   }
   
   public int[] getValue(){
      return value;
   }
   
   public int getValue(int i){
      return value[i];
   }
   
   public void setValue(Profile p){
      for (int i=0; i < p.getDegree(); i++)
         value[i] = p.getValue(i);
      for (int i= p.getDegree(); i < value.length; i++)
         value[i] = 0;
      degree = p.getDegree();
   }
   
   public int getDegree(){
      return degree;
   }
   
   /** increments the entry for rank i */
   public void inc(int i){
      value[i-1]++;
      if (degree == i && value[i-1] == 0){
         int j = i-1;
         while (j >= 0 && value[j] == 0)
            j--;
         degree = j+1;
      } else if (degree < i)
         degree = i;
   }
   
   /** decrements the entry for rank i */
   public void dec(int i){
      value[i-1]--;
      if (degree == i && value[i-1] == 0){
         int j = i-1;
         while (j >= 0 && value[j] == 0)
            j--;
         degree = j+1;
      } else if (degree < i)
         degree = i;
   }
   
   /** is the first non-zero entry positive? */
   public boolean isPos(){
      if (degree == 0)
         return false;
      else
         for (int i=0; i<degree; i++)
            if (value[i] > 0)
               return true;
            else if (value[i] < 0)
               return false;
      return false;
   }
   
   /* left domination - the appropriate test when searching for an
    ** optimal augmenting path in the greedy context */
   public boolean leftDominates(Profile p){
      int pdeg = p.getDegree();
      int lim;
      if (degree == 0)
         return false;
      if (pdeg == 0)
         return true;
      if (degree < pdeg)
         lim = degree;
      else
         lim= pdeg;
      for (int i=0; i<lim; i++){
         if (this.value[i] > p.value[i])
            return true;
         else if (this.value[i] < p.value[i])
            return false;
      }
      if (degree == pdeg)
         return false;
      else if (degree > pdeg) {
         for (int i = pdeg; i < degree; i++)
            if (value[i] > 0)
               return true;
            else if (value[i] < 0)
               return false;
      } else
         for (int i = degree; i < pdeg; i++)
            if (p.value[i] < 0)
               return true;
            else if (p.value[i] > 0)
               return false;
      return false;
   }
   
    /* right domination - the appropriate test when searching for an
    ** optimal augmenting path in the generous context */
   public boolean rightDominates(Profile p){
      int pdeg = p.getDegree();
      if (degree == 0)
         return false;
      else if (pdeg == 0)
         return true;
      else if (degree > pdeg)
         return false;
      else if (degree < pdeg)
         return true;
      else {
         for (int i=pdeg-1; i>=0; i--){
            if (this.value[i] < p.value[i])
               return true;
            else if (this.value[i] > p.value[i])
               return false;
         }
      }
      return false;
   }
   
   public String display(){
      String s;
      int size = 0;
      int weight = 0;
      for (int i=0; i < degree; i++){
         size += value[i];
         weight += (i+1)*value[i];
      }
      s = "Matching size: " + size + "\n";
      s = s + "Matching weight: " + weight + "\n";
      s = s + "Matching profile: (";
      for (int i=0; i<degree; i++){
         s = s + "" + value[i];
         if (i < degree-1)
            s = s + ",";
      }
      s = s + ")";
      return s;
   }
}

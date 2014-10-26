/*
 * Matching.java
 *
 * Created on 26 February 2007, 17:31
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

import java.util.*;

public class Matching {
   
   LinkedList<Pair> pairs;  // the pairs that constitute the matching
   
   /** Creates a new instance of Matching */
   public Matching() {
      pairs = new LinkedList<Pair>();
   }
   
   public void reset(){
      pairs = new LinkedList<Pair>();
   }
   
   public Profile computeProfile(int n){
      Profile prof = new Profile(n);
      for (Pair p : pairs){
         Applicant a = p.getApplicant();
         if (a.isAssigned()){
            int j = a.getAssignedRank();
            prof.inc(j);
         }
      }
      return prof;
   }
   
   public void addPair(Applicant a, Post p){
      pairs.add(new Pair(a,p));
   }
   
   public void removePair(Applicant a, Post p){
      int i = 0;
      Iterator<Pair> Iter = pairs.iterator();
      while (Iter.hasNext()){
         Pair pr = Iter.next();
         if (pr.getApplicant().isEqual(a) && pr.getPost().isEqual(p)){
            Iter.remove();
            break;
         }
      }
   }
   
   public int size(){
      return pairs.size();
   }
   
   public String display(){
      String s;
      Profile prof = computeProfile(pairs.size());
      s = prof.display() + "\nApplicant  Assigned post  Rank\n";
      for (Pair pr : pairs){
         Applicant a = pr.getApplicant();
         Post p = pr.getPost();
         s = s + a.displayAssignment() + "\n";
      }
      return s;
   }
}

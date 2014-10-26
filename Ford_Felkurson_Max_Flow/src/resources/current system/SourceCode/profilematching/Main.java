/*
 * Main.java
 *
 * Created on 22 February 2007, 16:43
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

import java.util.*;
import java.io.*;

public class Main {
   
   static int numApps = 0;
   static int numPosts = 0;
   static int numPrefs = 0;
   static Applicants apps;
   static Posts ps;
   static Graph g;  // the graph of the instance
   
   static FileWriter writer;
   
   /** input assumed to be of the following form
    * applicants and posts numbered from 0
    * line 1: the number of applicants (n)
    * line 2: the number of posts (m)
    * line i (3 <= i <= n+2):  preference list for applicant i-3
    *        ties represented by parentheses
    *        the parser requires that each parenthesis be followed by a space
    * line j (n + 3 <= j <= n + m + 2): capacity of post j-n-3
    */
   public static void getInput()
   throws IOException {
      final char LEFTPAR = '(';
      final char RIGHTPAR = ')';
      
   /*   Scanner stand = new Scanner(System.in);
      
      System.out.println("Type name of input file: ");
      String inputFileName = stand.nextLine();*/
      FileReader reader = new FileReader("matchinginput.txt");
   /*   System.out.println("Type name of output file: ");
      String outputFileName = stand.nextLine();*/
      writer = new FileWriter("matchingoutput.txt");
      Scanner in = new Scanner(reader);
      try {
         String nl = in.nextLine();
         Scanner stringin = new Scanner(nl);
         numApps = stringin.nextInt();
         nl = in.nextLine();
         stringin = new Scanner(nl);
         numPosts = stringin.nextInt();
      } catch (NumberFormatException e) {
         System.out.println("Invalid format");
         System.exit(1);
      }
      
      apps = new Applicants(numApps);
      ps = new Posts(numPosts);
      
      for (int i = 1; i <= numPosts; i++){
         Post p = new Post(i);
         ps.addPost(p);
      }
      
      for (int i=1; i<=numApps; i++){
         int rank = 0;
         int count = 0;
         boolean inTie = false;
         Applicant a = new Applicant(i);
         String nl = in.nextLine();

         StringBuilder text = new StringBuilder(nl);
         int index = 0;
         while (index != -1 && index<text.length()) {
            index = text.indexOf("(",index); //1st occurrence of "(" after position index
            if (index !=-1) {
               text.replace(index,index+1," ( "); 
               index += 3;
            }
	 }
	 nl = text.toString();
	 text = new StringBuilder(nl);
         index = 0;
         while (index != -1 && index<text.length()) {
            index = text.indexOf(")",index); //1st occurrence of ")" after position index
            if (index !=-1) {
               text.replace(index,index+1," ) "); 
               index += 3;
            }
	 }
	 nl = text.toString();

         Scanner stringin = new Scanner(nl);
         boolean moreInLine = true;
         while (moreInLine){
            while (stringin.hasNextInt()){
               if (!inTie)
                  rank++;
               count++;
               int k = stringin.nextInt();
               if (k < 1 || k > numPosts){
                  System.err.println("post index out of range - program terminating");
                  System.exit(1);
               }
               a.appendToPrefList(ps.getPost(k-1),rank);
            }
            if (stringin.hasNext()){
               String s = stringin.next();
               if (s.length() > 1){
                  System.err.println("badly formatted input");
                  System.err.println("parentheses must be delimited by whitespace - program terminating");
                  System.exit(1);
               }
               if (s.charAt(0) == LEFTPAR){
                  inTie = true;
                  rank++;
                  count = 0;
               } else if (s.charAt(0) == RIGHTPAR){
                  inTie = false;
                  rank = rank + count - 1;
               } else {
                  System.err.println("bad character in input - program terminating");
                  System.exit(1);
               }
            } else
               moreInLine = false;
         }
         apps.addApplicant(a);
      }
      
      for (int i=0; i<numPosts; i++){
         try {
            int c = in.nextInt();
            ps.getPost(i).setCapacity(c);
         } catch (NumberFormatException e) {
            break;
         }
      }
   }
   
   public static void findNaiveMatching(){
      Matching m = new Matching();
      apps.permute();
      for (int i=0; i<numApps; i++){
         Applicant a = apps.getApplicant(i);
         for (PrefListEntry pe : a.getPrefList()){
            Post p = pe.getPost();
            if (p.isUndersubscribed()){
               m.addPair(a,p);
               a.setAssignedPost(p);
               a.setAssignedRank(pe.getRank());
               p.addToAssignedApplicants(a,pe.getRank());
               break;
            }
         }
      }
      apps.restore();
   }
   
   public static void putOutput(int which)
   throws IOException {
      if (which == 1)
         writer.write("Greedy maximum matching\n");
      else if (which == 2)
         writer.write("\n\nGenerous maximum matching\n");
      else if (which == 3)
         writer.write("\n\nMincost maximum matching\n");
      else if (which == 4)
         writer.write("\n\nGreedy generous maximum matching\n"); 
      else
         writer.write("\n\nNaive greedy matching\n"); 
      writer.write(g.getMatching().display());
      if (g.getMatching().size() < numApps) {    
         writer.write("\nUnmatched applicants\n"); 
         writer.write(apps.displayUnmatched() + "\n");
      }
      writer.write("\nPost  Places filled Capacity          Assignees\n");
      for (Post p : ps.getJobs()){
         writer.write(p.display() + p.displayAssignment() + "\n");
      }
   }
   
   /** Program to find and report (1) a greedy maximum matching,
    ** (2) a generous maximum matching, and (3) a mincost maximum
    ** matching for a given instance of the applicants-posts problem.
    * Ties in the preference lists are allowed */
   public static void main(String[] args)
   throws IOException {
      PostVertex finish; // the final vertex in an augmenting path
      int degree;  // the degree of the current profile
      int ultDegree = 0;
      
      getInput();
      
      g = new Graph(numApps, numPosts); // create an empty graph of appropriate size
      g.buildGraph(apps, ps);  // build the actual graph
      // loop over the three kinds of optimal matching
      for (int whichMatching=1; whichMatching <= 3; whichMatching++){
         degree = 1;
         for (int iteration = 0; iteration < numApps; iteration++){
            if (whichMatching < 3)
               finish = g.findAugmentingPath(whichMatching, degree);
            else
               // a simpler augmenting path search in the mincost case
               finish = g.findMinCostAugmentingPath();
            if (finish == null)
               break; // no augmenting path; maximum matching found
            else {
               g.augmentMatching(finish);
               degree = g.getDegree();if (whichMatching == 2)
                  ultDegree = degree;
            }
         }
         
         putOutput(whichMatching);
         
         apps.reset();
         ps.reset();
         g.reset();
      }
      g.purge(ultDegree);
      degree = 1;
      for (int iteration = 0; iteration < numApps; iteration++){
      finish = g.findAugmentingPath(1, degree);
      if (finish == null)
          break; // no augmenting path; maximum matching found
      else {
          g.augmentMatching(finish);
          degree = g.getDegree();
            }
         }
      putOutput(4);
      apps.reset();
      ps.reset();
      findNaiveMatching();
      putOutput(5);
      writer.close();
   }
}


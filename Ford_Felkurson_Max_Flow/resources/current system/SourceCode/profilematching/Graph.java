/*
 * Graph.java
 *
 * Created on 28 February 2007, 15:01
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

import java.util.*;

/* class to represent the graph of an instance */
public class Graph {
   
   private ApplicantVertex[] appVertices;  // the applicant vertices
   private PostVertex[] postVertices;      // the post vertices
   private int numAppVertices = 0;
   private int numPostVertices = 0;
   private int maxRank = 0;             // maximum degree of an applicant vertex
   
   /** Creates a new instance of Graph */
   public Graph(int r, int s) {
      appVertices = new ApplicantVertex[r];
      postVertices = new PostVertex[s];
   }
   
   public void reset(){
      // only the post vertices need to be reset
      for (PostVertex pv : postVertices)
         pv.reset();
   }
   
   private void addAppVertex(ApplicantVertex a){
      appVertices[numAppVertices++] = a;
   }
   
   private void addPostVertex(PostVertex p){
      postVertices[numPostVertices++] = p;
   }
   
   public void buildGraph(Applicants as, Posts ps){
      for (Post p : ps.getJobs()){
         PostVertex pv = new PostVertex(p,ps.getCount());
         addPostVertex(pv);
         p.setVertex(pv);
      }
      for (Applicant a : as.getApps()){
         ApplicantVertex av = new ApplicantVertex(a);
         for (PrefListEntry ple : a.getPrefList()){
            Post p = ple.getPost();
            PostVertex pv = p.getVertex();
            AdjListEntry e = new AdjListEntry(pv, ple.getRank());
            av.addToAdjList(e);
            //if (av.getAdjList().size() > maxDegree)
               //maxDegree = av.getAdjList().size();
            if (ple.getRank() > maxRank)
               maxRank = ple.getRank();
         }
         addAppVertex(av);
         a.setVertex(av);
      } 
   }
   
   public void purge(int mR){
      for (int i = 0; i < appVertices.length; i++)
      {
         ApplicantVertex av = appVertices[i];
         LinkedList<AdjListEntry> aL = av.getAdjList();
         Iterator<AdjListEntry> Iter = aL.iterator();
         while (Iter.hasNext()){
            AdjListEntry e = Iter.next();
            if (e.getRank() > mR)
               Iter.remove();
         }
      }
      maxRank = mR;
   }
   
   public Matching getMatching(){
      Matching m = new Matching();
      for (ApplicantVertex av : appVertices) {
         Applicant a = av.getApplicant();
         if (a.isAssigned()){
            Post p = a.getAssignedPost();
            m.addPair(a,p);
         }
      }
      return m;
   }
   
   private int matchingSize(){
      int matchedApps = 0;
      for (ApplicantVertex av : appVertices) {
         Applicant a = av.getApplicant();
         if (a.isAssigned()){
            matchedApps++;
         }
      }
      return matchedApps;
   }
   
   /** returns the degree of the profile of the matching implicit in the graph */
   public int getDegree(){
      return getMatching().computeProfile(maxRank).getDegree();
   }
   
   /** augment the current matching using an augmenting path
    ** that ends at the post vertex 'finish' */
   public void augmentMatching(PostVertex finish){
      Post nextPost;
      Applicant prevApp = null;;
      Applicant nextApp;
      ApplicantVertex nextAppVertex;
      PostVertex nextPostVertex = null;
      Post currPost = finish.getPost();
      
      while (currPost != null) {
         nextAppVertex = currPost.getVertex().getPred();
         nextApp = nextAppVertex.getApplicant();
         if (nextApp.isAssigned()){
            nextPost = nextApp.getAssignedPost();
            nextPostVertex = nextPost.getVertex();
         } else
            nextPost = null;
         currPost.addToAssignedApplicants(nextApp,currPost.getVertex().getPredRank());
         if (currPost.getVertex() != finish)
            currPost.removeFromAssignedApplicants(prevApp);
         nextApp.setAssignedPost(currPost);
         nextApp.setAssignedRank(currPost.getVertex().getPredRank());
         prevApp = nextApp;
         currPost = nextPost;
      }
   }
   
    /* this method searches for an optimal augmenting path in the graph;
     * if it finds one, it returns the terminating post vertex of the path,
     * otherwise it returns null;
     * the first parameter is 1 for greedy and 2 for generous; these cases
     * are distinguisehd by the use of the leftDominates or rightDominates
     * relation when comparing values */
   public PostVertex findAugmentingPath(int which, int d){
      while (true){
         PostVertex finish = null;
         LinkedList<PostVertex> Q = new LinkedList<PostVertex>();
         for (PostVertex pv : postVertices){
            pv.resetValue();
         }
         for (ApplicantVertex av : appVertices){
            Applicant nextApp = av.getApplicant();
            if (!nextApp.isAssigned())
               for (AdjListEntry e : av.getAdjList()){
                  if (e.getRank() > d)
                     break;
                  PostVertex nextPV = e.getPostVertex();
                  if (nextPV.getValue().getDegree() == 0 ||
                          e.getRank() < nextPV.getValue().getDegree()){
                     if (nextPV.getValue().getDegree() == 0){
                        Q.addLast(nextPV);
                        nextPV.setUpdated(0);
                     }
                     nextPV.resetValue();
                     nextPV.getValue().inc(e.getRank());
                     nextPV.setPred(av);
                     nextPV.setPredRank(e.getRank());
                  }
                  if (nextPV.getPost().isUndersubscribed()){
                     finish = nextPV;
                     return finish;
                  }
               }
         }
         int size = matchingSize();
         for (int i=1; i<=size; i++){
            if (Q.isEmpty())
               break;
            LinkedList<PostVertex> newQ = new LinkedList<PostVertex>();
            while (!Q.isEmpty()){
               PostVertex nextPV = Q.removeFirst();
               if (nextPV.getUpdated() == i-1)
                  nextPV.setUpdated(0);
               if (nextPV.getPost().isUndersubscribed()){
                  if (finish == null ||
                          (which == 1 && nextPV.getValue().leftDominates(finish.getValue())) ||
                          (which == 2 && nextPV.getValue().rightDominates(finish.getValue()))){
                     finish = nextPV;
                  }
               }else {
                  TreeSet<AssignedAppInfo> mates =
                          nextPV.getPost().getAssignedApplicants();
                  for (AssignedAppInfo mi : mates){
                     Applicant nextApp = mi.getApplicant();
                     for ( PrefListEntry x : nextApp.getPrefList()){
                        if (x.getRank() > d)
                           break;
                        PostVertex currPV = x.getPost().getVertex();
                        Profile prof = new Profile(maxRank);
                        prof.setValue(nextPV.getValue());
                        prof.inc(x.getRank());
                        prof.dec(mi.getRank());
                        if ((which == 1 && prof.leftDominates(currPV.getValue())) ||
                                (which == 2 && prof.rightDominates(currPV.getValue()))){
                           currPV.setValue(prof);
                           currPV.setPred(nextApp.getVertex());
                           currPV.setPredRank(x.getRank());
                           if (currPV.getUpdated() != i){
                              newQ.addLast(currPV);
                              currPV.setUpdated(i);
                           }
                           if (currPV.getPost().isUndersubscribed()
                           && (finish == null ||
                                   (which == 1 && currPV.getValue().leftDominates(finish.getValue())) ||
                                   (which == 2 && currPV.getValue().rightDominates(finish.getValue()))))
                              finish = currPV;
                        }
                     }
                  }
               }
            }
            Q = newQ;
         }
         if ((d < maxRank) &&
                 ((which == 2 && finish == null) ||
                 (which == 1 && (finish == null ||  !finish.getValue().isPos())))){
            d++;
         } else
            return finish;
      }
   }
   
     /* this method searches for a mincost optimal augmenting path in the graph;
      * if it finds one, it returns the terminating post vertex of the path,
      * otherwise it returns null; */
   public PostVertex findMinCostAugmentingPath(){
      PostVertex finish = null;
      LinkedList<PostVertex> Q = new LinkedList<PostVertex>();
      for (PostVertex pv : postVertices){
         pv.reset();
      }
      for (ApplicantVertex av : appVertices){
         Applicant nextApp = av.getApplicant();
         if (!nextApp.isAssigned())
            for (AdjListEntry e : av.getAdjList()){
               PostVertex nextPV = e.getPostVertex();
               if ( e.getRank() < nextPV.getmcValue()){
                  Q.addLast(nextPV);
                  nextPV.setUpdated(0);
                  nextPV.setmcValue(e.getRank());
                  nextPV.setPred(av);
                  nextPV.setPredRank(e.getRank());
               }
               if (nextPV.getPost().isUndersubscribed() &&
                       (finish == null || nextPV.getmcValue() < finish.getmcValue())){
                  finish = nextPV;
               }
            }
      }
      int size = matchingSize();
      for (int i=1; i<=size; i++){
         if (Q.isEmpty())
            break;
         LinkedList<PostVertex> newQ = new LinkedList<PostVertex>();
         while (!Q.isEmpty()){
            PostVertex nextPV = Q.removeFirst();
            if (nextPV.getUpdated() == i-1)
               nextPV.setUpdated(0);
            if (nextPV.getPost().isUndersubscribed()){
               if (finish == null ||
                       (nextPV.getmcValue() < finish.getmcValue())) {
                  finish = nextPV;
               }
            }else {
               TreeSet<AssignedAppInfo> mates =
                       nextPV.getPost().getAssignedApplicants();
               for (AssignedAppInfo mi : mates){
                  Applicant nextApp = mi.getApplicant();
                  for ( PrefListEntry x : nextApp.getPrefList()){
                     PostVertex currPV = x.getPost().getVertex();
                     int z = nextPV.getmcValue() + x.getRank() - mi.getRank();
                     if (z < currPV.getmcValue()){
                        currPV.setmcValue(z);
                        currPV.setPred(nextApp.getVertex());
                        currPV.setPredRank(x.getRank());
                        if (currPV.getUpdated() != i){
                           newQ.addLast(currPV);
                           currPV.setUpdated(i);
                        }
                        if (currPV.getPost().isUndersubscribed()
                        && (finish == null || (currPV.getmcValue() < finish.getmcValue())))
                           finish = currPV;
                     }
                  }
               }
            }
         }
         Q = newQ;
      }
      return finish;
   }
}

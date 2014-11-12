/*
 * ApplicantVertex.java
 *
 * Created on 28 February 2007, 14:48
 *
 */

package profilematching;

/**
 * @author Rob Irving
 */

import java.util.*;

/* class to represent an applicant vertex in the graph of an instance */
public class ApplicantVertex {
    
    private Applicant a;                        // the corresponding applicant
    private LinkedList<AdjListEntry> adjList = new LinkedList<AdjListEntry>();
                                  // the adjacency list (of (post,rank) pairs)
     
    /** Creates a new instance of ApplicantVertex */
    public ApplicantVertex(Applicant a) {
        this.a = a;
    }
    
    public void setApplicant(Applicant a){
        this.a = a;
    }
    
    public Applicant getApplicant(){
        return a;
    }
    
    public LinkedList<AdjListEntry> getAdjList(){
        return adjList;
    }
    
    public void addToAdjList(AdjListEntry e){
        adjList.addLast(e);
    }
    
    public int vertexDegree(){
        return adjList.size();
    }
}

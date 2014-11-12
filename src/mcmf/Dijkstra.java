package mcmf;

/**
 * This class implements Dijkstra's algorithm for finding lowest
 * cost paths to vertices in a network.
 */
public class Dijkstra
{
   /**************
      DIJKSTRA
   ***************/   
       
   private Graph g;               // graph to apply algorithm to
   private Vertex [] v;           // array of vertices of graph
   private long infinity;         // integer representing infinity
   private Linked_List notInSet;  // maintains list of vertices to which 
                                  // distance is not yet known
   private long [] distance;      // array of distances to each vertex
   private boolean [] addedToSet; // array indicating whether distance to 
                                  // vertices have been found
   private Vertex [] predecessor; // array of vertices which preceed a vertex
                                  // on the shortest path to that vertex
    
   /**
    * constructor - initialises instance variables
    * @param   graph graph to perform algorithm on
    */
   public Dijkstra(Graph graph)
   {
      g=graph;
      v = g.getVertices();
      distance = new long [g.getNumVertices()];
      predecessor = new Vertex [g.getNumVertices()];
      addedToSet = new boolean[g.getNumVertices()];
      notInSet = new Linked_List();
      infinity = getTotalWeight()+1;   
   }
   
   /**
    * returns array of predeceeding vertices
    * @return  vertex array of predecessors
    */
   public Vertex [] getPredecessors() { return predecessor; }
   
   /**
    * returns array of shortest distances to vertices
    * @return  integer array of shortest distances to vertices
    */
   public long [] getShortestPaths()
   {
      for(int i=0; i<g.getNumVertices(); i++) 
      { 
         addedToSet[i]=false;
         distance[i]=infinity;
         predecessor[i]=null;
         notInSet.add(v[i]);
      }
      distance[0]=0;        
     
      /* while distances to vertices not all known,
       *    select vertex with minimum estimated distance and
       *    perform edge relaxation operation on adjacent vertices */
      while(notInSet.getNum() != 0)
      {
         // finds vertex with minimum estimated distance
         Vertex w = findVertexWithMin();
         Linked_List adjToW = w.getEdgesOut();
         adjToW.setToStart();
         while(adjToW.hasNext())
         {
            Vertex x = (Vertex)adjToW.getCurrentItem();
            // if distance to vertex x not already known
            if(addedToSet[x.getIndex()]==false)
            {  /* check to see if the estimated distance to vertex x can
                * be improved by going through vertex w */
               if((distance[w.getIndex()]+
                        g.getEdge(w,x).getWeight())<distance[x.getIndex()])
               {  // if it can, update distance to vertex x
                  distance[x.getIndex()]=distance[w.getIndex()]+
                                             g.getEdge(w,x).getWeight();
                  // set predecessor of x to be w
                  predecessor[x.getIndex()]=w;
               }
            }
            adjToW.increment();
         }
      }
      return distance;
   }
   
   /**
    * returns vertex with minimum estimated distance
    * @return vertex with shortest known distance
    */
   private Vertex findVertexWithMin ()
   {  /* goes through every vertex where the distance is not
       * yet known and finds the vertex with minimum estimate */
      Linked_List.Link link = null;
      // minimum distance found so far, initialised to infinity
      long min = infinity+1; 
      notInSet.setToStart();
      while(notInSet.hasNext())
      {
         Linked_List.Link l = notInSet.getLink();
         /* if distance of vertex less than minimum distance 
          * found so far, update minimum so far */
         if((distance[((Vertex)l.getItem()).getIndex()])<=min)
         {
            min = distance[((Vertex)l.getItem()).getIndex()];
            link = l;
         }
         notInSet.increment();
      }
      Vertex vrtx = v[((Vertex)link.getItem()).getIndex()];
      addedToSet[vrtx.getIndex()]=true; // sets vertex to distance known
      notInSet.delete(link); // deletes vertex from set of distances unknown
      return vrtx;
   }
   
   /*
    * @return sum of all edge weights
    */
   private long getTotalWeight()
   {
      long total=0; // sum of all edge weights
      for(int i=0; i<g.getNumVertices(); i++)
      {  /* for each edge:
          *     update sum with weight of edge */
         Vertex w = (Vertex)v[i];
         Linked_List adj = w.getEdgesOut();
         adj.setToStart();
         while(adj.hasNext())
         {
            Vertex x = (Vertex)adj.getCurrentItem();
            long wght = g.getEdge(w,x).getWeight();
	    total+=wght; 
            adj.increment();
         }
      }
      if (total < 0) {
	 System.out.println("Weights too large.");
         //System.exit(1);
      }
      return total;
   }
}

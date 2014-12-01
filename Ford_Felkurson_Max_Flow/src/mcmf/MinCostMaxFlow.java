package mcmf;

/**
 * This class implements the algorithm for finding the minimum cost-
 * maximum flow of a network. 
 */
public class MinCostMaxFlow
{
   /***************************
     MINIMUM COST MAXIMUM FLOW
    ***************************/
   
   private Graph oldResidual;       // most recent residual graph
   private Graph residual;          // current residual graph
   private Graph graph;             // graph to apply algorithm to
   private long [] distance;        // array of shortest distances to vertices
   private Vertex [] vertices;      // array of vertices in graph
   private int flow;				// the max flow through the graph
   private long totalWeight; 		// the total weight of the graph. for each edge: weight += edge.flow * edge.weight
   
   
   /**
    * constructor - initialises instance variables
    * @param   g     graph to which algorithm is applied
    */
   public MinCostMaxFlow(Graph g)
   { 
      graph = g; 
      vertices = graph.getVertices();
      flow = 0;
      totalWeight = 0;
      
   }
   
   
   /**
    * returns the maximum flow of a network
    * @return     the modified graph
    */
   public Graph getMinCostMaxFlow()
   {
      int max =0; // maximum flow value
      boolean success = false; // indicates when min cost-max flow found
      oldResidual = null;
      
      // compute residual graph
      residual = new Graph(graph.getNumVertices());
      
      // copy vertices from original graph to residual 
      for(int i=0; i<graph.getNumVertices(); i++)
      {  residual.addVertex(null); }// dont need ref object
      Vertex [] rVertices = residual.getVertices(); // vertices of residual
      
      while(!success)
      {
         // after first iteration, delete all edges in residual graph
         if(oldResidual != null) 
             residual.setEdgesToNull();
         
         // construct edges of new residual graph
         for(int i=0; i<graph.getNumVertices(); i++)
         {  
            /* for every edge in the original graph:
             *    if flow < capacity
             *       add edge to residual in same direction
             *    if flow > 0
             *       add edge to residual in opposite direction */
            
            Vertex v1 = vertices[i];
            if (v1!=null) {
            Linked_List adj = v1.getEdgesOut();
            adj.setToStart();
            while(adj.hasNext())
            {
               Vertex v2 = (Vertex)adj.getCurrentItem();
               Edge edge = graph.getEdge(v1,v2);        
               int capacity = edge.getCapacity(); 
               int flow = edge.getFlow(); 
               long weight;
               /* to calculate weight of residual edge:
                * if previous residual graph exists:
                *    if edge exists in old residual graph
                *       weight of edge is weight of original edge plus
                *       distance to v1 minus distance to v2
                *    else weight of edge is 0
                * else 
                *       weight is same as original edge */
               if(flow<capacity)
	       { // add forwards edge with appropriate weight
                  if(oldResidual != null)
                  {
                     Vertex [] oldV = oldResidual.getVertices();
                     if (oldResidual.isEdge(oldV[v1.getIndex()],oldV[v2.getIndex()]))
                         weight = (oldResidual.getEdge(oldV[v1.getIndex()],
                             oldV[v2.getIndex()]).getWeight())
                             +distance[v1.getIndex()]-distance[v2.getIndex()];
                     else 
                        weight = 0;
		  }
                  else
                     weight = edge.getWeight();
                  residual.addEdge((capacity-flow),rVertices[v1.getIndex()],
                     rVertices[v2.getIndex()],weight);
	       }
               if(flow>0)
	       { // add backwards edge with appropriate weight
                  if(oldResidual != null)
                  {
                     Vertex [] oldV = oldResidual.getVertices();
                     if (oldResidual.isEdge(oldV[v2.getIndex()],oldV[v1.getIndex()]))
                         weight = (oldResidual.getEdge(oldV[v2.getIndex()],
                             oldV[v1.getIndex()]).getWeight())
                             +distance[v2.getIndex()]-distance[v1.getIndex()];
                     else 
                        weight = 0;
		  }
                  else
                     weight = edge.getWeight();
                  residual.addEdge(flow,rVertices[v2.getIndex()], 
                         rVertices[v1.getIndex()], weight);
	       }
               adj.increment();
            }
	    }
         }
         
         /* apply Dijkstra's algorithm to residual graph
          * to determine whether a path exists between source and sink */
         Dijkstra dij = new Dijkstra(residual);
         distance = dij.getShortestPaths();
         //for (int i=0; i<=241; i++)
	 //    if (distance[i]>2)
	 //	 System.out.println(i+" "+distance[i]);
         int source = 0;
         int sink = graph.getNumVertices()-1;
         Vertex [] predecessor = dij.getPredecessors();
         if(predecessor[sink] != null)// if path exists
         {   
             /* calculate minimum residual capacity of path by finding
             * the edge in the path with minimum capacity */ 
            int min = residual.getEdge
                     (predecessor[sink],(Vertex)rVertices[sink]).getCapacity();
            int i = sink;
            while(i > source)
            {
               Vertex v2 = (Vertex)rVertices[i]; 
               Vertex v1 = predecessor[i]; // parent of v2
               int edgeCap = residual.getEdge(v1,v2).getCapacity();
               
               if(edgeCap<min){
                  min = edgeCap;
               }
               i = v1.getIndex();
            }
            
            // augment flow by that many units 
            int j = sink;
            long weight = 0 ;
            while(j>source)
            {
               
               Vertex v2 = vertices[j]; 
               Vertex v1 = vertices[predecessor[j].getIndex()];
               Edge ed = graph.getEdge(v1,v2);
               /* the edge from v1 to v2 on the path is a forward edge in 
                * the original graph if min units can be added to the
                * edge from v1 to v2 without violating its capacity */
               if((ed != null) && ((ed.getFlow()+min)<=ed.getCapacity())){
                  ed.adjustFlow(min); // forward edge
                  weight += (ed.getWeight()) * ed.getFlow();
                  //System.out.println("edge weight: " + ed.getWeight() + " edge flow: " + ed.getFlow() + " weight: " + weight);
               }
               else{
               /* else if edge from v1 to v2 on the path is not a forward
              * edge in the original graph then it must be a backward edge.
              * min must then be subtracted from the edge in the original 
              * graph from v2 to v1 */ 
                  graph.getEdge(v2,v1).adjustFlow(-min); // backward edge
                  weight -= (graph.getEdge(v2, v1).getWeight()) * graph.getEdge(v2, v1).getFlow();
               }
               
               
               
               
               j = v1.getIndex(); // increment
            }
            
           /* Vertex vert1 = predecessor[sink];
            Vertex vert2 = (Vertex)rVertices[sink];
            Edge sinkEdge =  graph.getEdge(vert1,vert2);
            long weight = sinkEdge.getWeight()* sinkEdge.getFlow();
            
            int s = sink;
            while(s > source)
            {
               Vertex v2 = (Vertex)rVertices[s]; 
               Vertex v1 = predecessor[s]; // parent of v2
               weight += graph.getEdge(v1,v2).getWeight() * graph.getEdge(v1,v2).getFlow();
               s = v1.getIndex();
            }
            */
            
            // augment total flow by min units
            max += min;
            this.totalWeight += weight;
            
            // set old residual to copy of current residual 
            oldResidual = residual.copy();  
         }
         else
            success = true; // maximum flow found
      }
      graph.setFlow(max);
      return graph;
   }
   
   public long getTotalWeight(){
	   return this.totalWeight;
   }
   
   public int getTotalFlow(){
	   return this.flow;
   }
   
   /**
    * returns the current residual graph
    * @return     current residual graph
    */
   public Graph getResidual() { return residual;}
   
}

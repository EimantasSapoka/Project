package mcmf;

/**
 * This class holds information about a graph. 
 */
public class Graph
{
   /****************
        GRAPH
    ****************/
   
   Vertex [] vertices;        // array of vertices in graph 
   Edge [] [] matrix;         // 2d array of edges in graph
   int numVertices, numEdges; // number of vertices and edges
   int flow;				  // flow of the graph
      
   /**
    * constructor - initialises instance variables
    * @param   numV  number of vertices in graph
    */
   public Graph(int numV) 
   {
      numVertices = 0; 
      numEdges = 0;
      vertices = new Vertex [numV];
      matrix = new Edge [numV] [numV]; 
      initialiseMatrix(numV);
      this.flow = 0;
   }

   public void incNumVertices() {
      numVertices++;
   }
   
   /**
    * makes and returns a copy of the graph
    * @return  copy of graph 
    */
   public Graph copy()
   {
      Graph cl = new Graph(numVertices);
      /* for every vertex in the graph:
       *   make a copy of the vertex and add to the new graph */
      for(int i=0; i<numVertices; i++) 
      { cl.addVertex(vertices[i].getReference()); }
      Vertex [] ver = cl.getVertices();
      /* for every edge in the graph:
       *   add a copy of the edge to the new graph */
      for(int i=0; i<numVertices; i++)
      {
         Vertex v1 = vertices[i];
         Linked_List adj = v1.getEdgesOut();
         adj.setToStart();
         while(adj.hasNext())
         {
            Vertex v2 = (Vertex)adj.getCurrentItem();
            Edge e = getEdge(v1,v2);
            cl.addEdge(e.getCapacity(),ver[v1.getIndex()], 
                        ver[v2.getIndex()], e.getWeight());
            adj.increment();
         }
      }
      return cl;
   }
   
   /**
    * intialises all elements in edge matrix to null
    * @param   num   number of edges
    */
   private void initialiseMatrix(int num)
   {
      for(int i=0; i<num; i++)
      {
         for(int j=0; j<num; j++)
         {
            matrix[i][j]=null;
         }
      }
   }
   
   /**
    * adds and returns a vertex to the graph
    * @param   o     reference to object which vertex contains
    * @return  vertex created 
    */
   public Vertex addVertex(Object o) 
   {
      Vertex v = new Vertex(numVertices, o);
      vertices[numVertices]=v; // adds vertex to vertex array
      numVertices++;
      return v;
   }
   
   
   /**
    * adds an edge to the graph
    * @param   c  capacity of the edge
    * @param   o  origin vertex of the edge
    * @param   d  destination vertex of the edge
    * @param   w  weight of the edge
    */
   public void addEdge(int c, Vertex o, Vertex d, long w) 
   {
      Edge e = new Edge(c, o, d, w); 
      numEdges++;
      o.addEdgeOut(d); // adds destination vertex to adjacency list
                       // of origin vertex 
      matrix[o.getIndex()][d.getIndex()] = e; // add edge to edge matrix
   }
   
   /**
    *  adds a residual edge to the graph
    * @param   c  capacity of the edge
    * @param   o  origin vertex of the edge
    * @param   d  destination vertex of the edge
    * @param   w  weight of the edge 
    */
   public void addResidualEdge(int c, Vertex o, Vertex d, int w)
   {
      if(matrix[o.getIndex()][d.getIndex()]!= null) 
      { /* if an edge exists here already, check which edge has a 
         * higher capacity and if so, update the capacity of the
         * existing edge to the higher capacity */
         Edge e = matrix[o.getIndex()][d.getIndex()];
         if(c>e.getCapacity())
            e.newCapacity(c);
      }
      /* if edge does not already exist, add edge as normal */
      else
         addEdge(c, o, d,w);
   }
     
    
   /**
    * returns true if the current flows on edges satisfy the 
    * conservation constraint
    * @return  true if satisfies constraint, false otherwise
    */
   public boolean satisfiesConservationConstraint()
   {
      /* for all vertices except the source and the sink:
       *   if the number of units of flow coming into the vertex
       *   do not equal the number of units of flow going out of the 
       *   vertex, return false */
      for(int index=1; index<numVertices-1; index++)
      {
         int out =0; // sum of flow of all outgoing edges
         int in =0;  // sum of flow of all incoming edges
         for(int i=0; i<numVertices; i++)
         {
            /* if outgoing edge exists, add flow assigned to that
             * edge to the sum of flow out */
            if(matrix[index][i] != null)
               out += getEdge(vertices[index],vertices[i]).getFlow();
            /* if incoming edge exists, add flow assigned to that
             * edge to the sum of flow out */
            if(matrix[i][index] != null)
               in += getEdge(vertices[i],vertices[index]).getFlow();
         }
         if(out != in)
         {
            return false;
         }
      }
      return true;
   }
   
   /**
    * returns true only if all edges satisfy capacity constraint
    * @return  true if capacity constraint satisfied
    */
   boolean satisfiesCapacityConstraint()
   {
      for(int i=0; i<numVertices; i++)
      {
         Vertex v1 = vertices[i]; 
         Linked_List adj = v1.getEdgesOut();
         adj.setToStart();
         while(adj.hasNext())
         {
            Vertex v2 = (Vertex)adj.getCurrentItem();
            Edge edge = getEdge(v1,v2); 
            int flow = edge.getFlow();
            int capacity = edge.getCapacity();
            if(!(0<= flow) || !(flow<=capacity))
            {
               return false;
            }
            adj.increment();
         }
      }
      return true;
   }
   
   /**
    * returns the minimum cut of a graph with respect to a flow
    * @param   Breadth-First Search object to retrieve parent vertex array
    * @return     minimum cut of graph
    */
     
   /**
    * returns true if edge exists between the two specified vertices
    * @param   u  origin vertex of potential edge
    * @param   v  destination vertex of potential edge
    * @return  true if edge exists, false otherwise
    */
   public boolean isEdge(Vertex u, Vertex v)
   {
      return matrix[u.getIndex()][v.getIndex()] !=null;
   }
   
   /**
    * sets all the elements in the edge matrix to null, and 
    * deletes all edges from vertex adjacency lists
    */
   public void setEdgesToNull()
   {
      for(int i=0; i<numVertices; i++)
      {
         Vertex v1 = vertices[i]; 
         Linked_List adj = v1.getEdgesOut();
         adj.setToStart();
         while(adj.hasNext())
         {
            Vertex v2 = (Vertex)adj.getCurrentItem();
            matrix[v1.getIndex()][v2.getIndex()]=null;        
            adj.delete(); 
         }
      }
   }
      
   /**
    * calculates and returns the total cost of the current flow
    * @return  total cost of flow
    */
   public int getCostOfFlow()
   {
      int cost=0; // stores cost of flow
      for(int i=0; i<numVertices; i++)
      {  /* for all edges in graph:
          *   multiply flow of edge by cost of edge and add to cost of flow */
         Vertex v1 = vertices[i];
         Linked_List adj = v1.getEdgesOut();
         adj.setToStart();
         while(adj.hasNext())
         {
            Vertex v2 = (Vertex)adj.getCurrentItem();
            Edge e = getEdge(v1, v2);
            cost+= e.getFlow()*e.getWeight();
            adj.increment();
         }
      }
      return cost;
   }
   
   
   /**
    * returns number of vertices in graph
    * @return  number of vertices
    */
   public int getNumVertices(){ return numVertices;}
   
   
   /**
    * returns array of vertices in graph
    * @return  array of vertices
    */
   public Vertex [] getVertices() { return vertices;}
    
   
   /**
    * returns the edge between the specified vertices, null
    * if no such edge exists
    * @param   u  origin vertex of potential edge
    * @param   v  destination vertex of potential edge
    * @return  edge between vertices specified
    */
   public Edge getEdge(Vertex u, Vertex v)
   {
      return matrix[u.getIndex()][v.getIndex()];
   }

public void setFlow(int max) {
	this.flow = max;
	
}

public int getFlow() {
	return this.flow;
}

public int getWeight() {
	int totalWeight = 0;
	for (int i = 0; i<matrix.length; i++){
		for (int j = 0; j< matrix[i].length; j++){
			if (matrix[i][j] != null){
				Edge edge = matrix[i][j];
				totalWeight += edge.getFlow() * edge.getWeight();
			}
		}
	}
	return totalWeight;
}

  
   
 
}

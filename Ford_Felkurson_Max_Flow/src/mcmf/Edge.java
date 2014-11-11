package mcmf;

/**
 * This class holds all the information for an edge.
 */
public class Edge
{
   
   /***************
        EDGE
    ***************/
     
   Vertex origin;          // vertex at origin of edge
   Vertex destination;     // vertex at destination of edge
   int capacity;           // capacity of edge
   int flow;               // flow assigned to edge
   long weight;             // weight of edge
   
   
   /**
    * constructor - initialises instance variables
    * @param   c  capacity of edge
    * @param   o  vertex at origin of edge
    * @param   d  vertex at destination of edge
    * @param   w  weight of edge   
    */
   public Edge(int c, Vertex o, Vertex d, long w)
   {
      capacity = c;
      origin = o;
      destination = d;
      flow = 0;
      weight = w;
   }
   
   /**
    * constructor - for graphs with no edge weights, 
    * initialises instance variables
    * @param   c  capacity of edge
    * @param   o  origin vertex of edge
    * @param   d  destination vertex of edge
    */
   public Edge(int c, Vertex o, Vertex d)
   {
      this(c,o,d,0);
   }
   
   /**
    * returns capacity of edge
    * @return  edge capacity
    */
   public int getCapacity(){ return capacity;}
   
   /**
    * returns weight of edge
    * @return  edge weight
    */
   public long getWeight() { return weight;}
   
   /**
    * returns flow assigned to edge
    * @return  edge flow
    */
   public int getFlow() { return flow;}
   
   /**
    * returns origin vertex of edge
    * @return  origin of edge
    */
   public Vertex getOrigin() { return origin; }
   
   /**
    * returns destination vertex of edge
    * @return  destination of edge
    */
   public Vertex getDestination() { return destination; }
   
   /**
    * increases flow assigned to edge by amount specified
    * @param   n  amount to increase flow by
    */
   public void adjustFlow(int n) { flow += n;}
   
   /**
    * sets flow to integer specified
    * @param   n  integer to set flow to
    */
   public void setFlow(int n) { flow = n;}
   
   /**
    * assigns a new specified capacity to the edge
    * @param   n  new integer capacity of edge
    */
   public void newCapacity(int n) { capacity = n;}
   
   /**
    * sets weight of edge to that specified
    * @param   n  integer weight to be assigned to edge
    */
   public void setWeight(long n) { weight = n; }
   

   
   
}

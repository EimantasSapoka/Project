package mcmf;

/**
 * This class stores information for a vertex.
 */
public class Vertex
{
   
   /********************
        VERTEX
   ********************/
   
   Object reference;          // object this vertex represents
   int outDegree;             // number of edges coming out of this vertex
   int index;                 // index of vertex in graph vertex array
   Linked_List incidenceOut;  // adjacency list of vertex   
   

   /**
    * constructor - initialises instance variables
    * @param   i     index of vertex in graph vertex array
    * @param   o     object this vertex reprsents
    */
   public Vertex(int i, Object o)
   {
      index =i;
      outDegree=0;
      incidenceOut = new Linked_List();
      reference = o;
   }
   
   /**
    * adds a specified vertex to the adjacency list
    * @param   v     vertex to be added to adjacency list
    */
   public void addEdgeOut(Vertex v)
   {
      incidenceOut.add(v);
      outDegree++;
   }
   
   /**
    * returns the adjacency list of the vertex
    * @return  list of adjacent vertices
    */
   public Linked_List getEdgesOut(){ return incidenceOut;}
   
   /**
    * returns the number of edges coming out of the vertex which
    * is also the number of adjacent vertices
    * @return     number of outgoing edges
    */
   public int getOutDegree(){ return outDegree;}
   
   
   /**
    * returns the array index of the vertex in the graph vertex array
    * @return     array index position of vertex in graph vertex array
    */
   public int getIndex(){ return index;}
   
   /**
    * returns the object this vertex represents
    * @return reference object
    */
   public Object getReference(){ return reference;}
   
   
}

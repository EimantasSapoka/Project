package ford_fulkerson.residual_classes;

import ford_fulkerson.graph.Vertex;

/**
 * Special class, extending the vertex class. It adds the 
 * reference to the original Vertex it was created from.
 * @author Eimantas
 *
 */
public class ResidualVertex extends Vertex{
	private Vertex originalVertex;
	
	public ResidualVertex(Vertex v){
		super(v);
		this.originalVertex = v;
	}

	public Vertex getOriginalVertex(){
		return this.originalVertex;
	}
}

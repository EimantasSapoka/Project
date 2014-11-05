package ford_fulkerson.residual_classes;

import ford_fulkerson.graph.Vertex;

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

package residual_classes;

import ford_fulkerson.Vertex;

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

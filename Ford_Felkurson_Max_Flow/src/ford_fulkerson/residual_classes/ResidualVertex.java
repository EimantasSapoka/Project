package ford_fulkerson.residual_classes;

import java.util.ArrayList;

import ford_fulkerson.graph.Edge;
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
	
	
	/**
	 * method which checks if the new path to the vertex is shorter than the previous one.
	 * if not, leaves it as it was. 
	 * if so, sets the vertex distance from source to be the new distance and sets the path
	 * variable to be the edge it took to get here. 
	 * @param distance
	 */
	public void relaxation(int distance, ResidualEdge e) {		
		if (distance < this.getDistanceFromSource() ){
			this.setDistanceFromSource(distance);
			this.getOriginalVertex().setDistanceFromSource(distance);						 
			this.setPath(e);
		}
	}

	
	/**
	 * used in bfs
	 * @param e
	 */
	@Deprecated
	public void visit(Edge e){
		this.setPath((ResidualEdge) e);
		this.visited = true;
	}

	
	
}

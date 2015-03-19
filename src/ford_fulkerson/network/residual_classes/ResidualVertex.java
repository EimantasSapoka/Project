package ford_fulkerson.network.residual_classes;

import ford_fulkerson.network.Vertex;

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
	
	
	/**
	 * method which checks if the new path to the vertex is shorter than the previous one.
	 * if not, leaves it as it was. 
	 * if so, sets the vertex distance from source to be the new distance and sets the path
	 * variable to be the edge it took to get here. 
	 * @param distance
	 */
	public void relaxation(int distance, ResidualEdge e) {		
		if (distance < this.getDistanceFromSource() || !this.isReachable() ){
			this.setDistanceFromSource(distance);
			originalVertex.setDistanceFromSource(distance);						 
			this.setPath(e);
		}
	}	
	
}

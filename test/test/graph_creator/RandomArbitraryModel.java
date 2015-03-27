package test.graph_creator;

import java.util.List;
import java.util.Random;

import ford_fulkerson.ReaderShortlistException;
import ford_fulkerson.model.MCMFModel;
import ford_fulkerson.network.Edge;
import ford_fulkerson.network.Network;
import ford_fulkerson.network.Vertex;

/**
 * class which creates a random arbitrary graph.
 * Creates a network with random number of vertices 
 * and randomly assigns edges between them. 
 * @author Eimantas
 *
 */
public class RandomArbitraryModel extends MCMFModel {
	
	private static final int PROBABILITY_CEILING = 100;
	private static final int EDGE_CAPACITY = 10;
	private static final int EDGE_WEIGHT_MAX = 4;
	Random rand;
    private Network network;
	
	private int pEdge;
	
	/**
	 * creates a random arbitrary graph with the default 
	 * probability values
	 * @throws ReaderShortlistException 
	 */
	public RandomArbitraryModel(int probEdge, int numVertices) throws ReaderShortlistException{
		super();
		rand = new Random();
		createNetwork();
		network = this.getNetwork();
		
		this.pEdge = probEdge;
		
		for (int i = 0; i < numVertices; i++){
			network.addVertex(new Vertex(new MockNetworkObject(i)));
		}
		
		add_source_sink_edges();
		generate_random_edges();
	}

	/**
	 * method which creates random edges between all the vertices
	 */
	private void generate_random_edges() {
		for (Vertex v: network.getVertices()){
			if (v.getObjectID() != Network.SINK_ID && v.getObjectID() != Network.SOURCE_ID){
				add_vertex_to_vertex_edges(v);
			}
		}
	}


	/**
	 * method which generates edges between vertices themselves. 
	 * each vertex has a probability of pEdge of having an edge with any other vertex 
	 * ( apart from itself, source and the sink ) going out of it. 
	 * @param v
	 */
	private void add_vertex_to_vertex_edges(Vertex v) {
		// for every other vertex, if it's not itself, the source or sink, add an edge at a probability pEdge
		for (Vertex vert: network.getVertices()){
			if (vert.getObjectID() != Network.SINK_ID && 
				vert.getObjectID() != Network.SOURCE_ID && 
				vert.getObjectID() != v.getObjectID() ){
				
				// if the random integer is lower than the probability, it's considered a hit
				if (rand.nextInt(PROBABILITY_CEILING) <= pEdge){
					Edge vertexVertexEdge = new Edge(v, vert, rand.nextInt(EDGE_CAPACITY)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
					network.addEdge(vertexVertexEdge);
				}
			}
		}
	}


	/**
	 * method which adds random edges between the source and the vertex
	 * or the vertex and the source
	 * @param v
	 */
	private void add_source_sink_edges() {
		
		List<Vertex> vertices = network.getVertices();
		vertices.remove(network.source());
		vertices.remove(network.sink());
		int randomInt; 
		
		// a number of random vertices will have the source-to-vertex edge
		
		for (int i = 0; i< vertices.size()/4 ; i++){
			randomInt = rand.nextInt(vertices.size());
			Edge sourceVertexEdge = new Edge(network.source(), vertices.remove(randomInt), rand.nextInt(EDGE_CAPACITY*4)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
			network.addEdge(sourceVertexEdge);
		}
		
		// and a number of random vertices will have a vertex-to-sink edge
		
		for (int i = 0; i< vertices.size()/4 ; i++){
			randomInt = rand.nextInt(vertices.size());
			Edge vertexSinkEdge = new Edge(vertices.remove(randomInt), network.sink(), rand.nextInt(EDGE_CAPACITY*4)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
			network.addEdge(vertexSinkEdge);
		}
	}
	

}

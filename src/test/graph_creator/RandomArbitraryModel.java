package test.graph_creator;

import java.util.ArrayList;
import java.util.Random;

import model.MCMFModel;
import ford_fulkerson.ReaderShortlistException;
import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Vertex;

/**
 * class which creates a random arbitrary graph
 * @author Eimantas
 *
 */
public class RandomArbitraryModel extends MCMFModel {
	
	private static final int PROBABILITY_CEILING = 100;
	private static final int EDGE_CAPACITY = 10;
	private static final int EDGE_WEIGHT_MAX = 4;
	Random rand;
    private Graph graph;
	
	private int pEdge;
	private int numVertices;
	
	/**
	 * creates a random arbitrary graph with the default 
	 * probability values
	 * @throws ReaderShortlistException 
	 */
	public RandomArbitraryModel() throws ReaderShortlistException{
		super();
		rand = new Random();
		createGraph();
		graph = this.getGraph();
		
		
		this.numVertices = rand.nextInt(8) + 25; 
		this.pEdge = rand.nextInt(20)+1; // 1 <= n < 21
		for (int i = 2; i < numVertices+2; i++){
			graph.addVertex(new Vertex(i,null));
		}
		
		add_source_sink_edges();
		generate_random_edges();
	}
	
	/**
	 * creates a random graph with the probability of the edge between any two 
	 * vertices being probEdge 
	 * @param probEdge (0<=pEdge<=100)
	 * @throws ReaderShortlistException 
	 */
	public RandomArbitraryModel(int probEdge) throws ReaderShortlistException{
		this();

		if (probEdge > 0 && probEdge < 100){
			this.pEdge = probEdge;
		} 
	}
	
	/**
	 * creates a random graph with the number of vertices 
	 * and the edge pobability between them provided
	 * @param probEdge
	 * @param numVertices
	 * @throws ReaderShortlistException 
	 */
	public RandomArbitraryModel(int probEdge, int numVertices) throws ReaderShortlistException{
		this(probEdge);
		this.numVertices = numVertices;
	}
	
	
	/**
	 * method which creates random edges between all the vertices
	 */
	private void generate_random_edges() {
		for (Vertex v: graph.getVertices()){
			if (v.getObjectID() != Graph.SINK_ID && v.getObjectID() != Graph.SOURCE_ID){
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
		for (Vertex vert: graph.getVertices()){
			if (vert.getObjectID() != Graph.SINK_ID && 
				vert.getObjectID() != Graph.SOURCE_ID && 
				vert.getObjectID() != v.getObjectID() ){
				
				// if the random integer is lower than the probability, it's considered a hit
				if (rand.nextInt(PROBABILITY_CEILING) <= pEdge){
					Edge vertexVertexEdge = new Edge(v, vert, rand.nextInt(EDGE_CAPACITY)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
					graph.addEdge(vertexVertexEdge);
				}
			}
		}
	}


	/**
	 * method which adds random edges between the source and the vertex
	 * or the vertex and the source
	 * @param v
	 */
	@SuppressWarnings("unchecked")
	private void add_source_sink_edges() {
		
		ArrayList<Vertex> vertices = (ArrayList<Vertex>) graph.getVertices().clone();
		vertices.remove(graph.source());
		vertices.remove(graph.sink());
		int randomInt; 
		
		// a number of random vertices will have the source-to-vertex edge
		
		for (int i = 0; i< vertices.size()/4 ; i++){
			randomInt = rand.nextInt(vertices.size());
			Edge sourceVertexEdge = new Edge(graph.source(), vertices.remove(randomInt), rand.nextInt(EDGE_CAPACITY*4)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
			graph.addEdge(sourceVertexEdge);
		}
		
		// and a number of random vertices will have a vertex-to-sink edge
		
		for (int i = 0; i< vertices.size()/4 ; i++){
			randomInt = rand.nextInt(vertices.size());
			Edge vertexSinkEdge = new Edge(vertices.remove(randomInt), graph.sink(), rand.nextInt(EDGE_CAPACITY*4)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
			graph.addEdge(vertexSinkEdge);
		}
	}
	

}

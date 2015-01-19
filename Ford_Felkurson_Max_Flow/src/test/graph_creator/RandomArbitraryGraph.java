package test.graph_creator;

import java.util.ArrayList;
import java.util.Random;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Vertex;

/**
 * class which creates a random arbitrary graph
 * @author Eimantas
 *
 */
public class RandomArbitraryGraph extends Graph {
	
	private static final int PROBABILITY_CEILING = 100;
	private static final int EDGE_CAPACITY = 10;
	private static final int EDGE_WEIGHT_MAX = 3;
	Random rand;
	
	private int pEdge;
	private int numVertices;
	
	/**
	 * creates a random arbitrary graph with the default 
	 * probability values
	 */
	public RandomArbitraryGraph(){
		super();
		rand = new Random();
		
		
		this.numVertices = rand.nextInt(50) + 50; // 50 <= n < 100
		this.pEdge = rand.nextInt(1000/numVertices)+1; // 1 <= n < 21
		System.out.println("Vertices: " + numVertices + " pEdge: " + pEdge);
		for (int i = 1; i < numVertices+1; i++){
			this.addVertex(new Vertex(i,null));
		}
		
		add_source_sink_edges();
		generate_random_edges();
	}
	
	/**
	 * creates a random graph with the probability of the edge between any two 
	 * vertices being probEdge 
	 * @param probEdge (0<=pEdge<=100)
	 */
	public RandomArbitraryGraph(int probEdge){
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
	 */
	public RandomArbitraryGraph(int probEdge, int numVertices){
		this(probEdge);
		this.numVertices = numVertices;
	}
	
	
	/**
	 * method which creates random edges between all the vertices
	 */
	private void generate_random_edges() {
		for (Vertex v: this.getVertices()){
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
		for (Vertex vert: this.getVertices()){
			if (vert.getObjectID() != Graph.SINK_ID && 
				vert.getObjectID() != Graph.SOURCE_ID && 
				vert.getObjectID() != v.getObjectID() ){
				
				// if the random integer is lower than the probability, it's considered a hit
				if (rand.nextInt(PROBABILITY_CEILING) <= pEdge){
					Edge vertexVertexEdge = new Edge(v, vert, rand.nextInt(EDGE_CAPACITY)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
					this.addEdge(vertexVertexEdge);
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
		
		ArrayList<Vertex> vertices = (ArrayList<Vertex>) this.getVertices().clone();
		vertices.remove(this.source());
		vertices.remove(this.sink());
		int randomInt; 
		
		// a number of random vertices will have the source-to-vertex edge
		
		for (int i = 0; i< vertices.size()/4 ; i++){
			randomInt = rand.nextInt(vertices.size());
			Edge sourceVertexEdge = new Edge(this.source(), vertices.remove(randomInt), rand.nextInt(EDGE_CAPACITY*4)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
			this.addEdge(sourceVertexEdge);
		}
		
		// and a number of random vertices will have a vertex-to-sink edge
		
		for (int i = 0; i< vertices.size()/4 ; i++){
			randomInt = rand.nextInt(vertices.size());
			Edge vertexSinkEdge = new Edge(vertices.remove(randomInt), this.sink(), rand.nextInt(EDGE_CAPACITY*4)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
			this.addEdge(vertexSinkEdge);
		}
	}
	

}

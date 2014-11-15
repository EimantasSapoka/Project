package test.graph_creator;

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
	
	private static final int SOURCE_SINK_PROBABILITY_OFFSET = 15;
	private static final int PROBABILITY_CEILING = 100;
	private static final int EDGE_CAPACITY = 10;
	private static final int EDGE_WEIGHT_MAX = 9;
	Random rand;
	
	private int sourceEdges;
	private int sinkEdges;
	private int pEdge;
	private int numVertices;
	
	/**
	 * creates a random arbitrary graph with the default 
	 * probability values
	 */
	public RandomArbitraryGraph(){
		super();
		rand = new Random();
		
		this.pEdge = rand.nextInt(20)+ 10; // 10 <= n < 30
		this.numVertices = rand.nextInt(50) + 50; // 50 <= n < 100
		
		this.sourceEdges = 0;
		this.sinkEdges = 0;
		
		for (int i = 1; i < numVertices+1; i++){
			this.addVertex(new Vertex(i));
		}
		
		generate_random_edges();

	}
	
	
	public RandomArbitraryGraph(int probEdge){
		this();

		if (probEdge > 0 && probEdge < 100){
			this.pEdge = probEdge;
		} 
	}
	
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
				add_source_sink_edges(v);
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
	private void add_source_sink_edges(Vertex v) {
		
		/*
		 * the probability of a source edge is equal to the regular edge probability with an offset to 
		 * make it more likely. Also, number of source to vertex edges cannot be greater than half the 
		 * number of vertices. And also, if the number of s-t-v edges is 0, automatic probability hit.
		 */
		if (  ((rand.nextInt(PROBABILITY_CEILING) <= (pEdge + SOURCE_SINK_PROBABILITY_OFFSET)) 
				&& sourceEdges <= numVertices/2 ) || sourceEdges == 0) {
			Edge sourceVertexEdge = new Edge(this.source(), v, rand.nextInt(EDGE_CAPACITY*4)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
			this.addEdge(sourceVertexEdge);
			sourceEdges++;
		} 
		
		// same as source ^
		if (  ((rand.nextInt(PROBABILITY_CEILING) <= (pEdge + SOURCE_SINK_PROBABILITY_OFFSET)) 
				&& sinkEdges <= numVertices/2 ) || sinkEdges == 0) {
			Edge vertexSinkEdge = new Edge(v, this.sink(), rand.nextInt(EDGE_CAPACITY*4)+1, rand.nextInt(EDGE_WEIGHT_MAX)+1);
			this.addEdge(vertexSinkEdge);
			sinkEdges++;
		}
		
	}
	

}

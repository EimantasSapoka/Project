package augustineMCMF;

import java.util.ArrayList;

import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Project;
import ford_fulkerson.graph.Reader;
import ford_fulkerson.graph.Vertex;

public class MinCostMaxFlowSPA 
{
	
	/**
	 * Main entry point for the algorithm. Throws any exceptions it encounters to the calling method
	 * @param m
	 * @return
	 * @throws Exception
	 */
	public Network solve(Network network) throws Exception
	{
		// now we implement the algorithm..
		// we're starting with an empty flow
		// keep looking for paths and augmenting the flow in the network
		Path path = null;
		Network resNetwork = generateResidualNetwork(network, network);
		
		// keep looking for augmenting paths and satisfying them
		while((path = findAugmentingPath(network, resNetwork)) != null)
		{			
			
			// augment the flow along path and report any errors
			if(!augmentFlow(network, path))
			{
				throw new Exception("Unable to augment the flow along path " + path);
			}
			
			resNetwork = generateResidualNetwork(network, resNetwork);
		}
		
		return network;
	}

	/**
	 * Converts an instance of spa to a network
	 */
	public Network generateRandomNetwork() throws Exception
	{
		Network network = new Network();
		
		// add some nodes
		Node n = new Node(1, "n1", null);
		network.addNode(n);
		
		Node n2 = new Node(2, "n2", null);
		network.addNode(n2);
		
		Node n3 = new Node(3, "n3", null);
		network.addNode(n3);
		
		Node n4 = new Node(4, "n4", null);
		network.addNode(n4);
		
		Node n5 = new Node(5, "n5", null);
		network.addNode(n5);
		
		Node s = new Node(6, "s", null);
		network.addNode(s);
		
		Node t = new Node(7, "t", null);
		network.addNode(t);
		
		// set the terminal nodes
		network.setSource(s);
		network.setSink(t);
		
		// add some edges
		network.addEdge(new Edge(s, n, 0, 5, 10));
		network.addEdge(new Edge(s, n2, 0, 5, 2));
		network.addEdge(new Edge(s, n3, 0, 5, 7));
		
		network.addEdge(new Edge(n, n3, 0, 5, 1));
		network.addEdge(new Edge(n, n4, 0, 2, 4));
		network.addEdge(new Edge(n, n5, 0, 8, 10));
		
		network.addEdge(new Edge(n2, n3, 0, 8, 0));
		network.addEdge(new Edge(n2, n4, 0, 6, 40));
		network.addEdge(new Edge(n2, n5, 0, 1, 12));
		
		network.addEdge(new Edge(n4, t, 0, 9, 17));
		network.addEdge(new Edge(n5, t, 0, 10, 24));
		network.addEdge(new Edge(n3, t, 0, 2, 16));
		
		return network;
	}


	/**
	 * Find a greedy maximum augmenting path in the network w.r.t the flow
	 */
	public Path findAugmentingPath(Network network, Network resNetwork) throws Exception
	{
		Path path = new Path();
		
		// 1. run dijkstra's algorithm and obtain augmenting path		
		ArrayList<Node> unvisitedNodes = new ArrayList<Node>();
		for(Node n : resNetwork.getNodes())
		{
			unvisitedNodes.add(n);
		}
				
		Node currentNode = resNetwork.getSource();
		currentNode.setDikjstraDistranceInfinity(false);
		
		while(!unvisitedNodes.isEmpty())
		{
			if(currentNode == null)
			{
				break;
			}
			
			for(Edge e : currentNode.getOutgoingEdges())
			{
				
				Node destinationNode = e.getDestNode();
				if(unvisitedNodes.contains(destinationNode))
				{
					long newDistance = currentNode.getDistance() + e.getCost();
					
					if(destinationNode.isDikjstraDistranceInfinity() || destinationNode.getDistance() > newDistance)
					{
						destinationNode.setDistance(newDistance);
						destinationNode.setPred(currentNode, e);
						destinationNode.setDikjstraDistranceInfinity(false);
						
					}
				}
			}
			
			unvisitedNodes.remove(currentNode);
			
			currentNode = null;
			
			for(int i=0; i<unvisitedNodes.size(); i++)
			{
				Node n = unvisitedNodes.get(i);
				if(!n.isDikjstraDistranceInfinity())
				{
					if(currentNode==null)
						currentNode = n;
					
					if(n.getDistance() < currentNode.getDistance())
						currentNode = n;
				}
				
			}
			
		}
		
		// 2. update the distance values on the nodes in the main network
		for(Node n : resNetwork.getNodes())
		{
			network.getNode(n.getName()).setDistance(n.getDistance());
		}
		
		// do we have a path? if not, return null
		if(resNetwork.getSink().isDikjstraDistranceInfinity()){
			return null;
		}
		
		// 3. trace the path
		Node cursorNode = resNetwork.getSink();
		Path tmpPath = new Path();
		while(cursorNode != null)
		{
			if(cursorNode != resNetwork.getSource())
				tmpPath.addEdge(cursorNode.getPredEdge(), true);
			cursorNode = cursorNode.getPredNode();
		}
		
		// move edges from tmpPath to path
		for(int i=tmpPath.size()-1; i>=0; i--)
		{
			path.addEdge(tmpPath.getEdge(i), true);
		}

		// all done?
		return path;
	}

	/**
	 * Generates a weighted residual network from the incumbent network and flow...
	 * @param network
	 * @return
	 */
	public Network generateResidualNetwork(Network network, Network previousNetwork)
	{
		// generate a residual network
		Network resNetwork = new Network();
		
		// copy in all the nodes
		for(Node n : network.getNodes())
		{
			// create a new clone node
			Node cloneNode = new Node(n.getId(), n.getName(), n.getType());
			cloneNode.setPred(null, null);
			
			// add clone to the new network
			resNetwork.addNode(cloneNode);
		}
		
		// create the edges
		for(Edge e : network.getEdges())
		{
			// what's the current flow w.r.t. upper quota
			if(e.getFlow() < e.getUpperQuota())
			{
				// create new edge in the forward direction
				Edge newEdge = new Edge(resNetwork.getNode(e.getSourceNode().getName()), resNetwork.getNode(e.getDestNode().getName()), e.getUpperQuota()-e.getFlow());
				resNetwork.addEdge(newEdge);
			}
			
			if(e.getFlow() > 0)
			{
				// create new edge in the reverse direction
				Edge newEdge = new Edge(resNetwork.getNode(e.getDestNode().getName()), resNetwork.getNode(e.getSourceNode().getName()), e.getFlow());
				resNetwork.addEdge(newEdge);
			}
		}
		
		// modify the edge weights
		for(Edge e : resNetwork.getEdges())
		{
			Edge previousEdge = previousNetwork.getEdge(e.getSourceNode().getName(), e.getDestNode().getName());
			if(previousEdge != null)
			{
				// what is the edge weight?
				long edgeWeight = network.getNode(e.getSourceNode().getName()).getDistance() + previousEdge.getCost() - network.getNode(e.getDestNode().getName()).getDistance();
				e.setCost(edgeWeight);
			}
		}
		
		// set network source and sink
		resNetwork.setSource(resNetwork.getNode(network.getSource().getName()));
		resNetwork.setSink(resNetwork.getNode(network.getSink().getName()));
		
		
		return resNetwork;
	}

	/**
	 * Augment a network along a path in the residual network
	 */
	public boolean augmentFlow(Network network, Path path) throws Exception
	{
		// loop through the edges in the path
		for(int i=0; i<path.size(); i++)
		{
			Edge pathEdge = path.getEdge(i);
			
			// try to find the corresponding edge in the network
			Edge networkEdge = network.getEdge(pathEdge.getSourceNode().getName(), pathEdge.getDestNode().getName());
			
			// did we find a corresponding edge?
			if(networkEdge != null)
			{
				if((networkEdge.getUpperQuota()-networkEdge.getFlow()) >= path.getCapacity())
				{
					networkEdge.increaseFlow(path.getCapacity());
				}
				else System.out.println("error. suppressed exception would be thrown.");
			/*
				else
					throw new Exception("Seems the flow cannot accomodate the capacity of the path. " +
							"It failed while trying to increase the flow through edge "+networkEdge+ " by "+path.getCapacity()+" units.");
			*/
			}
			// otherwise, it must be that the path edge is in the reverse direction. So lets find it
			else
			{
				networkEdge = network.getEdge(pathEdge.getDestNode().getName(), pathEdge.getSourceNode().getName());
				
				if(networkEdge != null)
				{
					if(networkEdge.getFlow() >= path.getCapacity())
					{
						networkEdge.reduceFlow(path.getCapacity());
					}
					else
						throw new Exception("Seems the flow is not enough to be reduced by the path capacity. " +
								"It failed while trying to reduce the flow through edge "+networkEdge+ " by "+path.getCapacity()+" units.");
				}
				
				else
				{
					throw new Exception("Seems i cannot find any paths in the residual network that correspond to a forward or backward edge in the network. " +
							"The path edge being considered is "+pathEdge);
				}
			}
		}
				
		return true;
	}
	
	
	// ***************************		ADDED METHODS 		*************************
	
	/**
	 * creates a network for this implementation from the given graph
	 * @param graph
	 * @return
	 */
	public Network createNetworkFromGraph(Graph graph){
		Network network = new Network();
		
		// copy nodes/vertices
		for (Vertex v: graph.getVertices()){
			Node node = new Node(v.getVertexID(), (v.getVertexID())+"", null);
			network.addNode(node);
			if (v.getVertexID() == 0){
				network.setSource(node);
			}
			if (v.getVertexID() == 1){
				network.setSink(node);
			}
		}
		
		// copy edges
		for (ford_fulkerson.graph.Edge e : graph.getEdges()){
			Edge edge = new Edge(network.getNode((e.getParent().getVertexID())+""), network.getNode((e.getDestination().getVertexID())+""), 0, e.getCapacity(), e.getWeight());
			network.addEdge(edge);
		}
		
		return network;
	}
	
	
	/**
	 * creates a network from the given graph using the reader and project classes. 
	 * The created network has nodes with types which specify either LECTURER for reader vertex
	 * or PROJECT for project vertices.
	 * @param graph
	 * @return
	 */
	public Network createReaderNetworkFromGraph(Graph graph){
		Network network = new Network();
		
		Vertex source = graph.source();
		Node node = new Node(source.getVertexID(), (source.getVertexID())+"", null);
		network.addNode(node);
		network.setSource(node);
		
		Vertex sink = graph.sink();
		node = new Node(sink.getVertexID(), (sink.getVertexID())+"", null);
		network.addNode(node);
		network.setSink(node);
		
		for (Reader reader: graph.getReaders()){
			Vertex v = reader.getVertex();
			node = new Node(v.getVertexID(), (v.getVertexID())+"", NodeType.LECTURER, reader.getCapacity());
			network.addNode(node);
			
		}
		
		for (Project project : graph.getProjects()){
			Vertex v = project.getVertex();
			node = new Node(v.getVertexID(), (v.getVertexID())+"", NodeType.PROJECT);
			network.addNode(node);
		}
		
		// copy edges
		for (ford_fulkerson.graph.Edge e : graph.getEdges()){
			Edge edge = new Edge(network.getNode((e.getParent().getVertexID())+""), network.getNode((e.getDestination().getVertexID())+""), e.getCapacity(), e.getCapacity(), e.getWeight());
			network.addEdge(edge);
		}
				
		
		return network;
	}
	
	
}

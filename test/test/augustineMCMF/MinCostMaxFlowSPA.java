package test.augustineMCMF;

import java.util.ArrayList;

import ford_fulkerson.model.MCMFModel;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;
import ford_fulkerson.network.Network;
import ford_fulkerson.network.Vertex;

public class MinCostMaxFlowSPA 
{
	
	/**
	 * Main entry point for the algorithm. Throws any exceptions it encounters to the calling method
	 * @param m
	 * @return
	 * @throws Exception
	 */
	public Augustine_Network solve(Augustine_Network augustine_Network) throws Exception
	{
		// now we implement the algorithm..
		// we're starting with an empty flow
		// keep looking for paths and augmenting the flow in the network
		Path path = null;
		Augustine_Network resAugustine_Network = generateResidualAugustine_Network(augustine_Network, augustine_Network);
		
		// keep looking for augmenting paths and satisfying them
		while((path = findAugmentingPath(augustine_Network, resAugustine_Network)) != null)
		{			
			
			// augment the flow along path and report any errors
			if(!augmentFlow(augustine_Network, path))
			{
				throw new Exception("Unable to augment the flow along path " + path);
			}
			
			resAugustine_Network = generateResidualAugustine_Network(augustine_Network, resAugustine_Network);
		}
		
		return augustine_Network;
	}

	/**
	 * Converts an instance of spa to a network
	 */
	public Augustine_Network generateRandomAugustine_Network() throws Exception
	{
		Augustine_Network augustine_Network = new Augustine_Network();
		
		// add some nodes
		Node n = new Node(1, "n1", null);
		augustine_Network.addNode(n);
		
		Node n2 = new Node(2, "n2", null);
		augustine_Network.addNode(n2);
		
		Node n3 = new Node(3, "n3", null);
		augustine_Network.addNode(n3);
		
		Node n4 = new Node(4, "n4", null);
		augustine_Network.addNode(n4);
		
		Node n5 = new Node(5, "n5", null);
		augustine_Network.addNode(n5);
		
		Node s = new Node(6, "s", null);
		augustine_Network.addNode(s);
		
		Node t = new Node(7, "t", null);
		augustine_Network.addNode(t);
		
		// set the terminal nodes
		augustine_Network.setSource(s);
		augustine_Network.setSink(t);
		
		// add some edges
		augustine_Network.addEdge(new Edge(s, n, 0, 5, 10));
		augustine_Network.addEdge(new Edge(s, n2, 0, 5, 2));
		augustine_Network.addEdge(new Edge(s, n3, 0, 5, 7));
		
		augustine_Network.addEdge(new Edge(n, n3, 0, 5, 1));
		augustine_Network.addEdge(new Edge(n, n4, 0, 2, 4));
		augustine_Network.addEdge(new Edge(n, n5, 0, 8, 10));
		
		augustine_Network.addEdge(new Edge(n2, n3, 0, 8, 0));
		augustine_Network.addEdge(new Edge(n2, n4, 0, 6, 40));
		augustine_Network.addEdge(new Edge(n2, n5, 0, 1, 12));
		
		augustine_Network.addEdge(new Edge(n4, t, 0, 9, 17));
		augustine_Network.addEdge(new Edge(n5, t, 0, 10, 24));
		augustine_Network.addEdge(new Edge(n3, t, 0, 2, 16));
		
		return augustine_Network;
	}


	/**
	 * Find a greedy maximum augmenting path in the network w.r.t the flow
	 */
	public Path findAugmentingPath(Augustine_Network augustine_Network, Augustine_Network resAugustine_Network) throws Exception
	{
		Path path = new Path();
		
		// 1. run dijkstra's algorithm and obtain augmenting path		
		ArrayList<Node> unvisitedNodes = new ArrayList<Node>();
		for(Node n : resAugustine_Network.getNodes())
		{
			unvisitedNodes.add(n);
		}
				
		Node currentNode = resAugustine_Network.getSource();
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
		for(Node n : resAugustine_Network.getNodes())
		{
			augustine_Network.getNode(n.getName()).setDistance(n.getDistance());
		}
		
		// do we have a path? if not, return null
		if(resAugustine_Network.getSink().isDikjstraDistranceInfinity()){
			return null;
		}
		
		// 3. trace the path
		Node cursorNode = resAugustine_Network.getSink();
		Path tmpPath = new Path();
		while(cursorNode != null)
		{
			if(cursorNode != resAugustine_Network.getSource())
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
	 * @param augustine_Network
	 * @return
	 */
	public Augustine_Network generateResidualAugustine_Network(Augustine_Network augustine_Network, Augustine_Network previousAugustine_Network)
	{
		// generate a residual network
		Augustine_Network resAugustine_Network = new Augustine_Network();
		
		// copy in all the nodes
		for(Node n : augustine_Network.getNodes())
		{
			// create a new clone node
			Node cloneNode = new Node(n.getId(), n.getName(), n.getType());
			cloneNode.setPred(null, null);
			
			// add clone to the new network
			resAugustine_Network.addNode(cloneNode);
		}
		
		// create the edges
		for(Edge e : augustine_Network.getEdges())
		{
			// what's the current flow w.r.t. upper quota
			if(e.getFlow() < e.getUpperQuota())
			{
				// create new edge in the forward direction
				Edge newEdge = new Edge(resAugustine_Network.getNode(e.getSourceNode().getName()), resAugustine_Network.getNode(e.getDestNode().getName()), e.getUpperQuota()-e.getFlow());
				resAugustine_Network.addEdge(newEdge);
			}
			
			if(e.getFlow() > 0)
			{
				// create new edge in the reverse direction
				Edge newEdge = new Edge(resAugustine_Network.getNode(e.getDestNode().getName()), resAugustine_Network.getNode(e.getSourceNode().getName()), e.getFlow());
				resAugustine_Network.addEdge(newEdge);
			}
		}
		
		// modify the edge weights
		for(Edge e : resAugustine_Network.getEdges())
		{
			Edge previousEdge = previousAugustine_Network.getEdge(e.getSourceNode().getName(), e.getDestNode().getName());
			if(previousEdge != null)
			{
				// what is the edge weight?
				long edgeWeight = augustine_Network.getNode(e.getSourceNode().getName()).getDistance() + previousEdge.getCost() - augustine_Network.getNode(e.getDestNode().getName()).getDistance();
				e.setCost(edgeWeight);
			}
		}
		
		// set network source and sink
		resAugustine_Network.setSource(resAugustine_Network.getNode(augustine_Network.getSource().getName()));
		resAugustine_Network.setSink(resAugustine_Network.getNode(augustine_Network.getSink().getName()));
		
		
		return resAugustine_Network;
	}

	/**
	 * Augment a network along a path in the residual network
	 */
	public boolean augmentFlow(Augustine_Network augustine_Network, Path path) throws Exception
	{
		// loop through the edges in the path
		for(int i=0; i<path.size(); i++)
		{
			Edge pathEdge = path.getEdge(i);
			
			// try to find the corresponding edge in the network
			Edge networkEdge = augustine_Network.getEdge(pathEdge.getSourceNode().getName(), pathEdge.getDestNode().getName());
			
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
				networkEdge = augustine_Network.getEdge(pathEdge.getDestNode().getName(), pathEdge.getSourceNode().getName());
				
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
	public Augustine_Network createAugustine_NetworkFromGraph(Network graph){
		Augustine_Network augustine_Network = new Augustine_Network();
		
		// copy nodes/vertices
		for (Vertex v: graph.getVertices()){
			Node node = new Node(v.getVertexID(), (v.getVertexID())+"", null);
			augustine_Network.addNode(node);
			if (v.getVertexID() == 0){
				augustine_Network.setSource(node);
			}
			if (v.getVertexID() == 1){
				augustine_Network.setSink(node);
			}
		}
		
		// copy edges
		for (ford_fulkerson.network.Edge e : graph.getEdges()){
			Edge edge = new Edge(augustine_Network.getNode((e.getSource().getVertexID())+""), augustine_Network.getNode((e.getDestination().getVertexID())+""), 0, e.getCapacity(), e.getWeight());
			augustine_Network.addEdge(edge);
		}
		
		return augustine_Network;
	}
	
	
	/**
	 * creates a network from the given model using the reader and project classes. 
	 * The created network has nodes with types which specify either LECTURER for reader vertex
	 * or PROJECT for project vertices.
	 * @param model
	 * @return
	 */
	public Augustine_Network createReaderAugustine_NetworkFromModel(MCMFModel model){
		Augustine_Network augustine_Network = new Augustine_Network();
		
		Vertex source = model.getNetwork().source();
		Node node = new Node(source.getVertexID(), (source.getVertexID())+"", null);
		augustine_Network.addNode(node);
		augustine_Network.setSource(node);
		
		Vertex sink = model.getNetwork().sink();
		node = new Node(sink.getVertexID(), (sink.getVertexID())+"", null);
		augustine_Network.addNode(node);
		augustine_Network.setSink(node);
		
		for (Reader reader: model.getReaders()){
			Vertex v = reader.getVertex();
			node = new Node(v.getVertexID(), (v.getVertexID())+"", NodeType.LECTURER, reader.getReaderTarget());
			augustine_Network.addNode(node);
			
		}
		
		for (Project project : model.getProjects()){
			Vertex v = project.getVertex();
			node = new Node(v.getVertexID(), (v.getVertexID())+"", NodeType.PROJECT);
			augustine_Network.addNode(node);
		}
		
		// copy edges
		for (ford_fulkerson.network.Edge e : model.getNetwork().getEdges()){
			Edge edge = new Edge(augustine_Network.getNode((e.getSource().getVertexID())+""), augustine_Network.getNode((e.getDestination().getVertexID())+""), e.getCapacity(), e.getCapacity(), e.getWeight());
			augustine_Network.addEdge(edge);
		}
				
		
		return augustine_Network;
	}
	
	
}

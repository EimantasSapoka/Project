package test.augustineMCMF;

import java.util.ArrayList;

public class Augustine_Network 
{
	private ArrayList<Edge> edges;
	private ArrayList<Node> nodes;
	
	private Node source, sink;
	
	public Augustine_Network()
	{
		edges = new ArrayList<Edge>();
		nodes = new ArrayList<Node>();
	}
	
	public boolean addEdge(Edge e)
	{
		return edges.add(e);
	}
	
	public boolean removeEdge(Edge e)
	{
		return edges.remove(e);
	}
	
	public ArrayList<Edge> getEdges()
	{
		return edges;
	}
	
	public Edge getEdge(String sourceNodeName, String destNodeName)
	{
		for(Edge e : edges)
		{
			if(e.getSourceNode().getName().equals(sourceNodeName) && e.getDestNode().getName().equals(destNodeName))
				return e;
		}
		return null;
	}
	
	public ArrayList<Node> getNodes()
	{
		return nodes;
	}
	
	public boolean addNode(Node n)
	{
		return nodes.add(n);
	}
	
	public boolean removeNode(Node n)
	{
		return nodes.remove(n);
	}
	
	public Node getSource() 
	{
		return source;
	}
	
	public void setSource(Node source) 
	{
		this.source = source;
	}
	
	public Node getSink() 
	{
		return sink;
	}
	
	public void setSink(Node sink) 
	{
		this.sink = sink;
	}	

	/**
	 * Searches the node collection for a specific node
	 * @param nodeName
	 * @return
	 */
	public Node getNode(String nodeName)
	{
		for(Node n : nodes)
			if(n.getName().toLowerCase().trim().equals(nodeName.toLowerCase().trim()))
				return n;
		
		return null;
	}
	
	/**
	 * Validate that the edges form a proper network
	 */
	public boolean isNetworkValid()
	{
		// TODO will need to implement this..
		return true;
	}
	
	/**
	 * Is the flow in the network valid?
	 */
	public boolean isFlowValid()
	{
		// TODO will need to implement this...
		return true;
	}
	
	/**
	 * Augments the current flow along the specified path
	 * Throws an error if any upper quota constraints will be violated and rolls back
	 */
	public boolean augmentFlow(Path path) throws Exception
	{
		// loop through the edges in the path
		for(int i=0; i<path.size(); i++)
		{
			Edge pathEdge = path.getEdge(i);
			
			// loop through all the edges in the network
			// TODO not very efficient. There should be a better way of doing this than O(n^2)
			for(Edge e : edges)
			{
				// find the corresponding edge in the network
				if(pathEdge.equals(e))
				{
					// whats the direction? if forward
					if(path.getDirection(i))
					{
						// make sure there is spare capacity to accomodate an increase in the flow
						if((e.getUpperQuota()-e.getFlow()) >= path.getCapacity())
						{
							e.increaseFlow(path.getCapacity());
						}
						else
							throw new Exception("Seems the flow cannot accomodate the capacity of the path. " +
									"It failed while trying to increase the flow through edge "+e+ " by "+path.getCapacity()+" units.");
					}
					//else if in backward direction..
					else
					{
						// make sure that the flow through the corresponding edge in the network is positive and can handle the proposed reduction
						if(e.getFlow() >= path.getCapacity())
						{
							e.reduceFlow(path.getCapacity());
						}
						else
							throw new Exception("Seems the flow cannot accomodate the capacity of the path. " +
									"It failed while trying to reduce the flow through edge "+e+ " by "+path.getCapacity()+" units.");
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the size of the current flow in the network
	 * @return
	 */
	public int getFlowSize()
	{
		int result = 0;
		
		for(Edge e : this.source.getOutgoingEdges())
		{
			result += e.getFlow();
		}
		
		return result;
	}
	
	/**
	 * Returns the cost of the current flow in the network
	 * @return
	 */
	public int getFlowCost()
	{
		int result = 0;
		
		for (Node v : this.getNodes())
		    for (Edge e : v.getOutgoingEdges())
			result += (e.getFlow() * e.getCost());
		
		return result;
	}

	@Override
	public String toString() 
	{
		StringBuilder outputBuilder = new StringBuilder();
		for(Edge e : edges)
			outputBuilder.append(e+"\r\n");
		
		return outputBuilder.toString();
	}
	
	/************* ADDED METHODS ******************/
	
	public void networkDescription(){
		for (Node n : this.nodes){
			if (NodeType.LECTURER.equals(n.getType())){
				
				System.out.print("\nReader " + n.getId() + " cap: " + n.capacity + " assigned: ");
				for (Edge e : n.getOutgoingEdges()){
					if (e.getFlow() > 0){
						System.out.print(e.getDestNode().getId() + " ");
					}
				}
				
			}
		}
		
		System.out.println(String.format(""
				+ "\n total flow: %d"
				+ "\n total weight: %d"
				+ "\n load balanced? : %b"
				+ "\n saturating flow? : %b", 
				this.getFlowSize(),
				this.getFlowCost(),
				this.isLoadBalanced(),
				this.isSaturatingFlow()));
	}
	
	private boolean isSaturatingFlow() {
		return this.getCapacityIn() == this.getFlowSize() || this.getCapacityOut() == this.getFlowSize();
	}

	private int getCapacityOut() {
		int cap = 0; 
		for (Edge e : this.sink.getIncommingEdges()){
			cap += e.getUpperQuota();
		}
		return cap;
	}

	private int getCapacityIn() {
		int cap = 0; 
		for (Edge e : this.source.getOutgoingEdges()){
			cap += e.getUpperQuota();
		}
		return cap;
	}

	public boolean isLoadBalanced(){		
		for (Node lecturer : this.nodes){
			if (NodeType.LECTURER.equals(lecturer.getType())){
				
				int readerFlow = calculateReaderFlow(lecturer);
				for (Node otherLecturer: this.nodes){
					if (NodeType.LECTURER.equals(otherLecturer.getType())){
						
						int otherFlow = calculateReaderFlow(otherLecturer);
						if (Math.abs( (lecturer.capacity -readerFlow) - (otherLecturer.capacity - otherFlow)) > 1){
							return false;
						}
					}
				}
			}
		}
		
		
		return true;
	}

	private int calculateReaderFlow(Node lecturer) {
		int readerFlow = 0;
		for (Edge e : lecturer.getOutgoingEdges()){
			if (e.getFlow() > 0){
				readerFlow++;
			}
		}
		return readerFlow;
	}
	
	
}

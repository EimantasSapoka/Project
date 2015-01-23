package augustineMCMF;

public class Edge 
{
	private Node[] nodes;
	private int upperQuota, lowerQuota, flow;
	
	// a variable to store edge costs
	private long cost;
	
	public Edge(Node n1, Node n2, int upperQuota)
	{
		// some initilisations
		nodes = new Node[]{n1, n2};
		this.upperQuota = upperQuota;
		lowerQuota = 0;
		flow = 0;
		cost = 0;
		
		// let each node know about the other
		// will this be of any use? might remove this later
		n1.addOutgoingNode(this);
		n2.addIncommingNode(this);
	}
	
	public Edge(Node n1, Node n2, int lowerQuota, int upperQuota, long cost)
	{
		this(n1, n2, upperQuota);
		this.lowerQuota = lowerQuota;
		this.cost = cost;
	}

	public int getFlow() 
	{
		return flow;
	}

	public void setFlow(int flow) 
	{
		this.flow = flow;
	}
	
	public void reduceFlow(int flow) 
	{
		this.flow -= flow;
	}
	
	public void increaseFlow(int flow) 
	{
		this.flow += flow;
	}
	
	public void setCost(long cost) 
	{
		this.cost = cost;
	}
	
	public long getCost() 
	{
		return cost;
	}

	public int getUpperQuota() 
	{
		return upperQuota;
	}

	public int getLowerQuota() 
	{
		return lowerQuota;
	}
	
	public void setLowerQuota(int lq)
	{
		this.lowerQuota = lq;
	}
	
	public void setUpperQuota(int uq)
	{
		this.upperQuota = uq;
	}
	
	public Node getSourceNode()
	{
		if(nodes!=null && nodes.length==2)
			return nodes[0];
		else 
			return null;
	}
	
	public Node getDestNode()
	{
		if(nodes!=null && nodes.length==2)
			return nodes[1];
		else 
			return null;
	}
	
	public boolean isSaturated()
	{
		return flow >= upperQuota;
	}
	
	@Override
	public String toString() 
	{
		if(nodes!=null && nodes.length==2)
			return nodes[0] + "->" + nodes[1] +" ; capacity " + lowerQuota +" weight: "+cost;
		else
			return "Edge is not consistent with requirements";
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		return nodes[0].equals(((Edge)obj).getSourceNode()) && nodes[1].equals(((Edge)obj).getDestNode());
	}
	
	public void detach()
	{	
		nodes = null;
		upperQuota = 0;
		lowerQuota = 0;
		flow = 0;
		cost = 0;
	}
}

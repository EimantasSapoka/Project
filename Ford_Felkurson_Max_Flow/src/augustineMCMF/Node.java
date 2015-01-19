package augustineMCMF;

import java.util.ArrayList;

public class Node implements Comparable<Node>
{
	private int id;
	private String name;
	private NodeType type;
	
	// holds the distance between this node and the source node
	// where distances are determined by the value of the Edge.cost variable
	private long dijkstraDistance;
	private boolean isDikjstraDistranceInfinity;
	
	// holds references to a set of incomming and outgoing edges
	private ArrayList<Edge> incommingEdges, outgoingEdges;
	
	// may hold a reference to a  preceeding node in some path
	private Node predNode;
	private Edge predEdge;
	
	// may hold the profile of a path that passes through this node
	private Profile profile;
	
	// node comparison options
	public static NodeOrderings NODE_ORDER = NodeOrderings.NONE;
	public enum NodeOrderings
	{
		NONE, // no node orderings 
		PROJECT_GAP_INC, // the difference between the number of assigned students and the project lower quotas (increasing)
		PROJECT_GAP_DEC, // the difference between the number of assigned students and the project lower quotas (decreasing)
		PROJECT_POPULARITY_INC,// the popularity of the project (increasing)
		PROJECT_POPULARITY_DEC, // the popularity of the project (decreasing)
		PROJECT_MATCH_SIZE_INC, // number of students currently assigned to this project (increasing)
		PROJECT_MATCH_SIZE_DEC; // number of students currently assigned to this project (decreasing)
	}
	
	/* added */ public int capacity;

	public Node(int id, String name, NodeType type, int capacity){
		this(id, name, type);
		this.capacity = capacity;
	}
	
	public Node(int id, String name, NodeType type)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		
		incommingEdges = new ArrayList<Edge>();
		outgoingEdges = new ArrayList<Edge>();
		
		dijkstraDistance = 0;
		isDikjstraDistranceInfinity = true;
	}
	
	public boolean isDikjstraDistranceInfinity() 
	{
		return isDikjstraDistranceInfinity;
	}

	public void setDikjstraDistranceInfinity(boolean isDikjstraDistranceInfinity) 
	{
		this.isDikjstraDistranceInfinity = isDikjstraDistranceInfinity;
	}

	public int getId() 
	{
		return id;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public NodeType getType() 
	{
		return type;
	}
	
	public void addIncommingNode(Edge e)
	{
		incommingEdges.add(e);
	}
	
	public void addOutgoingNode(Edge e)
	{
		outgoingEdges.add(e);
	}
	
	public ArrayList<Edge> getIncommingEdges() 
	{
		return incommingEdges;
	}

	public ArrayList<Edge> getOutgoingEdges() 
	{
		return outgoingEdges;
	}

	public Node getPredNode() 
	{
		return predNode;
	}
	
	public Edge getPredEdge() 
	{
		return predEdge;
	}

	public void setPred(Node predNode, Edge predEdge) 
	{
		this.predNode = predNode;
		this.predEdge = predEdge;
	}

	public Profile getProfile() 
	{
		return profile;
	}

	public void setProfile(Profile profile) 
	{
		this.profile = profile;
	}

	@Override
	public boolean equals(Object obj) 
	{
		// just evaluate the equality on their node name.
		return name.toLowerCase().trim().equals(((Node)obj).getName().toLowerCase().trim());
	}
	
	@Override
	public String toString() 
	{
		return name;
	}
	
	public void setDistance(long distance)
	{
		dijkstraDistance = distance;
	}
	
	public long getDistance()
	{
		return dijkstraDistance;
	}

	public int compareTo(Node o) 
	{
		if(NODE_ORDER == NodeOrderings.PROJECT_GAP_INC || NODE_ORDER == NodeOrderings.PROJECT_GAP_DEC)
		{
			if(this.type == NodeType.PROJECT)
			{
				int thisGap = this.getOutgoingEdges().get(0).getLowerQuota() - this.getOutgoingEdges().get(0).getFlow();
				int oGap = o.getOutgoingEdges().get(0).getLowerQuota() - o.getOutgoingEdges().get(0).getFlow();
				
				if(NODE_ORDER == NodeOrderings.PROJECT_GAP_INC)return thisGap-oGap;
				if(NODE_ORDER == NodeOrderings.PROJECT_GAP_DEC)return oGap-thisGap;
			}
		}
		
		if(NODE_ORDER == NodeOrderings.PROJECT_POPULARITY_INC || NODE_ORDER == NodeOrderings.PROJECT_POPULARITY_DEC)
		{
			if(this.type == NodeType.PROJECT)
			{
				int thisPop = this.getIncommingEdges().size();
				int oPop = o.getIncommingEdges().size();
				
				if(NODE_ORDER == NodeOrderings.PROJECT_POPULARITY_INC)return thisPop-oPop;
				if(NODE_ORDER == NodeOrderings.PROJECT_POPULARITY_DEC)return oPop-thisPop;
			}
		}
		
		if(NODE_ORDER == NodeOrderings.PROJECT_MATCH_SIZE_INC || NODE_ORDER == NodeOrderings.PROJECT_MATCH_SIZE_DEC)
		{
			if(this.type == NodeType.PROJECT)
			{
				int thisFlow = this.getOutgoingEdges().get(0).getFlow();
				int oFlow = o.getOutgoingEdges().get(0).getFlow();
				
				if(NODE_ORDER == NodeOrderings.PROJECT_MATCH_SIZE_INC)return thisFlow-oFlow;
				if(NODE_ORDER == NodeOrderings.PROJECT_MATCH_SIZE_DEC)return oFlow-thisFlow;
			}
		}
		
		return 0;
	}
}

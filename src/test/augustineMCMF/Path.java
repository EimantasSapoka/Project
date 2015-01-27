package test.augustineMCMF;

import java.util.ArrayList;

/**
 * Represents a path through the network. 
 * May be a partial augmenting path, a general alternating path or a full augmenting path
 * 
 * @author augustine kwanashie
 *
 */
public class Path 
{
	private ArrayList<Edge> edges;
	
	// specifies the directions of the corresponding edges
	// answers the question: is edge forward?
	private ArrayList<Boolean> directions;
	
	// represents the amount of flow to be passed through the path.
	private int capacity;
	
	public Path()
	{
		edges = new ArrayList<Edge>();
		directions = new ArrayList<Boolean>();
		
		// we default to 1
		capacity = 1;
	}
	
	public int getCapacity() 
	{
		return capacity;
	}

	public void setCapacity(int capacity) 
	{
		this.capacity = capacity;
	}


	public Edge getEdge(int index) throws Exception
	{
		if(index < edges.size())
			return edges.get(index);
		
		throw new Exception("Sorry cant find the edge you're looking for.");
	}
	
	public boolean getDirection(int index) throws Exception
	{
		if(index < directions.size())
			return directions.get(index);
		
		throw new Exception("Seems the path is not consistent. You've requested for the direction of an edge that does not exist.");
	}
	
	public int size()
	{
		return edges.size();
	}
	
	/**
	 * Adds an edge to the end of the path
	 * @return
	 */
	public boolean addEdge(Edge e, boolean isForward)
	{
		if(edges.add(e))
			return directions.add(isForward);
		return false;
	}
	
	@Override
	public String toString() 
	{
		StringBuilder outputBuilder = new StringBuilder();
		outputBuilder.append("Path capacity: " + capacity+"\r\n");
		
		// loop through edges and print them out
		for(int i=0; i<edges.size(); i++)
		{
			Edge e = edges.get(i);
			String dir = directions.get(i)?"forward":"backward";
			outputBuilder.append(e + "(" + dir + ")\r\n");
		}
		
		return outputBuilder.toString();
	}
}

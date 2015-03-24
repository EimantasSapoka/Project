package test.graph_creator;

import ford_fulkerson.network.NetworkObjectInterface;

public class MockNetworkObject implements NetworkObjectInterface {
	
	private int id;
	
	public MockNetworkObject(int id){
		this.id = id;
	}

	@Override
	public int getID() {
		return id;
	}

}

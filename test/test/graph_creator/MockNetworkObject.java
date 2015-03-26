package test.graph_creator;

import ford_fulkerson.network.NetworkObjectInterface;

/**
 * a mock network object which mocks a reader/project
 * @author Eimantas
 *
 */

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

package it.sky.mdw.api.network;

import java.util.Collection;
import java.util.Properties;

public interface NetworkNode {

	Object getValue();

	String getLabel();

	Properties getProperties();

	void addSuccessor(NetworkNode successor);

	Collection<NetworkNode> getAllSuccessors();

	// return all connected nodes with depth equal to 1
	Collection<NetworkNode> getClosestSuccessors();
}

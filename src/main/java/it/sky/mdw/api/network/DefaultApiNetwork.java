package it.sky.mdw.api.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultApiNetwork implements ApiNetwork {

	private Map<String, DefaultNetworkNode> network;

	public DefaultApiNetwork() {
		network = new ConcurrentHashMap<>();
	}

	public NetworkNode addEntity(String label, Object value, Properties properties) {
		Objects.requireNonNull(label);
		synchronized (network) {
			DefaultNetworkNode node = new DefaultNetworkNode(label, value, properties);

			if(network.containsKey(label)){
//				DefaultNetworkNode previous = network.get(label);
//				node.setSuccessors(previous.getSuccessors());
//				node.setProperties(previous.getProperties());
				node = network.get(label);
			}

			network.put(label, node);
			return node;
		}
	}

	public void addConnection(String entityLabelIn, String entityLabelOut) {
		Objects.requireNonNull(entityLabelIn);
		Objects.requireNonNull(entityLabelOut);
		synchronized (network) {
			NetworkNode node = network.get(entityLabelIn);
			if(node == null)
					node = addEntity(entityLabelIn, null, null);
			NetworkNode successor = network.get(entityLabelOut);
			if(successor == null)
					successor = addEntity(entityLabelOut, null, null);

			node.addSuccessor(successor);

			//		if(network.containsKey(entityLabelOut))
			//			network.remove(entityLabelOut);	
		}

	}

	public NetworkNode findEntityByApiName(String entityLabel){
		Objects.requireNonNull(entityLabel);
		if(network.containsKey(entityLabel))
			return network.get(entityLabel);
		else{
			NetworkNode value = null;
			for(DefaultNetworkNode t: network.values()){
				NetworkNode nodeValue = t.findEntityByApiName(entityLabel);
				if(nodeValue != null){
					value = nodeValue;
					break;
				}				
			}

			return value;
		}
	}

	public Collection<NetworkNode> findEntityConnections(String entityLabel) {
		Objects.requireNonNull(entityLabel);
		Collection<NetworkNode> connections = new ArrayList<>();
		NetworkNode node = findEntityByApiName(entityLabel);
		if(node!=null){
			connections.addAll(node.getAllSuccessors());
		}
		return connections;
	}

	public Collection<NetworkNode> findEntityConnections(NetworkNode entity) {
		Objects.requireNonNull(entity);
		return entity.getAllSuccessors();
	}

}

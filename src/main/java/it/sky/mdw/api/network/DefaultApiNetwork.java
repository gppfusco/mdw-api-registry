package it.sky.mdw.api.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultApiNetwork implements ApiNetwork {

	private Map<String, DefaultNetworkNode> network;

	public DefaultApiNetwork() {
		network = new ConcurrentHashMap<>();
	}

	public NetworkNode addEntity(String label, Object value, Optional<Properties> properties) {
		Objects.requireNonNull(label);
		synchronized (network) {
			DefaultNetworkNode node = new DefaultNetworkNode(label, value, properties);

			if(network.containsKey(label)){
//				DefaultNetworkNode previous = network.get(label);
//				node.setSuccessors(previous.getSuccessors());
//				node.setProperties(previous.getProperties());
				node = network.get(label);
			}

			return network.put(label, node);
		}
	}

	@Override
	public void addConnection(String entityLabelIn, String entityLabelOut) {
		Objects.requireNonNull(entityLabelIn);
		Objects.requireNonNull(entityLabelOut);
		synchronized (network) {
			NetworkNode node = network.getOrDefault(entityLabelIn, 
					(DefaultNetworkNode) addEntity(entityLabelIn, null, Optional.empty()));
			NetworkNode successor = network.getOrDefault(entityLabelOut, 
					(DefaultNetworkNode) addEntity(entityLabelOut, null, Optional.empty()));

			node.addSuccessor(successor);

			//		if(network.containsKey(entityLabelOut))
			//			network.remove(entityLabelOut);	
		}

	}

	@Override
	public Optional<NetworkNode> findEntityByApiName(String entityLabel){
		Objects.requireNonNull(entityLabel);
		if(network.containsKey(entityLabel))
			return Optional.of(network.get(entityLabel));
		else{
			Optional<NetworkNode> value = Optional.empty();
			for(DefaultNetworkNode t: network.values()){
				Optional<NetworkNode> nodeValue = t.findEntityByApiName(entityLabel);
				if(nodeValue.isPresent()){
					value = Optional.of(nodeValue.get());
					break;
				}				
			}

			return value;
		}
	}

	@Override
	public Collection<NetworkNode> findEntityConnections(String entityLabel) {
		Objects.requireNonNull(entityLabel);
		Collection<NetworkNode> connections = new ArrayList<>();
		NetworkNode node = findEntityByApiName(entityLabel).orElse(null);
		if(node!=null){
			connections.addAll(node.getAllSuccessors());
		}
		return connections;
	}

	@Override
	public Collection<NetworkNode> findEntityConnections(NetworkNode entity) {
		Objects.requireNonNull(entity);
		return entity.getAllSuccessors();
	}

}

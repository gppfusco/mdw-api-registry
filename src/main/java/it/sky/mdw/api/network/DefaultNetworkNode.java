package it.sky.mdw.api.network;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultNetworkNode implements NetworkNode {

	private String label;
	@JsonIgnore
	private Object value;
	private Properties properties;
	private Map<String, DefaultNetworkNode> successors;

	public DefaultNetworkNode(String label, Object value) {
		this(label, value, null);
	}

	public DefaultNetworkNode(String label, Object value, Properties properties) {
		this.label = label;
		this.value = value;
		this.properties = properties!=null ? properties : new Properties();
		successors = new ConcurrentHashMap<>();
	}

	public Map<String, DefaultNetworkNode> getSuccessors() {
		return successors;
	}

	public Object getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public void setSuccessors(Map<String, DefaultNetworkNode> successors) {
		this.successors = successors;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	private void addSuccessor(String label, Object value, Map<String, DefaultNetworkNode> optionalSuccessors, Properties properties) {
		synchronized (successors) {
			DefaultNetworkNode node = successors.get(label);
			if(node == null)
				node = new DefaultNetworkNode(label, value);

			if(optionalSuccessors !=null)
				node.setSuccessors(optionalSuccessors);

			if(properties!=null)
				node.setProperties(properties);

			if(successors.containsKey(label)){
				DefaultNetworkNode previous = successors.get(label);
				node.setSuccessors(previous.getSuccessors());
			}
			successors.put(label, node);	
		}
	}

	private void addSuccessor(DefaultNetworkNode successor) {
		addSuccessor(successor.getLabel(), successor.getValue(), successor.getSuccessors(), successor.getProperties());
	}

	public void addSuccessor(NetworkNode successor) {
		Objects.requireNonNull(successor);
		addSuccessor((DefaultNetworkNode)successor);
	}

	public NetworkNode findEntityByApiName(String entityLabel) {
		Objects.requireNonNull(entityLabel);
		if(successors.containsKey(entityLabel))
			return successors.get(entityLabel);
		else{
			NetworkNode value = null;
			for(DefaultNetworkNode t: successors.values()){
				NetworkNode nodeValue = t.findEntityByApiName(entityLabel);
				if(nodeValue != null){
					value = nodeValue;
					break;
				}				
			}

			return value;
		}
	}

	@JsonIgnore
	public Collection<NetworkNode> getAllSuccessors(){
		Collection<NetworkNode> connections = new ArrayList<>();
		for(NetworkNode t: successors.values()){
			connections.add(t);
			connections.addAll(t.getAllSuccessors());
		}
		return connections;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(label);
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if(obj instanceof NetworkNode)
				return ((NetworkNode)obj).getLabel().equals(label);
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	@JsonIgnore
	public Collection<NetworkNode> getClosestSuccessors() {
		return new ArrayList<NetworkNode>(successors.values());
	}

}

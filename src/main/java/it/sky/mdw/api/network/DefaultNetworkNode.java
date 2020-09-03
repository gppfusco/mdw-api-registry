package it.sky.mdw.api.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
		successors = new ConcurrentHashMap<String, DefaultNetworkNode>();
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

	public void setValue(Object value) {
		this.value = value;
	}

	public void setLabel(String label) {
		this.label = label;
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
		Collection<NetworkNode> connections = new ArrayList<NetworkNode>();
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
			if(obj!=null && obj instanceof NetworkNode)
				return ((NetworkNode)obj).getLabel().equals(label);
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	public Collection<NetworkNode> getClosestSuccessors() {
		Collection<NetworkNode> c = Collections.emptyList();
		c.addAll(successors.values());
		return c;
	}

}

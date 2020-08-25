package it.sky.mdw.api.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DefaultNetworkNode implements NetworkNode {

	private String label;
	@JsonIgnore
	private Object value;
	private Properties properties;
	private Map<String, DefaultNetworkNode> successors;

	public DefaultNetworkNode(String label, Object value) {
		this(label, value, Optional.empty());
	}

	public DefaultNetworkNode(String label, Object value, Optional<Properties> properties) {
		this.label = label;
		this.value = value;
		this.properties = properties.orElse(new Properties());
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

	private void addSuccessor(String label, Object value, Optional<Map<String, DefaultNetworkNode>> optionalSuccessors, Optional<Properties> properties) {
		synchronized (successors) {
			DefaultNetworkNode node = successors.getOrDefault(label, new DefaultNetworkNode(label, value));

			if(optionalSuccessors.isPresent())
				node.setSuccessors(optionalSuccessors.get());

			if(properties.isPresent())
				node.setProperties(properties.get());

			if(successors.containsKey(label)){
				DefaultNetworkNode previous = successors.get(label);
				node.setSuccessors(previous.getSuccessors());
			}
			successors.put(label, node);	
		}
	}

	private void addSuccessor(DefaultNetworkNode successor) {
		addSuccessor(successor.getLabel(), successor.getValue(), Optional.ofNullable(successor.getSuccessors()), Optional.ofNullable(successor.getProperties()));
	}

	@Override
	public void addSuccessor(NetworkNode successor) {
		Objects.requireNonNull(successor);
		addSuccessor((DefaultNetworkNode)successor);
	}

	public Optional<NetworkNode> findEntityByApiName(String entityLabel) {
		Objects.requireNonNull(entityLabel);
		if(successors.containsKey(entityLabel))
			return Optional.of(successors.get(entityLabel));
		else{
			Optional<NetworkNode> value = Optional.empty();
			for(DefaultNetworkNode t: successors.values()){
				Optional<NetworkNode> nodeValue = t.findEntityByApiName(entityLabel);
				if(nodeValue.isPresent()){
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
		successors.values().forEach(new Consumer<NetworkNode>() {
			@Override
			public void accept(NetworkNode t) {
				connections.add(t);
				connections.addAll(t.getAllSuccessors());
			}
		});

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

	@Override
	public Collection<NetworkNode> getClosestSuccessors() {
		return new ArrayList<>(successors.values());
	}

}

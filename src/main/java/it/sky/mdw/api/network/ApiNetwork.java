package it.sky.mdw.api.network;

import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

public interface ApiNetwork {

	NetworkNode addEntity(String label, Object value, Optional<Properties> properties);

	void addConnection(String entityLabelIn, String entityLabelOut);

	<N extends NetworkNode> Optional<N> findEntityByApiName(String entityLabel);

	<N extends NetworkNode> Collection<N> findEntityConnections(String entityLabel);

	<N extends NetworkNode> Collection<N> findEntityConnections(N entity);

}

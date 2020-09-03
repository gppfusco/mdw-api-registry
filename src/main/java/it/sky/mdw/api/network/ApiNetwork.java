package it.sky.mdw.api.network;

import java.util.Collection;
import java.util.Properties;

public interface ApiNetwork {

	NetworkNode addEntity(String label, Object value, Properties properties);

	void addConnection(String entityLabelIn, String entityLabelOut);

	NetworkNode findEntityByApiName(String entityLabel);

	<N extends NetworkNode> Collection<N> findEntityConnections(String entityLabel);

	<N extends NetworkNode> Collection<N> findEntityConnections(N entity);

}

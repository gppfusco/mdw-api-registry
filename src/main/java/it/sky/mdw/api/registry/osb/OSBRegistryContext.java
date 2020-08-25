package it.sky.mdw.api.registry.osb;

import javax.management.MBeanServerConnection;

import com.bea.wli.sb.management.configuration.ALSBConfigurationMBean;

import it.sky.mdw.api.DefaultRegistryContext;

public class OSBRegistryContext extends DefaultRegistryContext {

	private ALSBConfigurationMBean alsbConfigurationMBean;
	private MBeanServerConnection connection;
	private static OSBRegistryContext instance;
	
	private OSBRegistryContext() {
	}
	
	public static OSBRegistryContext getInstance() {
		if(instance == null)
			instance = new OSBRegistryContext();
		return instance;
	}
	
	public void initializeContext(ALSBConfigurationMBean alsbConfigurationMBean, MBeanServerConnection connection) {
		if(instance == null)
			instance = new OSBRegistryContext();
		if(this.alsbConfigurationMBean == null)
			this.alsbConfigurationMBean = alsbConfigurationMBean;
		if(this.connection == null)
			this.connection = connection;
	}

	public ALSBConfigurationMBean getAlsbConfigurationMBean() {
		return alsbConfigurationMBean;
	}

	public MBeanServerConnection getConnection() {
		return connection;
	}
	
}

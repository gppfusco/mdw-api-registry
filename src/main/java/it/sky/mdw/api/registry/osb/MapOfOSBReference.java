package it.sky.mdw.api.registry.osb;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.bea.wli.config.Ref;
import com.bea.wli.sb.management.configuration.ALSBConfigurationMBean;

public class MapOfOSBReference {

	private static Logger logger = Logger.getLogger(MapOfOSBReference.class);

	private static MapOfOSBReference instance;
	private Map<String, Ref> mapOfRef;

	private MapOfOSBReference() {
		mapOfRef = new ConcurrentHashMap<String, Ref>();
	}

	public static MapOfOSBReference getInstance() {
		if(instance==null)
			instance = new MapOfOSBReference();

		return instance;
	}

	public void onStart(ALSBConfigurationMBean alsbConfigurationMBean) throws Exception{
		Set<Ref> refs = alsbConfigurationMBean.getRefs(Ref.DOMAIN);
		logger.info("Retrieved Refs from server");
		logger.info("Processing Refs...");
		for (Ref ref : refs) {
			String typeId = ref.getTypeId();
			if(typeId.equalsIgnoreCase("WSDL"))
			{
				mapOfRef.put("wsdl_" + ref.getFullName(), ref);
				logger.debug("Found reference: " + ref.getFullName());
			}
			if(typeId.equalsIgnoreCase("WADL"))
			{
				mapOfRef.put("wadl_" + ref.getFullName(), ref);
				logger.debug("Found reference: " + ref.getFullName());
			}
			if (typeId.equalsIgnoreCase("XMLSchema")){
				mapOfRef.put("xsd_" + ref.getFullName(), ref);
				logger.debug("Found reference: " + ref.getFullName());
			}
		}
		logger.info("Processing Refs... completed");
		logger.info("Total number of Refs found: " + mapOfRef.size());
	}

	public Ref getRef(String refFullName){
		Ref foundRef = null;
		if(mapOfRef!=null && mapOfRef.containsKey(refFullName)){
			foundRef = mapOfRef.get(refFullName);
			logger.debug("Found Ref: " + refFullName);
		}
		return foundRef;
	}

}

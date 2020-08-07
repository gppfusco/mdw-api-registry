package it.sky.mdw.api.registry.osb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bea.wli.config.Ref;
import com.bea.wli.sb.management.configuration.ALSBConfigurationMBean;

import it.sky.mdw.api.Api;
import it.sky.mdw.api.ApiSpecification;
import it.sky.mdw.api.DefaultApiSpecification;
import it.sky.mdw.api.RestApi;
import it.sky.mdw.api.RestApiSpec;
import it.sky.mdw.api.SoapApi;
import it.sky.mdw.api.SoapApiSpec;
import it.sky.mdw.api.XSDExternalRef;

public class OSBProxyProcessor implements Callable<Api<? extends ApiSpecification>>{

	private static Logger logger = Logger.getLogger(OSBProxyProcessor.class);

	private ObjectName osbResourceConfiguration;
	private String environmentHostName;
	private MBeanServerConnection connection;
	private ALSBConfigurationMBean alsbConfigurationMBean;

	public OSBProxyProcessor(ObjectName osbResourceConfiguration, MBeanServerConnection connection, ALSBConfigurationMBean alsbConfigurationMBean, String environmentHostName) {
		super();
		this.osbResourceConfiguration = osbResourceConfiguration;
		this.environmentHostName = environmentHostName;
		this.connection = connection;
		this.alsbConfigurationMBean = alsbConfigurationMBean;
	}


	public Api<? extends ApiSpecification> call() throws Exception {
		String resourceName = osbResourceConfiguration.getKeyProperty("Name");
		String normalizedProxyName = resourceName.replaceAll("ProxyService\\W", "").replaceAll("\\W", "/");

		if(resourceName.startsWith("ProxyService$")){
			try {
				CompositeDataSupport metadata =
						(CompositeDataSupport)connection.getAttribute(osbResourceConfiguration,
								"Metadata");
				CompositeDataSupport configuration =
						(CompositeDataSupport)connection.getAttribute(osbResourceConfiguration,
								"Configuration");
				String servicetype =
						(String)configuration.get("service-type");
				CompositeDataSupport transportconfiguration =
						(CompositeDataSupport)configuration.get("transport-configuration");
				String transporttype =
						(String)transportconfiguration.get("transport-type");
				String url = (String)transportconfiguration.get("url");

				logger.debug("  Configuration of " + normalizedProxyName +
						":" + " service-type=" + servicetype +
						", transport-type=" + transporttype +
						", url=" + url);

				String[] dependencies =
						(String[])metadata.get("dependencies");

				if(transporttype.equals("http")){
					String fullApiName = normalizedProxyName.replaceAll("/", "_");
					String apiPath = ("/" + url).replace("//" ,"/");

					if(servicetype.equals("SOAP")){
						SoapApi api = new SoapApi();
						api.setEndpoint(environmentHostName);
						api.setName(fullApiName);
						api.setPath(apiPath);

						ApiSpecification apiSpec_ = exploreDependencies(api.getEndpoint(), dependencies);
						if(apiSpec_ instanceof SoapApiSpec){
							SoapApiSpec apiSpec = (SoapApiSpec) apiSpec_;
							apiSpec.setApiSpecEndpoint(environmentHostName + apiPath +"?WSDL");
							api.setApiSpecification(apiSpec);
						}

						return api;
					}
					if(servicetype.equals("REST")){
						RestApi api = new RestApi();
						api.setEndpoint(environmentHostName);
						api.setName(fullApiName);
						api.setPath(apiPath);

						ApiSpecification apiSpec_ = exploreDependencies(api.getEndpoint(), dependencies);
						if(apiSpec_ instanceof RestApiSpec){
							RestApiSpec apiSpec = (RestApiSpec) apiSpec_;
							apiSpec.setApiSpecEndpoint(environmentHostName + apiPath +"?WADL");
							api.setApiSpecification(apiSpec);
						}

						return api;
					}
				}
			} catch (Exception e) {
				logger.error("", e);
			}			
		}

		return null;
	}

	private DefaultApiSpecification exploreDependencies(String apiEndpoint, String[] dependencies) throws Exception {
		SoapApiSpec soapApiSpec = null;
		RestApiSpec restApiSpec = null;
		for (int i = 0; i < dependencies.length; i++) {
			String dependency = dependencies[i];
			logger.debug("Dependency found - " + dependency + "\n");
			if(dependency.startsWith("WSDL")){
				String normalizedWsdlName = "wsdl_" +dependency.replaceAll("WSDL\\W", "").replaceAll("\\W", "/");
				logger.debug("Asking for Ref: " + normalizedWsdlName);
				SimpleEntry<Ref, byte[]> wsdlRef = findRef(normalizedWsdlName);
				logger.debug("Retrieved Ref: " + normalizedWsdlName);
				if(wsdlRef != null){
					byte[] bytes = wsdlRef.getValue();
					soapApiSpec = (SoapApiSpec) extractWsdl(apiEndpoint, bytes, true);
					List<XSDExternalRef> xsdRefs = soapApiSpec.getXsdExternalRef();
					for(XSDExternalRef xsdRef: xsdRefs){
						String basePath = xsdRef.getXsdBasePath();
						logger.debug("Asking for Ref: " + "xsd_"+basePath);
						SimpleEntry<Ref, byte[]> xsd = findRef("xsd_"+basePath);
						logger.debug("Retrieved Ref: " + "xsd_"+basePath);
						if(xsd!=null){
							xsdRef.setXsdBasePath("/" + xsdRef.getXsdBasePath());
							byte[] xsdExportedBytes = xsd.getValue();
							byte[] xsdBytes = extractXsdSchema(xsdExportedBytes);
							xsdRef.setXsdSchema(xsdBytes);
						}
					}
				}
			}
			if(dependency.startsWith("WADL")){
				String normalizedWsdlName = "wadl_" + dependency.replaceAll("WADL\\W", "").replaceAll("\\W", "/");
				logger.debug("Asking for Ref: " + normalizedWsdlName);
				SimpleEntry<Ref, byte[]> wsdlRef = findRef(normalizedWsdlName);
				logger.debug("Retrieved Ref: " + normalizedWsdlName);
				if(wsdlRef != null){
					byte[] bytes = wsdlRef.getValue();
					restApiSpec = (RestApiSpec) extractWsdl(apiEndpoint, bytes, false);
				}
			}
		}

		if(restApiSpec==null && soapApiSpec==null)
			return null;

		if(restApiSpec != null){
			restApiSpec.setSoapApiSpec(soapApiSpec);
			return restApiSpec;
		}else
			return soapApiSpec;
	}


	@SuppressWarnings("deprecation")
	private SimpleEntry<Ref, byte[]> findRef(String normalizedWsdlName) {
		Ref ref;
		SimpleEntry<Ref, byte[]> entry = null;
		ref = MapOfOSBReference.getInstance().getRef(normalizedWsdlName);
		if(ref != null){
			byte[] bytes;
			try {
				bytes = alsbConfigurationMBean.export(Collections.singleton(ref), false, "");
				entry = new SimpleEntry<Ref, byte[]>(ref, bytes);
			} catch (Exception e) {
				logger.error("Error while exporting bytes for: " + normalizedWsdlName, e);
			}
		}

		return entry;
	}

	private DefaultApiSpecification extractWsdl(String apiEndpoint, byte[] bytes, boolean isSoapApiSpec) throws Exception {
		DefaultApiSpecification spec = isSoapApiSpec ? 
				new SoapApiSpec(apiEndpoint) : 
					new RestApiSpec(apiEndpoint);
				ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(bytes));
				ZipEntry entry;
				while((entry = zipStream.getNextEntry())!=null){
					String entryName = entry.getName().toLowerCase();
					String typeOfEntry = entryName.endsWith(".wsdl") ? "/wsdlEntry" : 
						entryName.endsWith(".wadl") ? "/wadlEntry" : "";
					if(typeOfEntry.equals("/wsdlEntry") || typeOfEntry.equals("/wadlEntry")){
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte[] buf = new byte[1024 * 4];
						int len = zipStream.read(buf);
						while (len > 0) {
							baos.write(buf, 0, len);
							len = zipStream.read(buf);
						}

						DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
						Document doc = dBuilder.parse(new ByteArrayInputStream(baos.toByteArray()));
						doc.getDocumentElement().normalize();

						XPath xPath =  XPathFactory.newInstance().newXPath();
						String apiSpecPathExpression = typeOfEntry.equals("/wsdlEntry") ? "/wsdl" : "/wadl";
						Node apiSpecNode = ((NodeList) xPath.compile(typeOfEntry + apiSpecPathExpression).evaluate(doc, XPathConstants.NODESET)).item(0);

						spec.setApiSpecContent(apiSpecNode.getTextContent().getBytes());

						String schemaRefPathExpression = typeOfEntry.equals("/wsdlEntry") ? "/schemaRef" : "/importSchema";
						NodeList schemaRef = (NodeList) xPath.compile(typeOfEntry + "/dependencies" + schemaRefPathExpression).evaluate(
								doc, XPathConstants.NODESET);

						List<XSDExternalRef> xsdRefs = new ArrayList<XSDExternalRef>();
						for (int i = 0; i < schemaRef.getLength(); i++) {
							Node nNode = schemaRef.item(i);
							XSDExternalRef xsdRef = new XSDExternalRef(
									apiEndpoint, 
									nNode.getAttributes().getNamedItem("ref").getTextContent(), 
									nNode.getAttributes().getNamedItem("namespace").getTextContent());

							xsdRefs.add(xsdRef);
						}

						spec.setXsdExternalRef(xsdRefs);
					}
				}

				return spec;
	}

	private byte[] extractXsdSchema(byte[] bytes) throws Exception {
		byte[] xsdBytes = null;
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(bytes));
		ZipEntry entry;
		while((entry = zipStream.getNextEntry())!=null){
			String entryName = entry.getName().toLowerCase();
			if(entryName.endsWith(".xmlschema")){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024 * 4];
				int len = zipStream.read(buf);
				while (len > 0) {
					baos.write(buf, 0, len);
					len = zipStream.read(buf);
				}

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(new ByteArrayInputStream(baos.toByteArray()));
				doc.getDocumentElement().normalize();

				XPath xPath =  XPathFactory.newInstance().newXPath();
				Node xsdSchemaNode = ((NodeList) xPath.compile("/schemaEntry/schema").evaluate(doc, XPathConstants.NODESET)).item(0);

				return xsdSchemaNode.getTextContent().getBytes();

			}
		}

		return xsdBytes;
	}

}

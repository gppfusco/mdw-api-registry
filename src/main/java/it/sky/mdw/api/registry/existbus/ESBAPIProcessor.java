package it.sky.mdw.api.registry.existbus;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import it.sky.mdw.api.Api;
import it.sky.mdw.api.ApiSpecification;
import it.sky.mdw.api.SoapApi;
import it.sky.mdw.api.SoapApiSpec;
import it.sky.mdw.api.XSDExternalRef;

public class ESBAPIProcessor implements Callable<Api<? extends ApiSpecification>>{

	private static Logger logger = Logger.getLogger(ESBAPIProcessor.class);
	private Element element;

	public ESBAPIProcessor(Element element) {
		this.element = element;
	}


	public Api<? extends ApiSpecification> call() throws Exception {
		String apiName = element.text();
		String endpoint = element.attr("abs:href").replaceAll(apiName, "").replaceAll("\\?WSDL", "");
		if(endpoint.replaceAll(" ", "").length()>0){
			SoapApi soapApi = new SoapApi();
			soapApi.setEndpoint(endpoint);
			soapApi.setName(apiName);
			soapApi.setPath(apiName);
			SoapApiSpec spec = new SoapApiSpec(endpoint + apiName + "?WSDL");
			soapApi.setApiSpecification(spec);
			logger.debug("Discovered api --> " + soapApi);
			parseApiWsdl(soapApi);
			return soapApi;
		}
		return null;
	}

	private static void parseApiWsdl(Api<? extends ApiSpecification> api) {
		ApiSpecification apiSpec = api.getApiSpecification();

		List<XSDExternalRef> xsdRefs = new ArrayList<XSDExternalRef>();
		logger.debug("Fetching api spec --> " + apiSpec.getApiSpecEndpoint());
		try {
			Document wsdl = Jsoup.parse(new URL(apiSpec.getApiSpecEndpoint()).openStream(), "UTF-8", "", Parser.xmlParser());
			apiSpec.setApiSpecContent(wsdl.toString().getBytes());
			Elements xsdImportTags = wsdl.getElementsByTag("xsd:import");
			if(xsdImportTags.size()>0){
				for(Element xsdImportTag: xsdImportTags){
					XSDExternalRef xsdRef = new XSDExternalRef(api.getEndpoint(), xsdImportTag.attr("schemaLocation"), xsdImportTag.attr("namespace"));
					Document xsd = Jsoup.parse(new URL(xsdRef.getXsdEndpoint()).openStream(), "UTF-8", "", Parser.xmlParser());
					xsdRef.setXsdSchema(xsd.toString().getBytes());
					xsdRefs.add(xsdRef);
				}
			}

			apiSpec.setXsdExternalRef(xsdRefs);
		} catch (Exception e) {
			logger.error("", e);
		}		
	}

}

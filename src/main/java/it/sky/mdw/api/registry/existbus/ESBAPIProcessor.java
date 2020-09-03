package it.sky.mdw.api.registry.existbus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import it.sky.mdw.api.security.SSLHelper;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.sky.mdw.api.Api;
import it.sky.mdw.api.ApiSpecification;
import it.sky.mdw.api.SoapApi;
import it.sky.mdw.api.SoapApiSpec;
import it.sky.mdw.api.XSDSchema;

public class ESBAPIProcessor implements Callable<Api<? extends ApiSpecification>> {

    private static Logger logger = Logger.getLogger(ESBAPIProcessor.class);
    private Element element;

    public ESBAPIProcessor(Element element) {
        this.element = element;
    }


    public Api<? extends ApiSpecification> call() throws Exception {
        String apiName = element.text();
        String endpoint = element.attr("abs:href").replaceAll(apiName, "").replaceAll("\\?WSDL", "");
        if (endpoint.replaceAll(" ", "").length() > 0) {
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

        List<XSDSchema> xsdRefs = new ArrayList<XSDSchema>();
        logger.debug("Fetching api spec --> " + apiSpec.getApiSpecEndpoint());
        try {
			/*Document wsdl = Jsoup.parse(new URL(apiSpec.getApiSpecEndpoint())
					.openStream(), "UTF-8", "", Parser.xmlParser());*/
            Document wsdl = Jsoup.connect(apiSpec.getApiSpecEndpoint())
                    .sslSocketFactory(SSLHelper.socketFactory())
                    .get();
            apiSpec.setApiSpecContent(wsdl.toString().getBytes());
            Elements xsdImportTags = wsdl.getElementsByTag("xsd:import");
            if (xsdImportTags.size() > 0) {
                for (Element xsdImportTag : xsdImportTags) {
                    String xsdPath = xsdImportTag.attr("schemaLocation");
                    XSDSchema xsdRef = new XSDSchema(xsdPath, xsdImportTag.attr("namespace"));
                    //Document xsd = Jsoup.parse(new URL(api.getEndpoint() + xsdPath).openStream(), "UTF-8", "", Parser.xmlParser());
                    Document xsd = Jsoup.connect(api.getEndpoint() + xsdPath)
                            .sslSocketFactory(SSLHelper.socketFactory())
                            .get();
                    xsdRef.setXsdSchema(xsd.toString().getBytes());
                    xsdRefs.add(xsdRef);
                }
            }

            apiSpec.setXsdSchemas(xsdRefs);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}

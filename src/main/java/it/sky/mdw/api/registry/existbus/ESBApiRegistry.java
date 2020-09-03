package it.sky.mdw.api.registry.existbus;

import it.sky.mdw.api.*;
import it.sky.mdw.api.security.PBE;
import it.sky.mdw.api.security.SSLHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ESBApiRegistry extends AbstractApiRegistry {

	private static final Logger logger = Logger.getLogger(ESBApiRegistry.class);
	private RegistryContext registriContext;

	private Authorization findAuthorization(Configuration configuration){
		if(configuration.containsKey(ESBConfigurationKeys.USERNAME) &&
				configuration.containsKey(ESBConfigurationKeys.PASSWORD)){
			return new Authorization(
					configuration.getProperty(ESBConfigurationKeys.USERNAME), 
					configuration.getProperty(ESBConfigurationKeys.PASSWORD));
		}
		else return null;
	}

	@Override
	protected Registry doInitializeRegistry(Configuration configuration) throws Exception {

		// Support for java 1.7
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		//System.setProperty("javax.net.debug", "all");

		final List<Api<? extends ApiSpecification>> apis = new ArrayList<>();
		String url = configuration.getProperty(ESBConfigurationKeys.API_URL);
		logger.info("Fetching..." + url);
		Document doc;
		Authorization auth = findAuthorization(configuration);
		logger.info("Discovering APIs.....");
		if(auth != null){
			String password = isEncryptionEnabled ?
					new String(PBE.getInstance().decrypt(auth.getPassword().getBytes())) : auth.getPassword();
			String login = auth.getUsername() + ":" + password;
			String base64login = new String(Base64.encodeBase64(login.getBytes()));
			doc = Jsoup.connect(url)
					.sslSocketFactory(SSLHelper.socketFactory())
					.header("Authorization", "Basic " + base64login).get();
		}else{
			doc = Jsoup.connect(url).get();
		}

		Elements tables = doc.body().select("table");
		Elements links = tables.get(7).select("a[href]");

		ExecutorService service = Executors.newFixedThreadPool(nThreads);


		Collection<ESBAPIProcessor> tasks = new ArrayList<>();

		for (Element link : links) {
			tasks.add(new ESBAPIProcessor(link));
		}

		try {
			List<Future<Api<? extends ApiSpecification>>> apisFuture = service.invokeAll(tasks);
			for(Future<Api<? extends ApiSpecification>> t: apisFuture){
				Api<? extends ApiSpecification> api;
				try {
					api = t.get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
					if(api!=null){
						IntegrationScenario intScenario = getIntegrationScenario(api);
						api.setIntegrationScenario(intScenario);
						apis.add(api);
					}
				} catch (Exception e) {
					logger.error("", e);
				} 
			}

		} catch (InterruptedException e) {
			logger.error("", e);
		}

		service.shutdown();
		try {
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.error("", e);
		}

		Registry repository = new Registry();
		repository.setApis(apis);

		logger.info("Discovering APIs..... Completed");
		return repository;
	}

	@Override
	public void onStoreRegistryCompleted(Environment environment, Configuration configuration) throws Exception {

	}

	public RegistryContext getRegistryContext() {
		if(registriContext == null)
			registriContext = new DefaultRegistryContext();
		return registriContext;
	}

	public IntegrationScenario getIntegrationScenario(Api<? extends ApiSpecification> api) {
		return IntegrationScenario.DATA_MANIPULATION;
	}

}

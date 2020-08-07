package it.sky.mdw.api.registry.existbus;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.sky.mdw.api.AbstractApiRegistry;
import it.sky.mdw.api.Api;
import it.sky.mdw.api.ApiSpecification;
import it.sky.mdw.api.Environment;
import it.sky.mdw.api.Registry;
import it.sky.mdw.api.Configuration;

public class ESBApiRegistry extends AbstractApiRegistry {

	private static Logger logger = Logger.getLogger(ESBApiRegistry.class);

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
	public Registry initializeRegistry(Configuration configuration) throws Exception {
		final List<Api<? extends ApiSpecification>> apis = new ArrayList<Api<? extends ApiSpecification>>();
		String url = configuration.getProperty(ESBConfigurationKeys.API_URL);
		logger.info("Fetching..." + url);
		Document doc;
		Authorization auth = findAuthorization(configuration);
		logger.info("Discovering APIs.....");
		if(auth != null){
			String login = auth.getUsername() + ":" + auth.getPassword();
			String base64login = new String(Base64.getEncoder().encode(login.getBytes()));
			doc = Jsoup.connect(url).header("Authorization", "Basic " + base64login).get();	
		}else{
			doc = Jsoup.connect(url).get();
		}

		Elements tables = doc.body().select("table");
		Elements links = tables.get(7).select("a[href]");

		ExecutorService service = Executors.newWorkStealingPool();


		Collection<ESBAPIProcessor> tasks = new ArrayList<ESBAPIProcessor>();

		for (Element link : links) {
			tasks.add(new ESBAPIProcessor(link));
		}

		try {
			List<Future<Api<? extends ApiSpecification>>> apisFuture = service.invokeAll(tasks);
			apisFuture.forEach(new Consumer<Future<Api<? extends ApiSpecification>>>() {
				public void accept(Future<Api<? extends ApiSpecification>> t) {
					Api<? extends ApiSpecification> api;
					try {
						api = t.get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
						if(api!=null)
							apis.add(api);
					} catch (Exception e) {
						logger.error("", e);
					} 
				}

			});
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

}

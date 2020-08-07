package it.sky.mdw.api;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class AbstractApiRegistry implements ApiRegistry{

	private static Logger logger = Logger.getLogger(AbstractApiRegistry.class);
	protected String dir_path_str, env_dir_str, repo_dir_str, wadl_dir_str, wsdl_dir_str,  xsd_dir_str;

	@Override
	public void storeFullRegistry(Environment environment, Configuration configuration) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

		logger.info("Read parameters from configuration...");
		dir_path_str = configuration.getProperty(ConfigurationKeys.BASE_DIR_PATH);
		env_dir_str = configuration.getProperty(ConfigurationKeys.ENV_DIR_NAME);
		repo_dir_str = configuration.getProperty(ConfigurationKeys.REPOSITORY_DIR_NAME);
		wadl_dir_str = configuration.getProperty(ConfigurationKeys.WADL_DIR_NAME);
		wsdl_dir_str = configuration.getProperty(ConfigurationKeys.WSDL_DIR_NAME);
		xsd_dir_str = configuration.getProperty(ConfigurationKeys.XSD_DIR_NAME);

		File env_dir = new File(dir_path_str + File.separator + env_dir_str);
		if(!env_dir.exists())
			env_dir.mkdir();

		File repo_dir = new File(env_dir.getAbsolutePath() + File.separator + repo_dir_str);
		if(!repo_dir.exists())
			repo_dir.mkdir();

		for(Api<? extends ApiSpecification> api: environment.getRegistry().getApis()){
			try {
				String apiName = api.getName();
				File api_dir = new File(repo_dir.getAbsolutePath() + File.separator + apiName);
				if(!api_dir.exists())
					api_dir.mkdir();

				mapper.writeValue(new File(api_dir.getAbsolutePath() + File.separator + apiName + ".json"), api);
				api.setLocalPath(env_dir_str + File.separator + repo_dir_str + File.separator + api.getName());

				ApiSpecification apiSpec = api.getApiSpecification();

				if(apiSpec != null){

					if(apiSpec instanceof RestApiSpec){
						writeApiSpec(apiSpec, api_dir, wadl_dir_str, apiName, xsd_dir_str, ".wadl");

						SoapApiSpec soapApiSpec = ((RestApiSpec) apiSpec).getSoapApiSpec();
						if(soapApiSpec != null)
							writeApiSpec(soapApiSpec, api_dir, wsdl_dir_str, apiName, xsd_dir_str, ".wsdl");

					}

					else if(apiSpec instanceof SoapApiSpec){
						writeApiSpec(apiSpec, api_dir, wsdl_dir_str, apiName, xsd_dir_str, ".wsdl");

					}
				}

			} catch (Exception e) {
				continue;
			}
		}

		try {
			logger.info("Executing onPersistRepositoryComplete...");
			onStoreRegistryCompleted(environment, configuration);
		} catch (Exception e) {
			logger.error("Error OnPersistRepositoryComplete: ", e);
		}

		logger.info("Writing repository metadata...");
		mapper.writeValue(new File(env_dir + File.separator + environment.getReferenceName() + ".json"), environment);
	}

	private void writeApiSpec(ApiSpecification apiSpec, File api_dir, String wsdl_dir_str, String apiName, String xsd_dir_str, String fileExtension) throws Exception {
		File apiSpec_dir = new File(api_dir.getAbsolutePath() + File.separator + wsdl_dir_str);
		if(!apiSpec_dir.exists())
			apiSpec_dir.mkdir();

		FileOutputStream fos = new FileOutputStream(new File(apiSpec_dir.getAbsolutePath() + File.separator + apiName + fileExtension));
		byte[] apiSpecContent = apiSpec.getApiSpecContent();
		if(apiSpecContent != null)
			fos.write(apiSpecContent);

		fos.flush();
		fos.close();
		List<XSDExternalRef> xsdRefs = apiSpec.getXsdExternalRef();
		if(xsdRefs.size() > 0){
			writeXsdExternalRefs(xsdRefs, api_dir, xsd_dir_str);
		}
	}

	private void writeXsdExternalRefs(List<XSDExternalRef> xsdRefs, File api_dir, String xsd_dir_str) {
		File xsd_dir = new File(api_dir.getAbsolutePath() + File.separator + xsd_dir_str);
		if(!xsd_dir.exists())
			xsd_dir.mkdir();

		for(XSDExternalRef xsdRef: xsdRefs){
			try {
				String basePath = xsdRef.getXsdBasePath();
				String[] splits = basePath.split("/");
				String xsdFileName = splits[splits.length-1];
				if(!xsdFileName.endsWith(".xsd"))
					xsdFileName = xsdFileName + ".xsd";
				FileOutputStream fos = new FileOutputStream(new File(xsd_dir.getAbsolutePath() + File.separator + xsdFileName));
				byte[] xsdSpecContent = xsdRef.getXsdSchema();
				if(xsdSpecContent != null)
					fos.write(xsdSpecContent);

				fos.flush();
				fos.close();
			} catch (Exception e) {
				logger.error("", e);
				continue;
			}
		}
	}

	public abstract void onStoreRegistryCompleted(Environment environment, Configuration configuration) throws Exception;

	@Override
	public void storeRegistryAPIs(Environment environment, Configuration configuration) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

		logger.info("Read parameters from configuration...");
		dir_path_str = configuration.getProperty(ConfigurationKeys.BASE_DIR_PATH);
		env_dir_str = configuration.getProperty(ConfigurationKeys.ENV_DIR_NAME);
		repo_dir_str = configuration.getProperty(ConfigurationKeys.REPOSITORY_DIR_NAME);

		logger.info("Writing repository metadata...");
		File env_dir = new File(dir_path_str + File.separator + env_dir_str);
		if(!env_dir.exists())
			env_dir.mkdir();

		environment.getRegistry().getApis().forEach(new Consumer<Api<? extends ApiSpecification>>() {
			@Override
			public void accept(Api<? extends ApiSpecification> api) {
				api.setLocalPath(env_dir_str + File.separator + repo_dir_str + File.separator + api.getName());
			}
		});

		mapper.writeValue(new File(env_dir + File.separator + environment.getReferenceName() + ".json"), environment);		
	}

}

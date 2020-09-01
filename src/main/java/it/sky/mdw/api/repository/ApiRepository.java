package it.sky.mdw.api.repository;

public interface ApiRepository {

	<C extends RepositoryConfiguration> void init(C repositoryConfiguration) throws Exception;

	void update() throws Exception;

}

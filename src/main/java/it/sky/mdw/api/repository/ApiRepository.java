package it.sky.mdw.api.repository;

public interface ApiRepository {

	void init(RepositoryConfiguration repositoryConfiguration) throws Exception;

	void update() throws Exception;

}

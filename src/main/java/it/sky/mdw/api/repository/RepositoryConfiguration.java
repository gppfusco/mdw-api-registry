package it.sky.mdw.api.repository;

public interface RepositoryConfiguration {

    String getDirectory();

    String getUri();

    String getUsername();

    String getPassword();

    boolean isPasswordEncrypted();

    String getBranch();
}

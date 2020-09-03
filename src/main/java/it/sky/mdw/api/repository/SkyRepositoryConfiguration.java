package it.sky.mdw.api.repository;

import java.util.ArrayList;
import java.util.List;

public class SkyRepositoryConfiguration extends AbstractRepositoryConfiguration {

    private List<String> environmentDirEntries = new ArrayList<>();

    public SkyRepositoryConfiguration() {
        super();
    }

    public List<String> getEnvironmentDirEntries() {
        return environmentDirEntries;
    }

}

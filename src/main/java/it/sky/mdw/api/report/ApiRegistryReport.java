package it.sky.mdw.api.report;

public interface ApiRegistryReport {

	<C extends ReportConfiguration> void report(C reportConfiguration) throws Exception;

}

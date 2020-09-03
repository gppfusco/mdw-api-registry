#!/bin/bash	

function usage_error() {
	echo "Usage: mdw-api-registry.cmd"
	echo "mdw_api_registry_jar_file"
	echo "osb_conf_file"
	echo "esb_conf_file"
	echo "report_conf_file"
	echo "repository_conf_file"
}

function run_mdw_api_registry() {
	mdw_api_registry_jar=$1
	osb_conf=$2
	esb_conf=$3
	report_conf=$4
	repository_conf=$5
	echo "Starting with the following configuration:"
	echo "mdw_api_registry_jar = $mdw_api_registry_jar"
	echo "osb conf = $osb_conf"
	echo "esb conf = $esb_conf"
	echo "report conf = $report_conf"
	echo "repository conf = $repository_conf"
	
	java -jar $mdw_api_registry_jar repository -c=$repository_conf -a=init &&
	java -jar $mdw_api_registry_jar registry -c=$osb_conf -r=OSBApiRegistry -l &&
	java -jar $mdw_api_registry_jar registry -c=$esb_conf -r=ESBApiRegistry -l &&
	java -jar $mdw_api_registry_jar report -c=$report_conf%  &&
	java -jar $mdw_api_registry_jar repository -c=$repository_conf -a=update
}

if [ $# -eq 5 ]
then
	run_mdw_api_registry $*
else
	usage_error
fi
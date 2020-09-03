#!/bin/bash
BASEDIR=$PWD/src/main/resources/lib/
mvn install:install-file -Dfile="${BASEDIR}"oracle.servicebus.kernel-wls.jar -DgroupId=oracle.servicebus -DartifactId=kernel-wls -Dversion=1.0 -Dpackaging=jar &&
mvn install:install-file -Dfile="${BASEDIR}"com.bea.core.management.jmx_4.0.0.0.jar -DgroupId=com.bea.core.management -DartifactId=jmx_4.0.0.0 -Dversion=1.0 -Dpackaging=jar &&
mvn install:install-file -Dfile="${BASEDIR}"oracle.servicebus.configfwk.jar -DgroupId=oracle.servicebus -DartifactId=configfwk -Dversion=1.0 -Dpackaging=jar &&
mvn install:install-file -Dfile="${BASEDIR}"oracle.servicebus.kernel-api.jar -DgroupId=oracle.servicebus -DartifactId=kernel-api -Dversion=1.0 -Dpackaging=jar &&
mvn install:install-file -Dfile="${BASEDIR}"wlfullclient.jar -DgroupId=wlfullclient -DartifactId=wlfullclient -Dversion=1.0 -Dpackaging=jar
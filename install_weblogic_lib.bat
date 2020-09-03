@echo off
SETLOCAL
SET dir=%cd%\src\main\resources\lib\
call mvn install:install-file -Dfile=%dir%oracle.servicebus.kernel-wls.jar -DgroupId=oracle.servicebus -DartifactId=kernel-wls -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=%dir%com.bea.core.management.jmx_4.0.0.0.jar -DgroupId=com.bea.core.management -DartifactId=jmx_4.0.0.0 -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=%dir%oracle.servicebus.configfwk.jar -DgroupId=oracle.servicebus -DartifactId=configfwk -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=%dir%oracle.servicebus.kernel-api.jar -DgroupId=oracle.servicebus -DartifactId=kernel-api -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=%dir%wlfullclient.jar -DgroupId=wlfullclient -DartifactId=wlfullclient -Dversion=1.0 -Dpackaging=jar
ENDLOCAL
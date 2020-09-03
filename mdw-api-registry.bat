@ECHO off 
SETLOCAL

SET mdw-api-registry-jar=%1
SET osb_conf=%2
SET esb_conf=%3
SET report_conf=%4
SET repository_conf=%5

ECHO Starting with the following configuration:
ECHO mdw-api-registry-jar = %mdw-api-registry-jar%
ECHO osb_conf = %osb_conf%
ECHO esb_conf = %esb_conf%
ECHO report_conf = %report_conf%
ECHO repository_conf = %repository_conf%

call java -jar %mdw-api-registry-jar% repository -c=%repository_conf% -a=init
call java -jar %mdw-api-registry-jar% registry -c=%osb_conf% -r=OSBApiRegistry -l
call java -jar %mdw-api-registry-jar% registry -c=%esb_conf% -r=ESBApiRegistry -l
call java -jar %mdw-api-registry-jar% report -c=%report_conf% 
call java -jar %mdw-api-registry-jar% repository -c=%repository_conf% -a=update

ENDLOCAL
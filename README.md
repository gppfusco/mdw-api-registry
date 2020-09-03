
# mdw-api-registry

## Requirements
- Java: >= 1.7.0_80
- Maven: 3.6.0

## Setup
Before to build the source code you must to run:

- On Windows: `install_weblogic_lib.bat`
- On Linux: `install_weblogic_lib.sh`

## Build
Run `mvn package`

## CLI
    Usage: <main class> [-hV] [COMMAND]
	MDW APIs registry commands.
	  -h, --help      Show this help message and exit.
	  -V, --version   Print version information and exit.
	Commands:
	  registry    Create a registry for all APIs from server.
	  report      Create index.md for explorer documentation.
	  pbe         Encrypt a given text.
	  repository  Initialize and update a repository for the Api registries.
	  info        Show useful information about commands.

## Commands:
### Registry

    Usage: <main class> reg [[-f] [-l]] [-hV] -c=<configurationFile>
                            -r=<registryType>
    Create a registry for all APIs from server.
      -c, --conf=<configurationFile>
                      Provide configuration file.
      -f, --full      Specify if the full registry should be stored on file system.
      -h, --help      Show this help message and exit.
      -l, --light     Specify if only APIs should be stored on file system.
      -r, --registry=<registryType>
                      Provide the registry type for APIs discovering.
      -V, --version   Print version information and exit.
Example of configuration file for `OSBApiRegistry`:
```json
{
	"env_dir_name": "osb_it",
	"port": "port",
	"username": "username",
	"env_host": "env_host",
	"base_dir_path": "C:\\path-to-api-explorer",
	"wsdl_dir_name": "wsdl",
	"env_name": "OSB_IT",
	"wadl_dir_name": "wadl",
	"repository_dir_name": "repo",
	"password": "password",
	"host": "host",
	"xsd_dir_name": "xsd"
}
```
Example of configuration file for `ESBApiRegistry`:
```json
{
	"env_dir_name": "esb_it",
	"url": "url",
	"username": "username",
	"env_host": "env_host",
	"base_dir_path": "C:\\path-to-api-explorer",
	"wsdl_dir_name": "wsdl",
	"env_name": "ESB_IT",
	"wadl_dir_name": "wadl",
	"repository_dir_name": "repo",
	"password": "password",
	"xsd_dir_name": "xsd"
}
```
> All paths are relative to `base_dir_path` field path.
### Report

	Usage: <main class> report [-hV] -c=<reportConfiguration>
	Create index.md for explorer documentation.
	  -c, --conf=<reportConfiguration>
					  Provide the report configuration file.
	  -h, --help      Show this help message and exit.
	  -V, --version   Print version information and exit.
	  
Example of report configuration file:
```json
{
	"explorerLocalBasePath": "C:\\path-to-api-explorer",
	"githubURLBaseDoc": "https://github.com/<username>/branch-name",
	"osbRegistryFile" : "osb_it\\OSB_IT.json",
	"esbRegistryFile": "esb_it\\ESB_IT.json"
}
```
> All paths are relative to `explorerLocalBasePath` field path.
### Repository

	Usage: <main class> repository [-hV] [-a=ACTION] -c=<configurationFile>
	Initialize and update a repository for the Api registries.
	  -a, --action=ACTION   <init> or <update>
	  -c, --conf=<configurationFile>
							Provide configuration file.
	  -h, --help            Show this help message and exit.
	  -V, --version         Print version information and exit.

Example of repository configuration file:
```json
{
	"directory": "C:\\path-to-api-explorer",
	"uri": "https://github.com/<username>/branch-name",
	"username": "username",
	"password": "password",
	"environmentDirEntries": [
		"osb_it\\OSB_IT.json",
		"esb_it\\ESB_IT.json"
	]
}
```
> All paths are relative to `directory` field path.
### Passwod-based-encryption

	Usage: <main class> pbe [-hV] <plainText>
	Encrypt a given text.
		  <plainText>
	  -h, --help        Show this help message and exit.
	  -V, --version     Print version information and exit. 
	  
### Info

	Usage: <main class> info [-hrV] [-c=FORMAT]
	Show useful information about commands.
	  -c, --conf=FORMAT   Provide an example of configuration file. The FORMAT
							parameter must be <json> or <xml> or <txt>
	  -h, --help          Show this help message and exit.
	  -r, --registry      Provide the available api registry types. For each
							registry type show an example of configuration.
	  -V, --version       Print version information and exit.


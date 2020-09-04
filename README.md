
# mdw-api-registry
Java tool to create a registry of APIs exposed on SKY integration platforms.

### Features
- OSB registry: set of APIs exposed on SKY Oracle Service Bus.
- ESB Registry: set of APIs exposed on SKY Exists Bus.
- APIs documentation
- GitHub repository management for APIs documentation.
- Password based encryption.

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

```shell script
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
```

### Commands:
#### Registry

```shell script
Usage: <main class> registry [[-f] [-l]] [-hV] -c=<configurationFile> -r=<registryType>
Create a registry for all APIs from server.
  -c, --conf=<configurationFile>
                  Provide configuration file.
  -f, --full      Specify if the full registry should be stored on filesystem.
  -h, --help      Show this help message and exit.
  -l, --light     Specify if only APIs should be stored on filesystem.
  -r, --registry=<registryType>
                  Provide the registry type for APIs discovering. Example:
                    <OSBApiRegistry> <ESBApiRegistry>.
  -V, --version   Print version information and exit.
```

> If option `-l` is enabled it will be created and stored only the registry, without any information about API resources (wadl, wsdl, xsd).

> If option `-f` is enabled it will be created and store both the registry and information about API resources. 

Example of configuration file for `OSBApiRegistry`:
```json
{
	"env_dir_name": "osb_it",
	"port": "port",
	"username": "username",
	"env_host": "https://servizicollaudo.sky.it/osbprj",
	"base_dir_path": "C:\\path-to-api-explorer",
	"wsdl_dir_name": "wsdl",
	"env_name": "OSB_IT",
	"wadl_dir_name": "wadl",
	"repository_dir_name": "repo",
	"password": "password",
	"host": "host",
	"xsd_dir_name": "xsd",
	"encryption_enabled": false,
	"nThreads": 16
}
```

Example of configuration file for `ESBApiRegistry`:
```json
{
	"env_dir_name": "esb_it",
	"url": "https://wssvil.sky.it/wsexistbus/web?cmd=managementwsdl",
	"username": "username",
	"env_host": "https://wssvil.sky.it/wsexistbus",
	"base_dir_path": "C:\\path-to-api-explorer",
	"wsdl_dir_name": "wsdl",
	"env_name": "ESB_IT",
	"wadl_dir_name": "wadl",
	"repository_dir_name": "repo",
	"password": "password",
	"xsd_dir_name": "xsd",
	"encryption_enabled": false,
	"nThreads": 16
}

```
> All paths are relative to `base_dir_path` field path.

>`encryption_enabled`: set password-based-encryption for `password` field. Default is false.
>The password can be encrypted by running the command `pbe`.

>`nThreads`: set number of concurrent threads. Default is 16.

For the documentation about the registry structure see [Registry structure](RegistryStructure.md).

#### Report

```shell script
Usage: <main class> report [-hV] -c=<reportConfiguration>
Create README.md for explorer documentation.
  -c, --conf=<reportConfiguration>
                  Provide the report configuration file.
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
```

Example of report configuration file:
```json
{
	"explorerLocalBasePath": "C:\\path-to-api-explorer",
	"githubURLBaseDoc": "https://github.com/<username>/<repository-name>",
	"osbRegistryFile" : "osb_it\\OSB_IT.json",
	"esbRegistryFile": "esb_it\\ESB_IT.json",
	"branch": "branch-name"
}

```
> All paths are relative to `explorerLocalBasePath` field path.

#### Repository

```shell script
Usage: <main class> repository [-hV] [-a=ACTION] -c=<configurationFile>
Initialize and update a repository for the Api registries.
  -a, --action=ACTION   <init> or <update>
  -c, --conf=<configurationFile>
                        Provide configuration file.
  -h, --help            Show this help message and exit.
  -V, --version         Print version information and exit.
```

Example of repository configuration file:
```json
{
	"directory": "C:\\path-to-api-explorer",
	"uri": "https://github.com/<username>/<repository-name>",
	"username": "username",
	"password": "password",
	"environmentDirEntries": [
		"osb_it\\OSB_IT.json",
		"esb_it\\ESB_IT.json"
	],
	"branch": "",
	"passwordEncrypted": false
}

```
> All paths are relative to `directory` field path.
 
>`passwordEncrypted`: set password-based-encryption for user password. Default is false.
>The password can be encrypted by running the command `pbe`.

#### Passwod-based-encryption

```shell script
Usage: <main class> pbe [-hV] <plainText>
Encrypt a given text.
      <plainText>
  -h, --help        Show this help message and exit.
  -V, --version     Print version information and exit. 
```
	  
#### Info

```shell script
Usage: <main class> info [-hrV] [-c=FORMAT]
Show useful information about commands.
  -c, --conf=FORMAT   Provide an example of configuration file. The FORMAT
                        parameter must be <json> or <xml> or <txt>
  -h, --help          Show this help message and exit.
  -r, --registry      Provide the available api registry types. For each
                        registry type show an example of configuration.
  -V, --version       Print version information and exit.
```

## Useful scripts

The full process to create the registries, store them on local filesystem and update the remote repository for documentation, is achieved by running `registry`, `report` and `repository` CLI commands.
To run all above CLI commands it can be executed the script below
- On Windows: `mdw-api-registry.bat`
- On Linux: `mdw-api-registry.sh`

The script accept, as input, five arguments:
- mdw_api_registry_jar_file: path to the mdw-api-registry jar
- osb_conf_file: path to configuration file for OSB registry
- esb_conf_file: path to configuration file for ESB registry
- report_conf_file: path to `report` command configuration file
- repository_conf_file: path to `repository` command configuration file
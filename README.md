# mdw-api-registry

## Requirements
- Java: >= 1.8
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
      reg   Create a registry for all APIs from server.
      ui    Create index.md for github pages.
      show  Show useful information about commands.enter code here

## Commands:
### Reg

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

### UI

    Usage: <main class> ui [-hV] -d=<githubURLBaseDoc> -e=<esbRegistryJsonFile>
                           -o=<osbRegistryJsonFile> -p=<githubPagesLocalRepoHome>
    Create index.md for github pages.
      -d, --git_doc=<githubURLBaseDoc>
                      Provide the base URL for github documentation repository.
      -e, --esb=<esbRegistryJsonFile>
                      Provide the JSON registry file for ESB.
      -h, --help      Show this help message and exit.
      -o, --osb=<osbRegistryJsonFile>
                      Provide the JSON registry file for OSB.
      -p, --explorer_dir=<explorerLocalBasePath>
                      Provide the base direcotory to save do explorer documentation.
      -V, --version   Print version information and exit.

### Show

    Usage: <main class> show [-hrV] [-c=FORMAT]
    Show useful information about commands.
      -c, --conf=FORMAT   Provide an example of configuration file. The FORMAT
                            parameter must be <json> or <xml> or <txt>
      -h, --help          Show this help message and exit.
      -r, --registry      Provide the available api registry types. For each
                            registry type show an example of configuration.
      -V, --version       Print version information and exit.


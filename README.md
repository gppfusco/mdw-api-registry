# mdw-api-registry

   # CLI
    Usage: <main class> [-hV] [COMMAND]
    MDW APIs registry commands.
      -h, --help      Show this help message and exit.
      -V, --version   Print version information and exit.
    Commands:
      run   Create a registry for all APIs from server.
      ui    Create index.md for github pages.
      show  Show useful information about commands.enter code here

## Commands:
### Run

    Usage: <main class> run [[-f] [-l]] [-hV] -c=<configurationFile>
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
      -p, --git_pages=<githubPagesLocalRepoHome>
                      Provide the base direcotory of github pages repository.
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


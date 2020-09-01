package it.sky.mdw.api.application.cli.command;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
		description={"MDW APIs registry commands."},
		mixinStandardHelpOptions=true,
		subcommands={
				RegistryCommand.class,
				RegistryReportCommand.class,
				PBECommand.class,
				RepositoryCommand.class,
				ShowInfoCommand.class
		})
public class MdwApiRegistryCommand implements Runnable {

	@Override
	public void run() {
		CommandLine.usage(MdwApiRegistryCommand.class, System.out);
	}
}

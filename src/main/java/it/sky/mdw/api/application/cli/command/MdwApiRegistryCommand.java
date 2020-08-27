package it.sky.mdw.api.application.cli.command;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
		description={"MDW APIs registry commands."},
		mixinStandardHelpOptions=true,
		subcommands={
				RegistryCommand.class,
				RegistryUIAlignmentCommand.class,
				PBECommand.class,
				RunCommandHelper.class
		})
public class MdwApiRegistryCommand implements Runnable {

	@Override
	public void run() {
		CommandLine.usage(MdwApiRegistryCommand.class, System.out);
	}
}

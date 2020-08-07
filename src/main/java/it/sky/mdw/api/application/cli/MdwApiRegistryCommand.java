package it.sky.mdw.api.application.cli;

import picocli.CommandLine.Command;

@Command(
		description={"MDW APIs registry commands."},
		mixinStandardHelpOptions=true,
		subcommands={
				RegistryCommand.class,
				RegistryUIAlignmentCommand.class,
				RunCommandHelper.class
		})
public class MdwApiRegistryCommand implements Runnable {

	@Override
	public void run() {
	}
}

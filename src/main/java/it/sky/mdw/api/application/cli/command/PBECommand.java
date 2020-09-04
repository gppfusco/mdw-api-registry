package it.sky.mdw.api.application.cli.command;

import org.apache.log4j.Logger;

import it.sky.mdw.api.security.PBE;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
		description={"Encrypt a given text."},
		mixinStandardHelpOptions=true,
		name="pbe",
		version = "1.0")
public class PBECommand implements Runnable{

	private static final Logger logger = Logger.getLogger(PBECommand.class);

	@Parameters(type=String.class, arity="1")
	private String plainText;

	public void run() {
		try {
			System.out.println(new String(PBE.getInstance().encrypt(plainText.getBytes())));
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		}
	}

}

package de.ollie.dbcomp.cli.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import de.ollie.dbcomp.cli.CLICommand;
import de.ollie.dbcomp.cli.CommonOptions;

/**
 * A simple reference implementation of the CLI command interface.
 *
 * @author ollie (27.11.2020)
 *
 */
@Parameters(commandDescription = "Just says 'hello'.")
public class SayHelloCLICommand implements CLICommand {

	private static final Logger LOG = LogManager.getLogger(SayHelloCLICommand.class);

	@Parameter(names = { "-n", "--name" }, description = "Set your name here, if you want addressed greetings ;).")
	private String name;

	@Override
	public int run(CommonOptions commonOptions) {
		if (commonOptions.isVerbose()) {
			LOG.info("Hello{}!", (name != null ? ", " + name : ""));
		}
		return 42;
	}

}
package de.ollie.dbcomp.cli.commands;

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

	@Parameter(names = { "-n", "--name" }, description = "Set your name here, if you want addressed greetings ;).")
	private String name;

	@Override
	public String getCommandStr() {
		return "greet";
	}

	@Override
	public int run(CommonOptions commonOptions) {
		if (commonOptions.isVerbose()) {
			System.out.println(String.format("Hello%s!", (name != null ? ", " + name : "")));
		} else {
			System.out.println("Set '-v' to have an output!");
		}
		return 42;
	}

}
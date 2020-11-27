package de.ollie.dbcomp.cli;

/**
 * An interface for CLI commands.
 *
 * @author ollie (27.11.2020)
 *
 */
public interface CLICommand {

	/**
	 * Returns the command string.
	 * 
	 * @return The command string.
	 */
	String getCommandStr();

	/**
	 * Runs the command.
	 * 
	 * @param commonOptions The common options passed while command call.
	 * @return A return code. Usually '0' if command has ended without any error.
	 */
	int run(CommonOptions commonOptions);

}
package de.ollie.dbcomp.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import lombok.Getter;

/**
 * The common options of the CLI application.
 *
 * @author ollie (27.11.2020)
 *
 */
@Getter
@Parameters(commandDescription = "Common options.")
public class CommonOptions {

	@Parameter(names = "--help", description = "Prints this help info to the console.")
	private boolean help = false;

	@Parameter(names = { "-v", "--verbose" }, description = "Adds more output while program is running.")
	private boolean verbose = false;

}
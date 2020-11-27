package de.ollie.dbcomp.cli;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.JCommander.Builder;

import de.ollie.dbcomp.cli.commands.SayHelloCLICommand;

/**
 * The CLI for the data model comparator.
 *
 * @author ollie (27.11.2020)
 *
 */
public class CLI {

	private static final Logger LOG = LogManager.getLogger(CLI.class);

	private Map<String, CLICommand> commands = new HashMap<>();

	public static void main(String... args) {
		System.exit(new CLI().start(args));
	}

	public int start(String... args) {
		CommonOptions commonOptions = new CommonOptions();
		Builder builder = JCommander.newBuilder() //
				.addObject(commonOptions);
		addCommands(builder);
		JCommander jc = builder.build();
		try {
			jc.parse(args);
		} catch (Exception e) {
			LOG.error("error while parsing command line: " + e.getMessage());
			return 1;
		}
		if (commonOptions.isHelp()) {
			jc.usage();
		} else {
			return runCommand(this.commands.get(jc.getParsedCommand()), commonOptions);
		}
		return 0;
	}

	private void addCommands(Builder builder) {
		addCommand(builder, "greet", new SayHelloCLICommand());
	}

	private void addCommand(Builder builder, String commandStr, CLICommand command) {
		builder.addCommand(commandStr, command);
		commands.put(commandStr, command);
	}

	private int runCommand(CLICommand selectedCommand, CommonOptions mainParameters) {
		int returnCode = 0;
		if (selectedCommand != null) {
			returnCode = selectedCommand.run(mainParameters);
		}
		LOG.info("program finished with code: " + returnCode);
		return returnCode;
	}

}
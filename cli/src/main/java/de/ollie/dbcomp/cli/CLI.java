package de.ollie.dbcomp.cli;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.JCommander.Builder;

import de.ollie.dbcomp.cli.commands.CompareCLICommand;
import de.ollie.dbcomp.cli.commands.SayHelloCLICommand;
import de.ollie.dbcomp.cli.commands.ShowCLICommand;

/**
 * The CLI for the data model comparator.
 *
 * @author ollie (27.11.2020)
 *
 */
public class CLI {

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
			System.out.println("ERROR while parsing command line: " + e.getMessage());
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
		addCommand(builder, new CompareCLICommand());
		addCommand(builder, new SayHelloCLICommand());
		addCommand(builder, new ShowCLICommand());
	}

	private void addCommand(Builder builder, CLICommand command) {
		builder.addCommand(command.getCommandStr(), command);
		commands.put(command.getCommandStr(), command);
	}

	private int runCommand(CLICommand selectedCommand, CommonOptions mainParameters) {
		int returnCode = 0;
		if (selectedCommand != null) {
			returnCode = selectedCommand.run(mainParameters);
		}
		return returnCode;
	}

}
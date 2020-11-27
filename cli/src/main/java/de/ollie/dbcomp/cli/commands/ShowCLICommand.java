package de.ollie.dbcomp.cli.commands;

import java.util.Arrays;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import de.ollie.dbcomp.cli.CLICommand;
import de.ollie.dbcomp.cli.CommonOptions;
import de.ollie.dbcomp.cli.ModelFileType;

/**
 * A command for detail information.
 *
 * @author ollie (27.11.2020)
 *
 */
@Parameters(commandDescription = "shows detail information different contexts.")
public class ShowCLICommand implements CLICommand {

	@Parameter(names = { "modelFileTypes" }, description = "Shows the valid model file types.")
	private boolean modelFileTypes;

	@Override
	public String getCommandStr() {
		return "show";
	}

	@Override
	public int run(CommonOptions commonOptions) {
		if (modelFileTypes) {
			Arrays.asList(ModelFileType.values()) //
					.forEach(System.out::println);
			;
		}
		return 0;
	}

}
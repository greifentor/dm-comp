package de.ollie.dbcomp.cli.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import de.ollie.dbcomp.cli.CLICommand;
import de.ollie.dbcomp.cli.CommonOptions;
import de.ollie.dbcomp.cli.ModelFileType;
import de.ollie.dbcomp.comparator.DataModelComparator;
import de.ollie.dbcomp.comparator.model.ComparisonResultCRO;
import de.ollie.dbcomp.javacodejpa.reader.JavaCodeFileModelReader;
import de.ollie.dbcomp.javacodejpa.reader.converter.FieldDeclarationToColumnCMOConverter;
import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.liquibase.writer.ChangeActionToDatabaseChangeLogConverter;
import de.ollie.dbcomp.model.ReaderResult;
import de.ollie.dbcomp.report.ImportReportMessageLevel;
import de.ollie.dbcomp.util.TypeConverter;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.serializer.core.xml.XMLChangeLogSerializer;

/**
 * A command to compare to data models and get a report of the necessary changes.
 *
 * @author ollie (27.11.2020)
 *
 */
@Parameters(commandDescription = "Compares two data models and creates a report for the necessary changes.")
public class CompareCLICommand implements CLICommand {

	private static final Logger LOG = LogManager.getLogger(CompareCLICommand.class);

	@Parameter(names = { "-s",
			"--source" }, required = true, description = "The name of the file with the source model.")
	private String sourceModelFileName;

	@Parameter(names = { "-st",
			"--sourceType" }, description = "The type of the source model (call 'show' command for details).")
	private ModelFileType sourceModelFileType = ModelFileType.LIQUIBASE;

	@Parameter(names = { "-t",
			"--target" }, required = true, description = "The name of the file with the target model.")
	private String targetModelFileName;

	@Parameter(names = { "-tt",
			"--targetType" }, description = "The type of the target model (call 'show' command for details).")
	private ModelFileType targetModelFileType = ModelFileType.LIQUIBASE;

	@Override
	public String getCommandStr() {
		return "compare";
	}

	@Override
	public int run(CommonOptions commonOptions) {
		ReaderResult sourceModelResult = null;
		ReaderResult targetModelResult = null;
		try {
			System.out.print(String.format("reading %s (%s) ... ", sourceModelFileName, sourceModelFileType));
			sourceModelResult = getDataModel(sourceModelFileName, sourceModelFileType);
			System.out.println("done");
		} catch (Exception e) {
			System.out.println(
					"\nERROR while reading source model from: " + sourceModelFileName + " -> " + e.getMessage());
			return 1;
		}
		if (!sourceModelResult.getImportReport().getMessagesForLevel(ImportReportMessageLevel.ERROR).isEmpty()) {
			System.out.println("\nERRORS found in source file:");
			sourceModelResult.getImportReport().getMessagesForLevel(ImportReportMessageLevel.ERROR) //
					.forEach(message -> System.out
							.println(String.format("%5s - %s", message.getLevel(), message.getMessage())));
			return 2;
		}
		verbose(sourceModelResult, commonOptions);
		try {
			System.out.print(String.format("reading %s (%s) ... ", targetModelFileName, targetModelFileType));
			targetModelResult = getDataModel(targetModelFileName, targetModelFileType);
			System.out.println("done");
		} catch (Exception e) {
			System.out
					.println("ERROR while reading target model from: " + targetModelFileName + " -> " + e.getMessage());
			return 1;
		}
		if (!targetModelResult.getImportReport().getMessagesForLevel(ImportReportMessageLevel.ERROR).isEmpty()) {
			System.out.println("ERRORS found in target file:");
			targetModelResult.getImportReport().getMessagesForLevel(ImportReportMessageLevel.ERROR) //
					.forEach(message -> System.out
							.println(String.format("%5s - %s", message.getLevel(), message.getMessage())));
			return 2;
		}
		verbose(targetModelResult, commonOptions);
		System.out.print("comparing models ... ");
		ComparisonResultCRO comparisonResult = new DataModelComparator().compare(sourceModelResult.getDataModel(),
				targetModelResult.getDataModel());
		System.out.println("done\n");
		if (!comparisonResult.getChangeActions().isEmpty()) {
			comparisonResult.getChangeActions() //
					.forEach(System.out::println);
		}
		if (!comparisonResult.getReport().getMessages().isEmpty()) {
			System.out.println("\nREPORT");
			comparisonResult.getReport().getMessages() //
					.forEach(System.out::println);
		}
		exportDatabaseChangeLog(
				new ChangeActionToDatabaseChangeLogConverter().convert(comparisonResult.getChangeActions()));
		return 0;
	}

	private ReaderResult getDataModel(String modelFileName, ModelFileType modelFileType) throws Exception {
		String basePath = getBasePath(modelFileName);
		String fileName = getFileName(modelFileName);
		ReaderResult result = null;
		if (modelFileType == ModelFileType.JAVA) {
			result = new JavaCodeFileModelReader(new FieldDeclarationToColumnCMOConverter()).read(modelFileName);
		} else {
			result = new LiquibaseFileModelReader(new TypeConverter(), new File(basePath), new File(fileName)).read();
		}
		return result;
	}

	private String getBasePath(String modelFileName) {
		modelFileName = modelFileName.replace('\\', '/');
		if (modelFileName.indexOf('/') < 0) {
			return ".";
		}
		modelFileName = StringUtils.reverse(modelFileName);
		return StringUtils.reverse(modelFileName.substring(modelFileName.indexOf('/')));
	}

	private String getFileName(String modelFileName) {
		modelFileName = modelFileName.replace('\\', '/');
		if (modelFileName.indexOf('/') < 0) {
			return modelFileName;
		}
		modelFileName = StringUtils.reverse(modelFileName);
		return StringUtils.reverse(modelFileName.substring(0, modelFileName.indexOf('/')));
	}

	private void exportDatabaseChangeLog(DatabaseChangeLog databaseChangeLog) {
		PrintStream printStreamFile = null;
		try {
			printStreamFile = new PrintStream("change-log.xml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		XMLChangeLogSerializer changeLogSerializer = new XMLChangeLogSerializer();
		try {
			changeLogSerializer.write(databaseChangeLog.getChangeSets(), printStreamFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void verbose(ReaderResult readerResult, CommonOptions commonOptions) {
		if (commonOptions.isVerbose()) {
			System.out.println(readerResult.getDataModel());
		}
	}

}
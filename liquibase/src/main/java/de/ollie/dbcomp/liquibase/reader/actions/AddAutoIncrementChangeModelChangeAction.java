package de.ollie.dbcomp.liquibase.reader.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.report.ImportReport;
import de.ollie.dbcomp.report.ImportReportMessage;
import de.ollie.dbcomp.report.ImportReportMessageLevel;
import liquibase.change.Change;
import liquibase.change.core.AddAutoIncrementChange;

/**
 * An implementation of the ModelChangeAction interface for add auto increment changes.
 *
 * @author ollie (26.11.2020)
 */
public class AddAutoIncrementChangeModelChangeAction implements ModelChangeAction {

	private static final Logger LOG = LogManager.getLogger(AddAutoIncrementChangeModelChangeAction.class);

	@Override
	public void processOnDataModel(Change change, DataModelCMO dataModel, ImportReport importReport) {
		AddAutoIncrementChange addAutoIncrementChange = (AddAutoIncrementChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, addAutoIncrementChange.getSchemaName());
		LiquibaseFileModelReader.getTable(schema, addAutoIncrementChange.getTableName(), importReport) //
				.ifPresent(table -> {
					table.getColumnByName(addAutoIncrementChange.getColumnName()) //
							.ifPresentOrElse( //
									column -> {
										column.setAutoIncrement(true);
										LOG.info("added auto increment for column '{}' in table: {}", column.getName(),
												table.getName());
										importReport.addMessages( //
												new ImportReportMessage() //
														.setMessage(String.format(
																"added auto increment for column '%s' in table: %s",
																column.getName(), table.getName())) //
										);
									}, //
									() -> {
										LOG.error("column '{}' not found in table: {}",
												addAutoIncrementChange.getColumnName(), table.getName());
										importReport.addMessages( //
												new ImportReportMessage() //
														.setLevel(ImportReportMessageLevel.ERROR) //
														.setMessage(String.format("column '%s' not found in table: %s",
																addAutoIncrementChange.getColumnName(),
																table.getName()))); //
									});
				}) //
		;
	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == AddAutoIncrementChange.class;
	}

}
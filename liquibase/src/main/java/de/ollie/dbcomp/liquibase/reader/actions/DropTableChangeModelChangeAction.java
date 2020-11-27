package de.ollie.dbcomp.liquibase.reader.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.report.ImportReport;
import de.ollie.dbcomp.report.ImportReportMessage;
import liquibase.change.Change;
import liquibase.change.core.DropTableChange;

/**
 * An implementation of the ModelChangeAction interface for drop column changes.
 *
 * @author ollie (23.11.2020)
 */
public class DropTableChangeModelChangeAction implements ModelChangeAction {

	private static final Logger LOG = LogManager.getLogger(DropTableChangeModelChangeAction.class);

	@Override
	public void processOnDataModel(Change change, DataModelCMO dataModel, ImportReport importReport) {
		DropTableChange dropTableChange = (DropTableChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, dropTableChange.getSchemaName());
		LiquibaseFileModelReader.getTable(schema, dropTableChange.getTableName(), importReport) //
				.ifPresent(table -> {
					schema.removeTable(table.getName());
					importReport.addMessages( //
							new ImportReportMessage() //
									.setMessage(String.format("dropped table '%s' from schema: %s",
											dropTableChange.getTableName(), schema.getName())) //
					);
				}) //
		;

	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == DropTableChange.class;
	}

}

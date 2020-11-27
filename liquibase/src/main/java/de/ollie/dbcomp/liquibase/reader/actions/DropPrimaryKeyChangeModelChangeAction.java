package de.ollie.dbcomp.liquibase.reader.actions;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.report.ImportReport;
import de.ollie.dbcomp.report.ImportReportMessage;
import liquibase.change.Change;
import liquibase.change.core.DropPrimaryKeyChange;

/**
 * An implementation of the ModelChangeAction interface for drop column changes.
 *
 * @author ollie (26.11.2020)
 */
public class DropPrimaryKeyChangeModelChangeAction implements ModelChangeAction {

	private static final Logger LOG = LogManager.getLogger(DropPrimaryKeyChangeModelChangeAction.class);

	@Override
	public void processOnDataModel(Change change, DataModelCMO dataModel, ImportReport importReport) {
		DropPrimaryKeyChange dropPrimaryKeyChange = (DropPrimaryKeyChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, dropPrimaryKeyChange.getSchemaName());
		LiquibaseFileModelReader.getTable(schema, dropPrimaryKeyChange.getTableName(), importReport) //
				.ifPresent(table -> {
					table.setPkMembers(new HashMap<>());
					importReport.addMessages( //
							new ImportReportMessage() //
									.setMessage(String.format("dropped primary key from table: %s", table.getName())) //
					);
				}) //
		;
	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == DropPrimaryKeyChange.class;
	}

}

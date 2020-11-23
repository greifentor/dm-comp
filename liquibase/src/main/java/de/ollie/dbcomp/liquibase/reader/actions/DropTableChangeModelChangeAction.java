package de.ollie.dbcomp.liquibase.reader.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.model.DatamodelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
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
	public void processOnDataModel(Change change, DatamodelCMO dataModel) {
		DropTableChange dropTableChange = (DropTableChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, dropTableChange.getSchemaName());
		LiquibaseFileModelReader.getTable(schema, dropTableChange.getTableName()) //
				.ifPresent(table -> schema.removeTable(table.getName())) //
		;
	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == DropTableChange.class;
	}

}

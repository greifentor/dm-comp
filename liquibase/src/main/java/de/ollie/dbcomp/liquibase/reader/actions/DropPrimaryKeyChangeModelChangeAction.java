package de.ollie.dbcomp.liquibase.reader.actions;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.model.DatamodelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
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
	public void processOnDataModel(Change change, DatamodelCMO dataModel) {
		DropPrimaryKeyChange dropPrimaryKeyChange = (DropPrimaryKeyChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, dropPrimaryKeyChange.getSchemaName());
		LiquibaseFileModelReader.getTable(schema, dropPrimaryKeyChange.getTableName()) //
				.ifPresent(table -> table.setPkMembers(new HashMap<>())) //
		;
	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == DropPrimaryKeyChange.class;
	}

}

package de.ollie.dbcomp.liquibase.reader.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.model.DatamodelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import liquibase.change.Change;
import liquibase.change.core.DropColumnChange;

/**
 * An implementation of the ModelChangeAction interface for drop column changes.
 *
 * @author ollie (23.11.2020)
 */
public class DropColumnChangeModelChangeAction implements ModelChangeAction {

	private static final Logger LOG = LogManager.getLogger(DropColumnChangeModelChangeAction.class);

	@Override
	public void processOnDataModel(Change change, DatamodelCMO dataModel) {
		DropColumnChange dropColumnChange = (DropColumnChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, dropColumnChange.getSchemaName());
		LiquibaseFileModelReader.getTable(schema, dropColumnChange.getTableName()) //
				.ifPresentOrElse( //
						table -> {
							if (dropColumnChange.getColumnName() != null) {
								table.removeColumn(dropColumnChange.getColumnName());
								LOG.info("dropped column '{}' from table: {}", dropColumnChange.getColumnName(),
										dropColumnChange.getTableName());
							}
							if (!dropColumnChange.getColumns().isEmpty()) {
								LOG.warn("Multiple column dropping is not supported");
							}
						}, //
						() -> LOG.warn("table not found: {}", dropColumnChange.getTableName()) //
				) //
		;
	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == DropColumnChange.class;
	}

}

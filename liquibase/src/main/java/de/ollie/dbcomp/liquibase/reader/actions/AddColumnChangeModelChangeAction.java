package de.ollie.dbcomp.liquibase.reader.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.model.DatamodelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import liquibase.change.Change;
import liquibase.change.ColumnConfig;
import liquibase.change.core.AddColumnChange;

/**
 * An implementation of the ModelChangeAction interface for add column changes.
 *
 * @author ollie (23.11.2020)
 */
public class AddColumnChangeModelChangeAction implements ModelChangeAction {

	private static final Logger LOG = LogManager.getLogger(AddColumnChangeModelChangeAction.class);

	@Override
	public void processOnDataModel(Change change, DatamodelCMO dataModel) {
		AddColumnChange addColumnChange = (AddColumnChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, addColumnChange.getSchemaName());
		LiquibaseFileModelReader.getTable(schema, addColumnChange.getTableName()) //
				.ifPresentOrElse(table -> {
					for (ColumnConfig columnConfig : addColumnChange.getColumns()) {
						table.addColumns(LiquibaseFileModelReader.getColumn(table, columnConfig));
						LOG.info("added column '{}' to table: {}", columnConfig.getName(), table.getName());
					}
				}, //
						() -> LOG.warn("table not found: {}", addColumnChange.getTableName()) //
				) //
		;
	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == AddColumnChange.class;
	}

}
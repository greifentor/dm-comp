package de.ollie.dbcomp.liquibase.reader.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.model.DatamodelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import liquibase.change.Change;
import liquibase.change.ColumnConfig;
import liquibase.change.core.CreateTableChange;

/**
 * An implementation of the ModelChangeAction interface for create table changes.
 *
 * @author ollie (23.11.2020)
 */
public class CreateTableChangeModelChangeAction implements ModelChangeAction {

	private static final Logger LOG = LogManager.getLogger(CreateTableChangeModelChangeAction.class);

	@Override
	public void processOnDataModel(Change change, DatamodelCMO dataModel) {
		CreateTableChange createTableChange = (CreateTableChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, createTableChange.getSchemaName());
		LiquibaseFileModelReader.createOrGetTable(schema, createTableChange.getTableName()) //
				.ifPresentOrElse(table -> {
					for (ColumnConfig columnConfig : createTableChange.getColumns()) {
						table.addColumns(LiquibaseFileModelReader.getColumn(table, columnConfig));
						LOG.info("added column '{}' to table: {}", columnConfig.getName(), table.getName());
					}
				}, //
						() -> LOG.warn("table not found: {}", createTableChange.getTableName()) //
				) //
		;
	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == CreateTableChange.class;
	}

}
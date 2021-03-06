package de.ollie.dbcomp.liquibase.reader.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.report.ImportReport;
import de.ollie.dbcomp.report.ImportReportMessage;
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
	public void processOnDataModel(Change change, DataModelCMO dataModel, ImportReport importReport) {
		AddColumnChange addColumnChange = (AddColumnChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, addColumnChange.getSchemaName());
		LiquibaseFileModelReader.getTable(schema, addColumnChange.getTableName(), importReport) //
				.ifPresent(table -> {
					for (ColumnConfig columnConfig : addColumnChange.getColumns()) {
						table.addColumns(LiquibaseFileModelReader.getColumn(table, columnConfig, importReport));
						LOG.info("added column '{}' to table: {}", columnConfig.getName(), table.getName());
						importReport.addMessages( //
								new ImportReportMessage() //
										.setMessage(String.format("added column '%s' to table: %s",
												columnConfig.getName(), table.getName())) //
						);
					}
				}) //
		;
	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == AddColumnChange.class;
	}

}
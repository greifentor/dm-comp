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
import liquibase.change.core.DropColumnChange;

/**
 * An implementation of the ModelChangeAction interface for drop column changes.
 *
 * @author ollie (23.11.2020)
 */
public class DropColumnChangeModelChangeAction implements ModelChangeAction {

	private static final Logger LOG = LogManager.getLogger(DropColumnChangeModelChangeAction.class);

	@Override
	public void processOnDataModel(Change change, DataModelCMO dataModel, ImportReport importReport) {
		DropColumnChange dropColumnChange = (DropColumnChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, dropColumnChange.getSchemaName());
		LiquibaseFileModelReader.getTable(schema, dropColumnChange.getTableName(), importReport) //
				.ifPresent(table -> {
					if (dropColumnChange.getColumnName() != null) {
						table.removeColumn(dropColumnChange.getColumnName());
						LOG.info("dropped column '{}' from table: {}", dropColumnChange.getColumnName(),
								dropColumnChange.getTableName());
						importReport.addMessages( //
								new ImportReportMessage() //
										.setMessage(String.format("dropped column '%s' from table: %s",
												dropColumnChange.getColumnName(), table.getName())) //
						);
					}
					if (!dropColumnChange.getColumns().isEmpty()) {
						LOG.warn("Multiple column dropping is not supported");
						importReport.addMessages( //
								new ImportReportMessage() //
										.setLevel(ImportReportMessageLevel.WARN) //
										.setMessage("Multiple column dropping is not supported.") //
						);
					}
				}) //
		;
	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == DropColumnChange.class;
	}

}

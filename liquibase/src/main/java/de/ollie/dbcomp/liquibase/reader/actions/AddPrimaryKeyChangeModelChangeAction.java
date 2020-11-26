package de.ollie.dbcomp.liquibase.reader.actions;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.LiquibaseFileModelReader;
import de.ollie.dbcomp.model.DatamodelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.report.ImportReport;
import de.ollie.dbcomp.report.ImportReportMessage;
import liquibase.change.Change;
import liquibase.change.core.AddPrimaryKeyChange;

/**
 * An implementation of the ModelChangeAction interface for add primary key changes.
 *
 * @author ollie (26.11.2020)
 */
public class AddPrimaryKeyChangeModelChangeAction implements ModelChangeAction {

	private static final Logger LOG = LogManager.getLogger(AddPrimaryKeyChangeModelChangeAction.class);

	@Override
	public void processOnDataModel(Change change, DatamodelCMO dataModel, ImportReport importReport) {
		AddPrimaryKeyChange addPrimaryKeyChange = (AddPrimaryKeyChange) change;
		SchemaCMO schema = LiquibaseFileModelReader.getSchema(dataModel, addPrimaryKeyChange.getSchemaName());
		LiquibaseFileModelReader.getTable(schema, addPrimaryKeyChange.getTableName(), importReport) //
				.ifPresent(table -> {
					table.addPrimaryKeys(StringUtils.split(addPrimaryKeyChange.getColumnNames(), ','));
					LOG.info("added primary key column(s) '{}' to table: {}", addPrimaryKeyChange.getColumnNames(),
							table.getName());
					importReport.addMessages( //
							new ImportReportMessage() //
									.setMessage(String.format("added primary key column(s) '%s' to table: %s",
											addPrimaryKeyChange.getColumnNames(), table.getName())) //
					);
				}) //
		;
	}

	@Override
	public boolean isMatchingForChange(Change change) {
		return change.getClass() == AddPrimaryKeyChange.class;
	}

}
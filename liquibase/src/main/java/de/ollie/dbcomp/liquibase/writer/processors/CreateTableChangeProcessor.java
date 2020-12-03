package de.ollie.dbcomp.liquibase.writer.processors;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ColumnDataCRO;
import de.ollie.dbcomp.comparator.model.actions.CreateTableChangeActionCRO;
import liquibase.change.Change;
import liquibase.change.ColumnConfig;
import liquibase.change.core.CreateTableChange;

public class CreateTableChangeProcessor implements ChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == CreateTableChangeActionCRO.class;
	}

	@Override
	public Change process(ChangeActionCRO action) {
		CreateTableChangeActionCRO createAction = (CreateTableChangeActionCRO) action;
		CreateTableChange change = new CreateTableChange();
		change.setSchemaName(createAction.getSchemaName());
		change.setTableName(createAction.getTableName());
		createAction.getColumns().forEach(column -> change.addColumn(getColumnConfig(column)));
		return change;
	}

	private ColumnConfig getColumnConfig(ColumnDataCRO column) {
		ColumnConfig columnConfig = new ColumnConfig();
		columnConfig.setName(column.getName());
		columnConfig.setType(column.getSqlType());
		return columnConfig;
	}

}
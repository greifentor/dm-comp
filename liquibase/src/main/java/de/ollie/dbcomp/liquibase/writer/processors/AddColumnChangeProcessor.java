package de.ollie.dbcomp.liquibase.writer.processors;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddColumnChangeActionCRO;
import liquibase.change.AddColumnConfig;
import liquibase.change.Change;
import liquibase.change.core.AddColumnChange;

public class AddColumnChangeProcessor implements ChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == AddColumnChangeActionCRO.class;
	}

	@Override
	public Change process(ChangeActionCRO action) {
		AddColumnChangeActionCRO addAction = (AddColumnChangeActionCRO) action;
		AddColumnChange change = new AddColumnChange();
		change.setSchemaName(addAction.getSchemaName());
		change.setTableName(addAction.getTableName());
		AddColumnConfig columnConfig = new AddColumnConfig();
		columnConfig.setName(addAction.getColumnName());
		columnConfig.setType(addAction.getSqlType());
		change.addColumn(columnConfig);
		return change;
	}

}
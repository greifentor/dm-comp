package de.ollie.dbcomp.liquibase.writer.processors;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropColumnChangeActionCRO;
import liquibase.change.AddColumnConfig;
import liquibase.change.Change;
import liquibase.change.core.DropColumnChange;

public class DropColumnChangeProcessor implements ChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == DropColumnChangeActionCRO.class;
	}

	@Override
	public Change process(ChangeActionCRO action) {
		DropColumnChangeActionCRO dropAction = (DropColumnChangeActionCRO) action;
		DropColumnChange change = new DropColumnChange();
		change.setSchemaName(dropAction.getSchemaName());
		change.setTableName(dropAction.getTableName());
		AddColumnConfig columnConfig = new AddColumnConfig();
		columnConfig.setName(dropAction.getColumnName());
		change.addColumn(columnConfig);
		return change;
	}

}

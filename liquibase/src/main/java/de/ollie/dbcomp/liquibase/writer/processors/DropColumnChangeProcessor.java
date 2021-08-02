package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.Arrays;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropColumnChangeActionCRO;
import liquibase.change.AddColumnConfig;
import liquibase.change.Change;
import liquibase.change.core.DropColumnChange;

public class DropColumnChangeProcessor extends AbstractChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == DropColumnChangeActionCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action, ChangeProcessorConfiguration configuration, List<Change> postChanges) {
		DropColumnChangeActionCRO dropAction = (DropColumnChangeActionCRO) action;
		DropColumnChange change = new DropColumnChange();
		change.setSchemaName(getSchemaName(dropAction, configuration));
		change.setTableName(dropAction.getTableName());
		AddColumnConfig columnConfig = new AddColumnConfig();
		columnConfig.setName(dropAction.getColumnName());
		change.addColumn(columnConfig);
		return Arrays.asList(change);
	}

}

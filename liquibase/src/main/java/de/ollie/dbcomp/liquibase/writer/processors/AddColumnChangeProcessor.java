package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.Arrays;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddColumnChangeActionCRO;
import liquibase.change.AddColumnConfig;
import liquibase.change.Change;
import liquibase.change.ConstraintsConfig;
import liquibase.change.core.AddColumnChange;

public class AddColumnChangeProcessor extends AbstractChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == AddColumnChangeActionCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action, ChangeProcessorConfiguration configuration, List<Change> postChanges) {
		AddColumnChangeActionCRO addAction = (AddColumnChangeActionCRO) action;
		AddColumnChange change = new AddColumnChange();
		change.setSchemaName(getSchemaName(addAction, configuration));
		change.setTableName(addAction.getTableName());
		AddColumnConfig columnConfig = new AddColumnConfig();
		columnConfig.setName(addAction.getColumnName());
		columnConfig.setType(addAction.getSqlType());
		if (addAction.isNotNull()) {
			columnConfig.setConstraints(new ConstraintsConfig().setNullable(false));
		}
		change.addColumn(columnConfig);
		return Arrays.asList(change);
	}

}
package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.Arrays;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ModifyDataTypeCRO;
import liquibase.change.Change;
import liquibase.change.core.ModifyDataTypeChange;

public class ModifyDataTypeChangeProcessor extends AbstractChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == ModifyDataTypeCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action, ChangeProcessorConfiguration configuration) {
		ModifyDataTypeCRO modifyAction = (ModifyDataTypeCRO) action;
		ModifyDataTypeChange change = new ModifyDataTypeChange();
		change.setSchemaName(getSchemaName(modifyAction, configuration));
		change.setTableName(modifyAction.getTableName());
		change.setColumnName(modifyAction.getColumnName());
		change.setNewDataType(modifyAction.getNewDataType());
		return Arrays.asList(change);
	}

}

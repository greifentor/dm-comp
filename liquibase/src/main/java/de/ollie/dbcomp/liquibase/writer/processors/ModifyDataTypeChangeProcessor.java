package de.ollie.dbcomp.liquibase.writer.processors;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ModifyDataTypeCRO;
import liquibase.change.Change;
import liquibase.change.core.ModifyDataTypeChange;

public class ModifyDataTypeChangeProcessor implements ChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == ModifyDataTypeCRO.class;
	}

	@Override
	public Change process(ChangeActionCRO action) {
		ModifyDataTypeCRO modifyAction = (ModifyDataTypeCRO) action;
		ModifyDataTypeChange change = new ModifyDataTypeChange();
		change.setSchemaName(modifyAction.getSchemaName());
		change.setTableName(modifyAction.getTableName());
		change.setColumnName(modifyAction.getColumnName());
		change.setNewDataType(modifyAction.getNewDataType());
		return change;
	}

}

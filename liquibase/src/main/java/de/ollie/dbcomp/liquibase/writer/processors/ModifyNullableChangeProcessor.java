package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.Arrays;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ModifyNullableCRO;
import liquibase.change.Change;
import liquibase.change.core.AddNotNullConstraintChange;
import liquibase.change.core.DropNotNullConstraintChange;

public class ModifyNullableChangeProcessor implements ChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == ModifyNullableCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action) {
		ModifyNullableCRO modifyAction = (ModifyNullableCRO) action;
		if (modifyAction.isNewNullable()) {
			DropNotNullConstraintChange change = new DropNotNullConstraintChange();
			change.setSchemaName(modifyAction.getSchemaName());
			change.setTableName(modifyAction.getTableName());
			change.setColumnName(modifyAction.getColumnName());
			return Arrays.asList(change);
		}
		AddNotNullConstraintChange change = new AddNotNullConstraintChange();
		change.setSchemaName(modifyAction.getSchemaName());
		change.setTableName(modifyAction.getTableName());
		change.setColumnName(modifyAction.getColumnName());
		return Arrays.asList(change);
	}

}
package de.ollie.dbcomp.liquibase.writer.processors;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddPrimaryKeyCRO;
import liquibase.change.Change;
import liquibase.change.core.AddPrimaryKeyChange;

/**
 * @author ollie (27.02.2021)
 */
public class AddPrimaryKeyChangeProcessor implements ChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == AddPrimaryKeyCRO.class;
	}

	@Override
	public Change process(ChangeActionCRO action) {
		AddPrimaryKeyCRO addAction = (AddPrimaryKeyCRO) action;
		AddPrimaryKeyChange change = new AddPrimaryKeyChange();
		change.setSchemaName(addAction.getSchemaName());
		change.setTableName(addAction.getTableName());
		change.setColumnNames(addAction.getPkMemberNames().stream().reduce((cn0, cn1) -> cn0 + ", " + cn1).orElse(""));
		return change;
	}

}
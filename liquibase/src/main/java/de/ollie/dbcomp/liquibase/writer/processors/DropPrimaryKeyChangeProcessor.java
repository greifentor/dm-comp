package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.Arrays;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropPrimaryKeyCRO;
import liquibase.change.Change;
import liquibase.change.core.DropPrimaryKeyChange;

/**
 * @author ollie (27.02.2021)
 */
public class DropPrimaryKeyChangeProcessor extends AbstractChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == DropPrimaryKeyCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action, ChangeProcessorConfiguration configuration, List<Change> postChanges) {
		DropPrimaryKeyCRO dropAction = (DropPrimaryKeyCRO) action;
		DropPrimaryKeyChange change = new DropPrimaryKeyChange();
		change.setSchemaName(getSchemaName(dropAction, configuration));
		change.setTableName(dropAction.getTableName());
		return Arrays.asList(change);
	}

}
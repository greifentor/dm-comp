package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.Arrays;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropTableChangeActionCRO;
import liquibase.change.Change;
import liquibase.change.core.DropTableChange;

public class DropTableChangeProcessor implements ChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == DropTableChangeActionCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action) {
		DropTableChangeActionCRO dropAction = (DropTableChangeActionCRO) action;
		DropTableChange change = new DropTableChange();
		change.setSchemaName(dropAction.getSchemaName());
		change.setTableName(dropAction.getTableName());
		return Arrays.asList(change);
	}

}
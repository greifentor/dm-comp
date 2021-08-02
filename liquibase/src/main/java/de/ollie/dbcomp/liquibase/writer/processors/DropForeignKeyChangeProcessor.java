package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.Arrays;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropForeignKeyCRO;
import liquibase.change.Change;
import liquibase.change.core.DropForeignKeyConstraintChange;

/**
 * @author ollie (27.02.2021)
 */
public class DropForeignKeyChangeProcessor extends AbstractChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == DropForeignKeyCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action, ChangeProcessorConfiguration configuration, List<Change> postChanges) {
		DropForeignKeyCRO dropAction = (DropForeignKeyCRO) action;
		DropForeignKeyConstraintChange change = new DropForeignKeyConstraintChange();
		dropAction.getMembers().forEach(member -> {
			change.setBaseTableSchemaName(getSchemaName(dropAction, configuration));
			change.setBaseTableName(member.getBaseTableName());
			change.setConstraintName(dropAction.getConstraintName());
		});
		return Arrays.asList(change);
	}

}
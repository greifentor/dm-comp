package de.ollie.dbcomp.liquibase.writer.processors;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddForeignKeyCRO;
import liquibase.change.Change;
import liquibase.change.core.AddForeignKeyConstraintChange;

/**
 * @author ollie (27.02.2021)
 */
public class AddForeignKeyChangeProcessor implements ChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == AddForeignKeyCRO.class;
	}

	@Override
	public Change process(ChangeActionCRO action) {
		AddForeignKeyCRO addAction = (AddForeignKeyCRO) action;
		AddForeignKeyConstraintChange change = new AddForeignKeyConstraintChange();
		addAction.getMembers().forEach(member -> {
			change.setBaseTableSchemaName(addAction.getSchemaName());
			change.setBaseTableName(member.getBaseTableName());
			change.setBaseColumnNames(member.getBaseColumnName());
			change.setReferencedTableSchemaName(addAction.getSchemaName());
			change.setReferencedTableName(member.getReferencedTableName());
			change.setReferencedColumnNames(member.getReferencedColumnName());
		});
		return change;
	}

}
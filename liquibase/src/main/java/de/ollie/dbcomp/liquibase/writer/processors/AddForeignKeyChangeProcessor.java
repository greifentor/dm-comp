package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.Arrays;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddForeignKeyCRO;
import liquibase.change.Change;
import liquibase.change.core.AddForeignKeyConstraintChange;

/**
 * @author ollie (27.02.2021)
 */
public class AddForeignKeyChangeProcessor extends AbstractChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == AddForeignKeyCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action, ChangeProcessorConfiguration configuration, List<Change> postChanges) {
		AddForeignKeyCRO addAction = (AddForeignKeyCRO) action;
		AddForeignKeyConstraintChange change = new AddForeignKeyConstraintChange();
		addAction.getMembers().forEach(member -> {
			change.setBaseTableSchemaName(getSchemaName(addAction, configuration));
			change.setBaseTableName(member.getBaseTableName());
			change.setBaseColumnNames(member.getBaseColumnName());
			change.setReferencedTableSchemaName(getSchemaName(addAction, configuration));
			change.setReferencedTableName(member.getReferencedTableName());
			change.setReferencedColumnNames(member.getReferencedColumnName());
		});
		return Arrays.asList(change);
	}

}
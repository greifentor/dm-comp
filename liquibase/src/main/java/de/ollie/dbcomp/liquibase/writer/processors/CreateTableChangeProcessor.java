package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.ArrayList;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ColumnDataCRO;
import de.ollie.dbcomp.comparator.model.actions.CreateTableChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ForeignKeyMemberCRO;
import liquibase.change.Change;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.core.AddForeignKeyConstraintChange;
import liquibase.change.core.AddPrimaryKeyChange;
import liquibase.change.core.CreateTableChange;

public class CreateTableChangeProcessor extends AbstractChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == CreateTableChangeActionCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action, ChangeProcessorConfiguration configuration) {
		CreateTableChangeActionCRO createAction = (CreateTableChangeActionCRO) action;
		CreateTableChange change = new CreateTableChange();
		List<Change> changes = new ArrayList<>();
		change.setSchemaName(getSchemaName(createAction, configuration));
		change.setTableName(createAction.getTableName());
		createAction.getColumns().forEach(column -> change.addColumn(getColumnConfig(column)));
		changes.add(change);
		if (!createAction.getPrimaryKeyMemberNames().isEmpty()) {
			AddPrimaryKeyChange addPKChange = new AddPrimaryKeyChange();
			addPKChange.setSchemaName(getSchemaName(createAction, configuration));
			addPKChange.setTableName(createAction.getTableName());
			addPKChange
					.setColumnNames(
							createAction
									.getPrimaryKeyMemberNames()
									.stream()
									.reduce((s0, s1) -> s0 + "," + s1)
									.orElse(null));
			changes.add(addPKChange);
		}
		if (!createAction.getForeignkeys().isEmpty()) {
			for (String key : createAction.getForeignkeys().keySet()) {
				for (ForeignKeyMemberCRO member : createAction.getForeignkeys().get(key)) {
					AddForeignKeyConstraintChange addFKChange = new AddForeignKeyConstraintChange();
					addFKChange.setConstraintName(key);
					addFKChange.setBaseTableSchemaName(getSchemaName(createAction, configuration));
					addFKChange.setBaseTableName(createAction.getTableName());
					addFKChange.setBaseColumnNames(member.getBaseColumnName());
					addFKChange.setReferencedTableSchemaName(getSchemaName(createAction, configuration));
					addFKChange.setReferencedTableName(member.getReferencedTableName());
					addFKChange.setReferencedColumnNames(member.getReferencedColumnName());
					changes.add(addFKChange);
				}
			}
		}
		return changes;
	}

	private ColumnConfig getColumnConfig(ColumnDataCRO column) {
		ColumnConfig columnConfig = new ColumnConfig();
		columnConfig.setName(column.getName());
		columnConfig.setType(column.getSqlType());
		if (!column.isNullable()) {
			columnConfig.setConstraints(new ConstraintsConfig().setNullable(false));
		}
		return columnConfig;
	}

}
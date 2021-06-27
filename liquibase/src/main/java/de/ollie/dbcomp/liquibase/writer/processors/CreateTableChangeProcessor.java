package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.ArrayList;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ColumnDataCRO;
import de.ollie.dbcomp.comparator.model.actions.CreateTableChangeActionCRO;
import liquibase.change.Change;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.core.AddPrimaryKeyChange;
import liquibase.change.core.CreateTableChange;

public class CreateTableChangeProcessor implements ChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == CreateTableChangeActionCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action) {
		CreateTableChangeActionCRO createAction = (CreateTableChangeActionCRO) action;
		CreateTableChange change = new CreateTableChange();
		List<Change> changes = new ArrayList<>();
		change.setSchemaName(createAction.getSchemaName());
		change.setTableName(createAction.getTableName());
		createAction.getColumns().forEach(column -> change.addColumn(getColumnConfig(column)));
		changes.add(change);
		if (!createAction.getPrimaryKeyMemberNames().isEmpty()) {
			AddPrimaryKeyChange addPKChange = new AddPrimaryKeyChange();
			addPKChange.setSchemaName(createAction.getSchemaName());
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
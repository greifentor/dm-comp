package de.ollie.dbcomp.comparator;

import de.ollie.dbcomp.comparator.model.actions.AddAutoIncrementChangeActionCRO;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.TableCMO;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static de.ollie.dbcomp.util.Check.ensure;

public class AutoIncrementHelper {

	public AddAutoIncrementChangeActionCRO[] getNecessaryAddAutoIncrementChangeActionsForCreateTable(TableCMO table,
	                                                                                                 String schemaName) {
		ensure(table != null, "table cannot be null!");
		return
				getAutoIncrementFields(table)
						.stream()
						.map(column -> new AddAutoIncrementChangeActionCRO()
								.setColumnName(column.getName())
								.setTableName(table.getName())
								.setSchemaName(schemaName)
								.setStartValue(1))
						.collect(Collectors.toList())
						.toArray(new AddAutoIncrementChangeActionCRO[0]);
	}

	List<ColumnCMO> getAutoIncrementFields(TableCMO table) {
		ensure(table != null, "table cannot be null!");
		return table
				.getColumns()
				.entrySet()
				.stream()
				.map(Entry::getValue)
				.filter(this::isAutoIncrementColumn)
				.collect(Collectors.toList());
	}

	boolean isAutoIncrementColumn(ColumnCMO column) {
		ensure(column != null, "column cannot be null!");
		return (column.getAutoIncrement() != null) && column.getAutoIncrement().booleanValue();
	}

}

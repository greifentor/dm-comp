package de.ollie.dbcomp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for table of a scheme.
 * 
 * @author ollie (04.07.2020)
 *
 */
@Accessors(chain = true)
@Data
public class TableCMO {

	private String name;
	private Map<String, ColumnCMO> columns = new HashMap<>();
	private Map<String, ColumnCMO> pkMembers = new HashMap<>();
	private Map<String, ForeignKeyCMO> foreignKeys = new HashMap<>();

	private TableCMO() {
		super();
	}

	public static TableCMO of(String name, ColumnCMO... columns) {
		return new TableCMO() //
				.addColumns(columns) //
				.setName(name) //
		;
	}

	public TableCMO addColumns(ColumnCMO... columns) {
		for (ColumnCMO column : columns) {
			this.columns.put(column.getName(), column);
		}
		return this;
	}

	public Optional<ColumnCMO> getColumnByName(String name) {
		ColumnCMO column = this.columns.get(name);
		return column != null ? Optional.of(column) : Optional.empty();
	}

	public void removeColumn(String name) {
		columns.remove(name);
	}

}
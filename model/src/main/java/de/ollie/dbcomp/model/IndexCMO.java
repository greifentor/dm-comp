package de.ollie.dbcomp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for index.
 * 
 * @author ollie (07.05.2021)
 *
 */
@Accessors(chain = true)
@Data
public class IndexCMO {

	private String name;
	private Map<String, ColumnCMO> memberColumns = new HashMap<>();

	private IndexCMO() {
		super();
	}

	public static IndexCMO of(String name, ColumnCMO... columns) {
		return new IndexCMO().addColumns(columns).setName(name);
	}

	public IndexCMO addColumns(ColumnCMO... columns) {
		for (ColumnCMO column : columns) {
			this.memberColumns.put(column.getName(), column);
		}
		return this;
	}

	public Optional<ColumnCMO> getColumnByName(String name) {
		ColumnCMO column = this.memberColumns.get(name);
		return column != null ? Optional.of(column) : Optional.empty();
	}

}
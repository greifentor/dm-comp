package de.ollie.dbcomp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for a scheme of a database.
 * 
 * @author ollie (04.07.2020)
 *
 */
@Accessors(chain = true)
@Data
public class SchemaCMO {

	private String name;
	private Map<String, TableCMO> tables = new HashMap<>();

	private SchemaCMO() {
		super();
	}

	public static SchemaCMO of(String name, TableCMO... tables) {
		return new SchemaCMO() //
				.addTables(tables) //
				.setName(name) //
		;
	}

	public static SchemaCMO of(TableCMO tables) {
		return new SchemaCMO() //
				.addTables(tables) //
		;
	}

	public SchemaCMO addTables(TableCMO... tables) {
		for (TableCMO table : tables) {
			this.tables.put(table.getName(), table);
		}
		return this;
	}

	public Optional<TableCMO> getTableByName(String name) {
		TableCMO table = this.tables.get(name);
		return table != null ? Optional.of(table) : Optional.empty();
	}

}
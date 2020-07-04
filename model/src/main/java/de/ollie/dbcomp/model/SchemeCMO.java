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
public class SchemeCMO {

	private String name;
	private Map<String, TableCMO> tables = new HashMap<>();

	private SchemeCMO() {
		super();
	}

	public static SchemeCMO of(String name, TableCMO... tables) {
		return new SchemeCMO() //
				.addTables(tables) //
				.setName(name) //
		;
	}

	public static SchemeCMO of(TableCMO tables) {
		return new SchemeCMO() //
				.addTables(tables) //
		;
	}

	public SchemeCMO addTables(TableCMO... tables) {
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
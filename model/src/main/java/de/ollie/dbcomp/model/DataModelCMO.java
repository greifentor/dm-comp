package de.ollie.dbcomp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for a database.
 * 
 * @author ollie (04.07.2020)
 *
 */
@Accessors(chain = true)
@Data
public class DataModelCMO {

	private Map<String, SchemaCMO> schemata = new HashMap<>();

	private DataModelCMO() {
		super();
	}

	public static DataModelCMO of(SchemaCMO... schemata) {
		return new DataModelCMO() //
				.addSchemata(schemata) //
		;
	}

	public DataModelCMO addSchemata(SchemaCMO... schemes) {
		for (SchemaCMO schema : schemes) {
			this.schemata.put(schema.getName(), schema);
		}
		return this;
	}

	public Optional<SchemaCMO> getSchemaByName(String name) {
		SchemaCMO schema = this.schemata.get(name);
		return schema != null ? Optional.of(schema) : Optional.empty();
	}

}
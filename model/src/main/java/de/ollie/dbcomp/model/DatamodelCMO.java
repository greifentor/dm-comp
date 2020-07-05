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
public class DatamodelCMO {

	private Map<String, SchemeCMO> schemes = new HashMap<>();

	private DatamodelCMO() {
		super();
	}

	public static DatamodelCMO of(SchemeCMO... schemes) {
		return new DatamodelCMO() //
				.addSchemes(schemes) //
		;
	}

	public DatamodelCMO addSchemes(SchemeCMO... schemes) {
		for (SchemeCMO scheme : schemes) {
			this.schemes.put(scheme.getName(), scheme);
		}
		return this;
	}

	public Optional<SchemeCMO> getSchemeByName(String name) {
		SchemeCMO scheme = this.schemes.get(name);
		return scheme != null ? Optional.of(scheme) : Optional.empty();
	}

}
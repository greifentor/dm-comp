package de.ollie.dbcomp.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for column of a table.
 * 
 * @author ollie (04.07.2020)
 *
 */
@Accessors(chain = true)
@Data
public class ColumnCMO {

	private Boolean autoIncrement;
	private String name;
	private TypeCMO type;

	private ColumnCMO() {
		super();
	}

	public static ColumnCMO of(String name, TypeCMO type, Boolean autoIncrement) {
		return new ColumnCMO() //
				.setAutoIncrement(autoIncrement) //
				.setName(name) //
				.setType(type) //
		;
	}

}
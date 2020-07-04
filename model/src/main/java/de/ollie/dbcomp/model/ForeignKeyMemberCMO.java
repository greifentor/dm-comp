package de.ollie.dbcomp.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for foreign key members.
 * 
 * @author ollie (04.07.2020)
 *
 */
@Accessors(chain = true)
@Data
public class ForeignKeyMemberCMO {

	private ColumnCMO referencee;
	private ColumnCMO referencer;

	private ForeignKeyMemberCMO() {
		super();
	}

	public static ForeignKeyMemberCMO of(ColumnCMO referencee, ColumnCMO referencer) {
		return new ForeignKeyMemberCMO() //
				.setReferencee(referencee) //
				.setReferencer(referencer) //
		;
	}

}
package de.ollie.dbcomp.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for foreign keys of a table.
 * 
 * @author ollie (04.07.2020)
 *
 */
@Accessors(chain = true)
@Data
public class ForeignKeyCMO {

	private String name;
	private List<ForeignKeyMemberCMO> members = new ArrayList<>();

	private ForeignKeyCMO() {
		super();
	}

	public static ForeignKeyCMO of(String name, ForeignKeyMemberCMO... members) {
		return new ForeignKeyCMO() //
				.addMembers(members) //
				.setName(name) //
		;
	}

	public ForeignKeyCMO addMembers(ForeignKeyMemberCMO... members) {
		for (ForeignKeyMemberCMO member : members) {
			this.members.add(member);
		}
		return this;
	}

}
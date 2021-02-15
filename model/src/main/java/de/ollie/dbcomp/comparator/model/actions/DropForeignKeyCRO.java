package de.ollie.dbcomp.comparator.model.actions;

import static de.ollie.dbcomp.util.Check.ensure;

import java.util.ArrayList;
import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for a drop foreign key change action.
 *
 * @author ollie (13.02.2021)
 */
@Accessors(chain = true)
@Data
public class DropForeignKeyCRO implements ChangeActionCRO {

	private String tableName;
	private String schemaName;

	private List<ForeignKeyMemberCRO> members = new ArrayList<>();

	public DropForeignKeyCRO addMembers(ForeignKeyMemberCRO... members) {
		for (ForeignKeyMemberCRO member : members) {
			ensure(member.getBaseTableName().equals(tableName), "base table of member must be same as of CRO.");
			this.members.add(member);
		}
		return this;
	}

}
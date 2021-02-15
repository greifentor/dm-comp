package de.ollie.dbcomp.comparator.model.actions;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for foreign key member data in the change action context.
 *
 * @author ollie (14.02.2021)
 */
@Accessors(chain = true)
@Data
public class ForeignKeyMemberCRO {

	private String baseColumnName;
	private String baseTableName;
	private String referencedColumnName;
	private String referencedTableName;

}
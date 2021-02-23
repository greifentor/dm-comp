package de.ollie.dbcomp.comparator.model.actions;

import java.util.HashSet;
import java.util.Set;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for a add foreign key change action.
 *
 * @author ollie (18.02.2021)
 */
@Accessors(chain = true)
@Data
public class AddPrimaryKeyCRO implements ChangeActionCRO {

	private String tableName;
	private String schemaName;

	private Set<String> pkMemberNames = new HashSet<>();

}
package de.ollie.dbcomp.comparator.model.actions;

import java.util.HashSet;
import java.util.Set;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for a drop index change action.
 *
 * @author ollie (08.05.2021)
 */
@Accessors(chain = true)
@Data
public class DropIndexCRO implements ChangeActionCRO {

	private String tableName;
	private String schemaName;

	private String indexName;
	private Set<String> indexMemberNames = new HashSet<>();

}
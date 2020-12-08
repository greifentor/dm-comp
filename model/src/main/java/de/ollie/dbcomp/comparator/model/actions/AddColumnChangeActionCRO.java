package de.ollie.dbcomp.comparator.model.actions;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for a add column change action.
 *
 * @author ollie (08.12.2020)
 */
@Accessors(chain = true)
@Data
public class AddColumnChangeActionCRO implements ChangeActionCRO {

	private String tableName;
	private String schemaName;

	private String columnName;
	private String sqlType;

}
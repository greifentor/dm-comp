package de.ollie.dbcomp.comparator.model.actions;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for a modify datatype of a column change action.
 *
 * @author ollie (15.01.2021)
 */
@Accessors(chain = true)
@Data
public class ModifyNullableCRO implements ChangeActionCRO {

	private String tableName;
	private String schemaName;

	private String columnName;
	private boolean newNullable;

}
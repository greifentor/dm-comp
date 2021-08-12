package de.ollie.dbcomp.comparator.model.actions;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for an add auto increment change action.
 *
 * @author ollie (08.08.2021)
 *
 */
@Accessors(chain = true)
@Data
public class AddAutoIncrementChangeActionCRO implements ChangeActionCRO {

	private String tableName;
	private String schemaName;
	private String columnName;
	private String dataType;
	private long startValue;

}
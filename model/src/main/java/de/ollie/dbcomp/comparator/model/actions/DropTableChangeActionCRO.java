package de.ollie.dbcomp.comparator.model.actions;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for a drop table change action.
 * 
 * @author ollie (27.11.2020)
 *
 */
@Accessors(chain = true)
@Data
public class DropTableChangeActionCRO implements ChangeActionCRO {

	private String tableName;
	private String schemaName;

}
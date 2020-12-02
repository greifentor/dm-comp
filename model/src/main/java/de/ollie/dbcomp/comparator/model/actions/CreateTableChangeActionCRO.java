package de.ollie.dbcomp.comparator.model.actions;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for a create table change action.
 * 
 * @author ollie (02.12.2020)
 *
 */
@Accessors(chain = true)
@Data
public class CreateTableChangeActionCRO implements ChangeActionCRO {

	private String tableName;
	private String schemaName;

}
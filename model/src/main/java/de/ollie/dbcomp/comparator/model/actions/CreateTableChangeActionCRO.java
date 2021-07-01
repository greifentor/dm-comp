package de.ollie.dbcomp.comparator.model.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private List<ColumnDataCRO> columns = new ArrayList<>();
	private Map<String, List<ForeignKeyMemberCRO>> foreignkeys = new HashMap<>();
	private String tableName;
	private String schemaName;
	private Set<String> primaryKeyMemberNames = new HashSet<>();

	public CreateTableChangeActionCRO addColumns(ColumnDataCRO... columns) {
		for (ColumnDataCRO column : columns) {
			this.columns.add(column);
		}
		return this;
	}

	public boolean isPrimaryKeyMember(String pkMemberName) {
		return primaryKeyMemberNames.contains(pkMemberName);
	}

}
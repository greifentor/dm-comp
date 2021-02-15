package de.ollie.dbcomp.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for foreign key members.
 * 
 * @author ollie (04.07.2020)
 *
 */
@Accessors(chain = true)
@Data
public class ForeignKeyMemberCMO {

	private ColumnCMO referenceeColumn;
	private TableCMO referenceeTable;
	private ColumnCMO referencerColumn;
	private TableCMO referencerTable;

	private ForeignKeyMemberCMO() {
		super();
	}

	public static ForeignKeyMemberCMO of(TableCMO referenceeTable, ColumnCMO referenceeColumn, TableCMO referencerTable,
			ColumnCMO referencerColumn) {
		return new ForeignKeyMemberCMO()
				.setReferenceeTable(referenceeTable)
				.setReferenceeColumn(referenceeColumn)
				.setReferencerTable(referencerTable)
				.setReferencerColumn(referencerColumn);
	}

}
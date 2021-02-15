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

	private ColumnCMO baseColumn;
	private TableCMO baseTable;
	private ColumnCMO referencedColumn;
	private TableCMO referencedTable;

	private ForeignKeyMemberCMO() {
		super();
	}

	public static ForeignKeyMemberCMO of(TableCMO baseTable, ColumnCMO baseColumn, TableCMO referencedTable,
			ColumnCMO referencedColumn) {
		return new ForeignKeyMemberCMO()
				.setBaseTable(baseTable)
				.setBaseColumn(baseColumn)
				.setReferencedTable(referencedTable)
				.setReferencedColumn(referencedColumn);
	}

	@Override
	public String toString() {
		return "ForeignKeyMemberCMO(baseTable=" + baseTable.getName() + "." + baseColumn.getName() + ",referencedTable="
				+ referencedTable.getName() + "." + referencedColumn.getName() + ")";
	}

}
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
	public boolean equals(Object o) {
		if (!(o instanceof ForeignKeyMemberCMO)) {
			return false;
		}
		ForeignKeyMemberCMO fk = (ForeignKeyMemberCMO) o;
		return getQualifiedColumnName(getBaseTable(), getBaseColumn())
				.equals(getQualifiedColumnName(fk.getBaseTable(), fk.getBaseColumn()))
				&& getQualifiedColumnName(getReferencedTable(), getReferencedColumn())
						.equals(getQualifiedColumnName(fk.getReferencedTable(), fk.getReferencedColumn()));
	}

	private String getQualifiedColumnName(TableCMO table, ColumnCMO column) {
		return (table == null ? "<NULL>" : table.getName()) + "." + (column == null ? "<NULL>" : column.getName());
	}

	@Override
	public int hashCode() {
		int hc = 33;
		int hashMultiplier = 43;
		hc = hc * hashMultiplier + getQualifiedColumnName(getBaseTable(), getBaseColumn()).hashCode();
		hc = hc * hashMultiplier + getQualifiedColumnName(getReferencedTable(), getReferencedColumn()).hashCode();
		return hc;
	}

	@Override
	public String toString() {
		return "ForeignKeyMemberCMO(baseTable=" + baseTable.getName()
				+ "."
				+ baseColumn.getName()
				+ ",referencedTable="
				+ referencedTable.getName()
				+ "."
				+ referencedColumn.getName()
				+ ")";
	}

}
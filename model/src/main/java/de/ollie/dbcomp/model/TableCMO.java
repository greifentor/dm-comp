package de.ollie.dbcomp.model;

import static de.ollie.dbcomp.util.Check.ensure;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for table of a scheme.
 * 
 * @author ollie (04.07.2020)
 *
 */
@Accessors(chain = true)
@Data
public class TableCMO {

	private String name;
	private Map<String, ColumnCMO> columns = new HashMap<>();
	private Map<String, ColumnCMO> pkMembers = new HashMap<>();
	private Map<String, ForeignKeyCMO> foreignKeys = new HashMap<>();
	private Map<String, IndexCMO> indices = new HashMap<>();

	private TableCMO() {
		super();
	}

	public static TableCMO of(String name, ColumnCMO... columns) {
		return new TableCMO().addColumns(columns).setName(name);
	}

	public TableCMO addColumns(ColumnCMO... columns) {
		for (ColumnCMO column : columns) {
			this.columns.put(column.getName(), column);
		}
		return this;
	}

	public TableCMO addForeignKeys(ForeignKeyCMO... foreignKeys) {
		for (ForeignKeyCMO foreignKey : foreignKeys) {
			ensure(
					foreignKey.getMembers().stream().anyMatch(fk -> fk.getBaseTable() == this),
					"fk base table must be the same table as the fk ist assgined to.");
			this.foreignKeys.put(foreignKey.getName(), foreignKey);
		}
		return this;
	}

	public TableCMO addIndex(IndexCMO... indices) {
		for (IndexCMO index : indices) {
			this.indices.put(index.getName(), index);
		}
		return this;
	}

	public TableCMO addPrimaryKeys(String... columnNames) {
		for (String columnName : columnNames) {
			getColumnByName(columnName.trim())
					.ifPresentOrElse(column -> this.pkMembers.put(column.getName(), column), () -> {
						throw new NoSuchElementException(
								"column '" + columnName + "' does not exists in table: " + getName());
					});
		}
		return this;
	}

	public boolean hasForeignKey(ForeignKeyCMO foreignKey) {
		return foreignKeys.entrySet().stream().anyMatch(fk -> isEqual(fk.getValue(), foreignKey));
	}

	private boolean isEqual(ForeignKeyCMO fk0, ForeignKeyCMO fk1) {
		return fk0.getMembers().stream().allMatch(fkm -> containsForeignKeyMember(fk1, fkm));
	}

	private boolean containsForeignKeyMember(ForeignKeyCMO fk, ForeignKeyMemberCMO fkm) {
		return fk.getMembers().stream().anyMatch(fkm0 -> fkm0.equals(fkm));
	}

	public boolean hasIndex(IndexCMO index) {
		return indices.entrySet().stream().anyMatch(index1 -> isEqual(index1.getValue(), index));
	}

	private boolean isEqual(IndexCMO index0, IndexCMO index1) {
		return index0
				.getMemberColumns()
				.entrySet()
				.stream()
				.allMatch(column -> containsIndexMember(index1, column.getValue()));
	}

	private boolean containsIndexMember(IndexCMO index, ColumnCMO column) {
		return index
				.getMemberColumns()
				.entrySet()
				.stream()
				.anyMatch(column0 -> column0.getValue().getName().equals(column.getName()));
	}

	public boolean isPrimaryKeyMember(String columnName) {
		return pkMembers.containsKey(columnName);
	}

	public Optional<ColumnCMO> getColumnByName(String name) {
		ColumnCMO column = this.columns.get(name);
		return column != null ? Optional.of(column) : Optional.empty();
	}

	public void removeColumn(String name) {
		columns.remove(name);
	}

}
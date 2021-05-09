package de.ollie.dbcomp.comparator;

import static de.ollie.dbcomp.util.Check.ensure;

import java.sql.Types;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.ComparisonResultCRO;
import de.ollie.dbcomp.comparator.model.actions.AddColumnChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddForeignKeyCRO;
import de.ollie.dbcomp.comparator.model.actions.AddIndexCRO;
import de.ollie.dbcomp.comparator.model.actions.ColumnDataCRO;
import de.ollie.dbcomp.comparator.model.actions.CreateTableChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropColumnChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropForeignKeyCRO;
import de.ollie.dbcomp.comparator.model.actions.DropIndexCRO;
import de.ollie.dbcomp.comparator.model.actions.DropTableChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ForeignKeyMemberCRO;
import de.ollie.dbcomp.comparator.model.actions.ModifyDataTypeCRO;
import de.ollie.dbcomp.comparator.model.actions.ModifyNullableCRO;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.ForeignKeyCMO;
import de.ollie.dbcomp.model.IndexCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.model.TableCMO;
import de.ollie.dbcomp.model.TypeCMO;

/**
 * Compares two data models and returns a report about the comparison and a list of actions to equalize both models.
 *
 * @author ollie (27.11.2020)
 *
 */
public class DataModelComparator {

	private static final PrimaryKeyComparator primaryKeyComparator = new PrimaryKeyComparator();

	/**
	 * Compares the two passed models and return a report of the comparison an a list of actions necessary to equalize
	 * both models.
	 * 
	 * @param sourceModel The model which the is to change to the target models structure.
	 * @param targetModel The model which the source model would be changed to if all the returned actions are executed
	 *                    onto.
	 * @return A report and a list of actions which are necessary to change the source model to the target models
	 *         structure.
	 */
	public ComparisonResultCRO compare(DataModelCMO sourceModel, DataModelCMO targetModel) {
		ensure(sourceModel != null, "source model cannot be null.");
		ensure(targetModel != null, "target model cannot be null.");
		ComparisonResultCRO result = new ComparisonResultCRO();
		addCreateTableChangeActions(sourceModel, targetModel, result);
		addDropTableChangeActions(sourceModel, targetModel, result);
		addAddColumnChangeActions(sourceModel, targetModel, result);
		addDropColumnChangeActions(sourceModel, targetModel, result);
		addModifyChangeActions(sourceModel, targetModel, result);
		addDropForeignKeyChangeActions(sourceModel, targetModel, result);
		addAddForeignKeyChangeActions(sourceModel, targetModel, result);
		addAddIndexChangeActions(sourceModel, targetModel, result);
		addDropIndexChangeActions(sourceModel, targetModel, result);
		result
				.addChangeActions(
						primaryKeyComparator
								.compareForPrimaryKeys(targetModel, sourceModel)
								.toArray(new ChangeActionCRO[0]));
		return result;
	}

	private void addCreateTableChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		for (Entry<String, SchemaCMO> schemaEntry : sourceModel.getSchemata().entrySet()) {
			targetModel.getSchemaByName(schemaEntry.getKey()).ifPresent(schema -> {
				for (Entry<String, TableCMO> tableEntry : schemaEntry.getValue().getTables().entrySet()) {
					if (schema.getTableByName(tableEntry.getKey()).isEmpty()) {
						result
								.addChangeActions(
										new CreateTableChangeActionCRO()
												.addColumns(getColumns(tableEntry.getValue()))
												.setSchemaName(schema.getName())
												.setTableName(tableEntry.getKey())
												.setPrimaryKeyMemberNames(
														tableEntry.getValue().getPkMembers().keySet()));
					}
				}
			});
		}
	}

	private ColumnDataCRO[] getColumns(TableCMO table) {
		return table
				.getColumns()
				.entrySet()
				.stream()
				.map(
						entry -> new ColumnDataCRO()
								.setName(entry.getValue().getName())
								.setSqlType(getSQLType(entry.getValue().getType()))
								.setNullable(entry.getValue().isNullable()))
				.collect(Collectors.toList())
				.toArray(new ColumnDataCRO[table.getColumns().size()]);
	}

	private String getSQLType(TypeCMO type) {
		if (type.getSqlType() == Types.BIGINT) {
			return "BIGINT";
		} else if (type.getSqlType() == Types.BIT) {
			return "BIT";
		} else if (type.getSqlType() == Types.BOOLEAN) {
			return "BOOLEAN";
		} else if (type.getSqlType() == Types.CHAR) {
			return "CHAR(" + type.getLength() + ")";
		} else if (type.getSqlType() == Types.DATE) {
			return "DATE";
		} else if (type.getSqlType() == Types.DECIMAL) {
			return "DECIMAL(" + type.getLength() + ", " + type.getDecimalPlace() + ")";
		} else if (type.getSqlType() == Types.DOUBLE) {
			return "DOUBLE";
		} else if (type.getSqlType() == Types.FLOAT) {
			return "FLOAT";
		} else if (type.getSqlType() == Types.INTEGER) {
			return "INTEGER";
		} else if (type.getSqlType() == Types.LONGVARCHAR) {
			return "LONGVARCHAR";
		} else if (type.getSqlType() == Types.NUMERIC) {
			return "NUMERIC(" + type.getLength() + ", " + type.getDecimalPlace() + ")";
		} else if (type.getSqlType() == Types.SMALLINT) {
			return "SMALLINT";
		} else if (type.getSqlType() == Types.TIME) {
			return "TIME";
		} else if (type.getSqlType() == Types.TIMESTAMP) {
			return "TIMESTAMP";
		} else if (type.getSqlType() == Types.TINYINT) {
			return "TINYINT";
		} else if (type.getSqlType() == Types.VARCHAR) {
			return "VARCHAR(" + type.getLength() + ")";
		}
		return "BIGINT";
	}

	private void addDropTableChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		for (Entry<String, SchemaCMO> schemaEntry : targetModel.getSchemata().entrySet()) {
			sourceModel.getSchemaByName(schemaEntry.getKey()).ifPresent(schema -> {
				for (Entry<String, TableCMO> tableEntry : schemaEntry.getValue().getTables().entrySet()) {
					if (schema.getTableByName(tableEntry.getKey()).isEmpty()) {
						result
								.addChangeActions(
										new DropTableChangeActionCRO()
												.setSchemaName(schema.getName())
												.setTableName(tableEntry.getKey()));
					}
				}
			});
		}
	}

	private void addAddColumnChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		sourceModel
				.getSchemata()
				.entrySet()
				.stream()
				.map(Entry::getValue)
				.forEach(
						schema -> schema
								.getTables()
								.entrySet()
								.stream()
								.map(Entry::getValue)
								.filter(table -> hasTable(targetModel, schema.getName(), table.getName()))
								.forEach(
										table -> table
												.getColumns()
												.entrySet()
												.stream()
												.map(Entry::getValue)
												.forEach(column -> {
													if (getColumn(
															targetModel,
															schema.getName(),
															table.getName(),
															column.getName()).isEmpty()) {
														result
																.addChangeActions(
																		new AddColumnChangeActionCRO()
																				.setColumnName(column.getName())
																				.setSchemaName(schema.getName())
																				.setSqlType(
																						getSQLType(column.getType()))
																				.setTableName(table.getName()));
													}
												})));
	}

	private boolean hasTable(DataModelCMO model, String schemaName, String tableName) {
		return model.getSchemaByName(schemaName).orElse(SchemaCMO.of("n/a")).getTableByName(tableName).isPresent();
	}

	private Optional<ColumnCMO> getColumn(DataModelCMO model, String schemaName, String tableName, String columnName) {
		return model
				.getSchemaByName(schemaName)
				.orElse(SchemaCMO.of("n/a"))
				.getTableByName(tableName)
				.orElse(TableCMO.of("n/a"))
				.getColumnByName(columnName);
	}

	private void addDropColumnChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		targetModel
				.getSchemata()
				.entrySet()
				.stream()
				.map(Entry::getValue)
				.forEach(
						schema -> schema
								.getTables()
								.entrySet()
								.stream()
								.map(Entry::getValue)
								.filter(table -> hasTable(sourceModel, schema.getName(), table.getName()))
								.forEach(
										table -> table
												.getColumns()
												.entrySet()
												.stream()
												.map(Entry::getValue)
												.forEach(column -> {
													if (getColumn(
															sourceModel,
															schema.getName(),
															table.getName(),
															column.getName()).isEmpty()) {
														result
																.addChangeActions(
																		new DropColumnChangeActionCRO()
																				.setColumnName(column.getName())
																				.setSchemaName(schema.getName())
																				.setTableName(table.getName()));
													}
												})));
	}

	private void addModifyChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		sourceModel
				.getSchemata()
				.entrySet()
				.stream()
				.map(Entry::getValue)
				.forEach(
						schema -> schema
								.getTables()
								.entrySet()
								.stream()
								.map(Entry::getValue)
								.filter(table -> hasTable(targetModel, schema.getName(), table.getName()))
								.forEach(
										table -> table
												.getColumns()
												.entrySet()
												.stream()
												.map(Entry::getValue)
												.forEach(
														column -> checkForColumn(
																targetModel,
																schema.getName(),
																table.getName(),
																column.getName(),
																column.getType(),
																column.isNullable(),
																result))));
	}

	private void checkForColumn(DataModelCMO model, String schemaName, String tableName, String columnName,
			TypeCMO type, boolean nullable, ComparisonResultCRO result) {
		getColumn(model, schemaName, tableName, columnName)
				.filter(column -> !column.getType().equals(type) || (column.isNullable() != nullable))
				.ifPresent(column -> {
					if (!column.getType().equals(type)) {
						result
								.addChangeActions(
										new ModifyDataTypeCRO()
												.setColumnName(columnName)
												.setNewDataType(getSQLType(type))
												.setSchemaName(schemaName)
												.setTableName(tableName));
					} else if (column.isNullable() != nullable) {
						result
								.addChangeActions(
										new ModifyNullableCRO()
												.setColumnName(columnName)
												.setNewNullable(nullable)
												.setSchemaName(schemaName)
												.setTableName(tableName));
					}
				});
	}

	private void addDropForeignKeyChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		sourceModel
				.getSchemata()
				.entrySet()
				.stream()
				.map(Entry::getValue)
				.forEach(
						schema -> schema
								.getTables()
								.entrySet()
								.stream()
								.map(Entry::getValue)
								.filter(table -> hasTable(targetModel, schema.getName(), table.getName()))
								.forEach(
										table -> table
												.getForeignKeys()
												.entrySet()
												.stream()
												.map(Entry::getValue)
												.forEach(
														fk -> checkForForeignKeyAdd(
																fk,
																table.getName(),
																schema.getName(),
																targetModel,
																result))));
	}

	private void checkForForeignKeyAdd(ForeignKeyCMO foreignKey, String tableName, String schemaName,
			DataModelCMO targetModel, ComparisonResultCRO result) {
		targetModel
				.getSchemaByName(schemaName)
				.ifPresent(
						schema -> schema
								.getTableByName(tableName)
								.filter(table -> !table.hasForeignKey(foreignKey))
								.ifPresent(
										table -> result
												.addChangeActions(
														new AddForeignKeyCRO()
																.setSchemaName(schemaName)
																.setTableName(tableName)
																.addMembers(getMembers(foreignKey)))));
	}

	private ForeignKeyMemberCRO[] getMembers(ForeignKeyCMO foreignKey) {
		return foreignKey
				.getMembers()
				.stream()
				.map(
						member -> new ForeignKeyMemberCRO()
								.setBaseColumnName(member.getBaseColumn().getName())
								.setBaseTableName(member.getBaseTable().getName())
								.setReferencedColumnName(member.getReferencedColumn().getName())
								.setReferencedTableName(member.getReferencedTable().getName()))
				.collect(Collectors.toList())
				.toArray(new ForeignKeyMemberCRO[foreignKey.getMembers().size()]);
	}

	private void addAddForeignKeyChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		targetModel
				.getSchemata()
				.entrySet()
				.stream()
				.map(Entry::getValue)
				.forEach(
						schema -> schema
								.getTables()
								.entrySet()
								.stream()
								.map(Entry::getValue)
								.filter(table -> hasTable(sourceModel, schema.getName(), table.getName()))
								.forEach(
										table -> table
												.getForeignKeys()
												.entrySet()
												.stream()
												.map(Entry::getValue)
												.forEach(
														fk -> checkForForeignKeyDrop(
																fk,
																table.getName(),
																schema.getName(),
																sourceModel,
																result))));
	}

	private void checkForForeignKeyDrop(ForeignKeyCMO foreignKey, String tableName, String schemaName,
			DataModelCMO sourceModel, ComparisonResultCRO result) {
		sourceModel
				.getSchemaByName(schemaName)
				.ifPresent(
						schema -> schema
								.getTableByName(tableName)
								.filter(table -> !table.hasForeignKey(foreignKey))
								.ifPresent(
										table -> result
												.addChangeActions(
														new DropForeignKeyCRO()
																.setSchemaName(schemaName)
																.setTableName(tableName)
																.setConstraintName(foreignKey.getName())
																.addMembers(getMembers(foreignKey)))));
	}

	private void addDropIndexChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		targetModel
				.getSchemata()
				.entrySet()
				.stream()
				.map(Entry::getValue)
				.forEach(
						schema -> schema
								.getTables()
								.entrySet()
								.stream()
								.map(Entry::getValue)
								.filter(table -> hasTable(sourceModel, schema.getName(), table.getName()))
								.forEach(
										table -> table
												.getIndices()
												.entrySet()
												.stream()
												.map(Entry::getValue)
												.forEach(
														index -> checkForIndexDrop(
																index,
																table.getName(),
																schema.getName(),
																sourceModel,
																result))));
	}

	private void checkForIndexDrop(IndexCMO index, String tableName, String schemaName, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		targetModel
				.getSchemaByName(schemaName)
				.ifPresent(
						schema -> schema
								.getTableByName(tableName)
								.filter(table -> !table.hasIndex(index))
								.ifPresent(
										table -> result
												.addChangeActions(
														new DropIndexCRO()
																.setSchemaName(schemaName)
																.setTableName(tableName)
																.setIndexName(index.getName())
																.setIndexMemberNames(
																		getIndexMemberColumnNames(index)))));
	}

	private void addAddIndexChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		sourceModel
				.getSchemata()
				.entrySet()
				.stream()
				.map(Entry::getValue)
				.forEach(
						schema -> schema
								.getTables()
								.entrySet()
								.stream()
								.map(Entry::getValue)
								.filter(table -> hasTable(targetModel, schema.getName(), table.getName()))
								.forEach(
										table -> table
												.getIndices()
												.entrySet()
												.stream()
												.map(Entry::getValue)
												.forEach(
														index -> checkForIndexAdd(
																index,
																table.getName(),
																schema.getName(),
																targetModel,
																result))));
	}

	private void checkForIndexAdd(IndexCMO index, String tableName, String schemaName, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		targetModel
				.getSchemaByName(schemaName)
				.ifPresent(
						schema -> schema
								.getTableByName(tableName)
								.filter(table -> !table.hasIndex(index))
								.ifPresent(
										table -> result
												.addChangeActions(
														new AddIndexCRO()
																.setSchemaName(schemaName)
																.setTableName(tableName)
																.setIndexName(index.getName())
																.setIndexMemberNames(
																		getIndexMemberColumnNames(index)))));
	}

	private Set<String> getIndexMemberColumnNames(IndexCMO index) {
		return index
				.getMemberColumns()
				.entrySet()
				.stream()
				.map(column -> column.getValue().getName())
				.collect(Collectors.toSet());
	}

}
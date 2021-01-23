package de.ollie.dbcomp.comparator;

import static de.ollie.dbcomp.util.Check.ensure;

import java.sql.Types;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import de.ollie.dbcomp.comparator.model.ComparisonResultCRO;
import de.ollie.dbcomp.comparator.model.actions.AddColumnChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ColumnDataCRO;
import de.ollie.dbcomp.comparator.model.actions.CreateTableChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropColumnChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropTableChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ModifyDataTypeCRO;
import de.ollie.dbcomp.comparator.model.actions.ModifyNullableCRO;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.DataModelCMO;
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
												.setTableName(tableEntry.getKey()));
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
		if (type.getSqlType() == Types.LONGVARCHAR) {
			return "LONGVARCHAR";
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
																				.setSqlType("BIGINT")
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

}
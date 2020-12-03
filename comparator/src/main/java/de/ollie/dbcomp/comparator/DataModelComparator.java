package de.ollie.dbcomp.comparator;

import static de.ollie.dbcomp.util.Check.ensure;

import java.sql.Types;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.ollie.dbcomp.comparator.model.ComparisonResultCRO;
import de.ollie.dbcomp.comparator.model.actions.ColumnDataCRO;
import de.ollie.dbcomp.comparator.model.actions.CreateTableChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropTableChangeActionCRO;
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
		return result;
	}

	private void addCreateTableChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		for (Entry<String, SchemaCMO> schemaEntry : sourceModel.getSchemata().entrySet()) {
			targetModel.getSchemaByName(schemaEntry.getKey()) //
					.ifPresent(schema -> {
						for (Entry<String, TableCMO> tableEntry : schemaEntry.getValue().getTables().entrySet()) {
							if (schema.getTableByName(tableEntry.getKey()).isEmpty()) {
								result.addChangeActions( //
										new CreateTableChangeActionCRO() //
												.addColumns(getColumns(tableEntry.getValue())) //
												.setSchemaName(schema.getName()) //
												.setTableName(tableEntry.getKey()) //
								);
							}
						}
					});
		}
	}

	private ColumnDataCRO[] getColumns(TableCMO table) {
		return table.getColumns().entrySet() //
				.stream() //
				.map(entry -> new ColumnDataCRO() //
						.setName(entry.getValue().getName()) //
						.setSqlType(getSQLType(entry.getValue().getType())) //
				)//
				.collect(Collectors.toList()) //
				.toArray(new ColumnDataCRO[table.getColumns().size()]) //
		;
	}

	private String getSQLType(TypeCMO type) {
		if (type.getSqlType() == Types.LONGVARCHAR) {
			return "LONGVARCHAR";
		}
		return "BIGINT";
	}

	private void addDropTableChangeActions(DataModelCMO sourceModel, DataModelCMO targetModel,
			ComparisonResultCRO result) {
		for (Entry<String, SchemaCMO> schemaEntry : targetModel.getSchemata().entrySet()) {
			sourceModel.getSchemaByName(schemaEntry.getKey()) //
					.ifPresent(schema -> {
						for (Entry<String, TableCMO> tableEntry : schemaEntry.getValue().getTables().entrySet()) {
							if (schema.getTableByName(tableEntry.getKey()).isEmpty()) {
								result.addChangeActions( //
										new DropTableChangeActionCRO() //
												.setSchemaName(schema.getName()) //
												.setTableName(tableEntry.getKey()) //
								);
							}
						}
					});
		}
	}

}
package de.ollie.dbcomp.comparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddPrimaryKeyCRO;
import de.ollie.dbcomp.comparator.model.actions.DropPrimaryKeyCRO;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.TableCMO;

/**
 * A class which is able to compare primary keys and return change result objects to show the differences between the
 * models.
 *
 * @author ollie (21.02.2021)
 */
public class PrimaryKeyComparator {

	public List<ChangeActionCRO> compareForPrimaryKeys(DataModelCMO sourceModel, DataModelCMO targetModel) {
		List<ChangeActionCRO> result = new ArrayList<>();
		sourceModel
				.getSchemata()
				.entrySet()
				.stream()
				.map(Entry::getValue)
				.forEach(schema -> schema.getTables().entrySet().stream().map(Entry::getValue).forEach(table -> {
					addDropForMissingKeysInTargetModel(table, schema.getName(), targetModel, result);
					addAddForMissingKeysInSourceModel(table, schema.getName(), targetModel, result);
					addAddAndDropForDifferentKeysInSourceModelAndTargetModel(
							table,
							schema.getName(),
							targetModel,
							result);
				}));
		return result;
	}

	private void addDropForMissingKeysInTargetModel(TableCMO sourceTable, String schemaName, DataModelCMO targetModel,
			List<ChangeActionCRO> result) {
		getTableByNameForSchemaWithName(sourceTable.getName(), schemaName, targetModel)
				.filter(targetTable -> !sourceTable.getPkMembers().isEmpty() && targetTable.getPkMembers().isEmpty())
				.ifPresent(
						targetTable -> result
								.add(
										new DropPrimaryKeyCRO()
												.setPkMemberNames(sourceTable.getPkMembers().keySet())
												.setSchemaName(schemaName)
												.setTableName(sourceTable.getName())));
	}

	private Optional<TableCMO> getTableByNameForSchemaWithName(String tableName, String schemaName,
			DataModelCMO model) {
		return model.getSchemaByName(schemaName).map(schema -> schema.getTableByName(tableName)).orElse(null);
	}

	private void addAddForMissingKeysInSourceModel(TableCMO sourceTable, String schemaName, DataModelCMO targetModel,
			List<ChangeActionCRO> result) {
		getTableByNameForSchemaWithName(sourceTable.getName(), schemaName, targetModel)
				.filter(targetTable -> sourceTable.getPkMembers().isEmpty() && !targetTable.getPkMembers().isEmpty())
				.ifPresent(
						targetTable -> result
								.add(
										new AddPrimaryKeyCRO()
												.setPkMemberNames(targetTable.getPkMembers().keySet())
												.setSchemaName(schemaName)
												.setTableName(targetTable.getName())));
	}

	private void addAddAndDropForDifferentKeysInSourceModelAndTargetModel(TableCMO sourceTable, String schemaName,
			DataModelCMO targetModel, List<ChangeActionCRO> result) {
		getTableByNameForSchemaWithName(sourceTable.getName(), schemaName, targetModel)
				.filter(
						targetTable -> !sourceTable.getPkMembers().isEmpty() && !targetTable.getPkMembers().isEmpty()
								&& !sourceTable.getPkMembers().keySet().equals(targetTable.getPkMembers().keySet()))
				.ifPresent(targetTable -> {
					System.out
							.println(
									"+++++ " + !sourceTable
											.getPkMembers()
											.keySet()
											.equals(targetTable.getPkMembers().keySet()));
					System.out.println(sourceTable.getPkMembers());
					System.out.println(targetTable.getPkMembers());
					result
							.add(
									new DropPrimaryKeyCRO()
											.setPkMemberNames(sourceTable.getPkMembers().keySet())
											.setSchemaName(schemaName)
											.setTableName(sourceTable.getName()));
					result
							.add(
									new AddPrimaryKeyCRO()
											.setPkMemberNames(targetTable.getPkMembers().keySet())
											.setSchemaName(schemaName)
											.setTableName(targetTable.getName()));
				});
	}

}
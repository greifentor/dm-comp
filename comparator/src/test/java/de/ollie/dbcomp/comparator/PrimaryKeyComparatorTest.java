package de.ollie.dbcomp.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddPrimaryKeyCRO;
import de.ollie.dbcomp.comparator.model.actions.DropPrimaryKeyCRO;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.model.TableCMO;
import de.ollie.dbcomp.model.TypeCMO;

@ExtendWith(MockitoExtension.class)
public class PrimaryKeyComparatorTest {

	private static final String COLUMN_NAME = "ColumnName";
	private static final String COLUMN_NAME2 = "ColumnName2";
	private static final String SCHEMA_NAME = "SchemaName";
	private static final String TABLE_NAME = "TableName";
	private static final TypeCMO TYPE = TypeCMO.of(Types.INTEGER, null, null);

	@InjectMocks
	private PrimaryKeyComparator unitUnderTest;

	@Test
	void passEmptyModels_ReturnsAnEmptyList() {
		// Prepare
		DataModelCMO sourceModel = DataModelCMO.of(SchemaCMO.of(SCHEMA_NAME));
		DataModelCMO targetModel = DataModelCMO.of(SchemaCMO.of(SCHEMA_NAME));
		// Run
		List<ChangeActionCRO> returned = unitUnderTest.compareForPrimaryKeys(sourceModel, targetModel);
		// Check
		assertEquals(new ArrayList<>(), returned);
	}

	@Test
	void passModelsWithEqualTableNoPrimaryKey_ReturnsAnEmptyList() {
		// Prepare
		DataModelCMO sourceModel = DataModelCMO
				.of(SchemaCMO.of(SCHEMA_NAME, TableCMO.of(TABLE_NAME, ColumnCMO.of(COLUMN_NAME, TYPE, false, false))));
		DataModelCMO targetModel = DataModelCMO
				.of(SchemaCMO.of(SCHEMA_NAME, TableCMO.of(TABLE_NAME, ColumnCMO.of(COLUMN_NAME, TYPE, false, false))));
		// Run
		List<ChangeActionCRO> returned = unitUnderTest.compareForPrimaryKeys(sourceModel, targetModel);
		// Check
		assertEquals(new ArrayList<>(), returned);
	}

	@Test
	void passModelsWithEqualTableWithPrimaryKey_ReturnsAnEmptyList() {
		// Prepare
		DataModelCMO sourceModel = DataModelCMO
				.of(
						SchemaCMO
								.of(
										SCHEMA_NAME,
										TableCMO
												.of(TABLE_NAME, ColumnCMO.of(COLUMN_NAME, TYPE, false, false))
												.addPrimaryKeys(COLUMN_NAME)));
		DataModelCMO targetModel = DataModelCMO
				.of(
						SchemaCMO
								.of(
										SCHEMA_NAME,
										TableCMO
												.of(TABLE_NAME, ColumnCMO.of(COLUMN_NAME, TYPE, false, false))
												.addPrimaryKeys(COLUMN_NAME)));
		// Run
		List<ChangeActionCRO> returned = unitUnderTest.compareForPrimaryKeys(sourceModel, targetModel);
		// Check
		assertEquals(new ArrayList<>(), returned);
	}

	@Test
	void passModelsSourceHasPrimaryKeyTargetNot_ReturnsAListWithADrop() {
		// Prepare
		DataModelCMO sourceModel = DataModelCMO
				.of(
						SchemaCMO
								.of(
										SCHEMA_NAME,
										TableCMO
												.of(TABLE_NAME, ColumnCMO.of(COLUMN_NAME, TYPE, false, false))
												.addPrimaryKeys(COLUMN_NAME)));
		DataModelCMO targetModel = DataModelCMO
				.of(SchemaCMO.of(SCHEMA_NAME, TableCMO.of(TABLE_NAME, ColumnCMO.of(COLUMN_NAME, TYPE, false, false))));
		// Run
		List<ChangeActionCRO> returned = unitUnderTest.compareForPrimaryKeys(sourceModel, targetModel);
		// Check
		assertEquals(
				Arrays
						.asList(
								new DropPrimaryKeyCRO()
										.setPkMemberNames(Set.of(COLUMN_NAME))
										.setSchemaName(SCHEMA_NAME)
										.setTableName(TABLE_NAME)),
				returned);
	}

	@Test
	void passModelsSourceNoPrimaryKeyButTarget_ReturnsAListWithAnAdd() {
		// Prepare
		DataModelCMO sourceModel = DataModelCMO
				.of(SchemaCMO.of(SCHEMA_NAME, TableCMO.of(TABLE_NAME, ColumnCMO.of(COLUMN_NAME, TYPE, false, false))));
		DataModelCMO targetModel = DataModelCMO
				.of(
						SchemaCMO
								.of(
										SCHEMA_NAME,
										TableCMO
												.of(TABLE_NAME, ColumnCMO.of(COLUMN_NAME, TYPE, false, false))
												.addPrimaryKeys(COLUMN_NAME)));
		// Run
		List<ChangeActionCRO> returned = unitUnderTest.compareForPrimaryKeys(sourceModel, targetModel);
		// Check
		assertEquals(
				Arrays
						.asList(
								new AddPrimaryKeyCRO()
										.setPkMemberNames(Set.of(COLUMN_NAME))
										.setSchemaName(SCHEMA_NAME)
										.setTableName(TABLE_NAME)),
				returned);
	}

	@Test
	void passModelsSourceAndTargetWithDifferentPrimaryKeys_ReturnsAListWithADropAndAnAdd() {
		// Prepare
		DataModelCMO sourceModel = DataModelCMO
				.of(
						SchemaCMO
								.of(
										SCHEMA_NAME,
										TableCMO
												.of(
														TABLE_NAME,
														ColumnCMO.of(COLUMN_NAME, TYPE, false, false),
														ColumnCMO.of(COLUMN_NAME2, TYPE, false, false))
												.addPrimaryKeys(COLUMN_NAME2)));
		DataModelCMO targetModel = DataModelCMO
				.of(
						SchemaCMO
								.of(
										SCHEMA_NAME,
										TableCMO
												.of(
														TABLE_NAME,
														ColumnCMO.of(COLUMN_NAME, TYPE, false, false),
														ColumnCMO.of(COLUMN_NAME2, TYPE, false, false))
												.addPrimaryKeys(COLUMN_NAME)));
		// Run
		List<ChangeActionCRO> returned = unitUnderTest.compareForPrimaryKeys(sourceModel, targetModel);
		// Check
		assertEquals(
				Arrays
						.asList(
								new DropPrimaryKeyCRO()
										.setPkMemberNames(Set.of(COLUMN_NAME2))
										.setSchemaName(SCHEMA_NAME)
										.setTableName(TABLE_NAME),
								new AddPrimaryKeyCRO()
										.setPkMemberNames(Set.of(COLUMN_NAME))
										.setSchemaName(SCHEMA_NAME)
										.setTableName(TABLE_NAME)),
				returned);
	}

}
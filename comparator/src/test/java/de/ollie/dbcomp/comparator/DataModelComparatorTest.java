package de.ollie.dbcomp.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
public class DataModelComparatorTest {

	@InjectMocks
	private DataModelComparator unitUnderTest;

	private static DataModelCMO createModel(TableCMO... tables) {
		return createModel("", tables);
	}

	private static DataModelCMO createModel(String schemeName, TableCMO... tables) {
		return DataModelCMO.of(new SchemaCMO[] { SchemaCMO.of(schemeName, tables) });
	}

	@DisplayName("Tests of method 'compare(DataModelCMO, DataModelCMO)'.")
	@Nested
	class TestsOfMethod_compare_DataModelCMO_DataModelCMO {

		@DisplayName("Returns an empty report for empty models.")
		@Test
		void passEmptyModels_ReturnsAnEmptyReport() {
			// Prepare
			ComparisonResultCRO expected = new ComparisonResultCRO();
			// Run
			ComparisonResultCRO returned = unitUnderTest.compare(DataModelCMO.of(), DataModelCMO.of());
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Throws an exception if the source model ist passed as a null value.")
		@Test
		void passNullValueAsSourceModel_ThrowsAnException() {
			IllegalArgumentException thrown =
					assertThrows(IllegalArgumentException.class, () -> unitUnderTest.compare(null, DataModelCMO.of()));
			// Check
			assertEquals("source model cannot be null.", thrown.getMessage());
		}

		@DisplayName("Throws an exception if the target model ist passed as a null value.")
		@Test
		void passNullValueAsTargetModel_ThrowsAnException() {
			IllegalArgumentException thrown =
					assertThrows(IllegalArgumentException.class, () -> unitUnderTest.compare(DataModelCMO.of(), null));
			// Check
			assertEquals("target model cannot be null.", thrown.getMessage());
		}

		@DisplayName("Returns a result with a create table action if the source model has a table more than the target "
				+ "model.")
		@Test
		void passSourceModelWithATableMoreThanTargetModel_ReturnsAResultWithACreateTableAction() {
			// Prepare
			ComparisonResultCRO expected =
					new ComparisonResultCRO()
							.addChangeActions(
									new CreateTableChangeActionCRO().setSchemaName("public").setTableName("TABLE"));
			DataModelCMO sourceModel = createModel("public", TableCMO.of("TABLE"));
			DataModelCMO targetModel = createModel("public");
			// Run
			ComparisonResultCRO returned = unitUnderTest.compare(sourceModel, targetModel);
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a result with a create table action only if the source model has a table more than the "
				+ "target model (with columns).")
		@Test
		void passSourceModelWithATableWithColumnsMoreThanTargetModel_ReturnsAResultWithOnlyACreateTableAction() {
			// Prepare
			ComparisonResultCRO expected =
					new ComparisonResultCRO()
							.addChangeActions(
									new CreateTableChangeActionCRO()
											.setSchemaName("public")
											.setTableName("TABLE")
											.addColumns(
													new ColumnDataCRO()
															.setName("ID")
															.setSqlType("BIGINT")
															.setNullable(false)));
			DataModelCMO sourceModel =
					createModel(
							"public",
							TableCMO
									.of(
											"TABLE",
											ColumnCMO.of("ID", TypeCMO.of(Types.BIGINT, null, null), false, false)));
			DataModelCMO targetModel = createModel("public");
			// Run
			ComparisonResultCRO returned = unitUnderTest.compare(sourceModel, targetModel);
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a result with a drop table action if the source model has a table less than the target "
				+ "model.")
		@Test
		void passSourceModelWithATableLessThanTargetModel_ReturnsAResultWithADropTableAction() {
			// Prepare
			ComparisonResultCRO expected =
					new ComparisonResultCRO()
							.addChangeActions(
									new DropTableChangeActionCRO().setSchemaName("public").setTableName("TABLE"));
			DataModelCMO sourceModel = createModel("public");
			DataModelCMO targetModel = createModel("public", TableCMO.of("TABLE"));
			// Run
			ComparisonResultCRO returned = unitUnderTest.compare(sourceModel, targetModel);
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a result with an add column action if a source model table has more columns than the "
				+ "target model table.")
		@Test
		void passSourceModelWithATableWithMoreColumnsThanTheTargetModelTable_ReturnsAResultWithAnAddColumnAction() {
			// Prepare
			ComparisonResultCRO expected =
					new ComparisonResultCRO()
							.addChangeActions(
									new AddColumnChangeActionCRO()
											.setSchemaName("public")
											.setTableName("TABLE")
											.setColumnName("COLUMN_NAME")
											.setSqlType("BIGINT"));
			DataModelCMO sourceModel =
					createModel(
							"public",
							TableCMO
									.of(
											"TABLE",
											ColumnCMO
													.of(
															"COLUMN_NAME",
															TypeCMO.of(Types.BIGINT, null, null),
															false,
															null)));
			DataModelCMO targetModel = createModel("public", TableCMO.of("TABLE"));
			// Run
			ComparisonResultCRO returned = unitUnderTest.compare(sourceModel, targetModel);
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a result with a drop column action if a source model table has less columns than the "
				+ "target model table.")
		@Test
		void passSourceModelWithATableWithLessColumnsThanTheTargetModelTable_ReturnsAResultWithADropColumnAction() {
			// Prepare
			ComparisonResultCRO expected =
					new ComparisonResultCRO()
							.addChangeActions(
									new DropColumnChangeActionCRO()
											.setSchemaName("public")
											.setTableName("TABLE")
											.setColumnName("COLUMN_NAME"));
			DataModelCMO sourceModel = createModel("public", TableCMO.of("TABLE"));
			DataModelCMO targetModel =
					createModel(
							"public",
							TableCMO
									.of(
											"TABLE",
											ColumnCMO
													.of(
															"COLUMN_NAME",
															TypeCMO.of(Types.BIGINT, null, null),
															false,
															null)));
			// Run
			ComparisonResultCRO returned = unitUnderTest.compare(sourceModel, targetModel);
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a result with a modify data type action if a source model table has a column with a "
				+ "different type than the same column in the target model table.")
		@Test
		void passSourceModelTableAndTargetModelTableHaveAColumnWithSameNameAndDifferentType_ReturnsAResultWithAModifyDataTypeAction() {
			// Prepare
			ComparisonResultCRO expected =
					new ComparisonResultCRO()
							.addChangeActions(
									new ModifyDataTypeCRO()
											.setSchemaName("public")
											.setTableName("TABLE")
											.setColumnName("COLUMN_NAME")
											.setNewDataType("VARCHAR(20)"));
			TypeCMO typeVarchar = TypeCMO.of(Types.VARCHAR, 20, null);
			TypeCMO typeBigint = TypeCMO.of(Types.BIGINT, null, null);
			DataModelCMO sourceModel =
					createModel("public", TableCMO.of("TABLE", ColumnCMO.of("COLUMN_NAME", typeVarchar, false, null)));
			DataModelCMO targetModel =
					createModel("public", TableCMO.of("TABLE", ColumnCMO.of("COLUMN_NAME", typeBigint, false, null)));
			// Run
			ComparisonResultCRO returned = unitUnderTest.compare(sourceModel, targetModel);
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a result with a modify data type action if a source model table has a column with a "
				+ "different nullable flag than the same column in the target model table.")
		@Test
		void passSourceModelTableAndTargetModelTableHaveAColumnWithSameNameAndDifferentNullableFlag_ReturnsAResultWithAModifyDataTypeAction() {
			// Prepare
			ComparisonResultCRO expected =
					new ComparisonResultCRO()
							.addChangeActions(
									new ModifyNullableCRO()
											.setSchemaName("public")
											.setTableName("TABLE")
											.setColumnName("COLUMN_NAME")
											.setNewNullable(true));
			TypeCMO type = TypeCMO.of(Types.VARCHAR, 20, null);
			DataModelCMO sourceModel =
					createModel("public", TableCMO.of("TABLE", ColumnCMO.of("COLUMN_NAME", type, false, null)));
			DataModelCMO targetModel =
					createModel("public", TableCMO.of("TABLE", ColumnCMO.of("COLUMN_NAME", type, false, false)));
			// Run
			ComparisonResultCRO returned = unitUnderTest.compare(sourceModel, targetModel);
			// Check
			assertEquals(expected, returned);
		}

	}

}
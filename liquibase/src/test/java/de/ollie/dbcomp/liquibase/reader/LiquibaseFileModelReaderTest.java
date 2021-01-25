package de.ollie.dbcomp.liquibase.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.sql.Types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.ReaderResult;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.model.TableCMO;
import de.ollie.dbcomp.model.TypeCMO;
import de.ollie.dbcomp.report.ImportReport;
import de.ollie.dbcomp.report.ImportReportMessageLevel;
import de.ollie.dbcomp.util.TypeConverter;

@ExtendWith(MockitoExtension.class)
public class LiquibaseFileModelReaderTest {

	private static final String BASE_PATH = "src/test/resources/liquibase";

	private LiquibaseFileModelReader unitUnderTest;

	private static void assertReportContains(ImportReportMessageLevel level, String message,
			ImportReport importReport) {
		importReport
				.getMessages()
				.stream()
				.filter(
						reportMessage -> (reportMessage.getLevel() == level)
								&& reportMessage.getMessage().equals(message))
				.findAny()
				.ifPresentOrElse(reportMessage -> {
				}, () -> fail(String.format("no match found for level '%s' and message: %s", level, message)));
	}

	private static DataModelCMO createModel(TableCMO... tables) {
		return createModel("", tables);
	}

	private static DataModelCMO createModel(String schemeName, TableCMO... tables) {
		return DataModelCMO.of(new SchemaCMO[] { SchemaCMO.of(schemeName, tables) });
	}

	@DisplayName("Test for create table statements")
	@Nested
	class TestsForCreateTableStatements {

		@DisplayName("Should create a new table in the model (one field, no scheme).")
		@Test
		void passLiquibaseFilesForASingleTableCreation_OneFieldNoSchema_CreatesAModelWithPublicSchemeAndTheTable()
				throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of(
											"TABLE",
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.VARCHAR, 42, null), null, null),
											ColumnCMO
													.of(
															"COLUMN_NULLABLE",
															TypeCMO.of(Types.VARCHAR, 42, null),
															null,
															true),
											ColumnCMO
													.of(
															"COLUMN_NOT_NULLABLE",
															TypeCMO.of(Types.VARCHAR, 42, null),
															null,
															false)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/create"),
							new File("createSingleTableWithFieldsNoSchema.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Should create a new table in the model (one field, with scheme).")
		@Test
		void passLiquibaseFilesForASingleTableCreation_OneFieldWithSchema_CreatesAModelWithPassedSchemeAndTheTable()
				throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							"SCHEME",
							TableCMO
									.of(
											"TABLE",
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.VARCHAR, 42, null), null, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/create"),
							new File("createSingleTableWithFieldsWithSchema.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

	}

	@DisplayName("Test for add column statements")
	@Nested
	class TestsForAddColumnStatements {

		@DisplayName("Should add a field to a table.")
		@Test
		void passLiquibaseFilesForAColumnAddition_OneFieldNoSchema_CreatesAModelWithPublicSchemeAndTheTableWithTwoFields()
				throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of("TABLE")
									.addColumns(
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.VARCHAR, 42, null), null, null),
											ColumnCMO.of("COLUMN_2", TypeCMO.of(Types.BOOLEAN, null, null), null, null),
											ColumnCMO.of("COLUMN_3", TypeCMO.of(Types.NUMERIC, 10, 2), null, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/add"),
							new File("addAColumnToAnExistingTable.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

	}

	@DisplayName("Test for add auto increment statements")
	@Nested
	class TestsForAddAutoIncrementStatements {

		@DisplayName("Should add a field to a table.")
		@Test
		void passLiquibaseFilesForAnAutoIncrementAddition_CreatesAModelWithPublicSchemeAndTheTableWithAutoIncrementFields()
				throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of("TABLE")
									.addColumns(
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.BIGINT, null, null), true, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/add"),
							new File("addAutoIncrement.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Should add a field to a table (master changelog).")
		@Test
		void passLiquibaseFilesForAnAutoIncrementAdditionWithMasterChangeLog_CreatesAModelWithPublicSchemeAndTheTableWithAutoIncrementFields()
				throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of("TABLE")
									.addColumns(
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.BIGINT, null, null), true, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH),
							new File("master-changelog.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Adds a specific entry to the report if table does not exists.")
		@Test
		void passLiquibaseFilesForAnAutoIncrementAddition_TableDoesNotExists_AddsASpecificEntryToTheReport()
				throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of("TABLE")
									.addColumns(
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.BIGINT, null, null), null, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/add"),
							new File("addAutoIncrement-TableNotExisting.xml"));
			// Run
			ReaderResult returned = unitUnderTest.read();
			// Check
			assertEquals(expected, returned.getDataModel());
			assertReportContains(
					ImportReportMessageLevel.ERROR,
					"table 'TABLE_NOT_EXISTING' not found in schema: n/a",
					returned.getImportReport());
		}

		@DisplayName("Adds a specific entry to the report if column does not exists.")
		@Test
		void passLiquibaseFilesForAnAutoIncrementAddition_ColumnDoesNotExists_AddsASpecificEntryToTheReport()
				throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of("TABLE")
									.addColumns(
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.BIGINT, null, null), null, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/add"),
							new File("addAutoIncrement-ColumnNotExisting.xml"));
			// Run
			ReaderResult returned = unitUnderTest.read();
			// Check
			assertEquals(expected, returned.getDataModel());
			assertReportContains(
					ImportReportMessageLevel.ERROR,
					"column 'COLUMN_NOT_EXISTING' not found in table: TABLE",
					returned.getImportReport());
		}

	}

	@DisplayName("Test for add primary key statements")
	@Nested
	class TestsForAddPrimaryKeyStatements {

		@DisplayName("Should add a primary key to a table.")
		@Test
		void passLiquibaseFileForPrimaryKeyAddition_CreatesAModelWithTheTableAndItsPrimaryKey() throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of("TABLE")
									.addColumns(ColumnCMO.of("COLUMN", TypeCMO.of(Types.VARCHAR, 42, null), null, null))
									.addPrimaryKeys("COLUMN"));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/add"),
							new File("addAPrimaryKey.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Should add multiple primary keys to a table.")
		@Test
		void passLiquibaseFileForMultiplePrimaryKeyAddition_CreatesAModelWithTheTableAndAMultiplePrimaryKey()
				throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of("TABLE")
									.addColumns(
											ColumnCMO.of("COLUMN1", TypeCMO.of(Types.INTEGER, null, null), null, null),
											ColumnCMO.of("COLUMN2", TypeCMO.of(Types.BIGINT, null, null), null, null),
											ColumnCMO.of("COLUMN3", TypeCMO.of(Types.VARCHAR, 42, null), null, null))
									.addPrimaryKeys("COLUMN1", "COLUMN2"));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/add"),
							new File("addMultiplePrimaryKey.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

	}

	@DisplayName("Test for drop column statements")
	@Nested
	class TestsForDropColumnStatements {

		@DisplayName("Should create a new table in the model with two field and drop one of this field thereafter.")
		@Test
		void passLiquibaseFilesForASingleTableCreation_DropAField_CreatesAModelCorrectTableShape() throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of(
											"TABLE",
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.VARCHAR, 42, null), null, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/drop"),
							new File("dropColumnFromTable.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Accepts a file with not existing column name.")
		@Test
		void doesNothingPassingAFileWithNotExitingColumnName() throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of(
											"TABLE",
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.VARCHAR, 42, null), null, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/drop"),
							new File("dropColumnFromTable-NotExistingColumnName.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Accepts a file with not existing table name.")
		@Test
		void doesNothingPassingAFileWithNotExitingTableName() throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of(
											"TABLE",
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.VARCHAR, 42, null), null, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/drop"),
							new File("dropColumnFromTable-NotExistingTableName.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

	}

	@DisplayName("Test for drop primary key statements")
	@Nested
	class TestsForDropPrimaryKeyStatements {

		@DisplayName("Should drop a primary key from a table.")
		@Test
		void passLiquibaseFileForPrimaryKeyDropping_CreatesATableWithoutPrimaryKey() throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of(
											"TABLE",
											ColumnCMO.of("COLUMN1", TypeCMO.of(Types.BIGINT, null, null), null, null),
											ColumnCMO.of("COLUMN2", TypeCMO.of(Types.VARCHAR, 42, null), null, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/drop"),
							new File("dropPrimaryKeyFromTable.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Accepts a file with not existing table name.")
		@Test
		void doesNothingPassingAFileWithNotExitingTableName() throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of(
											"TABLE",
											ColumnCMO.of("COLUMN1", TypeCMO.of(Types.BIGINT, null, null), null, null),
											ColumnCMO.of("COLUMN2", TypeCMO.of(Types.VARCHAR, 42, null), null, null))
									.addPrimaryKeys("COLUMN1"));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/drop"),
							new File("dropPrimaryKeyFromTable-NotExistingTableName.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

	}

	@DisplayName("Test for drop table statements")
	@Nested
	class TestsForDropTableStatements {

		@DisplayName("Should create a new table in the model and drop it thereafter.")
		@Test
		void passLiquibaseFilesForASingleTableCreation_DropTheTable_CreatesAnEmptyModel() throws Exception {
			// Prepare
			DataModelCMO expected = DataModelCMO.of(new SchemaCMO[] { SchemaCMO.of("") });
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/drop"),
							new File("dropTable.xml"));

			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();

			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Accepts a file with not existing table name.")
		@Test
		void doesNothingPassingAFileWithNotExitingTableName() throws Exception {
			// Prepare
			DataModelCMO expected =
					createModel(
							TableCMO
									.of(
											"TABLE",
											ColumnCMO.of("COLUMN", TypeCMO.of(Types.VARCHAR, 42, null), null, null)));
			unitUnderTest =
					new LiquibaseFileModelReader(
							new TypeConverter(),
							new File(BASE_PATH + "/drop"),
							new File("dropTable-NotExistingTableName.xml"));
			// Run
			DataModelCMO returned = unitUnderTest.read().getDataModel();
			// Check
			assertEquals(expected, returned);
		}

	}

}
package de.ollie.dbcomp.liquibase.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.sql.Types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.DatamodelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.model.TableCMO;
import de.ollie.dbcomp.model.TypeCMO;
import de.ollie.dbcomp.util.TypeConverter;

@ExtendWith(MockitoExtension.class)
public class LiquibaseFileModelReaderTest {

	private static final String BASE_PATH = "src/test/resources/liquibase";

	private LiquibaseFileModelReader unitUnderTest;

	@DisplayName("Test for create table statements")
	@Nested
	class TestsForCreateTableStatements {

		@DisplayName("Should create a new table in the model (one field, no scheme).")
		@Test
		void passLiquibaseFilesForASingleTableCreation_OneFieldNoSchema_CreatesAModelWithPublicSchemeAndTheTable()
				throws Exception {
			// Prepare
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE", //
													ColumnCMO.of( //
															"COLUMN", //
															TypeCMO.of(Types.VARCHAR, 42, null), //
															null //
													) //
											)//
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/create"),
					new File("createSingleTableNoFieldNoSchema.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Should create a new table in the model (one field, with scheme).")
		@Test
		void passLiquibaseFilesForASingleTableCreation_OneFieldWithSchema_CreatesAModelWithPassedSchemeAndTheTable()
				throws Exception {
			// Prepare
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"SCHEME", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE", //
													ColumnCMO.of( //
															"COLUMN", //
															TypeCMO.of(Types.VARCHAR, 42, null), //
															null //
													) //
											)//
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/create"),
					new File("createSingleTableNoFieldWithSchema.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
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
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE") //
													.addColumns( //
															ColumnCMO.of( //
																	"COLUMN", //
																	TypeCMO.of(Types.VARCHAR, 42, null), //
																	null //
															), //
															ColumnCMO.of( //
																	"COLUMN_2", //
																	TypeCMO.of(Types.BOOLEAN, null, null), //
																	null //
															), //
															ColumnCMO.of( //
																	"COLUMN_3", //
																	TypeCMO.of(Types.NUMERIC, 10, 2), //
																	null //
															) //
													) //
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/add"),
					new File("addAColumnToAnExistingTable.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
			// Check
			assertEquals(expected, returned);
		}

	}

	@DisplayName("Test for add primary key statements")
	@Nested
	class TestsForPrimaryKeyStatements {

		@DisplayName("Should add a primary key to a table.")
		@Test
		void passLiquibaseFileForPrimaryKeyAddition_CreatesAModelWithTheTableAndItsPrimaryKey() throws Exception {
			// Prepare
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE") //
													.addColumns( //
															ColumnCMO.of( //
																	"COLUMN", //
																	TypeCMO.of(Types.VARCHAR, 42, null), //
																	null //
															) //
													) //
													.addPrimaryKeys("COLUMN") //
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/add"),
					new File("addAPrimaryKey.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Should add multiple primary keys to a table.")
		@Test
		void passLiquibaseFileForMultiplePrimaryKeyAddition_CreatesAModelWithTheTableAndAMultiplePrimaryKey()
				throws Exception {
			// Prepare
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE") //
													.addColumns( //
															ColumnCMO.of( //
																	"COLUMN1", //
																	TypeCMO.of(Types.INTEGER, null, null), //
																	null //
															), //
															ColumnCMO.of( //
																	"COLUMN2", //
																	TypeCMO.of(Types.BIGINT, null, null), //
																	null //
															), //
															ColumnCMO.of( //
																	"COLUMN3", //
																	TypeCMO.of(Types.VARCHAR, 42, null), //
																	null //
															) //
													) //
													.addPrimaryKeys("COLUMN1", "COLUMN2") //
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/add"),
					new File("addMultiplePrimaryKey.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
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
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE", //
													ColumnCMO.of( //
															"COLUMN", //
															TypeCMO.of(Types.VARCHAR, 42, null), //
															null //
													) //
											)//
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/drop"),
					new File("dropColumnFromTable.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Accepts a file with not existing column name.")
		@Test
		void doesNothingPassingAFileWithNotExitingColumnName() throws Exception {
			// Prepare
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE", //
													ColumnCMO.of( //
															"COLUMN", //
															TypeCMO.of(Types.VARCHAR, 42, null), //
															null //
													) //
											)//
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/drop"),
					new File("dropColumnFromTable-NotExistingColumnName.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Accepts a file with not existing table name.")
		@Test
		void doesNothingPassingAFileWithNotExitingTableName() throws Exception {
			// Prepare
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE", //
													ColumnCMO.of( //
															"COLUMN", //
															TypeCMO.of(Types.VARCHAR, 42, null), //
															null //
													) //
											)//
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/drop"),
					new File("dropColumnFromTable-NotExistingTableName.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
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
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE", //
													ColumnCMO.of( //
															"COLUMN1", //
															TypeCMO.of(Types.BIGINT, null, null), //
															null //
													), //
													ColumnCMO.of( //
															"COLUMN2", //
															TypeCMO.of(Types.VARCHAR, 42, null), //
															null //
													) //
											)//
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/drop"),
					new File("dropPrimaryKeyFromTable.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Accepts a file with not existing table name.")
		@Test
		void doesNothingPassingAFileWithNotExitingTableName() throws Exception {
			// Prepare
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE", //
													ColumnCMO.of( //
															"COLUMN1", //
															TypeCMO.of(Types.BIGINT, null, null), //
															null //
													), //
													ColumnCMO.of( //
															"COLUMN2", //
															TypeCMO.of(Types.VARCHAR, 42, null), //
															null //
													) //
											).addPrimaryKeys( //
													"COLUMN1" //
											) //
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/drop"),
					new File("dropPrimaryKeyFromTable-NotExistingTableName.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
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
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"" //
							) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/drop"),
					new File("dropTable.xml"));

			// Run
			DatamodelCMO returned = unitUnderTest.readModel();

			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Accepts a file with not existing table name.")
		@Test
		void doesNothingPassingAFileWithNotExitingTableName() throws Exception {
			// Prepare
			DatamodelCMO expected = DatamodelCMO.of( //
					new SchemaCMO[] { //
							SchemaCMO.of( //
									"", //
									new TableCMO[] { //
											TableCMO.of( //
													"TABLE", //
													ColumnCMO.of( //
															"COLUMN", //
															TypeCMO.of(Types.VARCHAR, 42, null), //
															null //
													) //
											)//
									}) //
					});
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH + "/drop"),
					new File("dropTable-NotExistingTableName.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
			// Check
			assertEquals(expected, returned);
		}

	}

}
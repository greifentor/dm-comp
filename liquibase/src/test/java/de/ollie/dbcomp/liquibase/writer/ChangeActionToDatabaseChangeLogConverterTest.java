package de.ollie.dbcomp.liquibase.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddColumnChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddForeignKeyCRO;
import de.ollie.dbcomp.comparator.model.actions.AddPrimaryKeyCRO;
import de.ollie.dbcomp.comparator.model.actions.ColumnDataCRO;
import de.ollie.dbcomp.comparator.model.actions.CreateTableChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropColumnChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropPrimaryKeyCRO;
import de.ollie.dbcomp.comparator.model.actions.DropTableChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ForeignKeyMemberCRO;
import de.ollie.dbcomp.comparator.model.actions.ModifyDataTypeCRO;
import de.ollie.dbcomp.comparator.model.actions.ModifyNullableCRO;
import de.ollie.dbcomp.liquibase.writer.processors.ChangeProcessorConfiguration;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.serializer.core.xml.XMLChangeLogSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@ExtendWith(MockitoExtension.class)
public class ChangeActionToDatabaseChangeLogConverterTest {

	private static final String COLUMN_NAME = "column";
	private static final String SQL_TYPE_NAME = "sql type";
	private static final String TABLE_NAME = "table";

	private static final String XML_HEADER = "<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?>\n" //
			+ "<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\" xmlns:ext=\"http://www"
			+ ".liquibase"
			+ ".org/xml/ns/dbchangelog-ext\" xmlns:pro=\"http://www.liquibase.org/xml/ns/pro\" xmlns:xsi=\"http://www"
			+ ".w3"
			+ ".org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog-ext "
			+ "http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd http://www.liquibase.org/xml/ns/pro "
			+ "http://www.liquibase.org/xml/ns/pro/liquibase-pro-4.1.xsd http://www.liquibase.org/xml/ns/dbchangelog "
			+ "http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.1.xsd\">\n";

	@Spy
	private ChangeProcessorConfiguration configuration;

	@InjectMocks
	private ChangeActionToDatabaseChangeLogConverter unitUnderTest;

	private static String databaseChangeLogToString(DatabaseChangeLog databaseChangeLog) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(baos);
		XMLChangeLogSerializer changeLogSerializer = new XMLChangeLogSerializer();
		changeLogSerializer.write(databaseChangeLog.getChangeSets(), printStream);
		return baos.toString();
	}

	@Accessors(chain = true)
	@AllArgsConstructor
	@Data
	static class ColumnData {

		private String name;
		private String sqlType;

	}

	@DisplayName("Test of method 'convert(List<ChangeAction>, ChangeProcessorConfiguration)'.")
	@Nested
	class TestsOfMethod_convert_List_ChangeAction_ChangeProcessorConfiguration {

		@DisplayName("Returns a null value as action list if a null value is passed.")
		@Test
		void passNullValueAsActionList_ReturnsANullValue() {
			assertNull(unitUnderTest.convert(null, configuration));
		}

		@DisplayName("Returns a null value if a null value is passed.")
		@Test
		void passNullValueAsConfiguration_ThrowsAnException() {
			assertThrows(IllegalArgumentException.class, () -> unitUnderTest.convert(Arrays.asList(), null));
		}

		@DisplayName("Returns an empty DatabaseChangeLog if an empty list is passed.")
		@Test
		void passAnEmptyList_ReturnsAEmptyDatabaseChangeLog() throws Exception {
			// Prepare
			String expected = databaseChangeLogToString(new DatabaseChangeLog("change-log.xml"))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(new ArrayList<>(), configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with one ChangeSet and some CreateTableChanges if the passed list has "
				+ "CreateTableChangeActionCRO's only.")
		@Test
		void passAListWithCreateTableChangeActionCROsOnly_ReturnsADatabaseChangeLogWithOneChangeSetAndAllPassedCreateTableChanges()
				throws Exception {
			// Prepare
			String expected = XML_HEADER + "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\" runOnChange=\"true\">\n"
					+ "        <createTable tableName=\"table1\">\n"
					+ "            <column name=\"column\" type=\"sql type\">\n"
					+ "                <constraints nullable=\"false\"/>\n"
					+ "            </column>\n"
					+ "        </createTable>\n"
					+ "        <createTable tableName=\"table2\"/>\n"
					+ "    </changeSet>\n"
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList(
							new CreateTableChangeActionCRO()
									.setTableName(TABLE_NAME + 1)
									.addColumns(
											new ColumnDataCRO()
													.setName(COLUMN_NAME)
													.setSqlType(SQL_TYPE_NAME)
													.setNullable(false)),
							new CreateTableChangeActionCRO().setTableName(TABLE_NAME + 2));
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with one ChangeSet and a CreateTableChanges with a correct primary key "
				+ "settings.")
		@Test
		void passAListWithCreateTableChangeActionCROsOnly_ReturnsADatabaseChangeLogWithOneChangeSetAndCreateTableAndAddPrimaryKeyChange()
				throws Exception {
			// Prepare
			String expected = XML_HEADER + "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" " //
					+ "objectQuotingStrategy=\"LEGACY\" runOnChange=\"true\">\n" //
					+ "        <createTable tableName=\""
					+ TABLE_NAME
					+ "\">\n" //
					+ "            <column name=\""
					+ COLUMN_NAME
					+ "\" type=\"sql type\">\n" //
					+ "                <constraints nullable=\"false\"/>\n" //
					+ "            </column>\n" //
					+ "        </createTable>\n" //
					+ "        <addPrimaryKey columnNames=\""
					+ COLUMN_NAME
					+ "\" tableName=\""
					+ TABLE_NAME
					+ "\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList(
							new CreateTableChangeActionCRO()
									.setTableName(TABLE_NAME)
									.addColumns(
											new ColumnDataCRO()
													.setName(COLUMN_NAME)
													.setSqlType(SQL_TYPE_NAME)
													.setNullable(false))
									.setPrimaryKeyMemberNames(Set.of(COLUMN_NAME)));
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with one ChangeSet and a CreateTableChanges with a correct foreign key "
				+ "settings.")
		@Test
		void passAListWithCreateTableChangeActionCROsOnly_ReturnsADatabaseChangeLogWithOneChangeSetAndCreateTableAndAddForeignKeyChange()
				throws Exception {
			// Prepare
			String expected = XML_HEADER + "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" " //
					+ "objectQuotingStrategy=\"LEGACY\" runOnChange=\"true\">\n" //
					+ "        <createTable schemaName=\"public\" tableName=\""
					+ TABLE_NAME
					+ "\">\n" //
					+ "            <column name=\""
					+ COLUMN_NAME
					+ "\" type=\"sql type\">\n" //
					+ "                <constraints nullable=\"false\"/>\n" //
					+ "            </column>\n" //
					+ "        </createTable>\n" //
					+ "        <addForeignKeyConstraint baseColumnNames=\"base_column\" baseTableName=\"table\" "
					+ "baseTableSchemaName=\"public\" constraintName=\"fk_x_to_y\" "
					+ "referencedColumnNames=\"referenced_column\" referencedTableName=\"referenced_table\" "
					+ "referencedTableSchemaName=\"public\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			Map<String, List<ForeignKeyMemberCRO>> foreignKey = new HashMap<>();
			foreignKey
					.put(
							"fk_x_to_y",
							Arrays
									.asList(
											new ForeignKeyMemberCRO()
													.setBaseColumnName("base_column")
													.setBaseTableName("base_table")
													.setReferencedColumnName("referenced_column")
													.setReferencedTableName("referenced_table")));
			List<ChangeActionCRO> actions = Arrays
					.asList(
							new CreateTableChangeActionCRO()
									.setTableName(TABLE_NAME)
									.setSchemaName("public")
									.addColumns(
											new ColumnDataCRO()
													.setName(COLUMN_NAME)
													.setSqlType(SQL_TYPE_NAME)
													.setNullable(false))
									.setForeignkeys(foreignKey));
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with one ChangeSet and a CreateTableChanges with a correct foreign key "
				+ "settings (no scheme name).")
		@Test
		void passAListWithCreateTableChangeActionCROsOnly_ReturnsADatabaseChangeLogWithOneChangeSetAndCreateTableAndAddForeignKeyChange_NoSchemeName()
				throws Exception {
			// Prepare
			String expected = XML_HEADER + "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" " //
					+ "objectQuotingStrategy=\"LEGACY\" runOnChange=\"true\">\n" //
					+ "        <createTable tableName=\""
					+ TABLE_NAME
					+ "\">\n" //
					+ "            <column name=\""
					+ COLUMN_NAME
					+ "\" type=\"sql type\">\n" //
					+ "                <constraints nullable=\"false\"/>\n" //
					+ "            </column>\n" //
					+ "        </createTable>\n" //
					+ "        <addForeignKeyConstraint baseColumnNames=\"base_column\" baseTableName=\"table\" "
					+ "constraintName=\"fk_x_to_y\" referencedColumnNames=\"referenced_column\" "
					+ "referencedTableName=\"referenced_table\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			Map<String, List<ForeignKeyMemberCRO>> foreignKey = new HashMap<>();
			foreignKey
					.put(
							"fk_x_to_y",
							Arrays
									.asList(
											new ForeignKeyMemberCRO()
													.setBaseColumnName("base_column")
													.setBaseTableName("base_table")
													.setReferencedColumnName("referenced_column")
													.setReferencedTableName("referenced_table")));
			List<ChangeActionCRO> actions = Arrays
					.asList(
							new CreateTableChangeActionCRO()
									.setTableName(TABLE_NAME)
									.setSchemaName("public")
									.addColumns(
											new ColumnDataCRO()
													.setName(COLUMN_NAME)
													.setSqlType(SQL_TYPE_NAME)
													.setNullable(false))
									.setForeignkeys(foreignKey));
			// Run
			String returned =
					databaseChangeLogToString(unitUnderTest.convert(actions, configuration.setSchemeNameToSet(false)))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with one ChangeSet and some DropTableChanges if the passed list has "
				+ "DropTableChangeActionCRO's only.")
		@Test
		void passAListWithDropTableChangeActionCROsOnly_ReturnsADatabaseChangeLogWithOneChangeSetAndAllPassedDropTableChanges()
				throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\""
					+ " "
					+ "runOnChange=\"true\">\n"
					//
					+ "        <dropTable tableName=\"table1\"/>\n" //
					+ "        <dropTable tableName=\"table2\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList( //
							new DropTableChangeActionCRO().setTableName(TABLE_NAME + 1), //
							new DropTableChangeActionCRO().setTableName(TABLE_NAME + 2) //
					);
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with one ChangeSet and some CreateTableChanges and "
				+ "AddColumnChanges.")
		@Test
		void passAListWithAnAddColumnChange_ReturnsADatabaseChangeLogsWithTheCorrectChanges() throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\" runOnChange=\"true\">\n"
					+ "        <createTable tableName=\"table1\">\n"
					+ "            <column name=\"column\" type=\"sql type\"/>\n"
					+ "        </createTable>\n"
					+ "        <addColumn tableName=\"table2\">\n"
					+ "            <column name=\"column1\" type=\"BIGINT\"/>\n"
					+ "        </addColumn>\n"
					+ "    </changeSet>\n"
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList( //
							new CreateTableChangeActionCRO() //
									.setTableName(TABLE_NAME + 1) //
									.addColumns(new ColumnDataCRO().setName(COLUMN_NAME).setSqlType(SQL_TYPE_NAME)), //
							new AddColumnChangeActionCRO() //
									.setTableName(TABLE_NAME + 2) //
									.setColumnName(COLUMN_NAME + 1) //
									.setSqlType("BIGINT") //
					);
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with a DropColumnChange.")
		@Test
		void passAListWithADropColumnChange_ReturnsADatabaseChangeLogsWithTheCorrectChanges() throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\""
					+ " "
					+ "runOnChange=\"true\">\n"
					//
					+ "        <createTable tableName=\"table1\">\n" //
					+ "            <column name=\"column\" type=\"sql type\"/>\n" //
					+ "        </createTable>\n" //
					+ "        <dropColumn tableName=\"table2\">\n" //
					+ "            <column name=\"column1\"/>\n" //
					+ "        </dropColumn>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList( //
							new CreateTableChangeActionCRO() //
									.setTableName(TABLE_NAME + 1) //
									.addColumns(new ColumnDataCRO().setName(COLUMN_NAME).setSqlType(SQL_TYPE_NAME)), //
							new DropColumnChangeActionCRO() //
									.setTableName(TABLE_NAME + 2) //
									.setColumnName(COLUMN_NAME + 1) //
					);
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with a ModifyDataTypeChange.")
		@Test
		void passAListWithAModifyDataChangeChange_ReturnsADatabaseChangeLogsWithTheCorrectChanges() throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\""
					+ " "
					+ "runOnChange=\"true\">\n"
					//
					+ "        <modifyDataType columnName=\"ID\" newDataType=\"BIGINT\" schemaName=\"public\" "
					+ "tableName=\"table\"/>\n"
					//
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList( //
							new ModifyDataTypeCRO() //
									.setTableName(TABLE_NAME) //
									.setSchemaName("public") //
									.setColumnName("ID") //
									.setNewDataType("BIGINT") //
					);
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with a DropNotNullConstraintChange.")
		@Test
		void passAListWithADropNotNullConstraintChangeChange_ReturnsADatabaseChangeLogsWithTheCorrectChanges()
				throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\""
					+ " "
					+ "runOnChange=\"true\">\n"
					//
					+ "        <dropNotNullConstraint columnName=\"ID\" schemaName=\"public\" tableName=\"table\"/>\n"
					//
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList(
							new ModifyNullableCRO()
									.setTableName(TABLE_NAME)
									.setSchemaName("public")
									.setColumnName("ID")
									.setNewNullable(true));
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with a DropNotNullConstraintChange.")
		@Test
		void passAListWithAnAddNotNullConstraintChangeChange_ReturnsADatabaseChangeLogsWithTheCorrectChanges()
				throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\""
					+ " "
					+ "runOnChange=\"true\">\n"
					//
					+ "        <addNotNullConstraint columnName=\"ID\" schemaName=\"public\" "
					+ "tableName=\"table\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList(
							new ModifyNullableCRO()
									.setTableName(TABLE_NAME)
									.setSchemaName("public")
									.setColumnName("ID")
									.setNewNullable(false));
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with a AddPrimaryKeyChange.")
		@Test
		void passAListWithAnAddPrimaryKeyChangeChange_ReturnsADatabaseChangeLogsWithTheCorrectChanges()
				throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\""
					+ " "
					+ "runOnChange=\"true\">\n"
					//
					+ "        <addPrimaryKey columnNames=\"ID\" schemaName=\"public\" tableName=\"table\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList(
							new AddPrimaryKeyCRO()
									.setTableName(TABLE_NAME)
									.setSchemaName("public")
									.setPkMemberNames(Set.of("ID")));
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with a DropPrimaryKeyChange.")
		@Test
		void passAListWithAnDropPrimaryKeyChangeChange_ReturnsADatabaseChangeLogsWithTheCorrectChanges()
				throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\""
					+ " "
					+ "runOnChange=\"true\">\n"
					//
					+ "        <dropPrimaryKey schemaName=\"public\" tableName=\"table\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions =
					Arrays.asList(new DropPrimaryKeyCRO().setTableName(TABLE_NAME).setSchemaName("public"));
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with a DropPrimaryKeyChange (no scheme names).")
		@Test
		void passAListWithAnDropPrimaryKeyChangeChange_ReturnsADatabaseChangeLogsWithTheCorrectChangesNoSchemNames()
				throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\""
					+ " "
					+ "runOnChange=\"true\">\n"
					//
					+ "        <dropPrimaryKey tableName=\"table\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions =
					Arrays.asList(new DropPrimaryKeyCRO().setTableName(TABLE_NAME).setSchemaName("public"));
			configuration.setSchemeNameToSet(false);
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with a AddForeignKeyChange.")
		@Test
		void passAListWithAnAddForeignKeyChangeChange_ReturnsADatabaseChangeLogsWithTheCorrectChanges()
				throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\""
					+ " "
					+ "runOnChange=\"true\">\n"
					//
					+ "        <addForeignKeyConstraint baseColumnNames=\"BASE_COLUMN\" baseTableName=\"BASE_TABLE\" "
					+ "baseTableSchemaName=\"public\" referencedColumnNames=\"REF_COLUMN\" "
					+ "referencedTableName=\"REF_TABLE\" referencedTableSchemaName=\"public\"/>\n"
					//
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList(
							new AddForeignKeyCRO()
									.setTableName("BASE_TABLE")
									.setSchemaName("public")
									.addMembers(
											new ForeignKeyMemberCRO()
													.setBaseColumnName("BASE_COLUMN")
													.setBaseTableName("BASE_TABLE")
													.setReferencedColumnName("REF_COLUMN")
													.setReferencedTableName("REF_TABLE")));
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with a AddForeignKeyChange (no scheme name).")
		@Test
		void passAListWithAnAddForeignKeyChangeChange_ReturnsADatabaseChangeLogsWithTheCorrectChangesWithNoSchemeNames()
				throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" "
					+ "objectQuotingStrategy=\"LEGACY\""
					+ " "
					+ "runOnChange=\"true\">\n"
					//
					+ "        <addForeignKeyConstraint baseColumnNames=\"BASE_COLUMN\" baseTableName=\"BASE_TABLE\" "
					+ "referencedColumnNames=\"REF_COLUMN\" referencedTableName=\"REF_TABLE\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays
					.asList(
							new AddForeignKeyCRO()
									.setTableName("BASE_TABLE")
									.setSchemaName("public")
									.addMembers(
											new ForeignKeyMemberCRO()
													.setBaseColumnName("BASE_COLUMN")
													.setBaseTableName("BASE_TABLE")
													.setReferencedColumnName("REF_COLUMN")
													.setReferencedTableName("REF_TABLE")));
			configuration.setSchemeNameToSet(false);
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions, configuration))
					.replace("\r\n", "\n")
					.replace("\r", "\n");
			// Check
			assertEquals(expected, returned);
		}

	}

}
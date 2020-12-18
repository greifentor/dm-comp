package de.ollie.dbcomp.liquibase.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddColumnChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ColumnDataCRO;
import de.ollie.dbcomp.comparator.model.actions.CreateTableChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropColumnChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropTableChangeActionCRO;
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
			+ "<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\" xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\" xmlns:pro=\"http://www.liquibase.org/xml/ns/pro\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd http://www.liquibase.org/xml/ns/pro http://www.liquibase.org/xml/ns/pro/liquibase-pro-4.1.xsd http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.1.xsd\">\n";

	@Accessors(chain = true)
	@AllArgsConstructor
	@Data
	static class ColumnData {
		private String name;
		private String sqlType;
	}

	private static String databaseChangeLogToString(DatabaseChangeLog databaseChangeLog) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(baos);
		XMLChangeLogSerializer changeLogSerializer = new XMLChangeLogSerializer();
		changeLogSerializer.write(databaseChangeLog.getChangeSets(), printStream);
		return baos.toString();
	}

	@InjectMocks
	private ChangeActionToDatabaseChangeLogConverter unitUnderTest;

	@DisplayName("Test of method 'convert(List<ChangeAction>)'.")
	@Nested
	class TestsOfMethod_convert_List_ChangeAction {

		@DisplayName("Returns a null value if a null value is passed.")
		@Test
		void passNullValue_ReturnsANullValue() {
			assertNull(unitUnderTest.convert(null));
		}

		@DisplayName("Returns an empty DatabaseChangeLog if an empty list is passed.")
		@Test
		void passAnEmptyList_ReturnsAEmptyDatabaseChangeLog() throws Exception {
			// Prepare
			String expected = databaseChangeLogToString(new DatabaseChangeLog("change-log.xml")).replace("\r", "");
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(new ArrayList<>())).replace("\r", "");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with one ChangeSet and some CreateTableChanges if the passed list has "
				+ "CreateTableChangeActionCRO's only.")
		@Test
		void passAListWithCreateTableChangeActionCROsOnly_ReturnsADatabaseChangeLogWithOneChangeSetAndAllPassedCreateTableChanges()
				throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" objectQuotingStrategy=\"LEGACY\" runOnChange=\"true\">\n" //
					+ "        <createTable tableName=\"table1\">\n" //
					+ "            <column name=\"column\" type=\"sql type\"/>\n" //
					+ "        </createTable>\n" //
					+ "        <createTable tableName=\"table2\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays.asList( //
					new CreateTableChangeActionCRO().setTableName(TABLE_NAME + 1) //
							.addColumns(new ColumnDataCRO().setName(COLUMN_NAME).setSqlType(SQL_TYPE_NAME)), //
					new CreateTableChangeActionCRO().setTableName(TABLE_NAME + 2) //
			);
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions)).replace("\r", "");
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
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" objectQuotingStrategy=\"LEGACY\" runOnChange=\"true\">\n" //
					+ "        <dropTable tableName=\"table1\"/>\n" //
					+ "        <dropTable tableName=\"table2\"/>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays.asList( //
					new DropTableChangeActionCRO().setTableName(TABLE_NAME + 1), //
					new DropTableChangeActionCRO().setTableName(TABLE_NAME + 2) //
			);
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions)).replace("\r", "");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with one ChangeSet and some CreateTableChanges and AddColumnChanges.")
		@Test
		void passAListWithAnAddColumnChange_ReturnsADatabaseChangeLogsWithTheCorrectChanges() throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" objectQuotingStrategy=\"LEGACY\" runOnChange=\"true\">\n" //
					+ "        <createTable tableName=\"table1\">\n" //
					+ "            <column name=\"column\" type=\"sql type\"/>\n" //
					+ "        </createTable>\n" //
					+ "        <addColumn tableName=\"table2\">\n" //
					+ "            <column name=\"column1\" type=\"BIGINT\"/>\n" //
					+ "        </addColumn>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays.asList( //
					new CreateTableChangeActionCRO() //
							.setTableName(TABLE_NAME + 1) //
							.addColumns(new ColumnDataCRO().setName(COLUMN_NAME).setSqlType(SQL_TYPE_NAME)), //
					new AddColumnChangeActionCRO() //
							.setTableName(TABLE_NAME + 2) //
							.setColumnName(COLUMN_NAME + 1) //
							.setSqlType("BIGINT") //
			);
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions)).replace("\r", "");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Returns a DatabaseChangeLog with a DropColumnChange.")
		@Test
		void passAListWithADropColumnChange_ReturnsADatabaseChangeLogsWithTheCorrectChanges() throws Exception {
			// Prepare
			String expected = XML_HEADER //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" objectQuotingStrategy=\"LEGACY\" runOnChange=\"true\">\n" //
					+ "        <createTable tableName=\"table1\">\n" //
					+ "            <column name=\"column\" type=\"sql type\"/>\n" //
					+ "        </createTable>\n" //
					+ "        <dropColumn tableName=\"table2\">\n" //
					+ "            <column name=\"column1\"/>\n" //
					+ "        </dropColumn>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			List<ChangeActionCRO> actions = Arrays.asList( //
					new CreateTableChangeActionCRO() //
							.setTableName(TABLE_NAME + 1) //
							.addColumns(new ColumnDataCRO().setName(COLUMN_NAME).setSqlType(SQL_TYPE_NAME)), //
					new DropColumnChangeActionCRO() //
							.setTableName(TABLE_NAME + 2) //
							.setColumnName(COLUMN_NAME + 1) //
			);
			// Run
			String returned = databaseChangeLogToString(unitUnderTest.convert(actions)).replace("\r", "");
			// Check
			assertEquals(expected, returned);
		}

	}

}
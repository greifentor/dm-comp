package de.ollie.dbcomp.liquibase.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.ColumnDataCRO;
import de.ollie.dbcomp.comparator.model.actions.CreateTableChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropTableChangeActionCRO;
import liquibase.change.Change;
import liquibase.change.ColumnConfig;
import liquibase.change.core.CreateTableChange;
import liquibase.change.core.DropTableChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@ExtendWith(MockitoExtension.class)
public class ChangeActionToDatabaseChangeLogConverterTest {

	private static final String COLUMN_NAME = "column";
	private static final String SQL_TYPE_NAME = "sql type";
	private static final String TABLE_NAME = "table";

	@Accessors(chain = true)
	@AllArgsConstructor
	@Data
	static class ColumnData {
		private String name;
		private String sqlType;
	}

	@InjectMocks
	private ChangeActionToDatabaseChangeLogConverter unitUnderTest;

	private static void assertDatabaseChangeLogEquals(DatabaseChangeLog expected, DatabaseChangeLog returned,
			Comparator<Change> comparator) {
		assertEquals(expected, returned);
		assertEquals(expected.getChangeSets().size(), returned.getChangeSets().size());
		for (int i = 0, leni = expected.getChangeSets().size(); i < leni; i++) {
			assertEquals(expected.getChangeSets().get(i), returned.getChangeSets().get(i));
			for (int j = 0, lenj = expected.getChangeSets().get(i).getChanges().size(); j < lenj; j++) {
				assertEquals(0, comparator.compare(expected.getChangeSets().get(i).getChanges().get(j),
						returned.getChangeSets().get(i).getChanges().get(j)));
			}
		}
	}

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
		void passAnEmptyList_ReturnsAEmptyDatabaseChangeLog() {
			// Prepare
			DatabaseChangeLog expected = new DatabaseChangeLog("change-log.xml");
			// Run
			DatabaseChangeLog returned = unitUnderTest.convert(new ArrayList<>());
			// Check
			assertDatabaseChangeLogEquals(expected, returned, (c0, c1) -> 0);
		}

		@DisplayName("Returns a DatabaseChangeLog with one ChangeSet and some CreateTableChanges if the passed list has "
				+ "CreateTableChangeActionCRO's only.")
		@Test
		void passAListWithCreateTableChangeActionCROsOnly_ReturnsADatabaseChangeLogWithOneChangeSetAndAllPassedCreateTableChanges() {
			// Prepare
			List<ChangeActionCRO> actions = Arrays.asList( //
					new CreateTableChangeActionCRO().setTableName(TABLE_NAME + 1) //
							.addColumns(new ColumnDataCRO().setName(COLUMN_NAME).setSqlType(SQL_TYPE_NAME)), //
					new CreateTableChangeActionCRO().setTableName(TABLE_NAME + 2) //
			);
			DatabaseChangeLog expected = new DatabaseChangeLog("change-log.xml"); //
			ChangeSet changeSet = new ChangeSet("ADD-CHANGE-SET-ID-HERE", "dm-comp", false, true, null, null, null,
					expected);
			changeSet.addChange(
					createCreateTableChange(TABLE_NAME + 1, null, new ColumnData(COLUMN_NAME, SQL_TYPE_NAME)));
			changeSet.addChange(createCreateTableChange(TABLE_NAME + 2, null));
			expected.addChangeSet(changeSet);
			// Run
			DatabaseChangeLog returned = unitUnderTest.convert(actions);
			// Check
			assertDatabaseChangeLogEquals(expected, returned, (c0, c1) -> {
				CreateTableChange ctc0 = (CreateTableChange) c0;
				CreateTableChange ctc1 = (CreateTableChange) c1;
				return Objects.compare(ctc0.getSchemaName(), ctc1.getSchemaName(), (o0, o1) -> o0.compareTo(o1))
						+ Objects.compare(ctc0.getTableName(), ctc1.getTableName(), (o0, o1) -> o0.compareTo(o1))
						+ compareColumnConfigs(ctc0.getColumns(), ctc1.getColumns());
			});
		}

		private CreateTableChange createCreateTableChange(String tableName, String schemaName, ColumnData... columns) {
			CreateTableChange change = new CreateTableChange();
			change.setSchemaName(schemaName);
			change.setTableName(tableName);
			for (ColumnData column : columns) {
				ColumnConfig columnConfig = new ColumnConfig();
				columnConfig.setName(column.getName());
				columnConfig.setType(column.getSqlType());
				change.addColumn(columnConfig);
			}
			return change;
		}

		private int compareColumnConfigs(List<ColumnConfig> columns0, List<ColumnConfig> columns1) {
			if (columns0.size() != columns1.size()) {
				return 1;
			}
			for (int i = 0, leni = columns0.size(); i < leni; i++) {
				ColumnConfig cc0 = columns0.get(i);
				ColumnConfig cc1 = columns1.get(i);
				if (!cc0.getName().equals(cc1.getName()) //
						|| !cc0.getType().equals(cc1.getType()) //
				) {
					return 1;
				}
			}
			return 0;
		}

		@DisplayName("Returns a DatabaseChangeLog with one ChangeSet and some DropTableChanges if the passed list has "
				+ "DropTableChangeActionCRO's only.")
		@Test
		void passAListWithDropTableChangeActionCROsOnly_ReturnsADatabaseChangeLogWithOneChangeSetAndAllPassedDropTableChanges() {
			// Prepare
			List<ChangeActionCRO> actions = Arrays.asList( //
					new DropTableChangeActionCRO().setTableName(TABLE_NAME + 1), //
					new DropTableChangeActionCRO().setTableName(TABLE_NAME + 2) //
			);
			DatabaseChangeLog expected = new DatabaseChangeLog("change-log.xml"); //
			ChangeSet changeSet = new ChangeSet("ADD-CHANGE-SET-ID-HERE", "dm-comp", false, true, null, null, null,
					expected);
			changeSet.addChange(createDropTableChange(TABLE_NAME + 1, null));
			changeSet.addChange(createDropTableChange(TABLE_NAME + 2, null));
			expected.addChangeSet(changeSet);
			// Run
			DatabaseChangeLog returned = unitUnderTest.convert(actions);
			// Check
			assertDatabaseChangeLogEquals(expected, returned, (c0, c1) -> {
				DropTableChange dtc0 = (DropTableChange) c0;
				DropTableChange dtc1 = (DropTableChange) c1;
				return Objects.compare(dtc0.getSchemaName(), dtc1.getSchemaName(), (o0, o1) -> o0.compareTo(o1))
						+ Objects.compare(dtc0.getTableName(), dtc1.getTableName(), (o0, o1) -> o0.compareTo(o1));
			});
		}

		private DropTableChange createDropTableChange(String tableName, String schemaName) {
			DropTableChange change = new DropTableChange();
			change.setSchemaName(schemaName);
			change.setTableName(tableName);
			return change;
		}

	}

}
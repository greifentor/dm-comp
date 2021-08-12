package de.ollie.dbcomp.comparator;

import de.ollie.dbcomp.comparator.model.actions.AddAutoIncrementChangeActionCRO;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.TableCMO;
import de.ollie.dbcomp.model.TypeCMO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AutoIncrementHelperTest {

	private static final String COLUMN_NAME = "column";
	private static final String SCHEMA_NAME = "schema";
	private static final String TABLE_NAME = "table";

	@InjectMocks
	private AutoIncrementHelper unitUnderTest;

	private TableCMO createTable(ColumnCMO... columns) {
		return TableCMO.of(TABLE_NAME, columns);
	}

	private ColumnCMO createColumn(Boolean autoIncrement) {
		return createColumn(COLUMN_NAME, autoIncrement);
	}

	private ColumnCMO createColumn(String name, Boolean autoIncrement) {
		return ColumnCMO.of(name, TypeCMO.of(Types.VARCHAR, 42, 0), autoIncrement, true);
	}

	@Nested
	class TestsOfMethod_getNecessaryAddAutoIncrementChangeActionsForCreateTable_TableCMO_String {

		@Test
		void passANullValueAsTable_throwsAnException() {
			assertThrows(
					IllegalArgumentException.class,
					() -> unitUnderTest.getNecessaryAddAutoIncrementChangeActionsForCreateTable(null, SCHEMA_NAME));
		}

		@Test
		void passATableWithoutAAutoIncrementField_returnsAnEmptyArray() {
			assertEquals(
					0,
					unitUnderTest.getNecessaryAddAutoIncrementChangeActionsForCreateTable(
							createTable(createColumn(false)),
							"schema").length);
		}

		@Test
		void passATableWithAnAutoIncrementField_returnsTheCorrectArray() {
			// Prepare
			AddAutoIncrementChangeActionCRO[] expected = new AddAutoIncrementChangeActionCRO[]{
					new AddAutoIncrementChangeActionCRO()
							.setDataType("VARCHAR")
							.setColumnName(COLUMN_NAME)
							.setSchemaName(SCHEMA_NAME)
							.setStartValue(1).setTableName(TABLE_NAME)
			};
			// Run
			AddAutoIncrementChangeActionCRO[] returned =
					unitUnderTest.getNecessaryAddAutoIncrementChangeActionsForCreateTable(
					createTable(createColumn(true)), SCHEMA_NAME);
			// Check
			assertArrayEquals(expected, returned);
		}

	}

	@Nested
	class TestsOfMethod_getAutoIncrementFields_TableCMO {

		@Test
		void passANullValue_throwsAnException() {
			assertThrows(IllegalArgumentException.class, () -> unitUnderTest.getAutoIncrementFields(null));
		}

		@Test
		void passATableWithoutColumns_returnsAnEmptyList() {
			assertTrue(unitUnderTest.getAutoIncrementFields(createTable()).isEmpty());
		}

		@Test
		void passATableWithoutAutoIncrementColumns_returnsAnEmptyList() {
			assertTrue(unitUnderTest
					.getAutoIncrementFields(createTable(createColumn("col1", false), createColumn("col2", null)))
					.isEmpty());
		}

	}

	@Nested
	class TestsOfMethod_isAutoIncrementColumn_ColumnCMO {

		@Test
		void passANullValue_throwsAnException() {
			assertThrows(IllegalArgumentException.class, () -> unitUnderTest.isAutoIncrementColumn(null));
		}

		@Test
		void passAColumnWithANullValueAsAutoIncrement_returnsTrue() {
			assertFalse(unitUnderTest.isAutoIncrementColumn(createColumn(null)));
		}

		@Test
		void passAColumnWithATrueValueAsAutoIncrement_returnsTrue() {
			assertTrue(unitUnderTest.isAutoIncrementColumn(createColumn(true)));
		}

		@Test
		void passAColumnWithAFalseValueAsAutoIncrement_returnsFalse() {
			assertFalse(unitUnderTest.isAutoIncrementColumn(createColumn(false)));
		}

	}

}
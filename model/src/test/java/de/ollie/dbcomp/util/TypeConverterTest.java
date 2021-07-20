package de.ollie.dbcomp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Types;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for class "DBTypeConverter".
 * 
 * @author ollie
 *
 */
@ExtendWith(MockitoExtension.class)
public class TypeConverterTest {

	@InjectMocks
	private TypeConverter unitUnderTest;

	@Nested
	class TestsOfMethod_convert_int {

		@Test
		void passedAnInvalidValue_ThrowsException() {
			// Prepare
			int passed = Integer.MIN_VALUE;
			// Check
			IllegalArgumentException e =
					assertThrows(IllegalArgumentException.class, () -> unitUnderTest.convert(passed));
			assertEquals("there is no mapping for data type value: " + passed, e.getMessage());
		}

		@ParameterizedTest
		@CsvSource({
				Types.ARRAY + ",ARRAY",
				Types.BIGINT + ",BIGINT",
				Types.BINARY + ",BINARY",
				Types.BIT + ",BIT",
				Types.BLOB + ",BLOB",
				Types.BOOLEAN + ",BOOLEAN",
				Types.CHAR + ",CHAR",
				Types.CLOB + ",CLOB",
				Types.DATALINK + ",DATALINK",
				Types.DATE + ",DATE",
				Types.DECIMAL + ",DECIMAL",
				Types.DISTINCT + ",DISTINCT",
				Types.DOUBLE + ",DOUBLE",
				Types.FLOAT + ",FLOAT",
				Types.INTEGER + ",INTEGER",
				Types.JAVA_OBJECT + ",JAVA_OBJECT",
				Types.LONGNVARCHAR + ",LONGNVARCHAR",
				Types.LONGVARBINARY + ",LONGVARBINARY",
				Types.LONGVARCHAR + ",LONGVARCHAR",
				Types.NCHAR + ",NCHAR",
				Types.NCLOB + ",NCLOB",
				Types.NULL + ",NULL",
				Types.NUMERIC + ",NUMERIC",
				Types.NVARCHAR + ",NVARCHAR",
				Types.OTHER + ",OTHER",
				Types.REAL + ",REAL",
				Types.REF + ",REF",
				Types.REF_CURSOR + ",REF_CURSOR",
				Types.ROWID + ",ROWID",
				Types.SMALLINT + ",SMALLINT",
				Types.SQLXML + ",SQLXML",
				Types.STRUCT + ",STRUCT",
				Types.TIME + ",TIME",
				Types.TIME_WITH_TIMEZONE + ",TIME_WITH_TIMEZONE",
				Types.TIMESTAMP + ",TIMESTAMP",
				Types.TIMESTAMP_WITH_TIMEZONE + ",TIMESTAMP_WITH_TIMEZONE",
				Types.TINYINT + ",TINYINT",
				Types.VARBINARY + ",VARBINARY",
				Types.VARCHAR + ",VARCHAR" })
		void passTypesConstant_ReturnsCorrectDBType(int passed, DBType expected) {
			assertEquals(expected, unitUnderTest.convert(passed));
		}

	}

	@Nested
	class TestsOfMethod_isSimpleType_String {

		@ParameterizedTest
		@CsvSource(value = { "boolean", "byte", "char", "double", "float", "int", "long", "short" })
		void passSimpleType_ReturnsTrue(String simpleTypeName) {
			assertTrue(unitUnderTest.isSimpleType(simpleTypeName));
		}

		@ParameterizedTest
		@CsvSource(value = { "Boolean", "String", "java.lang.Integer", "Object" })
		void passNotSimpleType_ReturnsFalse(String typeName) {
			assertFalse(unitUnderTest.isSimpleType(typeName));
		}

		@Test
		void passANullValue_ReturnsFalse() {
			assertFalse(unitUnderTest.isSimpleType(null));
		}

		@Test
		void passAnEmptyString_ReturnsFalse() {
			assertFalse(unitUnderTest.isSimpleType(""));
		}

	}

}
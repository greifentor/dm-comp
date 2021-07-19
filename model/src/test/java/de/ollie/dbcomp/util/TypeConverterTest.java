package de.ollie.dbcomp.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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

		@Test
		void passTypesBINARY_ReturnsDBTypeBINARY() {
			// Prepare
			int passed = Types.BINARY;
			DBType expected = DBType.BINARY;
			// Run
			DBType returned = unitUnderTest.convert(passed);
			// Check
			assertThat(returned, equalTo(expected));
		}

		@Test
		void passTypesBIGINT_ReturnsDBTypeBIGINT() {
			// Prepare
			int passed = Types.BIGINT;
			DBType expected = DBType.BIGINT;
			// Run
			DBType returned = unitUnderTest.convert(passed);
			// Check
			assertThat(returned, equalTo(expected));
		}

		@Test
		void passTypesCHAR_ReturnsDBTypeCHAR() {
			// Prepare
			int passed = Types.CHAR;
			DBType expected = DBType.CHAR;
			// Run
			DBType returned = unitUnderTest.convert(passed);
			// Check
			assertThat(returned, equalTo(expected));
		}

		@Test
		void passTypesDECIMAL_ReturnsDBTypeDECIMAL() {
			// Prepare
			int passed = Types.DECIMAL;
			DBType expected = DBType.DECIMAL;
			// Run
			DBType returned = unitUnderTest.convert(passed);
			// Check
			assertThat(returned, equalTo(expected));
		}

		@Test
		void passTypesINTEGER_ReturnsDBTypeINTEGER() {
			// Prepare
			int passed = Types.INTEGER;
			DBType expected = DBType.INTEGER;
			// Run
			DBType returned = unitUnderTest.convert(passed);
			// Check
			assertThat(returned, equalTo(expected));
		}

		@Test
		void passTypesLONGVARCHAR_ReturnsDBTypeLONGVARCHAR() {
			// Prepare
			int passed = Types.LONGVARCHAR;
			DBType expected = DBType.LONGVARCHAR;
			// Run
			DBType returned = unitUnderTest.convert(passed);
			// Check
			assertThat(returned, equalTo(expected));
		}

		@Test
		void passTypesNUMERIC_ReturnsDBTypeNUMERIC() {
			// Prepare
			int passed = Types.NUMERIC;
			DBType expected = DBType.NUMERIC;
			// Run
			DBType returned = unitUnderTest.convert(passed);
			// Check
			assertThat(returned, equalTo(expected));
		}

		@Test
		void passTypesVARCHAR_ReturnsDBTypeVARCHAR() {
			// Prepare
			int passed = Types.VARCHAR;
			DBType expected = DBType.VARCHAR;
			// Run
			DBType returned = unitUnderTest.convert(passed);
			// Check
			assertThat(returned, equalTo(expected));
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
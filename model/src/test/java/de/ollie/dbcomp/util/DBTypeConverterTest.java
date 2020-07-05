package de.ollie.dbcomp.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Types;

import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for class "DBTypeConverter".
 * 
 * @author ollie
 *
 */
@ExtendWith(MockitoExtension.class)
public class DBTypeConverterTest {

	@InjectMocks
	private DBTypeConverter unitUnderTest;

	@Test
	public void convertInt_PassedAnInvalidValue_ThrowsException() {
		// Prepare
		int passed = Integer.MIN_VALUE;
		// Check
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> this.unitUnderTest.convert(passed));
		assertEquals("there is no mapping for data type value: " + passed, e.getMessage());
	}

	@Test
	public void convertInt_PassTypesBIGINT_ReturnsDBTypeBIGINT() {
		// Prepare
		int passed = Types.BIGINT;
		DBType expected = DBType.BIGINT;
		// Run
		DBType returned = this.unitUnderTest.convert(passed);
		// Check
		assertThat(returned, equalTo(expected));
	}

	@Test
	public void convertInt_PassTypesCHAR_ReturnsDBTypeCHAR() {
		// Prepare
		int passed = Types.CHAR;
		DBType expected = DBType.CHAR;
		// Run
		DBType returned = this.unitUnderTest.convert(passed);
		// Check
		assertThat(returned, equalTo(expected));
	}

	@Test
	public void convertInt_PassTypesDECIMAL_ReturnsDBTypeDECIMAL() {
		// Prepare
		int passed = Types.DECIMAL;
		DBType expected = DBType.DECIMAL;
		// Run
		DBType returned = this.unitUnderTest.convert(passed);
		// Check
		assertThat(returned, equalTo(expected));
	}

	@Test
	public void convertInt_PassTypesINTEGER_ReturnsDBTypeINTEGER() {
		// Prepare
		int passed = Types.INTEGER;
		DBType expected = DBType.INTEGER;
		// Run
		DBType returned = this.unitUnderTest.convert(passed);
		// Check
		assertThat(returned, equalTo(expected));
	}

	@Test
	public void convertInt_PassTypesNUMERIC_ReturnsDBTypeNUMERIC() {
		// Prepare
		int passed = Types.NUMERIC;
		DBType expected = DBType.NUMERIC;
		// Run
		DBType returned = this.unitUnderTest.convert(passed);
		// Check
		assertThat(returned, equalTo(expected));
	}

	@Test
	public void convertInt_PassTypesVARCHAR_ReturnsDBTypeVARCHAR() {
		// Prepare
		int passed = Types.VARCHAR;
		DBType expected = DBType.VARCHAR;
		// Run
		DBType returned = this.unitUnderTest.convert(passed);
		// Check
		assertThat(returned, equalTo(expected));
	}

}
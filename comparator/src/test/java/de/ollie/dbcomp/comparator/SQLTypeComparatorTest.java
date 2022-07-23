package de.ollie.dbcomp.comparator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Types;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.dbcomp.model.TypeCMO;

@ExtendWith(MockitoExtension.class)
public class SQLTypeComparatorTest {

	private static final int DECIMAL_PLACE = 1;
	private static final int LENGTH = 2;
	private static final int SQL_TYPE = Types.INTEGER;

	@InjectMocks
	private SQLTypeComparator unitUnderTest;

	@Nested
	class TestsOfMethod_isEqual_TypeCMO_TypeCMO {

		@Nested
		class Equal {

			@Test
			void bothPassedAsNullValues() {
				assertTrue(unitUnderTest.isEqual(null, null));
			}

			@Test
			void bothPassedWithTheSameType() {
				TypeCMO type = TypeCMO.of(SQL_TYPE, LENGTH, DECIMAL_PLACE);
				assertTrue(unitUnderTest.isEqual(type, type));
			}

			@Test
			void bothPassedWithTheEqualType() {
				TypeCMO type0 = TypeCMO.of(SQL_TYPE, LENGTH, DECIMAL_PLACE);
				TypeCMO type1 = TypeCMO.of(SQL_TYPE, LENGTH, DECIMAL_PLACE);
				assertTrue(unitUnderTest.isEqual(type0, type1));
			}

			@Test
			void bothPassedWithTheEqualTypeBothLengthAreNull() {
				TypeCMO type0 = TypeCMO.of(SQL_TYPE, null, DECIMAL_PLACE);
				TypeCMO type1 = TypeCMO.of(SQL_TYPE, null, DECIMAL_PLACE);
				assertTrue(unitUnderTest.isEqual(type0, type1));
			}

			@Test
			void bothPassedWithOneDecimalAndOneNumericType() {
				TypeCMO type0 = TypeCMO.of(Types.DECIMAL, LENGTH, DECIMAL_PLACE);
				TypeCMO type1 = TypeCMO.of(Types.NUMERIC, LENGTH, DECIMAL_PLACE);
				assertTrue(unitUnderTest.isEqual(type0, type1));
			}

		}

		@Nested
		class Unequal {

			@Test
			void passedANullValueAFirstValue() {
				TypeCMO type = TypeCMO.of(SQL_TYPE, LENGTH, DECIMAL_PLACE);
				assertFalse(unitUnderTest.isEqual(null, type));
			}

			@Test
			void passedANullValueASecondValue() {
				TypeCMO type = TypeCMO.of(SQL_TYPE, LENGTH, DECIMAL_PLACE);
				assertFalse(unitUnderTest.isEqual(type, null));
			}

			@Test
			void differenceInDecimalPlace() {
				TypeCMO type0 = TypeCMO.of(SQL_TYPE, LENGTH, DECIMAL_PLACE);
				TypeCMO type1 = TypeCMO.of(SQL_TYPE, LENGTH, DECIMAL_PLACE + 1);
				assertFalse(unitUnderTest.isEqual(type0, type1));
			}

			@Test
			void differenceInDecimalPlaceOneIsNull() {
				TypeCMO type0 = TypeCMO.of(SQL_TYPE, LENGTH, null);
				TypeCMO type1 = TypeCMO.of(SQL_TYPE, LENGTH, DECIMAL_PLACE);
				assertFalse(unitUnderTest.isEqual(type0, type1));
			}

			@Test
			void differenceInLength() {
				TypeCMO type0 = TypeCMO.of(SQL_TYPE, LENGTH, DECIMAL_PLACE);
				TypeCMO type1 = TypeCMO.of(SQL_TYPE, LENGTH + 1, DECIMAL_PLACE);
				assertFalse(unitUnderTest.isEqual(type0, type1));
			}

			@Test
			void differenceInLengthOneIsNull() {
				TypeCMO type0 = TypeCMO.of(SQL_TYPE, null, DECIMAL_PLACE);
				TypeCMO type1 = TypeCMO.of(SQL_TYPE, LENGTH, DECIMAL_PLACE);
				assertFalse(unitUnderTest.isEqual(type0, type1));
			}

			@Test
			void differenceInSQLType() {
				TypeCMO type0 = TypeCMO.of(Types.ARRAY, LENGTH, DECIMAL_PLACE);
				TypeCMO type1 = TypeCMO.of(Types.BIGINT, LENGTH, DECIMAL_PLACE);
				assertFalse(unitUnderTest.isEqual(type0, type1));
			}

		}

	}

}
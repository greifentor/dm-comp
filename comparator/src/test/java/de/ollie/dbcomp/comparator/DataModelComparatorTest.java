package de.ollie.dbcomp.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.dbcomp.comparator.model.ComparisonResultCRO;
import de.ollie.dbcomp.comparator.model.actions.DropTableChangeActionCRO;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.model.TableCMO;

@ExtendWith(MockitoExtension.class)
public class DataModelComparatorTest {

	@InjectMocks
	private DataModelComparator unitUnderTest;

	@DisplayName("Tests of method 'compare(DataModelCMO, DataModelCMO)'.")
	@Nested
	class TestsOfMethod_compare_DataModelCMO_DataModelCMO {

		@DisplayName("Returns an empty report for empty models.")
		@Test
		void passEmptyModels_ReturnsAnEmptyReport() {
			// Prepare
			ComparisonResultCRO expected = new ComparisonResultCRO();
			// Run
			ComparisonResultCRO returned = unitUnderTest.compare(DataModelCMO.of(), DataModelCMO.of());
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Throws an exception if the source model ist passed as a null value.")
		@Test
		void passNullValueAsSourceModel_ThrowsAnException() {
			IllegalArgumentException thrown = assertThrows( //
					IllegalArgumentException.class, //
					() -> unitUnderTest.compare(null, DataModelCMO.of()));
			// Check
			assertEquals("source model cannot be null.", thrown.getMessage());
		}

		@DisplayName("Throws an exception if the target model ist passed as a null value.")
		@Test
		void passNullValueAsTargetModel_ThrowsAnException() {
			IllegalArgumentException thrown = assertThrows( //
					IllegalArgumentException.class, //
					() -> unitUnderTest.compare(DataModelCMO.of(), null));
			// Check
			assertEquals("target model cannot be null.", thrown.getMessage());
		}

		@DisplayName("Returns a result with a remove table action if the source model has a table less than the target "
				+ "model.")
		@Test
		void passSourceModelWithATableLessThanTargetModel_ReturnsAResultWithaRemoveTableAction() {
			// Prepare
			ComparisonResultCRO expected = new ComparisonResultCRO() //
					.addChangeActions( //
							new DropTableChangeActionCRO() //
									.setSchemaName("public") //
									.setTableName("TABLE") //
					) //
			;
			DataModelCMO sourceModel = DataModelCMO.of(SchemaCMO.of("public"));
			DataModelCMO targetModel = DataModelCMO.of( //
					SchemaCMO.of( //
							"public", //
							TableCMO.of("TABLE") //
					) //
			);
			// Run
			ComparisonResultCRO returned = unitUnderTest.compare(sourceModel, targetModel);
			// Check
			assertEquals(expected, returned);
		}

	}

}
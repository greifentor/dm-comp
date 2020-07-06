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

	private static final String BASE_PATH = "src/test/resources/liquibase/create";

	private LiquibaseFileModelReader unitUnderTest;

	@DisplayName("Test for create table statements")
	@Nested
	class TestsForCreateTableStatements {

		@DisplayName("Should create a new table in the model (one field, no scheme).")
		@Test
		void passLiquibaseFilesForASingleCreation_OneFieldNoSchema_CreatesAModelWithPublicSchemeAndTheTable()
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
			unitUnderTest = new LiquibaseFileModelReader(new TypeConverter(), new File(BASE_PATH),
					new File("createSingleTableNoFieldNoScheme.xml"));
			// Run
			DatamodelCMO returned = unitUnderTest.readModel();
			// Check
			assertEquals(expected, returned);
		}

	}

}
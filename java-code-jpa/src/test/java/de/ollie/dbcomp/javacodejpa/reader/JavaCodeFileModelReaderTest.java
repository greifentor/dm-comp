package de.ollie.dbcomp.javacodejpa.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.ReaderResult;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.model.TableCMO;
import de.ollie.dbcomp.report.ImportReport;

@ExtendWith(MockitoExtension.class)
public class JavaCodeFileModelReaderTest {

	@InjectMocks
	private JavaCodeFileModelReader unitUnderTest;

	@DisplayName("Tests of table reads")
	@Nested
	class TestsOfTableReads {

		@DisplayName("Reads a table from a source code file.")
		@Test
		void readsTableInformationFromASourceCodeFile() throws Exception {
			// Prepare
			ReaderResult expected = new ReaderResult() //
					.setDataModel( //
							DataModelCMO.of( //
									SchemaCMO.of( //
											"", //
											new TableCMO[] { TableCMO.of("AClass") } //
									) //
							) //
					) //
					.setImportReport(new ImportReport()) //
			;
			// Run
			ReaderResult returned = unitUnderTest.read("src/test/resources/jpa/AClass.java");
			// Check
			assertEquals(expected, returned);
		}

		@DisplayName("Reads a table from a source code file with an alternate table name.")
		@Test
		void readsTableInformationWithAlternateTableNameFromASourceCodeFile() throws Exception {
			// Prepare
			ReaderResult expected = new ReaderResult() //
					.setDataModel( //
							DataModelCMO.of( //
									SchemaCMO.of( //
											"", //
											new TableCMO[] { TableCMO.of("ATABLE") } //
									) //
							) //
					) //
					.setImportReport(new ImportReport()) //
			;
			// Run
			ReaderResult returned = unitUnderTest.read("src/test/resources/jpa/AClassWithTableAnnotation.java");
			// Check
			assertEquals(expected, returned);
		}

	}

}
package de.ollie.dbcomp.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CLITest {

	@InjectMocks
	private CLI unitUnderTest;

	@DisplayName("Tests of the compare command.")
	@Nested
	class TestsOfTheCommand_compare {

		@DisplayName("Happy run")
		@Test
		void happyRun(@TempDir Path out) throws Exception {
			// Prepare
			String expectedContent = "<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?>\n" //
					+ "<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\" xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd\">\n" //
					+ "    <changeSet author=\"dm-comp\" id=\"ADD-CHANGE-SET-ID-HERE\" objectQuotingStrategy=\"LEGACY\" runOnChange=\"true\">\n" //
					+ "        <createTable schemaName=\"\" tableName=\"AClass\">\n" //
					+ "            <column name=\"id\" type=\"BIGINT\"/>\n" //
					+ "        </createTable>\n" //
					+ "        <dropTable schemaName=\"\" tableName=\"TABLE_TO_CREATE\"/>\n" //
					+ "        <addColumn schemaName=\"\" tableName=\"TABLE_DIFF_COLUMNS\">\n" //
					+ "            <column name=\"id\" type=\"BIGINT\"/>\n" //
					+ "        </addColumn>\n" //
					+ "        <dropColumn schemaName=\"\" tableName=\"TABLE\">\n" //
					+ "            <column name=\"COLUMN\"/>\n" //
					+ "        </dropColumn>\n" //
					+ "        <dropColumn schemaName=\"\" tableName=\"TABLE_DIFF_COLUMNS\">\n" //
					+ "            <column name=\"IDENT\"/>\n" //
					+ "        </dropColumn>\n" //
					+ "    </changeSet>\n" //
					+ "</databaseChangeLog>\n";
			String outputFileName = out.toString() + "/out.xml";
			int expected = 0;
			// Run
			int returned = unitUnderTest.start("compare", "-st", "JAVA", "-s", "it-data", "-t",
					"it-data/testTargetSchema.xml", "-o", outputFileName);
			// Check
			assertEquals(expectedContent,
					Files.readString(Path.of(outputFileName)).replace("\r\n", "\n").replace("\r", "\n"));
			assertEquals(expected, returned);
		}

	}

}
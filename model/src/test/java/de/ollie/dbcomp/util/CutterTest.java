package de.ollie.dbcomp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CutterTest {

	@Nested
	class TestsOfTheConstructor {

		@Test
		void throwsAnExceptionIfCalled() {
			assertThrows(UnsupportedOperationException.class, () -> new Cutter());
		}
	}

	@Nested
	class TestsOfMethod_cutQuotes_String {

		@Test
		void passANullValue_ReturnsANullValue() {
			// Prepare
			String expected = null;
			// Run
			String returned = Cutter.cutQuotes(null);
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passAnEmptyString_ReturnsAnEmptyString() {
			// Prepare
			String expected = "";
			// Run
			String returned = Cutter.cutQuotes("");
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passASingleCharacterString_ReturnsThePassedString() {
			// Prepare
			String expected = "x";
			// Run
			String returned = Cutter.cutQuotes("x");
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passASingleQuoteString_ReturnsAnEmptyString() {
			// Prepare
			String expected = "";
			// Run
			String returned = Cutter.cutQuotes("\"");
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passADoubleQuoteString_ReturnsAnEmptyString() {
			// Prepare
			String expected = "";
			// Run
			String returned = Cutter.cutQuotes("\"\"");
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passATripleQuoteString_ReturnsAStringWithASingleQuote() {
			// Prepare
			String expected = "\"";
			// Run
			String returned = Cutter.cutQuotes("\"\"\"");
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passAQuotedString_ReturnsTheStringWithoutQuotes() {
			// Prepare
			String expected = "lalala";
			// Run
			String returned = Cutter.cutQuotes("\"" + expected + "\"");
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passAQuotedStringContainingQuotes_ReturnsTheStringLeadingAndClosingButAllTheOtherQuotes() {
			// Prepare
			String expected = "la\"la\"la";
			// Run
			String returned = Cutter.cutQuotes("\"" + expected + "\"");
			// Check
			assertEquals(expected, returned);
		}

	}

}
package de.ollie.dbcomp.javacodejpa.reader.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.blueprints.codereader.java.model.FieldDeclaration;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.TypeCMO;

@ExtendWith(MockitoExtension.class)
public class FieldDeclarationToColumnCMOConverterTest {

	private static final String NAME = "name";
	private static final int SQL_TYPE = Types.BIGINT;

	private static final TypeCMO TYPE = TypeCMO.of(SQL_TYPE, null, null);

	@InjectMocks
	private FieldDeclarationToColumnCMOConverter unitUnderTest;

	@DisplayName("Returns a null value if one is passed.")
	@Test
	void passANullValue_ReturnsANullValue() {
		assertNull(unitUnderTest.convert(null));
	}

	@DisplayName("Returns a matching ColumnCMO for a passed field declaration (long).")
	@Test
	void passCorrectFieldDeclarationSimpleLong_ReturnsAMatchingColumnCMO() {
		// Vorbereitung
		ColumnCMO expected = ColumnCMO.of(NAME, TYPE, false);
		FieldDeclaration field = new FieldDeclaration() //
				.setName(NAME) //
				.setType("long") //
		;
		// Ausführung
		ColumnCMO returned = unitUnderTest.convert(field);
		// Prüfung
		assertEquals(expected, returned);
	}

	@DisplayName("Returns a matching ColumnCMO for a passed field declaration (int).")
	@Test
	void passCorrectFieldDeclarationSimpleInt_ReturnsAMatchingColumnCMO() {
		// Vorbereitung
		ColumnCMO expected = ColumnCMO.of(NAME, TypeCMO.of(Types.INTEGER, null, null), false);
		FieldDeclaration field = new FieldDeclaration() //
				.setName(NAME) //
				.setType("int") //
		;
		// Ausführung
		ColumnCMO returned = unitUnderTest.convert(field);
		// Prüfung
		assertEquals(expected, returned);
	}

	@DisplayName("Returns a matching ColumnCMO for a passed field declaration (String).")
	@Test
	void passCorrectFieldDeclarationString_ReturnsAMatchingColumnCMO() {
		// Vorbereitung
		ColumnCMO expected = ColumnCMO.of(NAME, TypeCMO.of(Types.VARCHAR, 255, null), false);
		FieldDeclaration field = new FieldDeclaration() //
				.setName(NAME) //
				.setType("String") //
		;
		// Ausführung
		ColumnCMO returned = unitUnderTest.convert(field);
		// Prüfung
		assertEquals(expected, returned);
	}

}
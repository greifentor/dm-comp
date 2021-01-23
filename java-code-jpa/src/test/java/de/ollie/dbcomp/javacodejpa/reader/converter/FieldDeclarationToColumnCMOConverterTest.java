package de.ollie.dbcomp.javacodejpa.reader.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.blueprints.codereader.java.model.Annotation;
import de.ollie.blueprints.codereader.java.model.ElementValuePair;
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
		ColumnCMO expected = ColumnCMO.of(NAME, TYPE, false, false);
		FieldDeclaration field = new FieldDeclaration().setName(NAME).setType("long");
		// Ausführung
		ColumnCMO returned = unitUnderTest.convert(field);
		// Prüfung
		assertEquals(expected, returned);
	}

	@DisplayName("Returns a matching ColumnCMO for a passed field declaration (int).")
	@Test
	void passCorrectFieldDeclarationSimpleInt_ReturnsAMatchingColumnCMO() {
		// Vorbereitung
		ColumnCMO expected = ColumnCMO.of(NAME, TypeCMO.of(Types.INTEGER, null, null), false, false);
		FieldDeclaration field = new FieldDeclaration().setName(NAME).setType("int");
		// Ausführung
		ColumnCMO returned = unitUnderTest.convert(field);
		// Prüfung
		assertEquals(expected, returned);
	}

	@DisplayName("Returns a matching ColumnCMO for a passed field declaration (String).")
	@Test
	void passCorrectFieldDeclarationString_ReturnsAMatchingColumnCMO() {
		// Vorbereitung
		ColumnCMO expected = ColumnCMO.of(NAME, TypeCMO.of(Types.VARCHAR, 255, null), false, null);
		FieldDeclaration field = new FieldDeclaration().setName(NAME).setType("String");
		// Ausführung
		ColumnCMO returned = unitUnderTest.convert(field);
		// Prüfung
		assertEquals(expected, returned);
	}

	@DisplayName("Returns a matching ColumnCMO for a passed field declaration with @Column(name) annotation.")
	@Test
	void passCorrectFieldDeclarationWithColumnAnnotationAndSetName_ReturnsAMatchingColumnCMO() {
		// Vorbereitung
		ColumnCMO expected = ColumnCMO.of(NAME, TypeCMO.of(Types.VARCHAR, 255, null), false, null);
		FieldDeclaration field =
				new FieldDeclaration()
						.setName(NAME + 1)
						.setType("String")
						.addAnnotations(
								new Annotation()
										.setName("Column")
										.addElementValues(
												new ElementValuePair().setKey("name").setValue("\"" + NAME + "\"")));
		// Ausführung
		ColumnCMO returned = unitUnderTest.convert(field);
		// Prüfung
		assertEquals(expected, returned);
	}

	@DisplayName("Returns a matching not nullable ColumnCMO for simple types.")
	@ParameterizedTest(name = "Nullable flag is set for simple type: {0}")
	@CsvSource(value = { "boolean", "byte", "char", "double", "float", "int", "long", "short" })
	void passCorrectFieldDeclarationSimpleTypes_ReturnsAMatchingNotNullableColumnCMO(String typeName) {
		// Vorbereitung
		FieldDeclaration field = new FieldDeclaration().setName(NAME).setType(typeName);
		// Ausführung
		ColumnCMO returned = unitUnderTest.convert(field);
		// Prüfung
		assertFalse(returned.isNullable());
	}

	@DisplayName("Returns a matching not nullable ColumnCMO for field with @NotNull annotation.")
	@Test
	void passFieldDeclarationWithNotNullAnnotation_ReturnsAMatchingNotNullableColumnCMO() {
		// Vorbereitung
		FieldDeclaration field =
				new FieldDeclaration()
						.setName(NAME)
						.setType("String")
						.addAnnotations(new Annotation().setName("NotNull"));
		// Ausführung
		ColumnCMO returned = unitUnderTest.convert(field);
		// Prüfung
		assertFalse(returned.isNullable());
	}

}
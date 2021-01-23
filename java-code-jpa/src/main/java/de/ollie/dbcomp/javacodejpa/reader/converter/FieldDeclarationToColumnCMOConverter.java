package de.ollie.dbcomp.javacodejpa.reader.converter;

import java.sql.Types;

import de.ollie.blueprints.codereader.java.model.FieldDeclaration;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.TypeCMO;
import de.ollie.dbcomp.util.Cutter;
import de.ollie.dbcomp.util.TypeConverter;

public class FieldDeclarationToColumnCMOConverter {

	private static final TypeConverter typeConverter = new TypeConverter();

	public ColumnCMO convert(FieldDeclaration field) {
		if (field == null) {
			return null;
		}
		return ColumnCMO.of(getName(field), getType(field.getType()), false, getNullable(field));
	}

	private String getName(FieldDeclaration field) {
		return field
				.getAnnotations()
				.stream()
				.filter(annotation -> annotation.getName().equals("Column") && annotation.hasElementWithKey("name"))
				.findFirst()
				.map(annotation -> Cutter.cutQuotes(annotation.getElementValues().get("name")))
				.orElse(field.getName());
	}

	private TypeCMO getType(String typeName) {
		if (typeName.equals("String")) {
			return TypeCMO.of(Types.VARCHAR, 255, null);
		} else if (typeName.equals("int")) {
			return TypeCMO.of(Types.INTEGER, null, null);
		}
		return TypeCMO.of(Types.BIGINT, null, null);
	}

	private boolean getNullable(FieldDeclaration field) {
		return !typeConverter.isSimpleType(field.getType()) && !hasNotNullAnnotation(field);
	}

	private boolean hasNotNullAnnotation(FieldDeclaration field) {
		return field.getAnnotations().stream().anyMatch(annotation -> "NotNull".equals(annotation.getName()));
	}
}
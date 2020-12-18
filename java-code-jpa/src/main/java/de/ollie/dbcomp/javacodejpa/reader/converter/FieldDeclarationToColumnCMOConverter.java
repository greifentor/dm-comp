package de.ollie.dbcomp.javacodejpa.reader.converter;

import java.sql.Types;

import de.ollie.blueprints.codereader.java.model.FieldDeclaration;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.TypeCMO;
import de.ollie.dbcomp.util.Cutter;

public class FieldDeclarationToColumnCMOConverter {

	public ColumnCMO convert(FieldDeclaration field) {
		if (field == null) {
			return null;
		}
		return ColumnCMO.of( //
				getName(field), //
				getType(field.getType()), //
				false //
		);
	}

	private String getName(FieldDeclaration field) {
		return field.getAnnotations() //
				.stream() //
				.filter(annotation -> annotation.getName().equals("Column") && annotation.hasElementWithKey("name")) //
				.findFirst() //
				.map(annotation -> Cutter.cutQuotes(annotation.getElementValues().get("name"))) //
				.orElse(field.getName()) //
		;
	}

	private TypeCMO getType(String typeName) {
		if (typeName.equals("String")) {
			return TypeCMO.of(Types.VARCHAR, 255, null);
		} else if (typeName.equals("int")) {
			return TypeCMO.of(Types.INTEGER, null, null);
		}
		return TypeCMO.of(Types.BIGINT, null, null);
	}

}
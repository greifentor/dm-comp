package de.ollie.dbcomp.javacodejpa.reader.converter;

import java.sql.Types;

import de.ollie.blueprints.codereader.java.model.FieldDeclaration;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.TypeCMO;

public class FieldDeclarationToColumnCMOConverter {

	public ColumnCMO convert(FieldDeclaration field) {
		if (field == null) {
			return null;
		}
		return ColumnCMO.of( //
				field.getName(), //
				getType(field.getType()), //
				false //
		);
	}

	private TypeCMO getType(String typeName) {
		if (typeName.equals("String")) {
			return TypeCMO.of(Types.LONGVARCHAR, null, null);
		}
		return TypeCMO.of(Types.BIGINT, null, null);
	}

}
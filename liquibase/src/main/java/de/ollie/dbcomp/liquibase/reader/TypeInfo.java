package de.ollie.dbcomp.liquibase.reader;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for type information.
 *
 * @author Oliver.Lieshoff
 *
 */
@Accessors(chain = true)
@Data
class TypeInfo {

	private String name;
	private int columnSize = -1;
	private int dataType;
	private int decimalDigits = -1;

}
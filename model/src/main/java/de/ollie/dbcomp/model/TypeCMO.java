package de.ollie.dbcomp.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for a type of a column.
 * 
 * @author ollie (04.07.2020)
 *
 */
@Accessors(chain = true)
@Data
public class TypeCMO {

	private Integer decimalPlace;
	private Integer length;
	private int sqlType;

	private TypeCMO() {
		super();
	}

	public static TypeCMO of(int sqlType, Integer length, Integer decimalPlace) {
		return new TypeCMO() //
				.setDecimalPlace(decimalPlace) //
				.setLength(length) //
				.setSqlType(sqlType) //
		;
	}

}
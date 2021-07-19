package de.ollie.dbcomp.util;

import java.sql.Types;
import java.util.Set;

/**
 * A converter for Types to DBType and vice versa.
 *
 * @author ollie (06.07.2020)
 *
 */
public class TypeConverter {

	/**
	 * Converts the passed Types value to an DBType enum.
	 * 
	 * @param dataType A Types value.
	 * @return A DBType for the passed Types constant.
	 * @throws IllegalArgumentException Passing an unknown value.
	 */
	public DBType convert(int dataType) {
		if (dataType == Types.BIGINT) {
			return DBType.BIGINT;
		} else if (dataType == Types.BINARY) {
			return DBType.BINARY;
		} else if (dataType == Types.BIT) {
			return DBType.BIT;
		} else if (dataType == Types.BLOB) {
			return DBType.BLOB;
		} else if (dataType == Types.BOOLEAN) {
			return DBType.BOOLEAN;
		} else if (dataType == Types.CHAR) {
			return DBType.CHAR;
		} else if (dataType == Types.CLOB) {
			return DBType.CLOB;
		} else if (dataType == Types.DATE) {
			return DBType.DATE;
		} else if (dataType == Types.DECIMAL) {
			return DBType.DECIMAL;
		} else if (dataType == Types.INTEGER) {
			return DBType.INTEGER;
		} else if (dataType == Types.LONGVARCHAR) {
			return DBType.LONGVARCHAR;
		} else if (dataType == Types.NUMERIC) {
			return DBType.NUMERIC;
		} else if (dataType == 1111) {
			return DBType.RAW;
		} else if (dataType == Types.ROWID) {
			return DBType.ROWID;
		} else if (dataType == Types.TIMESTAMP) {
			return DBType.TIMESTAMP;
		} else if (dataType == Types.VARCHAR) {
			return DBType.VARCHAR;
		}
		throw new IllegalArgumentException("there is no mapping for data type value: " + dataType);
	}

	private static final Set<String> JAVA_SIMPLE_TYPE_NAMES =
			Set.of("boolean", "byte", "char", "double", "float", "int", "long", "short");

	/**
	 * Checks the passed type name if it is the name of a simple type.
	 * 
	 * @param typeName The name of the type to check.
	 * @return "true" if the passed type name is a simple Java type.
	 */
	public boolean isSimpleType(String typeName) {
		return (typeName != null) && JAVA_SIMPLE_TYPE_NAMES.contains(typeName);
	}
}
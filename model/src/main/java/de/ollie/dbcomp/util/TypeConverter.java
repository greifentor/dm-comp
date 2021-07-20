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
		if (dataType == Types.ARRAY) {
			return DBType.ARRAY;
		} else if (dataType == Types.BIGINT) {
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
		} else if (dataType == Types.DATALINK) {
			return DBType.DATALINK;
		} else if (dataType == Types.DATE) {
			return DBType.DATE;
		} else if (dataType == Types.DECIMAL) {
			return DBType.DECIMAL;
		} else if (dataType == Types.DISTINCT) {
			return DBType.DISTINCT;
		} else if (dataType == Types.DOUBLE) {
			return DBType.DOUBLE;
		} else if (dataType == Types.FLOAT) {
			return DBType.FLOAT;
		} else if (dataType == Types.INTEGER) {
			return DBType.INTEGER;
		} else if (dataType == Types.JAVA_OBJECT) {
			return DBType.JAVA_OBJECT;
		} else if (dataType == Types.LONGNVARCHAR) {
			return DBType.LONGNVARCHAR;
		} else if (dataType == Types.LONGVARBINARY) {
			return DBType.LONGVARBINARY;
		} else if (dataType == Types.LONGVARCHAR) {
			return DBType.LONGVARCHAR;
		} else if (dataType == Types.NCHAR) {
			return DBType.NCHAR;
		} else if (dataType == Types.NCLOB) {
			return DBType.NCLOB;
		} else if (dataType == Types.NULL) {
			return DBType.NULL;
		} else if (dataType == Types.NUMERIC) {
			return DBType.NUMERIC;
		} else if (dataType == Types.NVARCHAR) {
			return DBType.NVARCHAR;
		} else if (dataType == Types.OTHER) {
			return DBType.OTHER;
		} else if (dataType == Types.REAL) {
			return DBType.REAL;
		} else if (dataType == Types.REF) {
			return DBType.REF;
		} else if (dataType == Types.REF_CURSOR) {
			return DBType.REF_CURSOR;
		} else if (dataType == Types.ROWID) {
			return DBType.ROWID;
		} else if (dataType == Types.SMALLINT) {
			return DBType.SMALLINT;
		} else if (dataType == Types.SQLXML) {
			return DBType.SQLXML;
		} else if (dataType == Types.STRUCT) {
			return DBType.STRUCT;
		} else if (dataType == Types.TIME) {
			return DBType.TIME;
		} else if (dataType == Types.TIME_WITH_TIMEZONE) {
			return DBType.TIME_WITH_TIMEZONE;
		} else if (dataType == Types.TIMESTAMP) {
			return DBType.TIMESTAMP;
		} else if (dataType == Types.TIMESTAMP_WITH_TIMEZONE) {
			return DBType.TIMESTAMP_WITH_TIMEZONE;
		} else if (dataType == Types.TINYINT) {
			return DBType.TINYINT;
		} else if (dataType == Types.VARBINARY) {
			return DBType.VARBINARY;
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
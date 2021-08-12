package de.ollie.dbcomp.util;

import de.ollie.dbcomp.model.TypeCMO;

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

	/**
	 * Returns a string for the passed type object.
	 *
	 * @param type The type object whose string repesentation is to return.
	 * @return The string for the passed type object.
	 */
	public String getSQLType(TypeCMO type) {
		return getSQLType(type, true);
	}
		/**
		 * Returns a string for the passed type object.
		 *
		 * @param type The type object whose string repesentation is to return.
		 * @param showLengthAndPrecision Set this flag to show length and precision in the type name.
		 * @return The string for the passed type object.
		 */
		public String getSQLType(TypeCMO type, boolean showLengthAndPrecision) {
		if (type.getSqlType() == Types.BIGINT) {
			return "BIGINT";
		} else if (type.getSqlType() == Types.BINARY) {
			return "BINARY";
		} else if (type.getSqlType() == Types.BIT) {
			return "BIT";
		} else if (type.getSqlType() == Types.BOOLEAN) {
			return "BOOLEAN";
		} else if (type.getSqlType() == Types.CHAR) {
			return "CHAR" + (showLengthAndPrecision ? "(" + type.getLength() + ")" : "");
		} else if (type.getSqlType() == Types.DATE) {
			return "DATE";
		} else if (type.getSqlType() == Types.DECIMAL) {
			return "DECIMAL" + (showLengthAndPrecision ? "(" + type.getLength() + ", " + type.getDecimalPlace() + ")" : "");
		} else if (type.getSqlType() == Types.DOUBLE) {
			return "DOUBLE";
		} else if (type.getSqlType() == Types.FLOAT) {
			return "FLOAT";
		} else if (type.getSqlType() == Types.INTEGER) {
			return "INTEGER";
		} else if (type.getSqlType() == Types.LONGVARCHAR) {
			return "LONGVARCHAR";
		} else if (type.getSqlType() == Types.NUMERIC) {
			return "NUMERIC" + (showLengthAndPrecision ? "(" + type.getLength() + ", " + type.getDecimalPlace() + ")" : "");
		} else if (type.getSqlType() == Types.SMALLINT) {
			return "SMALLINT";
		} else if (type.getSqlType() == Types.TIME) {
			return "TIME";
		} else if (type.getSqlType() == Types.TIMESTAMP) {
			return "TIMESTAMP";
		} else if (type.getSqlType() == Types.TINYINT) {
			return "TINYINT";
		} else if (type.getSqlType() == Types.VARCHAR) {
			return "VARCHAR" + (showLengthAndPrecision ? "(" + type.getLength() + ")" : "");
		}
		return "BIGINT";
	}

}
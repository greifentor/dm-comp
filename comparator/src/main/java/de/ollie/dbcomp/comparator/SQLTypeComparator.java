package de.ollie.dbcomp.comparator;

import java.sql.Types;
import java.util.Objects;

import de.ollie.dbcomp.model.TypeCMO;

public class SQLTypeComparator {

	public boolean isEqual(TypeCMO type0, TypeCMO type1) {
		if (type0 == type1) {
			return true;
		}
		if (isOnlyOneOfBothNull(type0, type1)) {
			return false;
		}
		return Objects.equals(type0.getDecimalPlace(), type1.getDecimalPlace())
				&& Objects.equals(type0.getLength(), type1.getLength())
				&& areSQLTypesEqual(type0.getSqlType(), type1.getSqlType());
	}

	private boolean isOnlyOneOfBothNull(TypeCMO type0, TypeCMO type1) {
		return ((type0 == null) && (type1 != null)) || ((type0 != null) && (type1 == null));
	}

	private boolean areSQLTypesEqual(int sqlType0, int sqlType1) {
		return (sqlType0 == sqlType1) || (isDecimalAndNumeric(sqlType0) && isDecimalAndNumeric(sqlType1));
	}

	private boolean isDecimalAndNumeric(int sqlType) {
		return (sqlType == Types.DECIMAL) || (sqlType == Types.NUMERIC);
	}

}
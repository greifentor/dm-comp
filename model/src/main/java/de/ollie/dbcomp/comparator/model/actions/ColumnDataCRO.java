package de.ollie.dbcomp.comparator.model.actions;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for column data.
 * 
 * @author ollie (03.12.2020)
 *
 */
@Accessors(chain = true)
@Data
public class ColumnDataCRO {

	private String name;
	private String sqlType;
	private boolean nullable = true;

}
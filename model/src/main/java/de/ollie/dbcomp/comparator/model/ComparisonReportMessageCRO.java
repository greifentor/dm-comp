package de.ollie.dbcomp.comparator.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A container for comparison report messages.
 * 
 * @author ollie (27.11.2020)
 *
 */
@Accessors(chain = true)
@Data
public class ComparisonReportMessageCRO {

	private ComparisonReportMessageLevelCRO level;
	private String message;

}
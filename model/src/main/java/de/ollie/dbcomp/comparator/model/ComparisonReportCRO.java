package de.ollie.dbcomp.comparator.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison result object for comparison report data.
 *
 * @author ollie (05.07.2020)
 */
@Accessors(chain = true)
@Data
public class ComparisonReportCRO {

	private List<ComparisonReportMessageCRO> messages = new ArrayList<>();

}
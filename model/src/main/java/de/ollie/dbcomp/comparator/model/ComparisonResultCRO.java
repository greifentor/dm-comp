package de.ollie.dbcomp.comparator.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison result object for comparison result data.
 *
 * @author ollie (05.07.2020)
 */
@Accessors(chain = true)
@Data
public class ComparisonResultCRO {

	private ComparisonReportCRO report = new ComparisonReportCRO();
	private List<ChangeActionCRO> changeActions = new ArrayList<>();

	public ComparisonResultCRO addChangeActions(ChangeActionCRO... changeActions) {
		for (ChangeActionCRO changeAction : changeActions) {
			this.changeActions.add(changeAction);
		}
		return this;
	}

}
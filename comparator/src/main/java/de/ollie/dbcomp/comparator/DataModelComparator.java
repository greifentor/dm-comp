package de.ollie.dbcomp.comparator;

import de.ollie.dbcomp.comparator.model.ComparisonResultCRO;
import de.ollie.dbcomp.model.DataModelCMO;

/**
 * Compares two data models and returns a report about the comparison and a list of actions to equalize both models.
 *
 * @author Oliver.Lieshoff (27.11.2020)
 *
 */
public class DataModelComparator {

	/**
	 * Compares the two passed models and return a report of the comparison an a list of actions necessary to equalize
	 * both models.
	 * 
	 * @param sourceModel The model which the is to change to the target models structure.
	 * @param targetModel The model which the source model would be changed to if all the returned actions are executed
	 *                    onto.
	 * @return A report and a list of actions which are necessary to change the source model to the target models
	 *         structure.
	 */
	public ComparisonResultCRO compare(DataModelCMO sourceModel, DataModelCMO targetModel) {
		return null;
	}

}
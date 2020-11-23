package de.ollie.dbcomp.liquibase.reader.actions;

import de.ollie.dbcomp.model.DatamodelCMO;
import liquibase.change.Change;

/**
 * An interface for model change actions.
 * 
 * @author ollie (23.11.2020)
 */
public interface ModelChangeAction {

	/**
	 * Processes the passed change to the passed data model.
	 * 
	 * @param change    The change to process on the data model.
	 * @param dataModel The data model which the action is to process onto.
	 */
	void processOnDataModel(Change change, DatamodelCMO dataModel);

	/**
	 * Checks if the passed liquibase change is to process by the implementation of the interface.
	 * 
	 * @param change The liquibase change object to check.
	 * @return boolean
	 */
	boolean isMatchingForChange(Change change);

}
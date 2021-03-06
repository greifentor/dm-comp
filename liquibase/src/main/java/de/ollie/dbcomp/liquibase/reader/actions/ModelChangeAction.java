package de.ollie.dbcomp.liquibase.reader.actions;

import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.report.ImportReport;
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
	 * @param change       The change to process on the data model.
	 * @param dataModel    The data model which the action is to process onto.
	 * @param importReport A report object.
	 */
	void processOnDataModel(Change change, DataModelCMO dataModel, ImportReport importReport);

	/**
	 * Checks if the passed liquibase change is to process by the implementation of the interface.
	 * 
	 * @param change The liquibase change object to check.
	 * @return boolean
	 */
	boolean isMatchingForChange(Change change);

}
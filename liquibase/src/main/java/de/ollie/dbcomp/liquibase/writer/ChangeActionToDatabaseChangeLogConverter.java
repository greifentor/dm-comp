package de.ollie.dbcomp.liquibase.writer;

import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.DropTableChangeActionCRO;
import liquibase.change.Change;
import liquibase.change.core.DropTableChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;

/**
 * A converter which is able to convert change actions from the comparator to a Liquibase database change log.
 *
 * @author ollie (30.11.2020)
 */
public class ChangeActionToDatabaseChangeLogConverter {

	public DatabaseChangeLog convert(List<ChangeActionCRO> changeActions) {
		if (changeActions == null) {
			return null;
		}
		DatabaseChangeLog result = new DatabaseChangeLog("change-log.xml");
		if (!changeActions.isEmpty()) {
			ChangeSet changeSet = new ChangeSet("ADD-CHANGE-SET-ID-HERE", "dm-comp", false, true, null, null, null,
					result);
			result.addChangeSet(changeSet);
			changeActions //
					.forEach(action -> changeSet.addChange(createChange(action)));
		}
		return result;
	}

	private Change createChange(ChangeActionCRO action) {
		if (action instanceof DropTableChangeActionCRO) {

			DropTableChangeActionCRO dropAction = (DropTableChangeActionCRO) action;
			DropTableChange change = new DropTableChange();
			change.setSchemaName(dropAction.getSchemaName());
			change.setTableName(dropAction.getTableName());
			return change;
		}
		return null;
	}

}
package de.ollie.dbcomp.liquibase.writer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.liquibase.writer.processors.AddColumnChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.ChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.CreateTableChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.DropTableChangeProcessor;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;

/**
 * A converter which is able to convert change actions from the comparator to a Liquibase database change log.
 *
 * @author ollie (30.11.2020)
 */
public class ChangeActionToDatabaseChangeLogConverter {

	private static final List<ChangeProcessor> CHANGE_PROCESSORS = Arrays.asList( //
			new AddColumnChangeProcessor(), //
			new CreateTableChangeProcessor(), //
			new DropTableChangeProcessor() //
	);

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
					.forEach(action -> createChange(action).ifPresent(changeSet::addChange));
		}
		return result;
	}

	private Optional<Change> createChange(ChangeActionCRO action) {
		return CHANGE_PROCESSORS //
				.stream() //
				.filter(processor -> processor.isToProcess(action)) //
				.findFirst() //
				.map(processor -> processor.process(action)) //
		;
	}

}
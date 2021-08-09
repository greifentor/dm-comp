package de.ollie.dbcomp.liquibase.writer;

import static de.ollie.dbcomp.util.Check.ensure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.liquibase.writer.processors.AddAutoIncrementChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.AddColumnChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.AddForeignKeyChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.AddPrimaryKeyChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.ChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.ChangeProcessorConfiguration;
import de.ollie.dbcomp.liquibase.writer.processors.CreateTableChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.DropColumnChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.DropForeignKeyChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.DropPrimaryKeyChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.DropTableChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.ModifyDataTypeChangeProcessor;
import de.ollie.dbcomp.liquibase.writer.processors.ModifyNullableChangeProcessor;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;

/**
 * A converter which is able to convert change actions from the comparator to a Liquibase database change log.
 *
 * @author ollie (30.11.2020)
 */
public class ChangeActionToDatabaseChangeLogConverter {

	private static final List<ChangeProcessor> CHANGE_PROCESSORS = Arrays
			.asList(
					new AddColumnChangeProcessor(),
					new AddForeignKeyChangeProcessor(),
					new AddPrimaryKeyChangeProcessor(),
					new CreateTableChangeProcessor(),
					new AddAutoIncrementChangeProcessor(),
					new DropColumnChangeProcessor(),
					new DropForeignKeyChangeProcessor(),
					new DropPrimaryKeyChangeProcessor(),
					new DropTableChangeProcessor(),
					new ModifyDataTypeChangeProcessor(),
					new ModifyNullableChangeProcessor());

	public DatabaseChangeLog convert(List<ChangeActionCRO> changeActions, ChangeProcessorConfiguration configuration) {
		ensure(configuration != null, "configuration cannot be null.");
		if (changeActions == null) {
			return null;
		}
		DatabaseChangeLog result = new DatabaseChangeLog("change-log.xml");
		if (!changeActions.isEmpty()) {
			ChangeSet changeSet =
					new ChangeSet("ADD-CHANGE-SET-ID-HERE", "dm-comp", false, true, null, null, null, result);
			result.addChangeSet(changeSet);
			List<Change> postChanges = new ArrayList<>();
			changeActions
					.forEach(
							action -> createChange(action, configuration, postChanges)
									.ifPresent(l -> l.forEach(changeSet::addChange)));
			postChanges.forEach(changeSet::addChange);
		}
		return result;
	}

	private Optional<List<Change>> createChange(ChangeActionCRO action, ChangeProcessorConfiguration configuration, List<Change> postChanges) {
		return CHANGE_PROCESSORS
				.stream()
				.filter(processor -> processor.isToProcess(action))
				.findFirst()
				.map(processor -> processor.process(action, configuration, postChanges));
	}

}
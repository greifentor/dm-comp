package de.ollie.dbcomp.liquibase.writer.processors;

import java.util.List;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import liquibase.change.Change;

public interface ChangeProcessor {

	boolean isToProcess(ChangeActionCRO action);

	List<Change> process(ChangeActionCRO action);

}
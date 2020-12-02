package de.ollie.dbcomp.liquibase.writer.processors;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import liquibase.change.Change;

public interface ChangeProcessor {

	boolean isToProcess(ChangeActionCRO action);

	Change process(ChangeActionCRO action);

}
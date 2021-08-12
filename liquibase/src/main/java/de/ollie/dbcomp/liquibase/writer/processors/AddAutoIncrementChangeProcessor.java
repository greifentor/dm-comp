package de.ollie.dbcomp.liquibase.writer.processors;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddAutoIncrementChangeActionCRO;
import de.ollie.dbcomp.comparator.model.actions.AddColumnChangeActionCRO;
import liquibase.change.AddColumnConfig;
import liquibase.change.Change;
import liquibase.change.ConstraintsConfig;
import liquibase.change.core.AddAutoIncrementChange;
import liquibase.change.core.AddColumnChange;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class AddAutoIncrementChangeProcessor extends AbstractChangeProcessor {

	@Override
	public boolean isToProcess(ChangeActionCRO action) {
		return action.getClass() == AddAutoIncrementChangeActionCRO.class;
	}

	@Override
	public List<Change> process(ChangeActionCRO action, ChangeProcessorConfiguration configuration,
	                            List<Change> postChanges) {
		AddAutoIncrementChangeActionCRO addAction = (AddAutoIncrementChangeActionCRO) action;
		AddAutoIncrementChange change = new AddAutoIncrementChange();
		change.setColumnName(addAction.getColumnName());
		change.setColumnDataType(addAction.getDataType());
		change.setSchemaName(getSchemaName(addAction, configuration));
		change.setTableName(addAction.getTableName());
		change.setStartWith(BigInteger.ONE);
		return Arrays.asList(change);
	}

}
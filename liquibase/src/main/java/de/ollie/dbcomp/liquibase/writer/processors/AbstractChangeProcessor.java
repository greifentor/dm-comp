package de.ollie.dbcomp.liquibase.writer.processors;

import de.ollie.dbcomp.comparator.model.ChangeActionCRO;

public abstract class AbstractChangeProcessor implements ChangeProcessor {

	protected String getSchemaName(ChangeActionCRO changeAction, ChangeProcessorConfiguration configuration) {
		return getSchemaName(changeAction.getSchemaName(), configuration);
	}

	protected String getSchemaName(String schemaName, ChangeProcessorConfiguration configuration) {
		return configuration.isSchemeNameToSet() ? schemaName : null;
	}

}
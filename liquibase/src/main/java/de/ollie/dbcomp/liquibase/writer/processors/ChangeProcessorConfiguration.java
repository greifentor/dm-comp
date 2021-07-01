package de.ollie.dbcomp.liquibase.writer.processors;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ChangeProcessorConfiguration {

	private boolean schemeNameToSet = true;

}
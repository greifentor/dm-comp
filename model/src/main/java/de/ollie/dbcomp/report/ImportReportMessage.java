package de.ollie.dbcomp.report;

import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * A container for import reports messages.
 * 
 * @author Oliver.Lieshoff (26.11.2020)
 */
@Accessors(chain = true)
@Data
@Generated
@NoArgsConstructor
public class ImportReportMessage {

	private ImportReportMessageLevel level = ImportReportMessageLevel.INFO;
	private String message;

}
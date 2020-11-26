package de.ollie.dbcomp.model;

import de.ollie.dbcomp.report.ImportReport;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * A container for reader results.
 * 
 * @author Oliver.Lieshoff (26.11.2020)
 */
@Accessors(chain = true)
@Data
@Generated
@NoArgsConstructor
public class ReaderResult {

	private ImportReport importReport;
	private DatamodelCMO datamodel;

}
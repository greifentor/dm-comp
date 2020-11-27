package de.ollie.dbcomp.report;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * A container for import reports.
 * 
 * @author Oliver.Lieshoff (26.11.2020)
 */
@Accessors(chain = true)
@Data
@Generated
@NoArgsConstructor
public class ImportReport {

	private List<ImportReportMessage> messages = new ArrayList<>();

	public ImportReport addMessages(ImportReportMessage... messages) {
		for (ImportReportMessage message : messages) {
			this.messages.add(message);
		}
		return this;
	}

	public List<ImportReportMessage> getMessagesForLevel(ImportReportMessageLevel level) {
		return messages //
				.stream() //
				.filter(message -> message.getLevel() == level) //
				.collect(Collectors.toList()) //
		;
	}

}
package de.ollie.dbcomp.model;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison model object for index.
 * 
 * @author ollie (07.05.2021)
 *
 */
@Accessors(chain = true)
@Data
public class IndexCMO {

	private String name;
	private TableCMO table;
	private List<ColumnCMO> memberColumns;

}
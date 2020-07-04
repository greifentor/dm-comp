package de.ollie.dbcomp.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A comparison object model for sequences of schemes.
 * 
 * @author ollie (04.07.2020)
 *
 */
@Accessors(chain = true)
@Data
public class SequenceCMO {

	private int increment;
	private String name;
	private int start;

	private SequenceCMO() {
		super();
	}

	public static SequenceCMO of(String name, int increment, int start) {
		return new SequenceCMO() //
				.setIncrement(increment) //
				.setName(name) //
				.setStart(start) //
		;
	}

}
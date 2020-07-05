package de.ollie.dbcomp.processor;

import de.ollie.dbcomp.result.ComparisonResultCRO;

/**
 * An interface which is to implement by comparison result processor classes.
 *
 * @author ollie (05.07.2020)
 */
public interface ComparisonResultProcessor {

	/**
	 * Processes the passed comparison result e. g. to an SQL update, a Liquibase script or some thing else.
	 * 
	 * @param comparisonResult The comparison result which should be processed.
	 * @throws ComparisonResultProcessorException If some thing went wrong while processing the comparison result.
	 */
	void process(ComparisonResultCRO comparisonResult) throws ComparisonResultProcessorException;

}
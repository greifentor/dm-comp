package de.ollie.dbcomp.processor;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class ComparisonResultProcessorExceptionTest {

	private static final RuntimeException CAUSE = new RuntimeException();
	private static final String MESSAGE = "message";

	private ComparisonResultProcessorException unitUnderTest = new ComparisonResultProcessorException(MESSAGE, CAUSE);

	@Test
	public void constructorSetCauseCorrectly() {
		assertThat(unitUnderTest.getCause(), equalTo(CAUSE));
	}

	@Test
	public void constructorSetMessageCorrectly() {
		assertThat(unitUnderTest.getMessage(), equalTo(MESSAGE));
	}

}
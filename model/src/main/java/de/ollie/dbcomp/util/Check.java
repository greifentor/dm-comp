package de.ollie.dbcomp.util;

/**
 * A utility class for checkers.
 *
 * @author ollie
 *
 */

public class Check {

	Check() {
		throw new UnsupportedOperationException("It's an utility class! Do not instantiate!");
	}

	/**
	 * Checks if the passed condition is "true" or throws the passed exception.
	 *
	 * @param condition        The condition which is to check.
	 * @param runtimeException The exception to throw.
	 * @throws NullPointerException Passing a null value as exception an a "false" condition.
	 */
	public static void ensure(boolean condition, RuntimeException runtimeException) {
		if (!condition) {
			throw runtimeException;
		}
	}

	/**
	 * Checks if the passed condition is "true" or throws an IllegalArgumentException with passed message.
	 *
	 * @param condition The condition which is to check.
	 * @param message   The message for the IllegalArgumentException.
	 */
	public static void ensure(boolean condition, String message) {
		if (!condition) {
			throw new IllegalArgumentException(message);
		}
	}

}
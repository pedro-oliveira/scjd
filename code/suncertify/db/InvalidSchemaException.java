/*
 * InvalidSchemaException.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.db;

/**
 * This exception should be thrown when a inconsistent schema is read
 * from the database.
 * 
 * @author Pedro Oliveira
 * @version 1.0
 * @see Exception
 */
public class InvalidSchemaException extends Exception {

	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code InvalidSchemaException} with no detail message.
	 */
	public InvalidSchemaException() {
		super();
	}

	/**
	 * Constructs a {@code InvalidSchemaException} with the specified
	 * detail message.
	 * 
	 * @param msg the detail message.
	 */
	public InvalidSchemaException(String msg) {
		super(msg);
	}
}

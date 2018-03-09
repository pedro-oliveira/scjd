/*
 * UnknownDBException.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.db;


/**
 * The exception is thrown when the database id read from the database header
 * does not match the one expected.
 * 
 * @author Pedro Oliveira
 * @version 1.0
 * @see Exception
 */
public class UnknownDBException extends Exception {

	/**
	 * Default serial version UID. 
	 */
    private static final long serialVersionUID = 1L;
    
	/**
	 * Constructs a {@code UnknownDBException} with no detail message.
	 */
	public UnknownDBException() {
		super();
	}

	/**
	 * Constructs a {@code UnknownDBException} with the specified
	 * detail message.
	 * 
	 * @param msg the detail message.
	 */
	public UnknownDBException(String msg) {
		super(msg);
	}	    

}

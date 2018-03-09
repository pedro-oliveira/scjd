/*
 * Controller.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;

import java.io.IOException;

import javax.swing.JOptionPane;

import suncertify.db.DB;
import suncertify.db.DuplicateKeyException;
import suncertify.db.Record;
import suncertify.db.RecordNotFoundException;
import suncertify.db.InvalidSchemaException;
import suncertify.db.UnknownDBException;


/**
 * This class handles all requests from the client user interface to
 * the data model. 
 * 
 * @author Pedro Oliveira
 * @version 1.3
 * @see ConfigOptions
 * @see RecordTableModel
 * @see ClientGui
 * @see GuiException
 */
public class Controller {

	/**
	 * Reference to the database interface. 
	 */
	private DB connection;

	/**
	 * The {@code String} title in a message of a booking error.
	 */
	private static final String BOOK_ERR_TITLE = "Booking not possible";

	/**
	 * The {@code String} title in a message of a connection error.
	 */
	private static final String CONN_ERR_TITLE = "Could not get connection";

	/**
	 * The {@code String} title in a message of a search error.
	 */
	private static final String SRCH_ERR_TITLE = "Search was not possible";    

	/**
	 * The {@code JOptionPane} error message type.
	 */
	private static final int ERR_MSG_TYPE = JOptionPane.ERROR_MESSAGE;

	/**
	 * The {@code JOptionPane} warning message type.
	 */
	private static final int WARN_MSG_TYPE = JOptionPane.WARNING_MESSAGE;    

	/**
	 * Creates an instance of the {@code Controller} class using the options
	 * configured by the user, that are used in the process of establishing
	 * a database connection.
	 * 
	 * @param options the configuration options selected by the user, used
	 * in the database connection.
	 * @throws GuiException Thrown if a connection to the database was not
	 * possible.
	 */
	public Controller(ConfigOptions options) throws GuiException {
		try {
			this.connection = DBConnector.getConnection(options);
		} catch (UnknownDBException e) {
			String msg = "Unknown database! Expecting a different schema.";
			throw new GuiException(CONN_ERR_TITLE, msg, ERR_MSG_TYPE);
		} catch (InvalidSchemaException e) {
			String msg = "Database with an invalid schema.";
			throw new GuiException(CONN_ERR_TITLE, msg, ERR_MSG_TYPE);
		} catch (IOException e) {
			String msg = "Error accessing database. Reason: " + e.getMessage();
			throw new GuiException(CONN_ERR_TITLE, msg, ERR_MSG_TYPE);
		}	
	} 

	/**
	 * Returns the {@code RecordTableModel} containing all records that met the
	 * search criteria. All fields in the criteria array are evaluated for 
	 * matching the start of the respective field. A <tt>null</tt> value 
	 * matches any field value.
	 *
	 * @param criteria The user defined search String
	 * @return the {@code RecordTableModel} containing all records that met the
	 * search criteria.
	 * @throws GuiException Indicates a problem in the database or network
	 * connection.
	 */
	public RecordTableModel search(String[] criteria) throws GuiException {
		RecordTableModel tableModel = new RecordTableModel();

		// Get record numbers that match the specified criteria.
		int[] recNumbers = this.connection.find(criteria);

		// Add records to the table model.
		try {
			for (int recNo : recNumbers) {
				String[] data = this.connection.read(recNo);
				Record record = new Record(data);
				tableModel.addRecord(record);
			}
		} catch (RecordNotFoundException e) {
			String msg = "Records not found!";
			throw new GuiException(SRCH_ERR_TITLE, msg, ERR_MSG_TYPE); 
		}        
		return tableModel;
	}

	/**
	 * Retrieves the {@code RecordTableModel} with all records from the 
	 * database.
	 *
	 * @return the {@code RecordTableModel} containing all records.
	 * @throws GuiException Indicates a problem in the database or network
	 * connection.
	 */
	public RecordTableModel getAllRecords() throws GuiException {
		RecordTableModel tableModel = new RecordTableModel();
		
		/*
		 * Assumption: number of columns in the table model match the database
		 * number of columns.
		 */
		int numberOfColumns = tableModel.getColumnCount();

		// Default values are "null" as needed for the search method.
		String[] criteria = new String[numberOfColumns];	 
		return search(criteria);
	}  

	/**
	 * This method performs the booking operation in the database.
	 * 
	 * @param record the {@code Record} to book in the database.
	 * @throws GuiException Indicates a problem in the database or network
	 * connection.
	 */
	public void bookRecord(Record record) throws GuiException {
		updateRecord(record, true);
	}

	/**
	 * This method performs the returning operation in the database.
	 * 
	 * @param record the {@code Record} to return in the database.
	 * @throws GuiException Indicates a problem in the database or network
	 * connection.
	 */
	public void returnRecord(Record record) throws GuiException {
		updateRecord(record, false);
	}    

	/**
	 * This method performs the booking or return operation depending on the
	 * <tt>isBooking</tt> flag. A <tt>true</tt> value is used for the 
	 * booking operation and the <tt>false</tt> value for the return. 
	 */
	private void updateRecord(Record record, boolean isBooking) 
			throws GuiException {
		long cookie = 0L;

		// ASSUMPTION: Is never going to be possible a change in the pr.keys.
		Record pkRecord = record.getPkRecord();
		int recNo = 0;
		try {
			recNo = getRecordNumber(pkRecord);
		} catch (RecordNotFoundException e1) {
			String msg = "Record is not available any more. " +
					"Refresh your search.";
			throw new GuiException(BOOK_ERR_TITLE, msg, WARN_MSG_TYPE);	    
		} catch (DuplicateKeyException e1) {
			String msg = "Inconsistent database! Reason: primary key " +
					"violation. Please contact the Database Administrator";
			throw new GuiException(BOOK_ERR_TITLE, msg, ERR_MSG_TYPE);	    
		}

		// 
		try {														
			cookie = this.connection.lock(recNo);				    
			/*
			 * Is necessary to check if the record was not changed
			 * or deleted after the last search.
			 */			
			String[] dbData = this.connection.read(recNo);
			Record dbRecord = new Record(dbData);

			if (isBooking) {							// Booking a record.
				if (dbRecord.isAvailable()) {
					String[] data = record.getStringArray();
					this.connection.update(recNo, data, cookie);
				} else {
					String msg = "Record is not available any more. " 
							+ "Refresh your search.";
					throw new GuiException(BOOK_ERR_TITLE, msg, WARN_MSG_TYPE);
				}		
			} else {									// Returning a record.
				
				// Validation to verify database consistency.
				if (dbRecord.isAvailable()) {
					String msg = "Record is already available. " 
							+ "No action taken.";
					throw new GuiException(BOOK_ERR_TITLE, msg, WARN_MSG_TYPE);
				} else {
					String[] data = record.getStringArray();
					this.connection.update(recNo, data, cookie);
				}				
			}	    	    	    
		} catch(RecordNotFoundException e) {
			String msg = "Record not found! Refresh your search.";
			throw new GuiException(BOOK_ERR_TITLE, msg, ERR_MSG_TYPE);
		} catch(SecurityException e) {
			String msg = "Concurrency error! Please retry the operation.";
			throw new GuiException(BOOK_ERR_TITLE, msg, ERR_MSG_TYPE);
		} finally {
			try {
				this.connection.unlock(recNo, cookie);
			} catch(RecordNotFoundException e) {
				String msg = "Record not found! Refresh your search.";
				throw new GuiException(BOOK_ERR_TITLE, msg, ERR_MSG_TYPE);
			} catch(SecurityException e) {
				String msg = "Unlocking error!";
				throw new GuiException(BOOK_ERR_TITLE, msg, ERR_MSG_TYPE);
			}
		}
	}

	/**
	 * Returns the record number of the given {@code Record}.
	 */
	private int getRecordNumber(Record record) throws RecordNotFoundException,
			DuplicateKeyException {
		int recNo = 0;

		// Get the record number for the given record.
		int[] records = this.connection.find(record.getStringArray());
		if (records.length == 0) {
			throw new RecordNotFoundException();
		} else if (records.length != 1) {
			throw new DuplicateKeyException();	// Inconsistent database.
		} else {
			recNo = records[0];
		}
		return recNo;
	}
}

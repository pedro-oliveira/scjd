/*
 * Schema.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.db;


/**
 * Represents the information retrieved from the database header.
 * 
 * @author Pedro Oliveira
 * @version 1.1
 */
class Schema {
	
	/**
	 * The number of bytes of a record.
	 */
	private int recordSize;

	/**
	 * The number of fields (columns) of the database.
	 */
	private int numberOfColumns;
	
	/**
	 * An array of strings with the column names.
	 */
	private String[] columnNames;
	
	/**
	 * An array of integers with the column sizes in bytes. 
	 */
	private int[] columnSizes;
	
	/**
	 * Creates an instance of this object with the specified values
	 * for the record size and number of columns.
	 * 
	 * @param recordSize the number of bytes of a record.
	 * @param numberOfColumns the number of columns in the database.
	 */
	public Schema(int recordSize, int numberOfColumns) {
		this.recordSize = recordSize;
		this.numberOfColumns = numberOfColumns;
		this.columnNames = new String[numberOfColumns];
		this.columnSizes = new int[numberOfColumns];
	}
	
	/**
	 * Adds the field information, column name and bytes, to the schema
	 * object.
	 * 
	 * @param index the index position of the data to add.
	 * @param name column name for the respective index.
	 * @param size number of bytes for this field.
	 */
	public void addColumn(int index, String name, int size) {
		this.columnNames[index] = name;
		this.columnSizes[index] = size;
	}
	
	/**
	 * Returns the column name for the respective index position.
	 * 
	 * @param index the index position of column name to retrieve.
	 * @return the column name for the given index.
	 */
	public String getColumnName(int index) {
		return this.columnNames[index];
	}
	
	/**
	 * Returns the field size in bytes for the respective index position.
	 * 
	 * @param index the index position of column name to retrieve.
	 * @return the size in bytes for the given index field.
	 */
	public int getColumnSize(int index) {
		return this.columnSizes[index];
	}

	/**
	 * Get the length, in bytes, of every data record.
	 * 
	 * @return the number of bytes in every record of data.
	 */
	public int getRecordSize() {
		return this.recordSize;
	}

	/**
	 * Returns the number of fields/columns that exists in this
	 * database schema.
	 * 
	 * @return the number of columns provided in this database.
	 */
	public int getNumberOfColumns() {
		return this.numberOfColumns;
	}	
	
	@Override
	public String toString() {
		String s = "Record Size: " + this.recordSize + "\n" + 
			   	   "Nbr of Columns: " + this.numberOfColumns + "\n";		
		for (int i = 0; i < this.columnNames.length; i++) {
			s += "Column Name: " + this.columnNames[i] + "\t" +
				 "Column Size: " + this.columnSizes[i] + "\n"; 
		}
		return s;
	}
}
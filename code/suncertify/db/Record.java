/*
 * Record.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.db;

import java.io.IOException;
import java.util.Properties;

import suncertify.utils.PropertyHelper;
import suncertify.utils.Utilities;


/**
 * The {@code Record} object is a representation of a record of the database.
 * This class can be parameterized to work with multiple database's, making 
 * it abstract to the actual data it stores.
 * 
 * @author Pedro Oliveira
 * @version 1.5
 */
public class Record {

	/**
	 * This field has the index value of the data array which indicates if the
	 * record is available for reservation/booking. 
	 */
	private static final int AVAILABILITY_INDEX;    

	/**
	 * An integer array with the indices that represent the primary key.
	 */
	private static int[] keyIndices;

	/**
	 * This initializer block loads property values from the application
	 * properties file.
	 */
	static {	  
		Properties prop;
		try {
			prop = Utilities.loadProperties("suncertify.properties");
		} catch (IOException e) {
			prop = System.getProperties();
		}			
		PropertyHelper propHelper = new PropertyHelper(prop);
		keyIndices = propHelper.getPropertiesAsIntegerArray("db.keyIndices");	
		AVAILABILITY_INDEX = propHelper.getPropertyAsInteger("db.availIndex");
	}

	/**
	 * The {@code String} array with the actual data of the record.
	 */
	private String[] data;

	/**
	 * This flag indicates if the record has been deleted or not.
	 */
	private boolean isDeleted;

	/**
	 * This Constructor creates a {@code Record} reference with the data
	 * given as parameter and sets the record as not deleted.
	 * 
	 * @param data the array of strings containing the actual record data.
	 */
	public Record(String[] data) {
		this.data = data;
		this.isDeleted = false;
	}    

	/**
	 * Constructs a {@code Record} reference with the specified data and 
	 * deleted flag.
	 * 
	 * @param data the array of strings containing the actual record data. 
	 * @param deletedFlag indicates if the record is deleted ("1") or 
	 * not ("0").
	 */
	public Record(String[] data, int deletedFlag) {
		this.data = data;
		this.isDeleted = (deletedFlag == 1 ? true : false);
	}

	/**
	 * Return the {@code String} element given by the index position.
	 * 
	 * @param index position in the record for the requested field.
	 * @return the element given by the index position.
	 */
	public String get(int index) {
		return this.data[index];
	}

	/**
	 * Sets the current record with {@code value} in the index position. 
	 * 
	 * @param index position in the record for the requested field.
	 * @param value the element to set in the in the index position.
	 */
	public void set(int index, String value) {
		this.data[index] = value;
	}  

	/**
	 * Returns a {@code String} array with the data hold by the record.
	 * 
	 * @return an array of strings with the record data.
	 */
	public String[] getStringArray() {
		return this.data;
	}


	/**
	 * Updates the record data with the new array of strings passed
	 * as parameter.
	 * 
	 * @param data an array of strings with the new record data.
	 */
	public void update(String[] data) {
		this.data = data;
	}  

	/**
	 * Returns <tt>true</tt> if each {@code non-null} field in specified
	 * criteria is part of the start of each element in data record. <p>
	 * 
	 * The elements in the search criteria that are {@code null} match
	 * any data record field value.
	 * 
	 * @param criteria an array of strings with the expressions to match.
	 * @return <tt>true</tt> if the record data matches the specified
	 * criteria and false otherwise.
	 */
	public boolean matches(String[] criteria) {				
		for (int i = 0; i < this.data.length; i++) {
			String field = this.data[i];
			String regex = criteria[i];
			if (regex == null) {
				continue;					// null matches any field value.
			} else if (field.startsWith(regex)){
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Set the record as being deleted(<tt>true</tt>) or not (<tt>false</tt>).
	 * 
	 * @param isDeleted the {@code boolean} flag indicating if the record 
	 * has been deleted (<tt>true</tt>) or not (<tt>false</tt>).
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * Returns <tt>true</tt> if the record has been deleted or <tt>false</tt>
	 * otherwise.
	 * 
	 * @return <tt>true</tt> if the record has been deleted or <tt>false</tt>
	 * otherwise.
	 */
	public boolean isDeleted() {
		return this.isDeleted;
	}	 

	/**
	 * Returns <tt>true</tt> if the record is available for reservation
	 * or <tt>false</tt> otherwise. <p> 
	 * 
	 * A record is considered available if the field which referes to 
	 * the availability is empty (available) or not (unavailable).
	 * 
	 * @return <tt>true</tt> if the record is available for reservation
	 * or <tt>false</tt> otherwise.
	 */
	public boolean isAvailable() {	
		String field = this.data[AVAILABILITY_INDEX];
		return field.trim().isEmpty();
	}

	/**
	 * Update availability status for the value specified as parameter.
	 */
	public void changeAvailability(String value) {	
		this.data[AVAILABILITY_INDEX] = value;
	}    

	/**
	 * Returns a subset of the data record, with only the elements that are
	 * part of the primary key.
	 * 
	 * @return an array of strings with only the data of the primary key 
	 * elements.
	 */
	public String[] clearNonKeyValues() {
		String[] onlyPKeys = new String[this.data.length];
		for (int i = 0; i < keyIndices.length; i++) {
			int keyIndex = keyIndices[i];
			onlyPKeys[keyIndex] = data[keyIndex];
		}
		return onlyPKeys;
	}

	/**
	 * Gets this object record with elements that are not part of the
	 * primary key set to {@code null}.
	 * 
	 * @return the {@code Record} of this object with only the primary
	 * key information.
	 */
	public Record getPkRecord() {
		Record pkRecord = new Record(clearNonKeyValues());
		return pkRecord;
	}

	
	// Object override methods.


	@Override    
	public String toString() {
		String s = "Is Deleted? " + this.isDeleted + "\n";
		for (String field : this.data) {
			s += field + "\n";
		}
		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;		

		/*
		 * Calculate hash code using the hash codes of the primary keys.
		 * A prime number is used in the calculations to improve efficiency.
		 */
		for (int keyIndex : keyIndices) {
			int keyHash = this.data[keyIndex].hashCode();
			result += prime * result + keyHash;
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}		
		if (obj == null || !(obj instanceof Record)) {
			return false;
		}	
		
		Record other = (Record) obj;

		// Two records are equal if their primary keys values are equal.
		for (int keyIndex : keyIndices) {	    
			String key = this.data[keyIndex];
			String otherKey = other.data[keyIndex];
			if (key == null && otherKey != null) {
				return false;
			} else if (!key.equals(otherKey)) {
				return false;
			}
		}
		return true;
	}   
}

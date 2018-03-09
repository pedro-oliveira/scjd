/*
 * Data.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.db;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import suncertify.utils.PropertyHelper;
import suncertify.utils.Utilities;


/**
 * This class does all the access manipulation relative to the physical
 * file that is the database.
 * 
 * @author Pedro Oliveira
 * @see DB
 * @version 1.5
 */
public class Data implements DB {

	/**
	 * The database Id or magic cookie.
	 */
	private static final int COOKIE;

	/**
	 * Character enconding presented in the database file.
	 */
	private static final String CHARSET;

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
		COOKIE = propHelper.getPropertyAsInteger("db.cookie");
		CHARSET = propHelper.getPropertyAsString("db.charset");
	}        

	/**
	 * The reference to access database information.
	 */
	private RandomAccessFile database;

	/**
	 * The database schema object retrieved from file.
	 */
	private Schema schema;

	/**
	 * The offset from the beginning of the data section in the file, 
	 * in bytes, at which the first read or write occurs. 
	 */
	private long dataOffset;

	/**
	 * The map which maps between record numbers and cookie values.
	 */
	private Map<Integer, Long> cookieMap;

	/**
	 * The set with the record numbers of all the deleted records.
	 */
	private Set<Integer> deletedRecNumbers;

	/**
	 * The initial number of records in the database.
	 */
	private int initialNumberOfRecords;

	/**
	 * 
	 * 
	 * @param dbPath the path to the database file.
	 * @throws FileNotFoundException if the database file cannot be located.
	 * @throws IOException if the user has no permissions for manipulating the
	 * file.
	 * @throws UnknownDBException if the database magic cookie is different
	 * from the expected.
	 * @throws InvalidSchemaException if the header information is not
	 * consistent with the number of records in the database.
	 */
	public Data(String dbPath) throws FileNotFoundException, IOException,
			UnknownDBException, InvalidSchemaException {
		this.cookieMap = new HashMap<Integer, Long>();
		this.database = new RandomAccessFile(dbPath, "rw");		

		// Read start of database file and validate database id.
		int magicCookie = this.database.readInt();	
		if (magicCookie != COOKIE) {
			throw new UnknownDBException();
		}	
		int recSize = this.database.readInt();
		short nbrOfFields = this.database.readShort();

		// Create schema using header information.
		buildSchema(recSize, nbrOfFields);	

		// Set variable with the current file position in bytes.
		this.dataOffset = this.database.getFilePointer();

		/*
		 * Initialize set with deleted record numbers and
		 * set initialNumberOfRecords variable. 
		 */
		this.deletedRecNumbers = getDeletedRecNumbers();

		// Validate the schema consistency.
		if(!isSchemaValid()) {
			throw new InvalidSchemaException();
		}
	}	

	/**
	 * Reads the header information presented in the database file and 
	 * creates a schema object dynamically. 
	 */
	private void buildSchema(int recSize, int nbrOfFields) throws IOException {
		this.schema = new Schema(recSize, nbrOfFields);	

		// Read database header and fill schema object.
		for (int i = 0; i < nbrOfFields; i++) {
			short numberOfBytes = this.database.readShort();
			byte[] fieldArray = new byte[numberOfBytes];
			this.database.readFully(fieldArray);
			String columnName = new String(fieldArray, CHARSET);			
			short fieldLength = this.database.readShort();
			this.schema.addColumn(i, columnName, fieldLength);
		}					
	}


	// Implemented methods from DB interface


	/* (non-Javadoc)
	 * @see suncertify.db.DB#create(String[])
	 */
	@Override
	public int create(String[] data) throws DuplicateKeyException {
		int recNo = 0;
		Record record = new Record(data);

		// Check for duplicate keys using the find() method.
		String[] criteria = record.clearNonKeyValues();
		int[] matches = find(criteria);
		if(matches.length > 0) {
			throw new DuplicateKeyException();
		}

		try {
			// If records have been deleted the numbers should be reused.
			synchronized (this.deletedRecNumbers) {
				if (!this.deletedRecNumbers.isEmpty()) {
					recNo = this.deletedRecNumbers.iterator().next();
					insertRecord(data);
					this.deletedRecNumbers.remove(recNo);
					return recNo;
				}
			}

			// If not, create a new record in the end of the database.
			synchronized (this.database) {
				this.database.seek(this.database.length());
				recNo = calculateRecordNumber(this.database.length());
				insertRecord(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return recNo;
	}

	/* (non-Javadoc)
	 * @see suncertify.db.DB#update(int,String[],long)
	 */
	@Override
	public void update(int recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {	
		
		long position = calculatePosition(recNo);

		// Before updating one must old the lock on the given record.
		if(!isCookieCorrect(recNo, lockCookie)) {
			throw new SecurityException();
		}

		synchronized (this.database) {
			try {
				this.database.seek(position);
				updateRecord(data);
			} catch (IOException e) {
				throw new RecordNotFoundException();
			}
		}			
	}	

	/* (non-Javadoc)
	 * @see suncertify.db.DB#delete(int,long)
	 */
	@Override
	public void delete(int recNo, long lockCookie) 
			throws RecordNotFoundException, SecurityException {

		long position = calculatePosition(recNo);

		// Before deleting one must old the lock on the given record.
		if(!isCookieCorrect(recNo, lockCookie)) {
			throw new SecurityException();
		}

		try {
			synchronized (this.database) {
				this.database.seek(position);
				deleteRecord();
			}
			synchronized (this.deletedRecNumbers) {
				this.deletedRecNumbers.add(recNo);
			}								
		} catch (IOException e) {
			throw new RecordNotFoundException();
		}
	}

	/* (non-Javadoc)
	 * @see suncertify.db.DB#find(String[])
	 */
	@Override
	public int[] find(String[] criteria) {
		List<Integer> matches = new ArrayList<Integer>();
		List<Record> records = new ArrayList<Record>();

		try {
			int recNo = 0;
			synchronized (this.database) {
				this.database.seek(this.dataOffset);
				records = getRecordList();
			}

			// Add record numbers for records that match the criteria.
			for (Record r : records) {
				if (!r.isDeleted() && r.matches(criteria)) {
					matches.add(recNo);
				}
				recNo++;
			}
		} catch (IOException e) {
			return Utilities.toIntArray(matches);
		}			
		return Utilities.toIntArray(matches);
	}

	/* (non-Javadoc)
	 * @see suncertify.db.DB#read(int)
	 */
	@Override
	public String[] read(int recNo) throws RecordNotFoundException {
		Record record = null;	
		
		long position = calculatePosition(recNo);

		synchronized (this.database) {
			try {
				this.database.seek(position);
				record = getRecord();
				if (record.isDeleted()) {
					throw new RecordNotFoundException();
				}
			} catch (IOException e) {
				throw new RecordNotFoundException();
			}	    
		}		
		return record.getStringArray();
	}


	// Lock and unlock methods


	/* (non-Javadoc)
	 * @see suncertify.db.DB#lock(int)
	 */
	@Override
	public long lock(int recNo) throws RecordNotFoundException {
		long cookie = 0;

		// Before locking the record one musth check if it exists.
		if (!hasRecord(recNo)) {
			throw new RecordNotFoundException();
		}

		/*
		 * Check the cookieMap to determine if the record as been locked and
		 * if it has wait for turn. Otherwise generated a cookie based on
		 * the current time and fill the map with locking information.
		 */		
		synchronized (this.cookieMap) {
			if (this.cookieMap.containsKey(recNo)) {
				try {
					this.cookieMap.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				cookie = System.currentTimeMillis();
				this.cookieMap.put(recNo, cookie);		
			}	    	   
		}	
		return cookie;
	}    

	/* (non-Javadoc)
	 * @see suncertify.db.DB#unlock(int, long)
	 */
	@Override
	public void unlock(int recNo, long lockCookie) 
			throws RecordNotFoundException, SecurityException {

		/*
		 * Checks the coookieMap for the record number and if it holds
		 * the lock notifies all threads waiting on the cookie map. 
		 */
		synchronized (this.cookieMap) {
			if (this.cookieMap.containsKey(recNo)) {
				long recordCookie = this.cookieMap.get(recNo);
				if (recordCookie == lockCookie) {
					this.cookieMap.remove(recNo);
					this.cookieMap.notifyAll();
				} else {
					throw new SecurityException();
				}
			} else {
				throw new RecordNotFoundException();
			}
		}	
	}

	/**
	 * Returns true if a record was found for the given record number and 
	 * false otherwise.
	 */
	private boolean hasRecord(int recNo) {

		long position = calculatePosition(recNo);

		try {
			synchronized (this.database) {
				this.database.seek(position);
				
				// First byte corresponds to the deleted flag.
				int deletedFlag = this.database.readByte();
				if (deletedFlag == 1) {
					return false;
				}				
			}	   
		} catch (IOException e) {
			return false;
		}
		return true;
	}		
	
	/**
	 * Returns true if the lock cookie passed has parameter is the correct
	 * cookie value locking the record number passed as parameter. 
	 */
	private boolean isCookieCorrect(int recNo, long lockCookie) {		
		synchronized (this.cookieMap) {
			if (this.cookieMap.containsKey(recNo)) {
				long actualCookie = cookieMap.get(recNo);
				return actualCookie == lockCookie;		
			} else {
				return false;
			}	    	   
		}	
	}


	// Database manipulation methods. 
	

	/**
	 * Inserts a data record in the current file position. It assumes
	 * the file pointer has just been set in the desired position.
	 * 
	 * Before inserting the record data, the first byte is set to "0" since it
	 * represents the deleted flag ("0" means the record is not deleted). 
	 */
	private void insertRecord(String[] data) throws IOException {

		// First byte corresponds to the deleted flag, must be set to "0".	 
		this.database.writeByte(0);

		// Using the database schema create the String array data dynamically.
		for (int i = 0; i < data.length; i++) {
			int size = this.schema.getColumnSize(i);
			byte[] field = new byte[size];
			field = data[i].getBytes(CHARSET);
			this.database.write(field);
		}		
	}    

	/**
	 * Deletes a data record in the current file position. It assumes
	 * the file pointer has just been set in the desired position.
	 * 
	 * The deletion process sets the first byte to "1" which means the record
	 * is deleted and than sets the remaing record size with null fields. 
	 */
	private void deleteRecord() throws IOException {	

		// First byte corresponds to the deleted flag, must be set to "1".	 
		this.database.writeByte(1);

		// Set the record with null fields. 
		byte[] recordArray = new byte[this.schema.getRecordSize()];	
		this.database.write(recordArray);	
	}    

	/**
	 * Updates a data record in the current file position. It assumes
	 * the file pointer has just been set in the desired position.
	 * 
	 * The first byte is skipped since it refers to the deleted flag. Than
	 * the remaing fields are updated with the respective string array data.
	 */
	private void updateRecord(String[] data) throws IOException {	

		// First byte corresponds to the deleted flag, which should be skipped. 
		this.database.skipBytes(1);	

		// Using the database schema create the String array data dynamically.
		for (int i = 0; i < data.length; i++) {
			int size = this.schema.getColumnSize(i);
			byte[] field = new byte[size];
			field = data[i].getBytes(CHARSET);
			this.database.write(field);
		}	
	}

	/**
	 * Returns the current Record object. It assumes the file pointer 
	 * has just been set in the desired position.
	 */
	private Record getRecord() throws RecordNotFoundException, IOException {
		String[] data = new String[this.schema.getNumberOfColumns()];

		// First byte corresponds to the deleted flag.	 
		int deletedFlag = this.database.readByte();

		// Read the full record from the database to a byte array. 
		byte[] recordArray = new byte[this.schema.getRecordSize()];
		this.database.readFully(recordArray);			

		// Using the database schema create the String array data dynamically.
		for (int i = 0, st = 0; i < data.length; i++) {
			int size = this.schema.getColumnSize(i);
			byte[] field = new byte[size];
			field = Arrays.copyOfRange(recordArray, st, st + size);			
			data[i] = new String(field, CHARSET);
			st = st + size;
		}	
		return new Record(data, deletedFlag);
	}    

	/**
	 * Returns a Record list with all elements in the database. It assumes
	 * the file pointer has just been set in the desired position. 
	 * 
	 * Typically the file pointer is going to be set with the dataOffset value.
	 */
	private List<Record> getRecordList() throws IOException {
		List<Record> recordList = new ArrayList<Record>();

		// Retrieve records until end-of-file occurs.
		while (true) {
			try {
				Record record = getRecord();					
				recordList.add(record);
			} catch (RecordNotFoundException e) {
				continue;			
			} catch (EOFException e) {
				return recordList;
			}		
		}
	}      

	/**
	 * Returns a set with the record numbers of all the deleted records and
	 * sets the initialNumberOfRecords variable with the number of total 
	 * records found (This avoids reading the database twice).
	 * 
	 * The set with the deleted records is orderer in natural order to make
	 * the insertion mechanism more fast.
	 */
	private Set<Integer> getDeletedRecNumbers() throws IOException {
		Set<Integer> deletedRecNumbers = new TreeSet<Integer>();
		int recNo = 0;
		
		// Retrieve records until end-of-file occurs.
		while (true) {
			try {
				Record record = getRecord();	
				if (record.isDeleted()) {
					deletedRecNumbers.add(recNo);
				}
				recNo++;
			} catch (RecordNotFoundException e) {
				continue;					// Deleted record skipped.
			} catch (EOFException e) {
				
				// No need to decrement because it starts with 0.
				this.initialNumberOfRecords = recNo;
				return deletedRecNumbers;	 
			}		
		}
	}  	

	/**
	 * Validates consistency between schema sizes. Calculate the database
	 * size using the schema information and the total number of records found
	 * and compares with the actual file size.
	 */
	private boolean isSchemaValid() throws IOException {

		// Calculated size.
		long actualRecordSize = this.schema.getRecordSize() + 1;
		long recordsSize = this.initialNumberOfRecords * actualRecordSize;		
		long size = this.dataOffset + recordsSize;

		return database.length() == size;
	}

	
	// Conversion methods.


	/**
	 * Calculates the number of bytes to get to the given record number.
	 */
	private long calculatePosition(int recNo) {	
		int totalRecordSize = this.schema.getRecordSize() + 1;
		return this.dataOffset + (recNo * totalRecordSize);
	}   

	/**
	 * Calculates the record number for the given position in number of bytes.
	 */
	private int calculateRecordNumber(long position) {
		int totalRecordSize = this.schema.getRecordSize() + 1;	
		int recNo = (int) ((position - this.dataOffset) / totalRecordSize);			
		return recNo;
	}   	
}

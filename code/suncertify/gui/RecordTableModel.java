/*
 * RecordTableModel.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import suncertify.db.Record;
import suncertify.utils.PropertyHelper;
import suncertify.utils.Utilities;


/**
 * 
 * 
 * @author Pedro Oliveira
 * @version 1.2 
 * @see AbstractTableModel
 * @see JTable
 * @see Record
 */
public class RecordTableModel extends AbstractTableModel {

	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This string array represents the table model header.
	 */
	private static final String[] COLUMN_NAMES;    

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
		COLUMN_NAMES = propHelper.getPropertiesAsStringArray("swing.columns");
	}

	/**
	 * The list of records in the table model.
	 */
	private List<Record> records = new ArrayList<Record>();		

	@Override
	public int getColumnCount() {	
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public int getRowCount() {
		return this.records.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		Record record = this.records.get(row);
		return record.get(column);
	}

	@Override
	public void setValueAt(Object obj, int row, int column) {
		Record record = this.records.get(row);
		record.set(column, (String) obj);
	}    

	/**
	 * This method adds a {@code Record} to the table model.
	 * 
	 * @param record the {@code Record} to add to table model.
	 */
	public void addRecord(Record record) {
		this.records.add(record);
	}   

	/**
	 * Returns the {@code Record} for the given row of this objects table 
	 * model.
	 * 
	 * @param row the row of the {@code Record} to return.
	 * @return the record at the specified row in this table model.
	 */
	public Record getRecord(int row) {
		return this.records.get(row);
	}  	
}

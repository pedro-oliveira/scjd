/*
 * Utilities.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.utils;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;
import java.util.Properties;


/**
 * This class provides access to static utility methods that could not
 * be logically grouped into other classes.
 *
 * 
 * @author Pedro Oliveira
 * @see Properties
 * @see List
 * @version 1.2 
 */
public class Utilities {

	/**
	 * Loads the properties inside the file specified and adds them
	 * to a <code>Properties</code> object initialized with the system
	 * properties.
	 * 
	 * @param filePath the path to the file with the properties.
	 * @return a <code>Properties</code> object with the file properties
	 * added to the system properties.
	 * @throws IOException if the properties file cannot be located.
	 */
	public static Properties loadProperties(String filePath) 
			throws IOException {
		InputStream is = null;
		Properties systemProperties = new Properties(System.getProperties());		
		try{
			is = new FileInputStream(filePath);    	        	        	   
			systemProperties.load(is);			
		} catch(IOException e){
			String msg = "Failed to locate file [" + filePath + "].";
			throw new IOException(msg);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return systemProperties;
	}	
	
	/**
	 * Saves the properties inside the file also specified.
	 * 
	 * @param prop the property list to be saved.
	 * @param filePath the path to the file with the properties.
	 * @throws IOException if the properties file cannot be located.
	 */
	public static void saveProperties(Properties prop, String filePath) 
			throws IOException {
		OutputStream os = null;				
		try{
			os = new FileOutputStream(filePath);    	        	        	   
			prop.store(os, " Configuration properties");			
		} catch(IOException e){
			String msg = "Failed to locate file [" + filePath + "].";
			throw new IOException(msg);
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}		

	/**
	 * Returns an integer array containing all of the elements in the list
	 * passed as argument. 
	 * 
	 * @param list the <code>List<Integer></code> for which elements are to
	 * be copied to an integer array. 
	 * @return an integer array with all the elements in the list.
	 */
	public static int[] toIntArray(List<Integer> list) {
		int[] array = new int[list.size()];
		for(int i = 0; i < array.length; i++) {
			array[i] = list.get(i);			
		}		
		return array;
	}       
}

/*
 * PropertyHelper.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.utils;


import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;


/**
 * Helper class to assist in the retrieval of values and typed values 
 * from any <code>Map</code> implementation classes, including 
 * <code>Properties</code> and <code>HashMap</code>. <p>
 * 
 * Allows default values to be specified and type conversions to be
 * performed.
 *  
 * @author Pedro Oliveira
 * @see Properties
 * @see Map
 * @version 1.4
 */
public class PropertyHelper {

	/**
	 * The default separator used to divide elements of a property 
	 * <code>String</code>.
	 */
	public static final String DEFAULT_SEPARATOR = ",";

	/**
	 * This <code>Map</code> contains all property elements to be 
	 * processed when using a <code>PropertyHelper</code> object. 
	 */
	private Map<Object, Object> properties = new HashMap<Object, Object>();

	/** 
	 * Creates a new instance of <code>PropertyHelper</code> containing
	 * the elements specified in the properties map.
	 * 
	 * @param properties the backing <code>Map</code> instance.
	 */
	public PropertyHelper(Map<Object, Object> properties) {
		this.properties = properties;
	}

	/** 
     * Returns the value to which the specified <code>String</code> key
     * is mapped, or {@code null} if the properties helper contains no 
     * mapping for the key.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @return the value to which the specified key is mapped, or
     * {@code null} if this map contains no mapping for the key.
     */
	public Object getProperty(String key) {
		return (this.properties != null) ? this.properties.get(key) : null;
	}

	/** 
     * Returns the value to which the specified <code>String</code> key
     * is mapped, or the <code>defaultValue</code> if the properties helper
     * contains no mapping for the key.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @return the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     */
	public Object getProperty(String key, Object defaultValue) {				
		return (getProperty(key) != null) ? this.properties.get(key) 
										  : defaultValue;
	}

	/** 
     * Returns the <code>String</code> value to which the specified 
     * <code>String</code> key is mapped, or {@code null} if the properties
     * helper contains no mapping for the key or if the mapped value cannot
     * be casted to a <code>String</code>.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @return the {@code String} value to which the specified key is 
	 * mapped, or {@code null} if this map contains no mapping for the key
	 * or a cast to <code>String</code> is not possible.
     */
	public String getPropertyAsString(String key) {
		Object obj = getProperty(key);
		return ((obj != null) && (obj instanceof String)) ? (String) obj 
														  : null;
	}

	/** 
     * Returns the <code>String</code> value to which the specified 
     * <code>String</code> key is mapped, or the {@code String defaultValue}
     * if the properties helper contains no mapping for the key.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @return the {@code String} value to which the specified key is 
	 * mapped, or the {@code String defaultValue} if this map contains no 
	 * mapping for the key.
     */
	public String getPropertyAsString(String key, String defaultValue) {
		String str = getPropertyAsString(key);
		return str != null ? str : defaultValue;
	}

	/** 
     * Returns the <code>Integer</code> value to which the specified 
     * <code>String</code> key is mapped, or {@code null} if the properties
     * helper contains no mapping for the key or if the mapped value cannot
     * be parsed to an <code>Integer</code>.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @return an {@code Integer} value to which the specified key is 
	 * mapped, or {@code null} if this map contains no mapping for the key
	 * or the value cannot be parsed to an {@code Integer}.
     */
	public Integer getPropertyAsInteger(String key) {
		String str = getPropertyAsString(key);
		return (str != null) ? Integer.valueOf(str) : null; 
	}

	/** 
     * Returns an <code>Integer</code> value to which the specified 
     * <code>String</code> key is mapped, or the {@code Integer defaultValue}
     * if the properties helper contains no mapping for the key.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @return an {@code Integer} value to which the specified key is 
	 * mapped, or an {@code Integer defaultValue} if this map contains no 
	 * mapping for the key.
     */
	public Integer getPropertyAsInteger(String key, Integer defaultValue) {
		Integer value = getPropertyAsInteger(key);
		return value != null ? value : defaultValue;
	}

	/**
	 * Returns the {@code String[]} with splitting results of applying the
	 * given regular expression to the property mappped by the key, or
	 * {@code null} if the properties helper contains no mapping for the
	 * key.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @param regex the delimiting regular expression.
	 * @return an array of strings with the matches of the given regular
	 * expression applied to {@code String } mapped value, or {@code null}
	 * if no mapping exists for the given key.
	 */
	public String[] getPropertiesAsStringArray(String key, String regex) {
		String str = getPropertyAsString(key);
		return (str != null) ? str.split(regex) : null;    	
	}

	/**
	 * Returns the {@code String[]} with splitting results of applying the
	 * default separator expression to the property mappped by the key, or
	 * {@code null} if the properties helper contains no mapping for the
	 * key.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @return an array of strings with the matches of the default regular
	 * expression applied to the {@code String} mapped value, or {@code null}
	 * if no mapping exists for the given key.
	 */
	public String[] getPropertiesAsStringArray(String key) {    	
		return getPropertiesAsStringArray(key, DEFAULT_SEPARATOR);   	
	}       

	/**
	 * Returns an integer array with splitting results of applying the
	 * default separator expression to the property mappped by the key, or
	 * {@code null} if the properties helper contains no mapping for the
	 * key.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @return an array of integers with the matches of the default regular
	 * expression applied to the {@code String} mapped value, or {@code null}
	 * if no mapping exists for the given key.
	 */
	public int[] getPropertiesAsIntegerArray(String key) {    		
		String[] strArray = getPropertiesAsStringArray(key, DEFAULT_SEPARATOR);
		if (strArray == null) {
			return null;
		}
		int[] array = new int[strArray.length];
		for (int i = 0; i < strArray.length; i++) {
			String s = strArray[i];
			array[i] = Integer.parseInt(s);
		}
		return array;
	}
	
	/** 
     * Sets the value to which the specified <code>String</code> key
     * is to be mapped. 
	 * 
	 * @param key the key whose associated value is to be set.
	 * @param value the value to set the property identified by the key.
     */
	public void setProperty(String key, Object value) {
		this.properties.put(key, value);
	}
	
	/**
	 * Returns a {@code Properties} object with the representation of the 
	 * properties inside this object.
	 * 
	 * @return a {@code Properties} with the properties inside this object.
	 */
	public Properties toProperties() {
		
		// Create a Properties object and override keys() for sorting the keys. 
		Properties p = new Properties(){
			
			/**
			 * The default serial version UID.
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public synchronized Enumeration<Object> keys() {
			     Enumeration<Object> keysEnum = super.keys();
			     TreeSet<Object> set = new TreeSet<Object>();
			     while (keysEnum.hasMoreElements()){
			       set.add(keysEnum.nextElement());
			     }
			     return Collections.enumeration(set);
			   }
		};
		for (Object property : this.properties.keySet()) {
			String value = getPropertyAsString(property.toString());
			p.setProperty(property.toString(), value);
		}
		return p;
	}
}

/**
 * Copyright (C) 2013 - 2014 <a href="http://www.wudsn.com" target="_top">Peter Dell</a>
 *
 * This file is part of a WUDSN software distribution.
 * 
 * The!Cart Studio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * The!Cart Studio distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the WUDSN software distribution. If not, see <http://www.gnu.org/licenses/>.
 */
package com.wudsn.tools.base.repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.wudsn.tools.base.common.Log;
import com.wudsn.tools.base.common.ResourceUtility;
import com.wudsn.tools.base.common.StringUtility;

public abstract class NLS {

    private static class ClassEntry {
	public ClassEntry(Class<? extends NLS> clazz, Class<?> containerClazz) {
	    if (clazz == null) {
		throw new IllegalArgumentException("Parameter 'clazz' must not be null.");
	    }
	    this.clazz = clazz;
	    this.containerClazz = containerClazz;
	}

	public final Class<? extends NLS> clazz;
	public final Class<?> containerClazz;
    }

    private static final String FILE_PATH = "$filePath";
    private final static String FILE_EXTENSION = ".properties";
    private static List<ClassEntry> initializedClasses;
    private static String[] suffixes;

    /**
     * Static initialization.
     */
    static {
	initializedClasses = new ArrayList<ClassEntry>();
	initializeLocale(Locale.getDefault().toString());
    }

    /**
     * Explicitly initialize the locale.
     * 
     * @param locale
     *            The locale, not empty and not <code>null</code>.
     */
    public static void initializeLocale(String locale) {
	if (locale == null) {
	    throw new IllegalArgumentException("Parameter 'locale' must not be null.");
	}
	if (StringUtility.isEmpty(locale)) {
	    throw new IllegalArgumentException("Parameter 'locale' must not be empty.");

	}

	synchronized (NLS.class) {

	    // Compute list of relevant properties file suffixes.
	    List<String> result = new ArrayList<String>(4);
	    int lastSeparator;
	    while (true) {
		result.add('_' + locale);
		lastSeparator = locale.lastIndexOf('_');
		if (lastSeparator == -1)
		    break;
		locale = locale.substring(0, lastSeparator);
	    }
	    result.add("");
	    suffixes = result.toArray(new String[result.size()]);

	    // Re-initialize the already registered classes.
	    for (ClassEntry entry : initializedClasses) {
		loadProperties(entry);
	    }

	}

    }

    /**
     * Creation is protected.
     */
    protected NLS() {
    }

    /**
     * Initialize the given class from its properties files.
     * 
     * @param clazz
     *            The class, not <code>null</code>.
     * @param containerClazz
     *            The representative class for the properties files, or
     *            <code>null</code> if <code>clazz</code> is already that class.
     */
    protected static void initializeClass(Class<? extends NLS> clazz, Class<?> containerClazz) {
	if (clazz == null) {
	    throw new IllegalArgumentException("Parameter 'clazz' must not be null.");
	}
	synchronized (NLS.class) {
	    ClassEntry entry = new ClassEntry(clazz, containerClazz);
	    loadProperties(entry);
	    initializedClasses.add(entry);
	}

    }

    /*
     * Load the given properties into the fields of the class.
     */
    private static void loadProperties(ClassEntry entry) {
	if (entry == null) {
	    throw new IllegalArgumentException("Parameter 'entry' must not be null.");
	}

	Class<? extends NLS> clazz = entry.clazz;
	Field[] fieldArray = clazz.getDeclaredFields();

	if ((clazz.getModifiers() & Modifier.PUBLIC) == 0) {
	    throw new RuntimeException("Class '" + clazz + "' is not public");
	}

	// Build a map of field names to Field objects
	final int length = fieldArray.length;
	Map<String, Object> fieldsMap = new HashMap<String, Object>(length);
	for (int i = 0; i < length; i++) {
	    Field field = fieldArray[i];
	    if (!field.isAccessible()) {
		field.setAccessible(true);
	    }
	    fieldsMap.put(fieldArray[i].getName(), field);
	}

	// By default, attribute have to be public static but not final.
	int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
	int MOD_MASK = MOD_EXPECTED;
	String pathPrefix;
	// ValueSets are separate sub classes with a common properties file
	// final final attributes.
	if (ValueSet.class.isAssignableFrom(clazz)) {
	    if (entry.containerClazz == null) {
		throw new RuntimeException("Class '" + clazz.getName()
			+ "' was registered without the required container class.");
	    }
	    pathPrefix = entry.containerClazz.getName();
	    MOD_EXPECTED |= Modifier.FINAL;
	} else {
	    pathPrefix = entry.clazz.getName();
	}
	pathPrefix = pathPrefix.replace('.', '/');
	MOD_MASK = MOD_EXPECTED | Modifier.FINAL;

	Properties[] properties = new Properties[suffixes.length];
	boolean error = false;
	boolean found = false;

	for (int i = 0; i < suffixes.length; i++) {
	    String specificPath = pathPrefix + suffixes[i] + FILE_EXTENSION;
	    // If there is no loader, the program was launched using the Java
	    // boot class path and the system class loader must be used.
	    byte[] input = ResourceUtility.loadResourceAsByteArray(specificPath);
	    if (input == null) {
		continue;
	    }
	    InputStream inputStream = new ByteArrayInputStream(input);
	    try {
		properties[i] = new Properties();
		properties[i].load(inputStream);
		properties[i].put(FILE_PATH, specificPath);
		found = true;
	    } catch (IOException ex) {
		Log.logError("Cannot load resource '{0}'.", new Object[] { specificPath }, ex);
		error = true;
	    }
	}
	if (!found) {
	    Log.logError("No resources for prefix '" + pathPrefix
		    + "' found in the classpath. Make sure there are no folder names with '!' characters involved.",
		    null, null);
	    error = true;
	} else {
	    clazz = entry.clazz;
	    for (Field field : fieldArray) {
		String prefix = field.getName();

		if ((field.getModifiers() & MOD_MASK) == MOD_EXPECTED) {
		    Object objectValue = null;

		    try {
			if (field.getType() == String.class) {
			    String text = getString(properties, prefix, "", true);
			    objectValue = text;

			} else if (field.getType() == Action.class) {
			    String label = getString(properties, prefix, ".label", true);
			    String toolTip = getString(properties, prefix, ".toolTip", false);
			    Action action = (Action) field.get(null);
			    // If the field is not yet initialized, initialize
			    // it with the texts only.
			    if (action == null) {
				objectValue = new Action(label, toolTip, null);
			    } else {
				// If the field is already initialized,
				// initialize it with the text and take
				// over the accelerator.
				objectValue = new Action(label, toolTip, action.getAccelerator());
			    }
			} else if (field.getType() == DataType.class) {
			    objectValue = field.get(null);
			    if (objectValue == null) {
				throw new RuntimeException("No data type instance in field '" + field + "'.");
			    }
			    DataType dataType = (DataType) objectValue;
			    String label = getString(properties, prefix, ".label", true);
			    String toolTip = getString(properties, prefix, ".toolTip", false);
			    dataType.setTexts(label, toolTip);
			    objectValue = dataType;

			} else if (ValueSet.class.isAssignableFrom(field.getType())) {
			    objectValue = field.get(null);
			    if (objectValue == null) {
				throw new RuntimeException("No value set instance in field '" + field + "'.");
			    }
			    ValueSet valueSet = (ValueSet) objectValue;
			    String idPath = valueSet.getClass().getName()
				    .substring(valueSet.getClass().getPackage().getName().length() + 1);
			    idPath = idPath + "_" + valueSet.getId();
			    String text = getString(properties, idPath, "", true);
			    valueSet.setText(text);
			    continue;

			} else if (field.getType() == Message.class) {
			    char firstChar = prefix.charAt(0);
			    int severity;
			    switch (firstChar) {
			    case 'S':
				severity = Message.STATUS;
				break;
			    case 'I':
				severity = Message.INFO;
				break;
			    case 'E':
				severity = Message.ERROR;
				break;
			    default:
				throw new RuntimeException("Unsupported message type '" + firstChar + "'.");
			    }
			    String shortText = getString(properties, prefix, "", true);
			    objectValue = new Message(prefix, severity, shortText);

			} else {
			    throw new RuntimeException("Unsupported field type " + field.getType() + ".");
			}
			field.set(null, objectValue);

		    } catch (Exception ex) {
			Log.logError("Cannot set value '{0}' for field '{1}' of class '{2}'.", new Object[] {
				objectValue, field.getName(), clazz.getName() }, ex);
			error = true;
		    }
		} else {
		    // Value sets can have arbitrary additional attributes.
		    if (!ValueSet.class.isAssignableFrom(clazz)) {
			Log.logError(
				"Cannot set value for field '{0}' of class '{1}'. Field is not public static or final.",
				new Object[] { field.getName(), clazz.getName() }, null);
			error = true;
		    }

		}
	    }
	}

	if (error) {
	    System.exit(-1);
	}
    }

    private static String getString(Properties[] properties, String prefix, String suffix, boolean mandatory) {
	if (properties == null) {
	    throw new IllegalArgumentException("Parameter 'properties' must not be null.");
	}
	if (properties.length == 0) {
	    throw new IllegalArgumentException("Parameter 'properties' must not be empty.");
	}
	if (prefix == null) {
	    throw new IllegalArgumentException("Parameter 'prefix' must not be null.");
	}
	if (suffix == null) {
	    throw new IllegalArgumentException("Parameter 'key' must not be null.");
	}
	String result;
	result = null;
	for (int i = 0; i < properties.length && result == null; i++) {
	    if (properties[i] == null) {
		continue;
	    }
	    String fullKey = prefix + suffix;
	    result = properties[i].getProperty(fullKey);
	    if (result == null && mandatory) {
		String filePath = properties[i].getProperty(FILE_PATH);
		Log.logError("Property '{0}' not defined in '{1}'.", new Object[] { fullKey, filePath }, null);
	    }
	}
	if (result == null) {
	    if (mandatory) {
		throw new RuntimeException("No text for mandatory key '" + prefix + suffix + "' in '"
			+ Arrays.toString(properties) + "'.");
	    }
	    result = "";
	}
	return result;
    }

    /*
     * Gets the a localized resource path.
     * 
     * @param path The path of the resource with '/' as path separators and with
     * file extension, not empty and not <code>null</code>.
     * 
     * @return The resource path or <code>null</code>.
     */
    public static String getResourcePath(String path) {
	if (path == null) {
	    throw new IllegalArgumentException("Parameter 'path' must not be null.");
	}
	int index = path.lastIndexOf('.');
	if (index == -1) {
	    throw new IllegalArgumentException("Parameter 'path' must have a file extension.");
	}
	String pathPrefix = path.substring(0, index);
	String fileExtension = path.substring(index);
	String result = null;
	for (int i = 0; i < suffixes.length && result == null; i++) {
	    String specificPath = pathPrefix + suffixes[i] + fileExtension;
	    if (ResourceUtility.loadResourceAsByteArray(specificPath) != null) {
		result = specificPath;
	    }
	}

	return result;
    }

}

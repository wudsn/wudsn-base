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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.wudsn.tools.base.common.StringUtility;

public final class Attribute {
    private static final int DEC = 10;
    private static final int HEX = 16;

    private String name;
    private DataType dataType;

    public Attribute(String name) {
	if (name == null) {
	    throw new IllegalArgumentException("Parameter 'name' must not be null.");
	}
	this.name = name;
	this.dataType = null;
    }

    public Attribute(String name, DataType dataType) {
	if (name == null) {
	    throw new IllegalArgumentException("Parameter 'name' must not be null.");
	}
	if (dataType == null) {
	    throw new IllegalArgumentException("Parameter 'dataType' must not be null.");
	}
	this.name = name;
	this.dataType = dataType;
    }

    public String getName() {
	return name;
    }

    public DataType getDataType() {
	if (dataType == null) {
	    throw new IllegalStateException("Attribute " + name + " has no data type.");
	}
	return dataType;
    }

    public void serializeBoolean(Element element, boolean value) {
	if (element == null) {
	    throw new IllegalArgumentException("Parameter 'element' must not be null.");
	}
	element.setAttribute(name, Boolean.toString(value));
    }

    public boolean deserializeBoolean(org.xml.sax.Attributes attributes, boolean defaultValue) {
	if (attributes == null) {
	    throw new IllegalArgumentException("Parameter 'attributes' must not be null.");
	}

	String value = attributes.getValue(name);
	boolean result = defaultValue;
	if (value != null) {
	    result = Boolean.parseBoolean(value);
	}
	return result;
    }

    public void serializeInteger(Element element, int value) {
	if (element == null) {
	    throw new IllegalArgumentException("Parameter 'element' must not be null.");
	}
	element.setAttribute(name, Integer.toString(value));
    }

    private static int deserializeInteger(org.xml.sax.Attributes attributes, String name, int base) throws SAXException {
	if (attributes == null) {
	    throw new IllegalArgumentException("Parameter 'attributes' must not be null.");
	}
	if (name == null) {
	    throw new IllegalArgumentException("Parameter 'name' must not be null.");
	}
	String value = attributes.getValue(name);
	int result = 0;
	if (value != null) {
	    try {
		result = Integer.parseInt(value, base);
	    } catch (NumberFormatException ex) {
		throw new SAXException("Attribute '" + name + "' cannot be deserialized, value '" + value
			+ "' is not an integer.");
	    }
	}
	return result;
    }

    public int deserializeInteger(org.xml.sax.Attributes attributes) throws SAXException {
	if (attributes == null) {
	    throw new IllegalArgumentException("Parameter 'attributes' must not be null.");
	}
	return deserializeInteger(attributes, name, DEC);
    }

    public void serializeLong(Element element, long value) {
	if (element == null) {
	    throw new IllegalArgumentException("Parameter 'element' must not be null.");
	}
	element.setAttribute(name, Long.toString(value));
    }

    public long deserializeLong(org.xml.sax.Attributes attributes) throws SAXException {
	if (attributes == null) {
	    throw new IllegalArgumentException("Parameter 'attributes' must not be null.");
	}

	String value = attributes.getValue(name);
	long result = 0;
	if (value != null) {
	    try {
		result = Long.parseLong(value);
	    } catch (NumberFormatException ex) {
		throw new SAXException("Attribute '" + name + "' cannot be deserialized, value '" + value
			+ "' is not a long.");
	    }
	}
	return result;
    }

    public void serializeLongAsHex(Element element, long value) {
	if (element == null) {
	    throw new IllegalArgumentException("Parameter 'element' must not be null.");
	}
	element.setAttribute(name, Long.toString(value, HEX).toUpperCase());
    }

    public long deserializeLongAsHex(org.xml.sax.Attributes attributes) throws SAXException {
	if (attributes == null) {
	    throw new IllegalArgumentException("Parameter 'attributes' must not be null.");
	}

	String value = attributes.getValue(name);
	long result = 0;
	if (value != null) {
	    try {
		result = Long.parseLong(value, HEX);
	    } catch (NumberFormatException ex) {
		throw new SAXException("Attribute '" + name + "' cannot be deserialized, value '" + value
			+ "' is not a long.");
	    }
	}
	return result;
    }

    public void serializeString(Element element, String value) {
	if (element == null) {
	    throw new IllegalArgumentException("Parameter 'element' must not be null.");
	}
	if (value != null) {
	    element.setAttribute(name, value);
	} else {
	    element.removeAttribute(name);
	}
    }

    public String deserializeString(org.xml.sax.Attributes attributes) {
	if (attributes == null) {
	    throw new IllegalArgumentException("Parameter 'attributes' must not be null.");
	}
	String result = attributes.getValue(name);
	return result;
    }

    public void serializeValueSet(Element element, ValueSet value) {
	if (element == null) {
	    throw new IllegalArgumentException("Parameter 'element' must not be null.");
	}
	if (value == null) {
	    throw new IllegalArgumentException("Parameter 'value' must not be null.");
	}
	element.setAttribute(name, value.getId());
    }

    @SuppressWarnings("unchecked")
    public <E extends ValueSet> E deserializeValueSet(org.xml.sax.Attributes attributes, Class<E> valueSetClass)
	    throws SAXException {
	if (attributes == null) {
	    throw new IllegalArgumentException("Parameter 'attributes' must not be null.");
	}
	if (valueSetClass == null) {
	    throw new IllegalArgumentException("Parameter 'valueSetClass' must not be null.");
	}
	String value = attributes.getValue(name);
	E result = null;
	if (value != null && StringUtility.isSpecified(value)) {
	    try {
		Method getInstanceMethod = valueSetClass.getMethod("getInstance", String.class);
		Object objectResult = getInstanceMethod.invoke(null, new Object[] { value });
		result = (E) objectResult;
	    } catch (IllegalAccessException ex) {
		throw new RuntimeException(ex);
	    } catch (IllegalArgumentException ex) {
		throw new RuntimeException(ex);
	    } catch (InvocationTargetException ex) {
		throw new RuntimeException(ex);
	    } catch (NoSuchMethodException ex) {
		throw new RuntimeException(ex);
	    } catch (SecurityException ex) {
		throw new RuntimeException(ex);
	    }
	    if (result == null) {
		throw new SAXException("Unknown value set id '" + value + "' for value set '" + valueSetClass.getName()
			+ "' of atribute '" + name + "'.");
	    }
	}
	return result;
    }

    public void serializeDimension(Element element, Dimension value) {
	if (element == null) {
	    throw new IllegalArgumentException("Parameter 'element' must not be null.");
	}
	final String widthName = name + ".width";
	final String heightName = name + ".height";
	if (value != null) {
	    element.setAttribute(widthName, Integer.toString(value.width));
	    element.setAttribute(heightName, Integer.toString(value.height));
	} else {
	    element.removeAttribute(widthName);
	    element.removeAttribute(heightName);
	}
    }

    public Dimension deserializeDimension(org.xml.sax.Attributes attributes) throws SAXException {
	final String widthName = name + ".width";
	final String heightName = name + ".height";
	Dimension result = null;
	if (attributes.getValue(widthName) != null && attributes.getValue(heightName) != null) {
	    result = new Dimension();
	    result.width = deserializeInteger(attributes, widthName, DEC);
	    result.height = deserializeInteger(attributes, heightName, DEC);
	}
	return result;
    }

    public void serializePoint(Element element, Point value) {
	if (element == null) {
	    throw new IllegalArgumentException("Parameter 'element' must not be null.");
	}
	final String xName = name + ".x";
	final String yName = name + ".y";
	if (value != null) {
	    element.setAttribute(xName, Integer.toString(value.x));
	    element.setAttribute(yName, Integer.toString(value.y));
	} else {
	    element.removeAttribute(xName);
	    element.removeAttribute(yName);
	}
    }

    public Point deserializePoint(org.xml.sax.Attributes attributes) throws SAXException {
	final String xName = name + ".x";
	final String yName = name + ".y";
	Point result = null;
	if (attributes.getValue(xName) != null && attributes.getValue(yName) != null) {
	    result = new Point();
	    result.x = deserializeInteger(attributes, xName, DEC);
	    result.y = deserializeInteger(attributes, yName, DEC);
	}
	return result;
    }

    public void serializeColor(Element element, Color value) {
	if (value != null) {
	    element.setAttribute(name, Integer.toString(value.getRGB(), HEX).toUpperCase());
	} else {
	    element.removeAttribute(name);
	}
    }

    public Color deserializeColor(org.xml.sax.Attributes attributes) throws SAXException {
	Color result = null;
	if (attributes.getValue(name) != null) {
	    result = new Color(deserializeInteger(attributes, name, HEX), true);
	}
	return result;
    }

    @Override
    public String toString() {
	return "name=" + name + ", dataType=[" + dataType + "]";
    }

}
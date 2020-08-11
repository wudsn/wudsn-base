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

package com.wudsn.tools.base.common;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Representation class of long values that are ASCII strings with binary
 * comparison.
 * 
 * @author Peter Dell
 * 
 */
public final class ASCIIString {

    public static Comparator<String> COMPARATOR;
    public static Comparator<String> CASE_INSENSITIVE_COMPARATOR;

    private String value;

    static {
	COMPARATOR = new Comparator<String>() {

	    @Override
	    public int compare(String o1, String o2) {
		return o1.compareTo(o2);
	    }
	};

	CASE_INSENSITIVE_COMPARATOR = StringUtility.CASE_INSENSITIVE_COMPARATOR;
    }

    /**
     * Creation is public, so it can be used in table cell editors.
     * 
     * @param value
     *            The string value, may be null.
     */
    public ASCIIString(String value) {
	this.value = value;

    }

    @Override
    public String toString() {
	return value;
    }

    /**
     * Gets the bytes representing the ASCII string.
     * 
     * @param value
     *            The value may be empty, not <code>null</code>.
     * @return The bytes representing the ASCII string, maybe empty, not
     *         <code>null</code>.
     */
    public static byte[] getBytes(String value) {
	if (value == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'value2' must not be null.");
	}
	try {
	    return value.getBytes("US-ASCII");
	} catch (UnsupportedEncodingException ex) {
	    throw new RuntimeException("Unsupported non ASCII characters in '"
		    + value + "'", ex);
	}
    }

    /**
     * Gets the bytes representing the ASCII string.
     * 
     * @param value
     *            The value may be empty, not <code>null</code>.
     * @param length
     *            The length of the result, a non-negative integer.
     * @return The bytes array with the specified length, not <code>null</code>.
     */
    public static byte[] getBytesCentered(String value, int length) {
	if (value == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'value' must not be null.");
	}
	if (length < 0) {
	    throw new IllegalArgumentException(
		    "Parameter 'line_length' must not be negative. Specified valus is "
			    + length + ".");
	}
	if (value.length() > length) {
	    value = value.substring(0, length);
	}
	byte[] result = new byte[length];
	Arrays.fill(result, (byte) ' ');
	byte[] valueBytes = getBytes(value);
	int middle = length / 2 - valueBytes.length / 2;
	System.arraycopy(valueBytes, 0, result, middle, valueBytes.length);
	return result;
    }

}

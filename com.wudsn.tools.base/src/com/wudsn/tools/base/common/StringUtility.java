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

import java.util.Comparator;

/**
 * Utility class to handle strings.
 * 
 * @author Peter Dell
 * 
 */
public final class StringUtility {

    private final static class CaseInsensitiveComparator implements
	    Comparator<String> {
	@Override
	public int compare(String s1, String s2) {
	    return s1.toUpperCase().compareTo(s2.toUpperCase());
	}
    };

    public final static Comparator<String> CASE_INSENSITIVE_COMPARATOR;

    static {
	CASE_INSENSITIVE_COMPARATOR = new CaseInsensitiveComparator();
    }

    /**
     * Creation is private.
     */
    private StringUtility() {

    }

    /**
     * Determines if a string value is empty, i.e. has zero length or is only
     * containing white spaces.
     * 
     * @param value
     *            The string value, not <code>null</code>.
     * @return <code>true</code> if the value is empty or only containing of
     *         white spaces, <code>false</code> otherwise.
     */
    public static boolean isEmpty(String value) {
	if (value == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'value' must not be null.");
	}
	return value.trim().length() == 0;
    }

    /**
     * Determines if a string value is specified, i.e. not empty and not only
     * containing white spaces.
     * 
     * @param value
     *            The string value, not <code>null</code>.
     * @return <code>true</code> if the value is not empty and not only
     *         containing of white spaces, <code>false</code> otherwise.
     */
    public static boolean isSpecified(String value) {
	if (value == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'value' must not be null.");
	}
	return value.trim().length() > 0;
    }
}

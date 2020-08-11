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

import com.wudsn.tools.base.common.StringUtility;
import com.wudsn.tools.base.common.TextUtility;

/**
 * Message repository object.
 * 
 * @author Peter Dell
 */
public final class Message {

    public static final int STATUS = 1;
    public static final int INFO = 2;
    public static final int ERROR = 3;

    private String id;
    private int severity;
    private String shortText;

    Message(String id, int severity, String shortText) {
	if (id == null) {
	    throw new IllegalArgumentException("Parameter 'id' must not be null.");
	}
	if (StringUtility.isEmpty(id)) {
	    throw new IllegalArgumentException("Parameter 'id' must not be empty.");
	}
	switch (severity) {
	case STATUS:
	case INFO:
	case ERROR:
	    break;
	default:
	    throw new IllegalArgumentException("Parameter 'severity' has illegal value " + severity + ".");
	}
	if (shortText == null) {
	    throw new IllegalArgumentException("Parameter 'shortText' must not be null.");
	}
	if (StringUtility.isEmpty(shortText)) {
	    throw new IllegalArgumentException("Parameter 'shortText' must not be empty.");
	}
	this.id = id;
	this.severity = severity;
	this.shortText = shortText;
    }

    public String getId() {
	return id;
    }

    public int getSeverity() {
	return severity;
    }

    public String getShortText() {
	return shortText;
    }

    @Override
    public String toString() {
	switch (severity) {
	case INFO:
	    return "INFO: " + shortText;
	case ERROR:
	    return "ERROR: " + shortText;
	}
	throw new IllegalStateException("Field 'severity' has illegal value " + severity + ".");
    }

    public String format(String... parameters) {
	return TextUtility.format(shortText, parameters);
    }

}

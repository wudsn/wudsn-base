/**
 * Copyright (C) 2013 - 2014 <a href="https://www.wudsn.com" target="_top">Peter Dell</a>
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
 * along with the WUDSN software distribution. If not, see <https://www.gnu.org/licenses/>.
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
	private String number;
	private int severity;
	private String shortText;

	Message(String id, String number, int severity, String shortText) {
		if (id == null) {
			throw new IllegalArgumentException("Parameter 'id' must not be null.");
		}
		if (!id.matches("[A-Z]{2,5}")) {
			throw new IllegalArgumentException("Parameter 'id' must be 2 to 5 upper-case letters but is '" + id + "'.");
		}
		if (number == null) {
			throw new IllegalArgumentException("Parameter 'number' must not be null.");
		}
		if (!number.matches("[0-9]{3}")) {
			throw new IllegalArgumentException("Parameter 'number' must be a 3-digit string but is '" + number + "'.");
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
		this.number = number;
		this.severity = severity;
		this.shortText = shortText;
	}

	/**
	 * Gets the 3-digit message number, unique within the messages repository
	 * class. The severity is not part of the number; it is carried by the
	 * repository field name's first letter and by {@link #getSeverity()}.
	 *
	 * @return The 3-digit message number, not <code>null</code>.
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Gets the full message identifier composed of the repository class's
	 * <code>ID</code> and the message number, for example "DMO-003".
	 *
	 * @return The full message identifier, not <code>null</code>.
	 */
	public String getFullId() {
		return id + "-" + number;
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

/**
 * Copyright (C) 2013 - 2018 <a href="http://www.wudsn.com" target="_top">Peter Dell</a>
 *
 * This file is part of The!Cart Studio distribution.
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
 * along with The!Cart Studio. If not, see <http://www.gnu.org/licenses/>.
 */

package com.wudsn.tools.base.atari;

public final class ExecutableFile {

    /**
     * Determines if a byte array contains a valid Atari executable file.
     * 
     * @param data
     *            The data, not <code>null</code>.
     * @param offset
     *            The offset to start the comparison from, a non-negative
     *            integer.
     * @return <code>true</code> If data has the required length, starts with
     *         the executable magic bytes and has correct segments,
     *         <code>false</code> otherwise.
     */
    public static boolean isHeader(byte[] data, int offset) {
	if (data == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'data' must not be null.");
	}
	if (offset < 0) {
	    throw new IllegalArgumentException(
		    "Parameter 'data' must not be negative.");
	}
	int i = offset;

	while (i < data.length) {
	    if (i + 2 < data.length) {
		if ((data[i] & 0xff) == 0xff || (data[i + 1] & 0xff) == 0xff) {
		    i = i + 2;
		} else {
		    if (i == offset) {
			return false; // No valid header at first index
		    }
		}
	    } else {
		return false; // Too short, no header
	    }
	    if (i + 4 < data.length) {

		int startAddress = (data[i] & 0xff) + 256
			* (data[i + 1] & 0xff);
		int endAddress = (data[i + 2] & 0xff) + 256
			* (data[i + 3] & 0xff);
		int length = endAddress - startAddress + 1;
		if (length < 1) {
		    return false;
		}
		i = i + 4 + length;
	    } else {
		return false; // Too short, no address or data
	    }
	}
	return true;
    }
}

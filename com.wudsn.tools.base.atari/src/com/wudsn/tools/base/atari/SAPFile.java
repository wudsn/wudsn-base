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

import java.nio.charset.Charset;

public final class SAPFile {

    /**
     * Determines if a byte array contains a valid Atari music file.
     * See http://asap.sourceforge.net/sap-format.html
     * 
     * @param data
     *            The data, not <code>null</code>.
     * @return <code>true</code> If data has the required length and starts with
     *         the SAP magic bytes and has a correct binary part,
     *         <code>false</code> otherwise.
     */
    public static boolean isHeader(byte[] data) {
	if (data == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'data' must not be null.");
	}
	Charset charset = Charset.forName("US-ASCII");
	byte[] magicBytes = "SAP\n\r".getBytes(charset);
	int i = 0;
	if (i + magicBytes.length < data.length) {
	    for (int j = 0; j < magicBytes.length; j++) {
		if (data[i] != magicBytes[i]) {
		    return false;
		}
	    }
	}
	i = i + magicBytes.length;
	while (i + 5 < data.length) {
	    if ((data[i] & 0xff) == 0xff && (data[i + 1] & 0xff) == 0xff) {
		break;
	    }
	    i++;
	}
	if (!ExecutableFile.isHeader(data, i)) {
	    return false;
	}
	return true;
    }
}

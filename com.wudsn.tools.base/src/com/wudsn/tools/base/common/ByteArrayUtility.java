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

import java.util.zip.CRC32;

public final class ByteArrayUtility {

    public static final int MASK_FF = 0xff;

    public static final int KB = 1024;
    public static final int MB = KB * KB;

    /**
     * Static array of all one-byte upper case hex numbers (00...FF)
     */
    private static final String[] hexStrings;

    /**
     * Static initialization.
     */
    static {

	// Fill in the array of hex strings
	hexStrings = new String[256];
	for (int i = 0; i < 256; i++) {
	    String s = Integer.toHexString(i).toUpperCase();
	    hexStrings[i] = (s.length() < 2) ? ("0" + s) : s;
	}
    }

    /**
     * Creation is private.
     */
    private ByteArrayUtility() {

    }

    public static int getIndexOf(byte[] content, int startOffset, int length,
	    byte[] pattern) {
	if (content == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'content' must not be null.");
	}
	if (startOffset < 0) {
	    throw new IllegalArgumentException(
		    "Parameter 'startOffset' must not be negative. Specified value is "
			    + startOffset + ".");
	}
	if (length < 0) {
	    throw new IllegalArgumentException(
		    "Parameter 'length' must not be negative. Specified value is "
			    + length + ".");
	}

	if (pattern == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'pattern' must not be null.");
	}
	int endOffset = Math.min(startOffset + length - pattern.length,
		content.length);
	for (int i = startOffset; i < endOffset; i++) {
	    if (content[i] == pattern[0]) {
		boolean equal = true;

		for (int j = 1; equal && j < pattern.length
			&& (i + j) < endOffset; j++) {
		    equal = (content[i + j] == pattern[j]);
		}

		if (equal) {
		    return i;
		}
	    }
	}
	return -1;
    }

    public static String toHexString(byte[] content) {
	if (content == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'content' must not be null.");
	}
	StringBuilder builder = new StringBuilder(content.length * 2);
	for (int i = 0; i < content.length; i++) {
	    builder.append(hexStrings[content[i] & 0xff]);
	}
	return builder.toString();

    }

    /**
     * Compute the CRC32 values for a byte array.
     * 
     * @param content
     *            The binary content, not <code>null</code>
     * 
     * @return The CRC32 value for the content.
     */
    public static int getCRC32(byte[] content) {
	if (content == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'content' must not be null.");
	}

	CRC32 crc32 = new CRC32();
	crc32.update(content, 0, content.length);

	int result = (int) crc32.getValue();
	return result;
    }
}

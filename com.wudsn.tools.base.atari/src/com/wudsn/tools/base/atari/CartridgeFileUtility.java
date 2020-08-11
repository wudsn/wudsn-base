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

import static com.wudsn.tools.base.common.ByteArrayUtility.KB;

/**
 * Utility class to handle ".CAR" files.
 */
public final class CartridgeFileUtility {

    // Number of bytes for the "CART" header.
    public static final int CART_HEADER_SIZE = 16;

    /**
     * Creation is private.
     */
    private CartridgeFileUtility() {

    }

    /**
     * Determine the cartridge type of a cartridge header in a byte array.
     * 
     * @param content
     *            The byte array, may be empty not <code>null</code>.
     * @return The cartridge type or 0 if the cartridge type cannot be
     *         determined.
     */
    public static int getCartridgeTypeNumericId(byte[] content) {
	if (content == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'content' must not be null.");
	}
	int cartridgeTypeNumericId = 0;
	// Check if file is in CART format
	if (content.length > CART_HEADER_SIZE
		&& content.length % KB == CART_HEADER_SIZE) {
	    if (content[0] == 'C' && content[1] == 'A' && content[2] == 'R'
		    && content[3] == 'T') {
		cartridgeTypeNumericId = (content[4] & 0xff);
		cartridgeTypeNumericId = cartridgeTypeNumericId << 8;
		cartridgeTypeNumericId = cartridgeTypeNumericId
			| (content[5] & 0xff);
		cartridgeTypeNumericId = cartridgeTypeNumericId << 8;
		cartridgeTypeNumericId = cartridgeTypeNumericId
			| (content[6] & 0xff);
		cartridgeTypeNumericId = cartridgeTypeNumericId << 8;
		cartridgeTypeNumericId = cartridgeTypeNumericId
			| (content[7] & 0xff);
	    }

	}
	return cartridgeTypeNumericId;
    }

    /**
     * Strip the cartridge header and return the content as new byte array.
     * 
     * @param content
     *            The file content, not <code>null</code>. The type of the
     *            content must have been detected with a cartridge type using
     *            {@link CartridgeFileUtility#getCartridgeTypeNumericId(byte[])}
     *            .
     * 
     * @return The new content without header, not <code>null</code>.
     */
    public static byte[] getCartridgeContent(byte[] content) {
	if (content == null) {
	    throw new IllegalArgumentException(
		    "Parameter content must not be null.");
	}
	int newLength = content.length - CART_HEADER_SIZE;
	byte[] newContent = new byte[newLength];
	System.arraycopy(content, CART_HEADER_SIZE, newContent, 0, newLength);
	return newContent;
    }

    /**
     * Computes the 32 bit / 4 byte ROM checksum. See <a href=
     * "http://atari800.cvs.sourceforge.net/viewvc/atari800/atari800/src/cartridge.c"
     * >Atari800</a>.
     * 
     * @param content
     *            The ROM content, not <code>null</code>.
     * @param startOffset
     *            The start offset, a non-negative integer.
     * @return The 32 bit / 4 byte checksum.
     */
    private static int getCartridgeHeaderCheckSum(byte[] content,
	    int startOffset) {
	if (content == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'content' must not be null.");
	}
	if (startOffset < 0) {
	    throw new IllegalArgumentException(
		    "Parameter 'startOffset' must not be negative. Specified values is "
			    + startOffset + ".");
	}
	int result = 0;
	for (int i = startOffset; i < content.length; i++) {
	    result += (content[i] & 0xff); // add unsigned bytes
	}
	return result;
    }

    /**
     * Create header of {@link #CART_HEADER_SIZE} to make the content a valid
     * cartridge file of the specified type.
     * 
     * @param cartridgeTypeNumericId
     *            The cartridge type numeric id, see {@link CartridgeType}.
     * @param content
     *            The cartridge content, not <code>null</code>.
     * 
     * @return The cartridge header, not <code>null</code>.
     */
    public static byte[] createCartridgeHeaderWithCheckSum(
	    int cartridgeTypeNumericId, byte[] content) {
	if (content == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'content' must not be null.");
	}

	// Magic number
	byte[] header = new byte[CART_HEADER_SIZE];
	header[0] = 'C';
	header[1] = 'A';
	header[2] = 'R';
	header[3] = 'T';

	// Cartridge type
	header[7] = (byte) (cartridgeTypeNumericId & 0xff);
	cartridgeTypeNumericId = cartridgeTypeNumericId >>> 8;
	header[6] = (byte) (cartridgeTypeNumericId & 0xff);
	cartridgeTypeNumericId = cartridgeTypeNumericId >>> 8;
	header[5] = (byte) (cartridgeTypeNumericId & 0xff);
	cartridgeTypeNumericId = cartridgeTypeNumericId >>> 8;
	header[4] = (byte) (cartridgeTypeNumericId & 0xff);

	// Checksum
	int checkSum = getCartridgeHeaderCheckSum(content, 0);
	header[11] = (byte) (checkSum & 0xff);
	checkSum = checkSum >>> 8;
	header[10] = (byte) (checkSum & 0xff);
	checkSum = checkSum >>> 8;
	header[9] = (byte) (checkSum & 0xff);
	checkSum = checkSum >>> 8;
	header[8] = (byte) (checkSum & 0xff);

	// Reserved
	header[12] = 0x00;
	header[13] = 0x00;
	header[14] = 0x00;
	header[15] = 0x00;
	return header;
    }

}

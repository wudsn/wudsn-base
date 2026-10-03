/**
 * Copyright (C) 2013 - 2020 <a href="https://www.wudsn.com" target="_top">Peter Dell</a>
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
 * along with The!Cart Studio. If not, see <https://www.gnu.org/licenses/>.
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
	 * Atrax SDX (48, 49): the encoded image's address bit for each address bit
	 * of the plain image, see cart.txt.
	 */
	private static final int[] ATRAX_SDX_ADDRESS_BITS = { 6, 7, 12, 15, 14, 13, 8, 5, 4, 3, 0, 1, 2, 9, 11, 10, 16 };

	/** Atrax SDX (48, 49): the encoded byte's bit for each bit of the plain byte. */
	private static final int[] ATRAX_SDX_DATA_BITS = { 4, 0, 5, 1, 7, 6, 3, 2 };

	/**
	 * Atrax 128 (68): the encoded image's address bit for each address bit of
	 * the plain image, see cart.txt.
	 */
	private static final int[] ATRAX_128_ADDRESS_BITS = { 5, 6, 7, 12, 0, 1, 2, 3, 4, 8, 10, 11, 9, 13, 14, 15, 16 };

	/** Atrax 128 (68): the encoded byte's bit for each bit of the plain byte. */
	private static final int[] ATRAX_128_DATA_BITS = { 5, 6, 2, 4, 0, 1, 7, 3 };

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
	 * @return The cartridge type or 0 if the cartridge type cannot be determined.
	 */
	public static int getCartridgeTypeNumericId(byte[] content) {
		if (content == null) {
			throw new IllegalArgumentException("Parameter 'content' must not be null.");
		}
		int cartridgeTypeNumericId = 0;
		// Check if file is in CART format
		if (content.length > CART_HEADER_SIZE && content.length % KB == CART_HEADER_SIZE) {
			if (content[0] == 'C' && content[1] == 'A' && content[2] == 'R' && content[3] == 'T') {
				cartridgeTypeNumericId = (content[4] & 0xff);
				cartridgeTypeNumericId = cartridgeTypeNumericId << 8;
				cartridgeTypeNumericId = cartridgeTypeNumericId | (content[5] & 0xff);
				cartridgeTypeNumericId = cartridgeTypeNumericId << 8;
				cartridgeTypeNumericId = cartridgeTypeNumericId | (content[6] & 0xff);
				cartridgeTypeNumericId = cartridgeTypeNumericId << 8;
				cartridgeTypeNumericId = cartridgeTypeNumericId | (content[7] & 0xff);
			}

		}
		return cartridgeTypeNumericId;
	}

	/**
	 * Strip the cartridge header and return the content as new byte array.
	 * 
	 * @param content
	 *            The file content, not <code>null</code>. The type of the content
	 *            must have been detected with a cartridge type using
	 *            {@link CartridgeFileUtility#getCartridgeTypeNumericId(byte[])} .
	 * 
	 * @return The new content without header, not <code>null</code>.
	 */
	public static byte[] getCartridgeContent(byte[] content) {
		if (content == null) {
			throw new IllegalArgumentException("Parameter content must not be null.");
		}
		int newLength = content.length - CART_HEADER_SIZE;
		byte[] newContent = new byte[newLength];
		System.arraycopy(content, CART_HEADER_SIZE, newContent, 0, newLength);
		return newContent;
	}

	/**
	 * Encodes a plain cartridge image into the interleaved form of an Atrax
	 * cartridge type, as read directly from its ROM chip.
	 *
	 * @param cartridgeType
	 *            The cartridge type, one for which
	 *            {@link CartridgeType#isAtraxInterleaved()} is
	 *            <code>true</code>.
	 * @param content
	 *            The plain content, not <code>null</code>.
	 * @return The new, encoded content, not <code>null</code>.
	 */
	public static byte[] encodeAtraxContent(CartridgeType cartridgeType, byte[] content) {
		return permuteAtraxContent(cartridgeType, content, true);
	}

	/**
	 * Decodes the interleaved image of an Atrax cartridge type into the plain
	 * image, as seen by the CPU. Inverse of {@link #encodeAtraxContent}.
	 *
	 * @param cartridgeType
	 *            The cartridge type, one for which
	 *            {@link CartridgeType#isAtraxInterleaved()} is
	 *            <code>true</code>.
	 * @param content
	 *            The encoded content, not <code>null</code>.
	 * @return The new, plain content, not <code>null</code>.
	 */
	public static byte[] decodeAtraxContent(CartridgeType cartridgeType, byte[] content) {
		return permuteAtraxContent(cartridgeType, content, false);
	}

	private static byte[] permuteAtraxContent(CartridgeType cartridgeType, byte[] content, boolean encode) {
		if (cartridgeType == null) {
			throw new IllegalArgumentException("Parameter 'cartridgeType' must not be null.");
		}
		if (content == null) {
			throw new IllegalArgumentException("Parameter 'content' must not be null.");
		}
		int[] addressBits;
		int[] dataBits;
		if (cartridgeType == CartridgeType.CARTRIDGE_ATRAX_SDX_64
				|| cartridgeType == CartridgeType.CARTRIDGE_ATRAX_SDX_128) {
			addressBits = ATRAX_SDX_ADDRESS_BITS;
			dataBits = ATRAX_SDX_DATA_BITS;
		} else if (cartridgeType == CartridgeType.CARTRIDGE_ATRAX_128) {
			addressBits = ATRAX_128_ADDRESS_BITS;
			dataBits = ATRAX_128_DATA_BITS;
		} else {
			throw new IllegalArgumentException(
					"Parameter 'cartridgeType' must be an Atrax interleaved type. Specified value is " + cartridgeType
							+ ".");
		}
		if (content.length > 1 << addressBits.length || Integer.bitCount(content.length) != 1) {
			throw new IllegalArgumentException("Parameter 'content' must have a size that is a power of 2 up to "
					+ (1 << addressBits.length) + ". Specified size is " + content.length + ".");
		}

		byte[] result = new byte[content.length];
		for (int address = 0; address < content.length; address++) {
			int value = content[address] & 0xff;
			if (encode) {
				result[permuteBits(address, addressBits, true)] = (byte) permuteBits(value, dataBits, true);
			} else {
				result[permuteBits(address, addressBits, false)] = (byte) permuteBits(value, dataBits, false);
			}
		}
		return result;
	}

	/**
	 * Moves bit i of a plain value to bit bits[i] (encode), or bit bits[i] of an
	 * encoded value back to bit i (decode).
	 */
	private static int permuteBits(int value, int[] bits, boolean encode) {
		int result = 0;
		for (int i = 0; i < bits.length; i++) {
			if (encode) {
				result |= ((value >>> i) & 1) << bits[i];
			} else {
				result |= ((value >>> bits[i]) & 1) << i;
			}
		}
		return result;
	}

	/**
	 * Computes the 32 bit / 4 byte ROM checksum. See <a href=
	 * "https://atari800.cvs.sourceforge.net/viewvc/atari800/atari800/src/cartridge.c"
	 * >Atari800</a>.
	 * 
	 * @param content
	 *            The ROM content, not <code>null</code>.
	 * @param startOffset
	 *            The start offset, a non-negative integer.
	 * @return The 32 bit / 4 byte checksum.
	 */
	private static int getCartridgeHeaderCheckSum(byte[] content, int startOffset) {
		if (content == null) {
			throw new IllegalArgumentException("Parameter 'content' must not be null.");
		}
		if (startOffset < 0) {
			throw new IllegalArgumentException(
					"Parameter 'startOffset' must not be negative. Specified values is " + startOffset + ".");
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
	public static byte[] createCartridgeHeaderWithCheckSum(int cartridgeTypeNumericId, byte[] content) {
		if (content == null) {
			throw new IllegalArgumentException("Parameter 'content' must not be null.");
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

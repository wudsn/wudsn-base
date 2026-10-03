/**
 * Copyright (C) 2026 <a href="https://www.wudsn.com" target="_top">Peter Dell</a>
 *
 * This file is part of a WUDSN software distribution.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the WUDSN software distribution. If not, see <https://www.gnu.org/licenses/>.
 */

package com.wudsn.tools.base.atari;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Checks the Atrax encoding against TheCartStudio's original implementation,
 * kept here as reference, and the decoding as its inverse.
 */
public class CartridgeFileUtilityTest {

	@Test
	public void testAtraxEncodingMatchesReference() {
		byte[] sdx64 = randomContent(0x10000);
		assertArrayEquals(referenceEncodeAtraxSDX(sdx64),
				CartridgeFileUtility.encodeAtraxContent(CartridgeType.CARTRIDGE_ATRAX_SDX_64, sdx64));
		byte[] sdx128 = randomContent(0x20000);
		assertArrayEquals(referenceEncodeAtraxSDX(sdx128),
				CartridgeFileUtility.encodeAtraxContent(CartridgeType.CARTRIDGE_ATRAX_SDX_128, sdx128));
		byte[] atrax128 = randomContent(0x20000);
		assertArrayEquals(referenceEncodeAtrax128(atrax128),
				CartridgeFileUtility.encodeAtraxContent(CartridgeType.CARTRIDGE_ATRAX_128, atrax128));
	}

	@Test
	public void testAtraxDecodingIsInverse() {
		for (CartridgeType cartridgeType : new CartridgeType[] { CartridgeType.CARTRIDGE_ATRAX_SDX_64,
				CartridgeType.CARTRIDGE_ATRAX_SDX_128, CartridgeType.CARTRIDGE_ATRAX_128 }) {
			byte[] plain = randomContent(cartridgeType.getSize());
			byte[] encoded = CartridgeFileUtility.encodeAtraxContent(cartridgeType, plain);
			assertArrayEquals(plain, CartridgeFileUtility.decodeAtraxContent(cartridgeType, encoded),
					cartridgeType.getId());
		}
	}

	/** Atrax SDX: CPU address line A0 is EPROM pin A6, data line D0 is EPROM pin Q4 (cart.txt). */
	@Test
	public void testAtraxSingleBits() {
		byte[] plain = new byte[0x10000];
		plain[0x0001] = 0x01;
		byte[] encoded = CartridgeFileUtility.encodeAtraxContent(CartridgeType.CARTRIDGE_ATRAX_SDX_64, plain);
		assertEquals(0x10, encoded[0x0040]);
		assertEquals(0x00, encoded[0x0001]);
	}

	@Test
	public void testAtraxInvalidArguments() {
		assertThrows(IllegalArgumentException.class,
				() -> CartridgeFileUtility.decodeAtraxContent(CartridgeType.CARTRIDGE_SDX_64, new byte[0x10000]));
		assertThrows(IllegalArgumentException.class, () -> CartridgeFileUtility
				.decodeAtraxContent(CartridgeType.CARTRIDGE_ATRAX_128, new byte[0x30000]));
	}

	private static byte[] randomContent(int size) {
		byte[] content = new byte[size];
		new Random(size).nextBytes(content);
		return content;
	}

	/** TheCartStudio's CartridgeTypeSampleCreator.createInterleavedAtraxSDXContent, unchanged. */
	private static byte[] referenceEncodeAtraxSDX(byte[] content) {
		byte[] result = new byte[content.length];
		for (int a0 = 0; a0 < content.length; a0++) {
			byte b0 = content[a0];

			int a0_6 = (a0 >>> 0) & 0x1;
			int a0_7 = (a0 >>> 1) & 0x1;
			int a0_12 = (a0 >>> 2) & 0x1;
			int a0_15 = (a0 >>> 3) & 0x1;
			int a0_14 = (a0 >>> 4) & 0x1;
			int a0_13 = (a0 >>> 5) & 0x1;
			int a0_8 = (a0 >>> 6) & 0x1;
			int a0_5 = (a0 >>> 7) & 0x1;
			int a0_4 = (a0 >>> 8) & 0x1;
			int a0_3 = (a0 >>> 9) & 0x1;
			int a0_0 = (a0 >>> 10) & 0x1;
			int a0_1 = (a0 >>> 11) & 0x1;
			int a0_2 = (a0 >>> 12) & 0x1;
			int a0_9 = (a0 >>> 13) & 0x1;
			int a0_11 = (a0 >>> 14) & 0x1;
			int a0_10 = (a0 >>> 15) & 0x1;
			int a0_16 = (a0 >>> 16) & 0x1;

			int b0_4 = (b0 >>> 0) & 0x1;
			int b0_0 = (b0 >>> 1) & 0x1;
			int b0_5 = (b0 >>> 2) & 0x1;
			int b0_1 = (b0 >>> 3) & 0x1;
			int b0_7 = (b0 >>> 4) & 0x1;
			int b0_6 = (b0 >>> 5) & 0x1;
			int b0_3 = (b0 >>> 6) & 0x1;
			int b0_2 = (b0 >>> 7) & 0x1;

			int a1 = 0;
			a1 |= a0_0 * 0x00001;
			a1 |= a0_1 * 0x00002;
			a1 |= a0_2 * 0x00004;
			a1 |= a0_3 * 0x00008;
			a1 |= a0_4 * 0x00010;
			a1 |= a0_5 * 0x00020;
			a1 |= a0_6 * 0x00040;
			a1 |= a0_7 * 0x00080;
			a1 |= a0_8 * 0x00100;
			a1 |= a0_9 * 0x00200;
			a1 |= a0_10 * 0x00400;
			a1 |= a0_11 * 0x00800;
			a1 |= a0_12 * 0x01000;
			a1 |= a0_13 * 0x02000;
			a1 |= a0_14 * 0x04000;
			a1 |= a0_15 * 0x08000;
			a1 |= a0_16 * 0x10000;
			a1 = a1 & 0x1ffff;

			int b1 = 0;
			b1 |= b0_0 * 0x00001;
			b1 |= b0_1 * 0x00002;
			b1 |= b0_2 * 0x00004;
			b1 |= b0_3 * 0x00008;
			b1 |= b0_4 * 0x00010;
			b1 |= b0_5 * 0x00020;
			b1 |= b0_6 * 0x00040;
			b1 |= b0_7 * 0x00080;
			b1 = b1 & 0xff;

			result[a1] = (byte) b1;
		}
		return result;
	}

	/** TheCartStudio's CartridgeTypeSampleCreator.createInterleavedAtrax128Content, unchanged. */
	private static byte[] referenceEncodeAtrax128(byte[] content) {
		byte[] result = new byte[content.length];
		for (int a0 = 0; a0 < content.length; a0++) {
			byte b0 = content[a0];

			int a0_5 = (a0 >>> 0) & 0x1;
			int a0_6 = (a0 >>> 1) & 0x1;
			int a0_7 = (a0 >>> 2) & 0x1;
			int a0_12 = (a0 >>> 3) & 0x1;
			int a0_0 = (a0 >>> 4) & 0x1;
			int a0_1 = (a0 >>> 5) & 0x1;
			int a0_2 = (a0 >>> 6) & 0x1;
			int a0_3 = (a0 >>> 7) & 0x1;
			int a0_4 = (a0 >>> 8) & 0x1;
			int a0_8 = (a0 >>> 9) & 0x1;
			int a0_10 = (a0 >>> 10) & 0x1;
			int a0_11 = (a0 >>> 11) & 0x1;
			int a0_9 = (a0 >>> 12) & 0x1;
			int a0_13 = (a0 >>> 13) & 0x1;
			int a0_14 = (a0 >>> 14) & 0x1;
			int a0_15 = (a0 >>> 15) & 0x1;
			int a0_16 = (a0 >>> 16) & 0x1;

			int b0_5 = (b0 >>> 0) & 0x1;
			int b0_6 = (b0 >>> 1) & 0x1;
			int b0_2 = (b0 >>> 2) & 0x1;
			int b0_4 = (b0 >>> 3) & 0x1;
			int b0_0 = (b0 >>> 4) & 0x1;
			int b0_1 = (b0 >>> 5) & 0x1;
			int b0_7 = (b0 >>> 6) & 0x1;
			int b0_3 = (b0 >>> 7) & 0x1;

			int a1 = 0;
			a1 |= a0_0 * 0x00001;
			a1 |= a0_1 * 0x00002;
			a1 |= a0_2 * 0x00004;
			a1 |= a0_3 * 0x00008;
			a1 |= a0_4 * 0x00010;
			a1 |= a0_5 * 0x00020;
			a1 |= a0_6 * 0x00040;
			a1 |= a0_7 * 0x00080;
			a1 |= a0_8 * 0x00100;
			a1 |= a0_9 * 0x00200;
			a1 |= a0_10 * 0x00400;
			a1 |= a0_11 * 0x00800;
			a1 |= a0_12 * 0x01000;
			a1 |= a0_13 * 0x02000;
			a1 |= a0_14 * 0x04000;
			a1 |= a0_15 * 0x08000;
			a1 |= a0_16 * 0x10000;
			a1 = a1 & 0x1ffff;

			int b1 = 0;
			b1 |= b0_0 * 0x00001;
			b1 |= b0_1 * 0x00002;
			b1 |= b0_2 * 0x00004;
			b1 |= b0_3 * 0x00008;
			b1 |= b0_4 * 0x00010;
			b1 |= b0_5 * 0x00020;
			b1 |= b0_6 * 0x00040;
			b1 |= b0_7 * 0x00080;
			b1 = b1 & 0xff;

			result[a1] = (byte) b1;
		}
		return result;
	}
}

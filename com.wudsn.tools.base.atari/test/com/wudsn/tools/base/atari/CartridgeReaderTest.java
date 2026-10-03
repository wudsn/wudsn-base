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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.wudsn.tools.base.atari.CartridgeReader.Bank;
import com.wudsn.tools.base.atari.CartridgeReader.Cartridge;

/**
 * Reads synthetic cartridge images whose 4 KB pages start with their page
 * number as a little-endian word, and checks every bank's file offset, size
 * and address against hand-computed values. Expected messages are formatted
 * through {@link Messages}, so the test does not depend on the locale.
 */
public class CartridgeReaderTest {

	private static final long MAXIMUM_SIZE = 4 * 1024 * 1024;

	@Test
	public void testDetection() {
		assertSame(CartridgeType.CARTRIDGE_STD_2, detect(Platform.ATARI_800, 0x0800));
		assertSame(CartridgeType.CARTRIDGE_STD_16, detect(Platform.ATARI_800, 0x4000));
		assertSame(CartridgeType.UNKNOWN, detect(Platform.ATARI_800, 0x8000));
		assertSame(CartridgeType.CARTRIDGE_5200_4, detect(Platform.ATARI_5200, 0x1000));
		assertSame(CartridgeType.CARTRIDGE_5200_40, detect(Platform.ATARI_5200, 0xA000));

		byte[] file = createCartridgeFile(CartridgeType.CARTRIDGE_WILL_64);
		assertTrue(CartridgeReader.hasCartridgeHeader(file.length, file));
		assertSame(CartridgeType.CARTRIDGE_WILL_64,
				CartridgeReader.detectCartridgeType(Platform.ATARI_800, file.length, file));
		file[7] = (byte) 200; // No such type number.
		assertSame(CartridgeType.UNKNOWN, CartridgeReader.detectCartridgeType(Platform.ATARI_800, file.length, file));
		assertTrue(!CartridgeReader.hasCartridgeHeader(file.length - 1, file));
	}

	@Test
	public void testCandidates() {
		List<CartridgeType> candidates = CartridgeReader.getCandidateTypes(Platform.ATARI_800, 0x10000);
		assertEquals(11, candidates.size());
		assertSame(CartridgeType.CARTRIDGE_ADAWLIAH_64, candidates.get(0)); // Sorted ignoring case.
		for (int i = 1; i < candidates.size(); i++) {
			assertTrue(candidates.get(i - 1).getText().compareToIgnoreCase(candidates.get(i).getText()) <= 0);
		}
		assertEquals(Collections.singletonList(CartridgeType.CARTRIDGE_5200_SUPER_64),
				CartridgeReader.getCandidateTypes(Platform.ATARI_5200, 0x10000));
		assertTrue(!CartridgeReader.isSupported(Platform.ATARI_800, CartridgeType.CARTRIDGE_AST_32));
		assertTrue(!CartridgeReader.isSupported(Platform.ATARI_800, CartridgeType.CARTRIDGE_5200_32));
	}

	@Test
	public void testLayouts() throws IOException {
		// Single window: Williams 64 KB, 8 banks at $A000, bank 0 initial.
		Cartridge cartridge = read(Platform.ATARI_800, createCartridgeFile(CartridgeType.CARTRIDGE_WILL_64));
		assertBanks(cartridge, 0, bank(0, 0x0000, 0x2000, 0xA000), bank(1, 0x2000, 0x2000, 0xA000),
				bank(2, 0x4000, 0x2000, 0xA000), bank(3, 0x6000, 0x2000, 0xA000), bank(4, 0x8000, 0x2000, 0xA000),
				bank(5, 0xA000, 0x2000, 0xA000), bank(6, 0xC000, 0x2000, 0xA000), bank(7, 0xE000, 0x2000, 0xA000));

		// Fixed plus switchable: XEGS 32 KB, banks 0-2 at $8000, bank 3 at $A000 and initial.
		cartridge = read(Platform.ATARI_800, createCartridgeFile(CartridgeType.CARTRIDGE_XEGS_32));
		assertBanks(cartridge, 3, bank(0, 0x0000, 0x2000, 0x8000), bank(1, 0x2000, 0x2000, 0x8000),
				bank(2, 0x4000, 0x2000, 0x8000), bank(3, 0x6000, 0x2000, 0xA000));

		// OSS (M091): bank 0 fixed at $B000 and initial, banks 1-3 at $A000.
		cartridge = read(Platform.ATARI_800, createCartridgeFile(CartridgeType.CARTRIDGE_OSS_M091_16));
		assertBanks(cartridge, 0, bank(0, 0x0000, 0x1000, 0xB000), bank(1, 0x1000, 0x1000, 0xA000),
				bank(2, 0x2000, 0x1000, 0xA000), bank(3, 0x3000, 0x1000, 0xA000));

		// Bounty Bob 5200: 4 KB banks at $4000 and $5000, the last 8 KB at $A000 and initial.
		cartridge = read(Platform.ATARI_5200, createCartridgeFile(CartridgeType.CARTRIDGE_5200_40));
		assertBanks(cartridge, 8, bank(0, 0x0000, 0x1000, 0x4000), bank(1, 0x1000, 0x1000, 0x4000),
				bank(2, 0x2000, 0x1000, 0x4000), bank(3, 0x3000, 0x1000, 0x4000), bank(4, 0x4000, 0x1000, 0x5000),
				bank(5, 0x5000, 0x1000, 0x5000), bank(6, 0x6000, 0x1000, 0x5000), bank(7, 0x7000, 0x1000, 0x5000),
				bank(8, 0x8000, 0x2000, 0xA000));

		// SIC! 128 KB: alternating $8000 and $A000, bank 1 initial.
		cartridge = read(Platform.ATARI_800, createCartridgeFile(CartridgeType.CARTRIDGE_SIC_128));
		assertEquals(16, cartridge.getBanks().size());
		assertBank(bank(14, 0x1C000, 0x2000, 0x8000), cartridge.getBanks().get(14));
		assertBank(bank(15, 0x1E000, 0x2000, 0xA000), cartridge.getBanks().get(15));
		assertSame(cartridge.getBanks().get(1), cartridge.getInitialBank());

		// Two chip 5200: the initial bank is the one with the vectors, the second chip.
		cartridge = read(Platform.ATARI_5200, createCartridgeFile(CartridgeType.CARTRIDGE_5200_EE_16));
		assertBanks(cartridge, 1, bank(0, 0x0000, 0x2000, 0x4000), bank(1, 0x2000, 0x2000, 0xA000));

		// Raw image of a standard size, and raw image as a chosen type.
		cartridge = read(Platform.ATARI_800, createContent(0x2000));
		assertBanks(cartridge, 0, bank(0, 0x0000, 0x2000, 0xA000));
		byte[] raw = createContent(0x8000);
		cartridge = CartridgeReader.readCartridge(Platform.ATARI_800, CartridgeType.CARTRIDGE_DB_32,
				new ByteArrayInputStream(raw), raw.length, MAXIMUM_SIZE);
		assertBanks(cartridge, 3, bank(0, 0x0000, 0x2000, 0x8000), bank(1, 0x2000, 0x2000, 0x8000),
				bank(2, 0x4000, 0x2000, 0x8000), bank(3, 0x6000, 0x2000, 0xA000));
	}

	@Test
	public void testAtrax() throws IOException {
		CartridgeType cartridgeType = CartridgeType.CARTRIDGE_ATRAX_128;
		byte[] plain = createContent(cartridgeType.getSize());
		byte[] encoded = CartridgeFileUtility.encodeAtraxContent(cartridgeType, plain);
		byte[] file = concat(CartridgeFileUtility.createCartridgeHeaderWithCheckSum(cartridgeType.getNumericId(), encoded),
				encoded);
		Cartridge cartridge = read(Platform.ATARI_800, file);
		assertTrue(Arrays.equals(plain, cartridge.getContent()));
		assertEquals(16, cartridge.getBanks().size());
		assertBank(bank(15, 0x1E000, 0x2000, 0xA000), cartridge.getBanks().get(15));
	}

	@Test
	public void testErrors() {
		assertError(Platform.ATARI_800, new byte[0x3000], Messages.E700.format("12288"));

		byte[] file = createCartridgeFile(CartridgeType.CARTRIDGE_STD_8);
		file[7] = (byte) 200;
		assertError(Platform.ATARI_800, file, Messages.E701.format("200"));

		CartridgeType cartridgeType = CartridgeType.CARTRIDGE_5200_32;
		assertError(Platform.ATARI_800, createCartridgeFile(cartridgeType), Messages.E702.format("4",
				cartridgeType.getText(), Platform.ATARI_5200.getText(), Platform.ATARI_800.getText()));

		cartridgeType = CartridgeType.CARTRIDGE_AST_32;
		assertError(Platform.ATARI_800, createCartridgeFile(cartridgeType),
				Messages.E703.format("47", cartridgeType.getText()));

		// A content size that does not match the type, also from a truncated stream.
		cartridgeType = CartridgeType.CARTRIDGE_STD_8;
		byte[] content = new byte[0x4000];
		assertError(Platform.ATARI_800,
				concat(CartridgeFileUtility.createCartridgeHeaderWithCheckSum(cartridgeType.getNumericId(), content),
						content),
				Messages.E704.format("16384", "1", cartridgeType.getText(), "8192"));
		byte[] complete = createCartridgeFile(cartridgeType);
		byte[] truncated = Arrays.copyOf(complete, CartridgeFileUtility.CART_HEADER_SIZE + 0x1000);
		IOException truncatedException = assertThrows(IOException.class, () -> CartridgeReader.readCartridge(
				Platform.ATARI_800, null, new ByteArrayInputStream(truncated), complete.length, MAXIMUM_SIZE));
		assertEquals(Messages.E704.format("4096", "1", cartridgeType.getText(), "8192"),
				truncatedException.getMessage());

		// Larger than the maximum: by the type in the CART header, and by the file size alone.
		file = concat(CartridgeFileUtility.createCartridgeHeaderWithCheckSum(
				CartridgeType.CARTRIDGE_THECART_32M.getNumericId(), new byte[0x2000]), new byte[0x2000]);
		assertError(Platform.ATARI_800, file, Messages.E705.format("33554432", "4194304"));
		IOException exception = assertThrows(IOException.class, () -> CartridgeReader
				.readCartridge(Platform.ATARI_800, null, new ByteArrayInputStream(new byte[0]), 0x500000, MAXIMUM_SIZE));
		assertEquals(Messages.E705.format("5242880", "4194304"), exception.getMessage());
	}

	private static CartridgeType detect(Platform platform, long fileSize) {
		return CartridgeReader.detectCartridgeType(platform, fileSize, new byte[16]);
	}

	private static Cartridge read(Platform platform, byte[] file) throws IOException {
		return CartridgeReader.readCartridge(platform, null, new ByteArrayInputStream(file), file.length, MAXIMUM_SIZE);
	}

	/** {@code size} bytes where every 4 KB page starts with its page number as a little-endian word. */
	private static byte[] createContent(int size) {
		byte[] content = new byte[size];
		for (int page = 0; page * 0x1000 < size; page++) {
			content[page * 0x1000] = (byte) page;
			content[page * 0x1000 + 1] = (byte) (page >> 8);
		}
		return content;
	}

	private static byte[] createCartridgeFile(CartridgeType cartridgeType) {
		byte[] content = createContent(cartridgeType.getSize());
		return concat(CartridgeFileUtility.createCartridgeHeaderWithCheckSum(cartridgeType.getNumericId(), content),
				content);
	}

	private static byte[] concat(byte[] a, byte[] b) {
		byte[] result = Arrays.copyOf(a, a.length + b.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	private static int[] bank(int number, int offset, int size, int address) {
		return new int[] { number, offset, size, address };
	}

	private static void assertBanks(Cartridge cartridge, int initialBank, int[]... expected) {
		assertEquals(expected.length, cartridge.getBanks().size());
		for (int i = 0; i < expected.length; i++) {
			assertBank(expected[i], cartridge.getBanks().get(i));
		}
		assertSame(cartridge.getBanks().get(initialBank), cartridge.getInitialBank());
		// The content of each bank is the image's at the bank's offset.
		for (Bank bank : cartridge.getBanks()) {
			int page = bank.getOffset() / 0x1000;
			assertEquals(page & 0xFF, cartridge.getContent()[bank.getOffset()] & 0xFF);
		}
	}

	private static void assertBank(int[] expected, Bank bank) {
		assertEquals(expected[0], bank.getNumber());
		assertEquals(expected[1], bank.getOffset());
		assertEquals(expected[2], bank.getSize());
		assertEquals(expected[3], bank.getAddress());
	}

	private static void assertError(Platform platform, byte[] file, String expectedMessage) {
		IOException exception = assertThrows(IOException.class, () -> read(platform, file));
		assertEquals(expectedMessage, exception.getMessage());
	}
}

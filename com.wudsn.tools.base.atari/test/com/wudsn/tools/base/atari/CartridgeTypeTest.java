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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.wudsn.tools.base.atari.CartridgeType.BankRegion;

/**
 * Checks the bank regions of {@link CartridgeType} against cart.txt: the
 * structure for every type, and the exact regions for one type of each layout.
 */
public class CartridgeTypeTest {

	private static final Set<CartridgeType> RIGHT_SLOT_OR_LOW_BANK = set(CartridgeType.CARTRIDGE_RIGHT_4,
			CartridgeType.CARTRIDGE_RIGHT_8, CartridgeType.CARTRIDGE_LOW_BANK_8);

	@Test
	public void testRegionsCoverEveryImage() {
		for (CartridgeType cartridgeType : CartridgeType.getValues()) {
			List<BankRegion> regions = cartridgeType.getBankRegions();
			if (cartridgeType == CartridgeType.UNKNOWN || cartridgeType == CartridgeType.CARTRIDGE_AST_32) {
				assertTrue(regions.isEmpty(), cartridgeType.getId());
				continue;
			}
			assertFalse(regions.isEmpty(), cartridgeType.getId());
			int offset = 0;
			for (BankRegion region : regions) {
				assertEquals(offset, region.getOffset(), cartridgeType.getId());
				assertTrue(region.getSize() > 0 && region.getSize() % region.getBankSize() == 0, cartridgeType.getId());
				assertFalse(region.getAddresses().isEmpty(), cartridgeType.getId());
				int lowest = cartridgeType.getPlatform() == Platform.ATARI_5200 ? 0x4000 : 0x8000;
				for (int address : region.getAddresses()) {
					assertTrue(address >= lowest && address + region.getBankSize() <= 0xC000, cartridgeType.getId());
				}
				offset += region.getSize();
			}
			assertEquals(cartridgeType.getSize(), offset, cartridgeType.getId());
		}
	}

	/**
	 * The bank with the last byte of the initial bank ends at $BFFF, where the
	 * Atari 800 cartridge header and the Atari 5200 title and vectors are -
	 * except for the right slot and the low bank, which end at $9FFF. The last
	 * byte, not the first: for the two chip 5200 cartridge, the initial bank is
	 * the whole image, and only its second chip ends at $BFFF.
	 */
	@Test
	public void testInitialBankEndsWithVectors() {
		for (CartridgeType cartridgeType : CartridgeType.getValues()) {
			int initialOffset = cartridgeType.getInitialBankOffset() + cartridgeType.getBankSize() - 1;
			for (BankRegion region : cartridgeType.getBankRegions()) {
				if (initialOffset >= region.getOffset() && initialOffset < region.getOffset() + region.getSize()) {
					int bank = (initialOffset - region.getOffset()) / region.getBankSize();
					int address = region.getAddresses().get(bank % region.getAddresses().size());
					int end = RIGHT_SLOT_OR_LOW_BANK.contains(cartridgeType) ? 0xA000 : 0xC000;
					assertEquals(end, address + region.getBankSize(), cartridgeType.getId());
				}
			}
		}
	}

	@Test
	public void testRegionsPerLayout() {
		assertRegions(CartridgeType.CARTRIDGE_WILL_64, region(0, 0x10000, 0x2000, Arrays.asList(0xA000), Collections.<Integer>emptyList()));
		assertRegions(CartridgeType.CARTRIDGE_MEGA_4096, region(0, 0x400000, 0x4000, Arrays.asList(0x8000), Collections.<Integer>emptyList()));
		assertRegions(CartridgeType.CARTRIDGE_STD_2, region(0, 0x800, 0x800, Arrays.asList(0xB800), Collections.<Integer>emptyList()));
		assertRegions(CartridgeType.CARTRIDGE_XEGS_128, region(0, 0x1E000, 0x2000, Arrays.asList(0x8000), Collections.<Integer>emptyList()),
				region(0x1E000, 0x2000, 0x2000, Arrays.asList(0xA000), Collections.<Integer>emptyList()));
		assertRegions(CartridgeType.CARTRIDGE_DB_32, region(0, 0x6000, 0x2000, Arrays.asList(0x8000), Collections.<Integer>emptyList()),
				region(0x6000, 0x2000, 0x2000, Arrays.asList(0xA000), Collections.<Integer>emptyList()));
		assertRegions(CartridgeType.CARTRIDGE_OSS_034M_16, region(0, 0x3000, 0x1000, Arrays.asList(0xA000), Collections.<Integer>emptyList()),
				region(0x3000, 0x1000, 0x1000, Arrays.asList(0xB000), Collections.<Integer>emptyList()));
		assertRegions(CartridgeType.CARTRIDGE_OSS_M091_16, region(0, 0x1000, 0x1000, Arrays.asList(0xB000), Collections.<Integer>emptyList()),
				region(0x1000, 0x3000, 0x1000, Arrays.asList(0xA000), Collections.<Integer>emptyList()));
		assertRegions(CartridgeType.CARTRIDGE_BBSB_40, region(0, 0x4000, 0x1000, Arrays.asList(0x8000), Collections.<Integer>emptyList()),
				region(0x4000, 0x4000, 0x1000, Arrays.asList(0x9000), Collections.<Integer>emptyList()),
				region(0x8000, 0x2000, 0x2000, Arrays.asList(0xA000), Collections.<Integer>emptyList()));
		assertRegions(CartridgeType.CARTRIDGE_5200_40, region(0, 0x4000, 0x1000, Arrays.asList(0x4000), Collections.<Integer>emptyList()),
				region(0x4000, 0x4000, 0x1000, Arrays.asList(0x5000), Collections.<Integer>emptyList()),
				region(0x8000, 0x2000, 0x2000, Arrays.asList(0xA000), Arrays.asList(0x8000)));
		assertRegions(CartridgeType.CARTRIDGE_SIC_128,
				region(0, 0x20000, 0x2000, Arrays.asList(0x8000, 0xA000), Collections.<Integer>emptyList()));
		assertRegions(CartridgeType.CARTRIDGE_5200_EE_16,
				region(0, 0x2000, 0x2000, Arrays.asList(0x4000), Arrays.asList(0x6000)),
				region(0x2000, 0x2000, 0x2000, Arrays.asList(0xA000), Arrays.asList(0x8000)));
		assertRegions(CartridgeType.CARTRIDGE_BLIZZARD_4,
				region(0, 0x1000, 0x1000, Arrays.asList(0xB000), Arrays.asList(0xA000)));
		assertRegions(CartridgeType.CARTRIDGE_5200_4,
				region(0, 0x1000, 0x1000, Arrays.asList(0xB000), Arrays.asList(0x8000, 0x9000, 0xA000)));
		assertRegions(CartridgeType.CARTRIDGE_5200_SUPER_128,
				region(0, 0x20000, 0x8000, Arrays.asList(0x4000), Collections.<Integer>emptyList()));
	}

	@Test
	public void testAtraxInterleaved() {
		Set<CartridgeType> interleaved = set(CartridgeType.CARTRIDGE_ATRAX_SDX_64,
				CartridgeType.CARTRIDGE_ATRAX_SDX_128, CartridgeType.CARTRIDGE_ATRAX_128);
		for (CartridgeType cartridgeType : CartridgeType.getValues()) {
			assertEquals(interleaved.contains(cartridgeType), cartridgeType.isAtraxInterleaved(),
					cartridgeType.getId());
		}
	}

	private static Set<CartridgeType> set(CartridgeType... cartridgeTypes) {
		return new HashSet<CartridgeType>(Arrays.asList(cartridgeTypes));
	}

	private static BankRegion region(int offset, int size, int bankSize, List<Integer> addresses,
			List<Integer> mirrorAddresses) {
		return new BankRegion(offset, size, bankSize, addresses, mirrorAddresses);
	}

	private static void assertRegions(CartridgeType cartridgeType, BankRegion... expected) {
		assertEquals(Arrays.asList(expected), cartridgeType.getBankRegions(), cartridgeType.getId());
	}
}

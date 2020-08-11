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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.wudsn.tools.base.repository.ValueSet;

/**
 * Values representing all known cartridge types as defined in <a href=
 * "https://sourceforge.net/p/atari800/source/ci/master/tree/src/cartridge.h"
 * >Atari800</a> by Tomasz Krasuski (Kr0tki). Their properties are described in
 * <a href=
 * "https://sourceforge.net/p/atari800/source/ci/master/tree/DOC/cart.txt"
 * >cart.txt<a/>.
 * 
 * @author Tomasz Krasuski
 * @author Peter Dell
 * 
 *         <pre>
 *    enum { CARTRIDGE_UNKNOWN        = -1,
 *        CARTRIDGE_NONE           =  0,
 *        CARTRIDGE_STD_8          =  1,
 *        CARTRIDGE_STD_16         =  2,
 *        CARTRIDGE_OSS_034M_16    =  3,
 *        CARTRIDGE_5200_32        =  4,
 *        CARTRIDGE_DB_32          =  5,
 *        CARTRIDGE_5200_EE_16     =  6,
 *        CARTRIDGE_5200_40        =  7,
 *        CARTRIDGE_WILL_64        =  8,
 *        CARTRIDGE_EXP_64         =  9,
 *        CARTRIDGE_DIAMOND_64     = 10,
 *        CARTRIDGE_SDX_64         = 11,
 *        CARTRIDGE_XEGS_32        = 12,
 *        CARTRIDGE_XEGS_64        = 13,
 *        CARTRIDGE_XEGS_128       = 14,
 *        CARTRIDGE_OSS_M091_16    = 15,
 *        CARTRIDGE_5200_NS_16     = 16,
 *        CARTRIDGE_ATRAX_DEC_128  = 17,
 *        CARTRIDGE_BBSB_40        = 18,
 *        CARTRIDGE_5200_8         = 19,
 *        CARTRIDGE_5200_4         = 20,
 *        CARTRIDGE_RIGHT_8        = 21,
 *        CARTRIDGE_WILL_32        = 22,
 *        CARTRIDGE_XEGS_256       = 23,
 *        CARTRIDGE_XEGS_512       = 24,
 *        CARTRIDGE_XEGS_1024      = 25,
 *        CARTRIDGE_MEGA_16        = 26,
 *        CARTRIDGE_MEGA_32        = 27,
 *        CARTRIDGE_MEGA_64        = 28,
 *        CARTRIDGE_MEGA_128       = 29,
 *        CARTRIDGE_MEGA_256       = 30,
 *        CARTRIDGE_MEGA_512       = 31,
 *        CARTRIDGE_MEGA_1024      = 32,
 *        CARTRIDGE_SWXEGS_32      = 33,
 *        CARTRIDGE_SWXEGS_64      = 34,
 *        CARTRIDGE_SWXEGS_128     = 35,
 *        CARTRIDGE_SWXEGS_256     = 36,
 *        CARTRIDGE_SWXEGS_512     = 37,
 *        CARTRIDGE_SWXEGS_1024    = 38,
 *        CARTRIDGE_PHOENIX_8      = 39,
 *        CARTRIDGE_BLIZZARD_16    = 40,
 *        CARTRIDGE_ATMAX_128      = 41,
 *        CARTRIDGE_ATMAX_1024     = 42,
 *        CARTRIDGE_SDX_128        = 43,
 *        CARTRIDGE_OSS_8          = 44,
 *        CARTRIDGE_OSS_043M_16    = 45,
 *        CARTRIDGE_BLIZZARD_4     = 46,
 *        CARTRIDGE_AST_32         = 47,
 *        CARTRIDGE_ATRAX_SDX_64   = 48,
 *        CARTRIDGE_ATRAX_SDX_128  = 49,
 *        CARTRIDGE_TURBOSOFT_64   = 50,
 *        CARTRIDGE_TURBOSOFT_128  = 51,
 *        CARTRIDGE_ULTRACART_32   = 52,
 *        CARTRIDGE_LOW_BANK_8     = 53,
 *        CARTRIDGE_SIC_128        = 54,
 *        CARTRIDGE_SIC_256        = 55,
 *        CARTRIDGE_SIC_512        = 56,
 *        CARTRIDGE_STD_2          = 57,
 *        CARTRIDGE_STD_4          = 58,
 *        CARTRIDGE_RIGHT_4        = 59,
 *        CARTRIDGE_BLIZZARD_32    = 60,
 *        CARTRIDGE_MEGAMAX_2048   = 61,
 *        CARTRIDGE_THECART_128M   = 62,
 *        CARTRIDGE_MEGA_4096      = 63,
 *        CARTRIDGE_MEGA_2048      = 64,
 *        CARTRIDGE_THECART_32M    = 65,
 *        CARTRIDGE_THECART_64M    = 66,
 *        CARTRIDGE_XEGS_8F_64     = 67,
 *        CARTRIDGE_ATRAX_128      = 68,
 *        CARTRIDGE_ADAWLIAH_32    = 69,
 *        CARTRIDGE_ADAWLIAH_64    = 70,
 *        CARTRIDGE_LAST_SUPPORTED = 70
 * };
 * </pre>
 * 
 *         <pre>
 * #define CARTRIDGE_STD_8_DESC         "Standard 8 KB cartridge"
 * #define CARTRIDGE_STD_16_DESC        "Standard 16 KB cartridge"
 * #define CARTRIDGE_OSS_034M_16_DESC   "OSS two chip 16 KB cartridge (034M)"
 * #define CARTRIDGE_5200_32_DESC       "Standard 32 KB 5200 cartridge"
 * #define CARTRIDGE_DB_32_DESC         "DB 32 KB cartridge"
 * #define CARTRIDGE_5200_EE_16_DESC    "Two chip 16 KB 5200 cartridge"
 * #define CARTRIDGE_5200_40_DESC       "Bounty Bob 40 KB 5200 cartridge"
 * #define CARTRIDGE_WILL_64_DESC       "64 KB Williams cartridge"
 * #define CARTRIDGE_EXP_64_DESC        "Express 64 KB cartridge"
 * #define CARTRIDGE_DIAMOND_64_DESC    "Diamond 64 KB cartridge"
 * #define CARTRIDGE_SDX_64_DESC        "SpartaDOS X 64 KB cartridge"
 * #define CARTRIDGE_XEGS_32_DESC       "XEGS 32 KB cartridge"
 * #define CARTRIDGE_XEGS_64_DESC       "XEGS 64 KB cartridge"
 * #define CARTRIDGE_XEGS_128_DESC      "XEGS 128 KB cartridge"
 * #define CARTRIDGE_OSS_M091_16_DESC   "OSS one chip 16 KB cartridge"
 * #define CARTRIDGE_5200_NS_16_DESC    "One chip 16 KB 5200 cartridge"
 * #define CARTRIDGE_ATRAX_128_DEC_DESC "Decoded Atrax 128 KB cartridge"
 * #define CARTRIDGE_BBSB_40_DESC       "Bounty Bob 40 KB cartridge"
 * #define CARTRIDGE_5200_8_DESC        "Standard 8 KB 5200 cartridge"
 * #define CARTRIDGE_5200_4_DESC        "Standard 4 KB 5200 cartridge"
 * #define CARTRIDGE_RIGHT_8_DESC       "Right slot 8 KB cartridge"
 * #define CARTRIDGE_WILL_32_DESC       "32 KB Williams cartridge"
 * #define CARTRIDGE_XEGS_256_DESC      "XEGS 256 KB cartridge"
 * #define CARTRIDGE_XEGS_512_DESC      "XEGS 512 KB cartridge"
 * #define CARTRIDGE_XEGS_1024_DESC     "XEGS 1 MB cartridge"
 * #define CARTRIDGE_MEGA_16_DESC       "MegaCart 16 KB cartridge"
 * #define CARTRIDGE_MEGA_32_DESC       "MegaCart 32 KB cartridge"
 * #define CARTRIDGE_MEGA_64_DESC       "MegaCart 64 KB cartridge"
 * #define CARTRIDGE_MEGA_128_DESC      "MegaCart 128 KB cartridge"
 * #define CARTRIDGE_MEGA_256_DESC      "MegaCart 256 KB cartridge"
 * #define CARTRIDGE_MEGA_512_DESC      "MegaCart 512 KB cartridge"
 * #define CARTRIDGE_MEGA_1024_DESC     "MegaCart 1 MB cartridge"
 * #define CARTRIDGE_SWXEGS_32_DESC     "Switchable XEGS 32 KB cartridge"
 * #define CARTRIDGE_SWXEGS_64_DESC     "Switchable XEGS 64 KB cartridge"
 * #define CARTRIDGE_SWXEGS_128_DESC    "Switchable XEGS 128 KB cartridge"
 * #define CARTRIDGE_SWXEGS_256_DESC    "Switchable XEGS 256 KB cartridge"
 * #define CARTRIDGE_SWXEGS_512_DESC    "Switchable XEGS 512 KB cartridge"
 * #define CARTRIDGE_SWXEGS_1024_DESC   "Switchable XEGS 1 MB cartridge"
 * #define CARTRIDGE_PHOENIX_8_DESC     "Phoenix 8 KB cartridge"
 * #define CARTRIDGE_BLIZZARD_16_DESC   "Blizzard 16 KB cartridge"
 * #define CARTRIDGE_ATMAX_128_DESC     "Atarimax 128 KB Flash cartridge"
 * #define CARTRIDGE_ATMAX_1024_DESC    "Atarimax 1 MB Flash cartridge"
 * #define CARTRIDGE_SDX_128_DESC       "SpartaDOS X 128 KB cartridge"
 * #define CARTRIDGE_OSS_8_DESC         "OSS 8 KB cartridge"
 * #define CARTRIDGE_OSS_043M_16_DESC   "OSS two chip 16 KB cartridge (043M)"
 * #define CARTRIDGE_BLIZZARD_4_DESC    "Blizzard 4 KB cartridge"
 * #define CARTRIDGE_AST_32_DESC        "AST 32 KB cartridge"
 * #define CARTRIDGE_ATRAX_SDX_64_DESC  "Atrax SDX 64 KB cartridge"
 * #define CARTRIDGE_ATRAX_SDX_128_DESC "Atrax SDX 128 KB cartridge"
 * #define CARTRIDGE_TURBOSOFT_64_DESC  "Turbosoft 64 KB cartridge"
 * #define CARTRIDGE_TURBOSOFT_128_DESC "Turbosoft 128 KB cartridge"
 * #define CARTRIDGE_ULTRACART_32_DESC  "Ultracart 32 KB cartridge"
 * #define CARTRIDGE_LOW_BANK_8_DESC    "Low bank 8 KB cartridge"
 * #define CARTRIDGE_SIC_128_DESC       "SIC! 128 KB cartridge"
 * #define CARTRIDGE_SIC_256_DESC       "SIC! 256 KB cartridge"
 * #define CARTRIDGE_SIC_512_DESC       "SIC! 512 KB cartridge"
 * #define CARTRIDGE_STD_2_DESC         "Standard 2 KB cartridge"
 * #define CARTRIDGE_STD_4_DESC         "Standard 4 KB cartridge"
 * #define CARTRIDGE_RIGHT_4_DESC       "Right slot 4 KB cartridge"
 * #define CARTRIDGE_BLIZZARD_32_DESC   "Blizzard 32 KB cartridge"
 * #define CARTRIDGE_MEGAMAX_2048_DESC  "MegaMax 2 MB cartridge"
 * #define CARTRIDGE_THECART_128M_DESC  "The!Cart 128 MB cartridge"
 * #define CARTRIDGE_MEGA_4096_DESC     "Flash MegaCart 4 MB cartridge"
 * #define CARTRIDGE_MEGA_2048_DESC     "MegaCart 2 MB cartridge"
 * #define CARTRIDGE_THECART_32M_DESC   "The!Cart 32 MB cartridge"
 * #define CARTRIDGE_THECART_64M_DESC   "The!Cart 64 MB cartridge"
 * #define CARTRIDGE_XEGS_8F_64_DESC    "XEGS 64 KB cartridge (banks 8-15)"
 * #define CARTRIDGE_ATRAX_128_DESC     "Atrax 128 KB cartridge"
 * #define CARTRIDGE_ADAWLIAH_32_DESC   "aDawliah 32 KB cartridge"
 * #define CARTRIDGE_ADAWLIAH_64_DESC   "aDawliah 64 KB cartridge"
 * </pre>
 */
public final class CartridgeType extends ValueSet {

	public static final CartridgeType UNKNOWN;

	public static final CartridgeType CARTRIDGE_STD_8;
	public static final CartridgeType CARTRIDGE_STD_16;
	public static final CartridgeType CARTRIDGE_OSS_034M_16;
	public static final CartridgeType CARTRIDGE_5200_32;
	public static final CartridgeType CARTRIDGE_DB_32;
	public static final CartridgeType CARTRIDGE_5200_EE_16;
	public static final CartridgeType CARTRIDGE_5200_40;
	public static final CartridgeType CARTRIDGE_WILL_64;
	public static final CartridgeType CARTRIDGE_EXP_64;
	public static final CartridgeType CARTRIDGE_DIAMOND_64;
	public static final CartridgeType CARTRIDGE_SDX_64;
	public static final CartridgeType CARTRIDGE_XEGS_32;
	public static final CartridgeType CARTRIDGE_XEGS_64;
	public static final CartridgeType CARTRIDGE_XEGS_128;
	public static final CartridgeType CARTRIDGE_OSS_M091_16;
	public static final CartridgeType CARTRIDGE_5200_NS_16;
	public static final CartridgeType CARTRIDGE_ATRAX_DEC_128;
	public static final CartridgeType CARTRIDGE_BBSB_40;
	public static final CartridgeType CARTRIDGE_5200_8;
	public static final CartridgeType CARTRIDGE_5200_4;
	public static final CartridgeType CARTRIDGE_RIGHT_8;
	public static final CartridgeType CARTRIDGE_WILL_32;
	public static final CartridgeType CARTRIDGE_XEGS_256;
	public static final CartridgeType CARTRIDGE_XEGS_512;
	public static final CartridgeType CARTRIDGE_XEGS_1024;
	public static final CartridgeType CARTRIDGE_MEGA_16;
	public static final CartridgeType CARTRIDGE_MEGA_32;
	public static final CartridgeType CARTRIDGE_MEGA_64;
	public static final CartridgeType CARTRIDGE_MEGA_128;
	public static final CartridgeType CARTRIDGE_MEGA_256;
	public static final CartridgeType CARTRIDGE_MEGA_512;
	public static final CartridgeType CARTRIDGE_MEGA_1024;
	public static final CartridgeType CARTRIDGE_SWXEGS_32;
	public static final CartridgeType CARTRIDGE_SWXEGS_64;
	public static final CartridgeType CARTRIDGE_SWXEGS_128;
	public static final CartridgeType CARTRIDGE_SWXEGS_256;
	public static final CartridgeType CARTRIDGE_SWXEGS_512;
	public static final CartridgeType CARTRIDGE_SWXEGS_1024;
	public static final CartridgeType CARTRIDGE_PHOENIX_8;
	public static final CartridgeType CARTRIDGE_BLIZZARD_16;
	public static final CartridgeType CARTRIDGE_ATMAX_128;
	public static final CartridgeType CARTRIDGE_ATMAX_1024;
	public static final CartridgeType CARTRIDGE_SDX_128;
	public static final CartridgeType CARTRIDGE_OSS_8;
	public static final CartridgeType CARTRIDGE_OSS_043M_16;
	public static final CartridgeType CARTRIDGE_BLIZZARD_4;
	public static final CartridgeType CARTRIDGE_AST_32;
	public static final CartridgeType CARTRIDGE_ATRAX_SDX_64;
	public static final CartridgeType CARTRIDGE_ATRAX_SDX_128;
	public static final CartridgeType CARTRIDGE_TURBOSOFT_64;
	public static final CartridgeType CARTRIDGE_TURBOSOFT_128;
	public static final CartridgeType CARTRIDGE_ULTRACART_32;
	public static final CartridgeType CARTRIDGE_LOW_BANK_8;
	public static final CartridgeType CARTRIDGE_SIC_128;
	public static final CartridgeType CARTRIDGE_SIC_256;
	public static final CartridgeType CARTRIDGE_SIC_512;
	public static final CartridgeType CARTRIDGE_STD_2;
	public static final CartridgeType CARTRIDGE_STD_4;
	public static final CartridgeType CARTRIDGE_RIGHT_4;
	public static final CartridgeType CARTRIDGE_BLIZZARD_32;
	public static final CartridgeType CARTRIDGE_MEGAMAX_2048;
	public static final CartridgeType CARTRIDGE_THECART_128M;
	public static final CartridgeType CARTRIDGE_MEGA_4096;
	public static final CartridgeType CARTRIDGE_MEGA_2048;
	public static final CartridgeType CARTRIDGE_THECART_32M;
	public static final CartridgeType CARTRIDGE_THECART_64M;
	public static final CartridgeType CARTRIDGE_XEGS_8F_64;
	public static final CartridgeType CARTRIDGE_ATRAX_128;
	public static final CartridgeType CARTRIDGE_ADAWLIAH_32;
	public static final CartridgeType CARTRIDGE_ADAWLIAH_64;

	// Instances
	private static final Map<String, CartridgeType> values;

	// Instance variables.
	private Platform platform;
	private int numericId;
	private int sizeInKB;
	private int bankSize;

	private int initialBankOffset;
	private int initialBankAddress;
	private int initialBankNumber;

	private int flashBlockSize;

	static {

		// Offset in the file to the 8k bank which contains the initial bank.
		final int offset_0000 = 0x0000;
		final int offset_2000 = 0x2000;
		final int offset_3000 = 0x3000; // for 16k cartridges with bank 0/3 at
		// fixed position
		final int offset_6000 = 0x6000; // for 32k cartridges with bank 0/7 at
		// fixed position
		final int offset_8000 = 0x8000; // for 40k cartridges
		final int offset_e000 = 0xe000; // For 64k cartridges
		final int offset_1e000 = 0x1e000; // For 128k cartridges
		final int offset_3e000 = 0x3e000; // For 256k cartridges
		final int offset_7e000 = 0x7e000; // For 512k cartridges
		final int offset_fe000 = 0xfe000; // For 1MB cartridges
		final int offset_3F8000 = 0x3F8000; // For 4MB cartridges with 16k banks
		// starting at bank 254
		// Address of the initial bank.
		final int adr_4000 = 0x4000;
		final int adr_8000 = 0x8000; // Not auto startable
		final int adr_9000 = 0x9000; // Not auto startable
		final int adr_a000 = 0xa000;
		final int adr_b000 = 0xb000;
		final int adr_b800 = 0xb800;

		// Size of the initial bank, required to compute mirroring.
		final int bank_size_0100 = 0x100;
		final int bank_size_0800 = 0x800;
		final int bank_size_1000 = 0x1000;
		final int bank_size_2000 = 0x2000;
		final int bank_size_4000 = 0x4000;
		final int bank_size_8000 = 0x8000;

		// Number of the initial bank in the bank switching scheme of the
		// cartridge. Not all cartridge type are linear in this regards, so it
		// cannot be computed from the offset and the bank size.
		// in <a
		// href="http://atari800.cvs.sourceforge.net/viewvc/atari800/atari800/src/cartridge.c">
		// ResetCartState(CARTRIDGE_image_t *cart) </>
		final int initial_bank_0 = 0;
		final int initial_bank_1 = 1;
		final int initial_bank_127 = 127;
		final int initial_bank_254 = 254;

		// Size of flash blocks.
		final int block_size_none = 0x00000; // None
		final int block_size_10000 = 0x10000; // 64 KB
		final int block_size_20000 = 0x20000; // 128 KB

		values = new TreeMap<String, CartridgeType>();

		UNKNOWN = add(0, "UNKNOWN", Platform.UNKNOWN, 0, 0x0000, 0x0000,
				0x0000, initial_bank_0, block_size_none);

		CARTRIDGE_STD_8 = add(1, "CARTRIDGE_STD_8", Platform.ATARI_800, 8,
				bank_size_2000, offset_0000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_STD_16 = add(2, "CARTRIDGE_STD_16", Platform.ATARI_800, 16,
				bank_size_4000, offset_0000, adr_8000, initial_bank_0,
				block_size_none);
		CARTRIDGE_OSS_034M_16 = add(3, "CARTRIDGE_OSS_034M_16",
				Platform.ATARI_800, 16, bank_size_1000, offset_3000, adr_b000,
				initial_bank_1, block_size_none);
		CARTRIDGE_5200_32 = add(4, "CARTRIDGE_5200_32", Platform.ATARI_5200,
				32, bank_size_8000, offset_0000, adr_4000, initial_bank_0,
				block_size_none);
		CARTRIDGE_DB_32 = add(5, "CARTRIDGE_DB_32", Platform.ATARI_800, 32,
				bank_size_2000, offset_6000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_5200_EE_16 = add(6, "CARTRIDGE_5200_EE_16",
				Platform.ATARI_5200, 16, bank_size_4000, offset_0000, adr_4000,
				initial_bank_0, block_size_none);
		CARTRIDGE_5200_40 = add(7, "CARTRIDGE_5200_40", Platform.ATARI_5200,
				40, bank_size_2000, offset_8000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_WILL_64 = add(8, "CARTRIDGE_WILL_64", Platform.ATARI_800, 64,
				bank_size_2000, offset_0000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_EXP_64 = add(9, "CARTRIDGE_EXP_64", Platform.ATARI_800, 64,
				bank_size_2000, offset_0000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_DIAMOND_64 = add(10, "CARTRIDGE_DIAMOND_64",
				Platform.ATARI_800, 64, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_SDX_64 = add(11, "CARTRIDGE_SDX_64", Platform.ATARI_800, 64,
				bank_size_2000, offset_0000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_XEGS_32 = add(12, "CARTRIDGE_XEGS_32", Platform.ATARI_800,
				32, bank_size_2000, offset_6000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_XEGS_64 = add(13, "CARTRIDGE_XEGS_64", Platform.ATARI_800,
				64, bank_size_2000, offset_e000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_XEGS_128 = add(14, "CARTRIDGE_XEGS_128", Platform.ATARI_800,
				128, bank_size_2000, offset_1e000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_OSS_M091_16 = add(15, "CARTRIDGE_OSS_M091_16",
				Platform.ATARI_800, 16, bank_size_1000, offset_0000, adr_b000,
				initial_bank_0, block_size_none);
		CARTRIDGE_5200_NS_16 = add(16, "CARTRIDGE_5200_NS_16",
				Platform.ATARI_5200, 16, bank_size_4000, offset_0000, adr_8000,
				initial_bank_0, block_size_none);
		CARTRIDGE_ATRAX_DEC_128 = add(17, "CARTRIDGE_ATRAX_DEC_128",
				Platform.ATARI_800, 128, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_BBSB_40 = add(18, "CARTRIDGE_BBSB_40", Platform.ATARI_800,
				40, bank_size_2000, offset_8000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_5200_8 = add(19, "CARTRIDGE_5200_8", Platform.ATARI_5200, 8,
				bank_size_2000, offset_0000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_5200_4 = add(20, "CARTRIDGE_5200_4", Platform.ATARI_5200, 4,
				bank_size_1000, offset_0000, adr_a000, initial_bank_0,
				block_size_none);
		// Autostart for CARTRIDGE_RIGHT_8 only works with Atari 800 / OS-A or
		// OS-B.
		CARTRIDGE_RIGHT_8 = add(21, "CARTRIDGE_RIGHT_8", Platform.ATARI_800, 8,
				bank_size_2000, offset_0000, adr_8000, initial_bank_0,
				block_size_none);
		CARTRIDGE_WILL_32 = add(22, "CARTRIDGE_WILL_32", Platform.ATARI_800,
				32, bank_size_2000, offset_0000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_XEGS_256 = add(23, "CARTRIDGE_XEGS_256", Platform.ATARI_800,
				256, bank_size_2000, offset_3e000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_XEGS_512 = add(24, "CARTRIDGE_XEGS_512", Platform.ATARI_800,
				512, bank_size_2000, offset_7e000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_XEGS_1024 = add(25, "CARTRIDGE_XEGS_1024",
				Platform.ATARI_800, 1024, bank_size_2000, offset_fe000,
				adr_a000, initial_bank_0, block_size_none);
		CARTRIDGE_MEGA_16 = add(26, "CARTRIDGE_MEGA_16", Platform.ATARI_800,
				16, bank_size_4000, offset_0000, adr_8000, initial_bank_0,
				block_size_none);
		CARTRIDGE_MEGA_32 = add(27, "CARTRIDGE_MEGA_32", Platform.ATARI_800,
				32, bank_size_4000, offset_0000, adr_8000, initial_bank_0,
				block_size_none);
		CARTRIDGE_MEGA_64 = add(28, "CARTRIDGE_MEGA_64", Platform.ATARI_800,
				64, bank_size_4000, offset_0000, adr_8000, initial_bank_0,
				block_size_none);
		CARTRIDGE_MEGA_128 = add(29, "CARTRIDGE_MEGA_128", Platform.ATARI_800,
				128, bank_size_4000, offset_0000, adr_8000, initial_bank_0,
				block_size_none);
		CARTRIDGE_MEGA_256 = add(30, "CARTRIDGE_MEGA_256", Platform.ATARI_800,
				256, bank_size_4000, offset_0000, adr_8000, initial_bank_0,
				block_size_none);
		CARTRIDGE_MEGA_512 = add(31, "CARTRIDGE_MEGA_512", Platform.ATARI_800,
				512, bank_size_4000, offset_0000, adr_8000, initial_bank_0,
				block_size_none);
		CARTRIDGE_MEGA_1024 = add(32, "CARTRIDGE_MEGA_1024",
				Platform.ATARI_800, 1024, bank_size_4000, offset_0000,
				adr_8000, initial_bank_0, block_size_none);
		CARTRIDGE_SWXEGS_32 = add(33, "CARTRIDGE_SWXEGS_32",
				Platform.ATARI_800, 32, bank_size_2000, offset_6000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_SWXEGS_64 = add(34, "CARTRIDGE_SWXEGS_64",
				Platform.ATARI_800, 64, bank_size_2000, offset_e000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_SWXEGS_128 = add(35, "CARTRIDGE_SWXEGS_128",
				Platform.ATARI_800, 128, bank_size_2000, offset_1e000,
				adr_a000, initial_bank_0, block_size_none);
		CARTRIDGE_SWXEGS_256 = add(36, "CARTRIDGE_SWXEGS_256",
				Platform.ATARI_800, 256, bank_size_2000, offset_3e000,
				adr_a000, initial_bank_0, block_size_none);
		CARTRIDGE_SWXEGS_512 = add(37, "CARTRIDGE_SWXEGS_512",
				Platform.ATARI_800, 512, bank_size_2000, offset_7e000,
				adr_a000, initial_bank_0, block_size_none);
		CARTRIDGE_SWXEGS_1024 = add(38, "CARTRIDGE_SWXEGS_1024",
				Platform.ATARI_800, 1024, bank_size_2000, offset_fe000,
				adr_a000, initial_bank_0, block_size_none);
		CARTRIDGE_PHOENIX_8 = add(39, "CARTRIDGE_PHOENIX_8",
				Platform.ATARI_800, 8, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_BLIZZARD_16 = add(40, "CARTRIDGE_BLIZZARD_16",
				Platform.ATARI_800, 16, bank_size_4000, offset_0000, adr_8000,
				initial_bank_0, block_size_none);
		CARTRIDGE_ATMAX_128 = add(41, "CARTRIDGE_ATMAX_128",
				Platform.ATARI_800, 128, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_10000);
		CARTRIDGE_ATMAX_1024 = add(42, "CARTRIDGE_ATMAX_1024",
				Platform.ATARI_800, 1024, bank_size_2000, offset_fe000,
				adr_a000, initial_bank_127, block_size_10000);
		CARTRIDGE_SDX_128 = add(43, "CARTRIDGE_SDX_128", Platform.ATARI_800,
				128, bank_size_2000, offset_0000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_OSS_8 = add(44, "CARTRIDGE_OSS_8", Platform.ATARI_800, 8,
				bank_size_1000, offset_0000, adr_b000, initial_bank_0,
				block_size_none);
		CARTRIDGE_OSS_043M_16 = add(45, "CARTRIDGE_OSS_043M_16",
				Platform.ATARI_800, 16, bank_size_1000, offset_3000, adr_b000,
				initial_bank_0, block_size_none);
		CARTRIDGE_BLIZZARD_4 = add(46, "CARTRIDGE_BLIZZARD_4",
				Platform.ATARI_800, 4, bank_size_1000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_AST_32 = add(47, "CARTRIDGE_AST_32", Platform.ATARI_800, 32,
				bank_size_0100, offset_0000, adr_a000, initial_bank_0,
				block_size_none);
		// This Atrax ROM uses interleaved address and data bits, otherwise it's
		// equal to type type 11.
		CARTRIDGE_ATRAX_SDX_64 = add(48, "CARTRIDGE_ATRAX_SDX_64",
				Platform.ATARI_800, 64, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		// This Atrax ROM uses interleaved address and data bits, otherwise it's
		// equal to type type 43.
		CARTRIDGE_ATRAX_SDX_128 = add(49, "CARTRIDGE_ATRAX_SDX_128",
				Platform.ATARI_800, 128, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_TURBOSOFT_64 = add(50, "CARTRIDGE_TURBOSOFT_64",
				Platform.ATARI_800, 64, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_TURBOSOFT_128 = add(51, "CARTRIDGE_TURBOSOFT_128",
				Platform.ATARI_800, 128, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_ULTRACART_32 = add(52, "CARTRIDGE_ULTRACART_32",
				Platform.ATARI_800, 32, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_LOW_BANK_8 = add(53, "CARTRIDGE_LOW_BANK_8",
				Platform.ATARI_800, 8, bank_size_2000, offset_0000, adr_8000,
				initial_bank_0, block_size_none);
		CARTRIDGE_SIC_128 = add(54, "CARTRIDGE_SIC_128", Platform.ATARI_800,
				128, bank_size_2000, offset_2000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_SIC_256 = add(55, "CARTRIDGE_SIC_256", Platform.ATARI_800,
				256, bank_size_2000, offset_2000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_SIC_512 = add(56, "CARTRIDGE_SIC_512", Platform.ATARI_800,
				512, bank_size_2000, offset_2000, adr_a000, initial_bank_0,
				block_size_none);
		CARTRIDGE_STD_2 = add(57, "CARTRIDGE_STD_2", Platform.ATARI_800, 2,
				bank_size_0800, offset_0000, adr_b800, initial_bank_0,
				block_size_none);
		CARTRIDGE_STD_4 = add(58, "CARTRIDGE_STD_4", Platform.ATARI_800, 4,
				bank_size_1000, offset_0000, adr_b000, initial_bank_0,
				block_size_none);
		// Autostart for CARTRIDGE_RIGHT_4 only works with Atari 800 / OS-A or
		// OS-B.
		CARTRIDGE_RIGHT_4 = add(59, "CARTRIDGE_RIGHT_4", Platform.ATARI_800, 4,
				bank_size_1000, offset_0000, adr_9000, initial_bank_0,
				block_size_none);
		CARTRIDGE_BLIZZARD_32 = add(60, "CARTRIDGE_BLIZZARD_32",
				Platform.ATARI_800, 32, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		CARTRIDGE_MEGAMAX_2048 = add(61, "CARTRIDGE_MEGAMAX_2048",
				Platform.ATARI_800, 2048, bank_size_4000, offset_0000,
				adr_8000, initial_bank_0, block_size_none);
		CARTRIDGE_THECART_128M = add(62, "CARTRIDGE_THECART_128M",
				Platform.ATARI_800, 131072, bank_size_2000, offset_0000,
				adr_a000, initial_bank_0, block_size_20000);
		CARTRIDGE_MEGA_4096 = add(63, "CARTRIDGE_MEGA_4096",
				Platform.ATARI_800, 4096, bank_size_4000, offset_3F8000,
				adr_8000, initial_bank_254, block_size_10000);
		CARTRIDGE_MEGA_2048 = add(64, "CARTRIDGE_MEGA_2048",
				Platform.ATARI_800, 2048, bank_size_4000, offset_0000,
				adr_8000, initial_bank_0, block_size_10000);
		CARTRIDGE_THECART_32M = add(65, "CARTRIDGE_THECART_32M",
				Platform.ATARI_800, 32768, bank_size_2000, offset_0000,
				adr_a000, initial_bank_0, block_size_20000);
		CARTRIDGE_THECART_64M = add(66, "CARTRIDGE_THECART_64M",
				Platform.ATARI_800, 65536, bank_size_2000, offset_0000,
				adr_a000, initial_bank_0, block_size_20000);
		CARTRIDGE_XEGS_8F_64 = add(67, "CARTRIDGE_XEGS_8F_64",
				Platform.ATARI_800, 64, bank_size_2000, offset_e000, adr_a000,
				initial_bank_0, block_size_none);
		// This Atrax ROM uses interleaved address and data bits, otherwise it's
		// equal to type type 17.
		CARTRIDGE_ATRAX_128 = add(68, "CARTRIDGE_ATRAX_128",
				Platform.ATARI_800, 128, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none); 
		CARTRIDGE_ADAWLIAH_32 = add(69, "CARTRIDGE_ADAWLIAH_32",
				Platform.ATARI_800, 32, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none); 
		CARTRIDGE_ADAWLIAH_64 = add(70, "CARTRIDGE_ADAWLIAH_64",
				Platform.ATARI_800, 64, bank_size_2000, offset_0000, adr_a000,
				initial_bank_0, block_size_none);
		initializeClass(CartridgeType.class, ValueSets.class);
	}

	private CartridgeType(int numericId, String id, Platform platform,
			int sizeInK, int bankSize, int initialBankOffset,
			int initialBankAddress, int initialBankNumber, int flashBlockSize) {
		super(id, 0);
		if (platform == null) {
			throw new IllegalArgumentException(
					"Parameter 'platform' must not be null.");
		}
		this.platform = platform;
		this.numericId = numericId;
		this.sizeInKB = sizeInK;
		this.bankSize = bankSize;
		this.initialBankOffset = initialBankOffset;
		this.initialBankAddress = initialBankAddress;
		this.initialBankNumber = initialBankNumber;
		this.flashBlockSize = flashBlockSize;
	}

	private static CartridgeType add(int numericId, String id,
			Platform platform, int sizeInKB, int bankSize,
			int initialBankOffset, int initialBankAddress,
			int initialBankNumber, int flashBlockSize) {
		if (numericId < 0) {
			throw new IllegalArgumentException(
					"Parameter 'numericId' must not be negative. Specified values is "
							+ numericId + ".");
		}
		if (sizeInKB < 0) {
			throw new IllegalArgumentException(
					"Parameter 'sizeInKB' must not be negative. Specified values is "
							+ sizeInKB + ".");
		}
		CartridgeType result;
		result = new CartridgeType(numericId, id, platform, sizeInKB, bankSize,
				initialBankOffset, initialBankAddress, initialBankNumber,
				flashBlockSize);
		values.put(id, result);
		return result;
	}

	/**
	 * Gets the unmodifiable list of all values
	 * 
	 * @return The unmodifiable list of all values, not <code>null</code>.
	 */
	public static List<CartridgeType> getValues() {
		return Collections.unmodifiableList(new ArrayList<CartridgeType>(values
				.values()));
	}

	/**
	 * Gets a value set instance by its numeric ID.
	 * 
	 * @param numbericId
	 *            The numeric ID.
	 * @return The value set instance or <code>null</code>.
	 */
	public static CartridgeType getInstance(int numbericId) {
		for (CartridgeType i : getValues()) {
			if (i.getNumericId() == numbericId) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Gets a value set instance by its id.
	 * 
	 * @param id
	 *            The id, not <code>null</code>.
	 * @return The value set instance or <code>null</code>.
	 */
	public static CartridgeType getInstance(String id) {
		if (id == null) {
			throw new IllegalArgumentException(
					"Parameter 'id' must not be null.");
		}
		return values.get(id);
	}

	/**
	 * Gets the platform of the cartridge type.
	 * 
	 * @return The platform of the cartridge type, not <code>null</code>.
	 */
	public Platform getPlatform() {
		return platform;
	}

	/**
	 * Gets the numeric id of the cartridge type.
	 * 
	 * @return The numeric id of the cartridge type, a non-negative integer.
	 */
	public int getNumericId() {
		return numericId;
	}

	/**
	 * Gets the (file) size of the module in KB (1024 bytes). Note that this
	 * size in larger than the CPU view of the module for all bank switching
	 * cartridges.
	 * 
	 * @return The (file) size of the module in KB (1024 bytes) or 0 if the size
	 *         is not defined/variable.
	 */
	public int getSizeInKB() {
		return sizeInKB;
	}

	public int getBankSize() {
		return bankSize;
	}

	public int getInitialBankOffset() {
		return initialBankOffset;
	}

	public int getInitialBankNumber() {
		return initialBankNumber;

	}

	public int getInitialBankAddress() {
		return initialBankAddress;
	}

	/**
	 * Gets the size of a flash block in bytes.
	 * 
	 * @return The size of a flash block in bytes or <code>0</code> if it is
	 *         unknown.
	 */
	public int getFlashBlockSize() {
		return flashBlockSize;
	}

	/**
	 * Determines the cartridge type is a The!Cart cartridge type.
	 * 
	 * @return <code>true</code> if the cartridge type is a The!Cart cartridge
	 *         type.
	 */
	public boolean isTheCart() {
		return this.equals(CARTRIDGE_THECART_32M)
				|| this.equals(CARTRIDGE_THECART_64M)
				|| this.equals(CARTRIDGE_THECART_128M);
	}

	/**
	 * Determines if incremental flashing is supported by the standard flasher
	 * of this cartridge type.
	 * 
	 * @return <code>true</code> if incremental flashing is supported and banks
	 *         should be kept stable.
	 */
	public boolean isIncrementalFlashingSupported() {
		return isTheCart();
	}

}

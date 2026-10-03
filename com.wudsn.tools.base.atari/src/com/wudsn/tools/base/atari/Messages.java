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

import com.wudsn.tools.base.repository.Message;
import com.wudsn.tools.base.repository.NLS;

/**
 * Messages of the Atari-specific WUDSN Base classes. The numbers start at 700,
 * clear of {@link com.wudsn.tools.base.Messages}' 200-304 and the ranges of
 * the applications.
 */
public final class Messages extends NLS {

	// Reading cartridge images
	/** A raw image whose size has no standard cartridge type: the size. */
	public static Message E700;
	/** A CART header with an unknown type number: the number. */
	public static Message E701;
	/** A cartridge of the other platform: type number and text, its platform, the expected platform. */
	public static Message E702;
	/** A cartridge type without known bank mapping: type number and text. */
	public static Message E703;
	/** An image size that does not match the type: the size, type number and text, the type's size. */
	public static Message E704;
	/** An image larger than allowed: the size, the maximum. */
	public static Message E705;

	static {
		initializeClass(Messages.class, null);
	}
}

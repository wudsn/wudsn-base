/*
 * Hello, a Java test program
 * Copyright (C) 2020 - 2021 Peter Dell
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.wudsn.tools.base.hello;

import org.eclipse.osgi.util.NLS;

final class Texts extends NLS {
	public static final String EMPTY = "";

	public static String APPLICATION_TITLE;
	/**
	 * Initializes the constants.
	 */
	static {
		NLS.initializeMessages(Texts.class.getName(), Texts.class);
	}
}
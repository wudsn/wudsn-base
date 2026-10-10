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

package com.wudsn.tools.base.repository;

/**
 * A messages repository class for {@link NLSMessagesTest}, built like an
 * application's: an area, one message per severity, texts in {@code
 * TestMessages.properties}.
 */
public final class TestMessages extends NLS {

	/** The area of the test messages. */
	public static final String AREA = "TST";

	public static Message E001;
	public static Message I002;
	public static Message S003;

	static {
		initializeClass(TestMessages.class, null);
	}
}

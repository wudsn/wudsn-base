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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * The message area and number rules of plan 01: {@link Message} itself, a
 * messages repository class loaded by {@link NLS}, and the checks
 * {@link NLS#getMessagesArea(Class)} and {@link NLS#getMessageNumber} make
 * while loading one. A failing check makes {@code NLS} log and exit the
 * JVM, so the checks are tested directly, not through a broken repository
 * class.
 */
public class NLSMessagesTest {

	public static final class ValidArea {
		public static final String AREA = "TST";
	}

	public static final class NoArea {
	}

	public static final class LowerCaseArea {
		public static final String AREA = "tst";
	}

	public static final class TwoLetterArea {
		public static final String AREA = "TS";
	}

	public static final class FourLetterArea {
		public static final String AREA = "TEST";
	}

	public static final class DigitArea {
		public static final String AREA = "T5T";
	}

	public static final class NullArea {
		public static final String AREA = null;
	}

	public static final class NotFinalArea {
		public static String AREA = "TST";
	}

	public static final class NotPublicArea {
		static final String AREA = "TST";
	}

	public static final class NotStringArea {
		public static final Integer AREA = Integer.valueOf(1);
	}

	@Test
	public void testMessage() {
		Message message = new Message("DMZ", "003", Message.ERROR, "Text {0}");
		assertEquals("DMZ", message.getArea());
		assertEquals("003", message.getNumber());
		assertEquals("DMZ-003", message.getIdentifier());
		assertEquals(Message.ERROR, message.getSeverity());

		for (String area : new String[] { null, "", "dmz", "DM", "DMZX", "D1Z" }) {
			assertThrows(IllegalArgumentException.class, () -> new Message(area, "003", Message.ERROR, "Text"),
					String.valueOf(area));
		}
		for (String number : new String[] { null, "", "03", "0003", "abc", "-03" }) {
			assertThrows(IllegalArgumentException.class, () -> new Message("DMZ", number, Message.ERROR, "Text"),
					String.valueOf(number));
		}
	}

	@Test
	public void testLoadedMessages() {
		assertEquals("TST-001", TestMessages.E001.getIdentifier());
		assertEquals(Message.ERROR, TestMessages.E001.getSeverity());
		assertEquals("File 'x.rom' cannot be read.", TestMessages.E001.format("x.rom"));

		assertEquals("TST-002", TestMessages.I002.getIdentifier());
		assertEquals(Message.INFO, TestMessages.I002.getSeverity());

		assertEquals("TST-003", TestMessages.S003.getIdentifier());
		assertEquals(Message.STATUS, TestMessages.S003.getSeverity());
	}

	@Test
	public void testMessagesArea() {
		assertEquals("TST", NLS.getMessagesArea(ValidArea.class));
		for (Class<?> clazz : new Class<?>[] { NoArea.class, LowerCaseArea.class, TwoLetterArea.class,
				FourLetterArea.class, DigitArea.class, NullArea.class, NotFinalArea.class, NotPublicArea.class,
				NotStringArea.class }) {
			assertThrows(RuntimeException.class, () -> NLS.getMessagesArea(clazz), clazz.getSimpleName());
		}
	}

	@Test
	public void testMessageNumber() {
		Set<String> numbers = new HashSet<String>();
		assertEquals("110", NLS.getMessageNumber(TestMessages.class, "E110", numbers));
		assertEquals("111", NLS.getMessageNumber(TestMessages.class, "I111", numbers));

		// The same number with another severity would show the same identifier.
		assertThrows(RuntimeException.class, () -> NLS.getMessageNumber(TestMessages.class, "I110", numbers));
		assertThrows(RuntimeException.class, () -> NLS.getMessageNumber(TestMessages.class, "E110", numbers));

		for (String fieldName : new String[] { "E11", "E1100", "EABC", "E", "E1-1" }) {
			assertThrows(RuntimeException.class,
					() -> NLS.getMessageNumber(TestMessages.class, fieldName, new HashSet<String>()), fieldName);
		}
	}
}

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

package com.wudsn.tools.base.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.prefs.BackingStoreException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Sections are created once and kept in their own node under the
 * application's root; values round-trip; {@code clear()} brings back the
 * defaults.
 */
public class ApplicationSettingsTest {

	private TestSettings testSettings;
	private ApplicationSettings settings;

	@BeforeEach
	public void setUp() {
		testSettings = new TestSettings();
		settings = new ApplicationSettings(testSettings.getRoot());
	}

	@AfterEach
	public void tearDown() throws BackingStoreException {
		testSettings.remove();
	}

	@Test
	public void testSections() throws BackingStoreException {
		ApplicationSettingsSection fonts = settings.getSection("Fonts");
		assertSame(fonts, settings.getSection("Fonts"));
		assertNotSame(fonts, settings.getSection("Folders"));
		assertSame(testSettings.getRoot(), settings.getRoot());

		fonts.writeString("Family", "Consolas");
		assertTrue(testSettings.getRoot().nodeExists("Fonts"));
		assertEquals("Consolas", testSettings.getRoot().node("Fonts").get("Family", ""));

		assertThrows(IllegalArgumentException.class, () -> new ApplicationSettings(null));
		assertThrows(IllegalArgumentException.class, () -> settings.getSection(""));
	}

	@Test
	public void testValues() {
		ApplicationSettingsSection section = settings.getSection("Values");
		assertEquals("default", section.getString("Text", "default"));
		assertEquals(12, section.getUnsignedInt("Size", 12));

		section.writeString("Text", "value");
		section.writeUnsignedInt("Size", 16);
		assertEquals("value", section.getString("Text", "default"));
		assertEquals(16, section.getUnsignedInt("Size", 12));

		// A second instance on the same root sees the same values.
		ApplicationSettingsSection other = new ApplicationSettings(testSettings.getRoot()).getSection("Values");
		assertEquals("value", other.getString("Text", "default"));

		section.clear();
		assertEquals("default", section.getString("Text", "default"));
		assertEquals(12, section.getUnsignedInt("Size", 12));
	}
}

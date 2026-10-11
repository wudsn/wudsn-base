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

package com.wudsn.tools.base.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.prefs.BackingStoreException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wudsn.tools.base.common.ApplicationSettings;
import com.wudsn.tools.base.common.TestSettings;

/**
 * The layout properties round-trip through a settings section; keys removed
 * from the map are removed from the section on save.
 */
public class SettingsAttributeTablePreferencesTest {

	private TestSettings testSettings;

	@BeforeEach
	public void setUp() {
		testSettings = new TestSettings();
	}

	@AfterEach
	public void tearDown() throws BackingStoreException {
		testSettings.remove();
	}

	private SettingsAttributeTablePreferences create() {
		return new SettingsAttributeTablePreferences(
				new ApplicationSettings(testSettings.getRoot()).getSection("AttributeTables"));
	}

	@Test
	public void testRoundTrip() {
		SettingsAttributeTablePreferences preferences = create();
		assertTrue(preferences.getLayoutProperties().isEmpty());

		Map<String, String> properties = preferences.getLayoutProperties();
		properties.put("list.columns.title.visible", "true");
		properties.put("list.columns.title.width", "120");
		properties.put("list.columns.date.visible", "false");
		preferences.save();
		assertEquals(properties, create().getLayoutProperties());

		properties.remove("list.columns.date.visible");
		preferences.save();
		assertEquals(Map.of("list.columns.title.visible", "true", "list.columns.title.width", "120"),
				create().getLayoutProperties());
	}

	@Test
	public void testNullSection() {
		assertThrows(IllegalArgumentException.class, () -> new SettingsAttributeTablePreferences(null));
	}
}

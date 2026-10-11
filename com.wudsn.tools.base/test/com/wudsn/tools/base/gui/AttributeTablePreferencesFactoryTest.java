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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.prefs.BackingStoreException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wudsn.tools.base.common.ApplicationSettings;
import com.wudsn.tools.base.common.ApplicationSettingsSection;
import com.wudsn.tools.base.common.TestSettings;

/**
 * In-memory preferences are independent maps; persistent preferences write
 * every change through to their section, so a new instance on the same
 * section sees it without any explicit save.
 */
public class AttributeTablePreferencesFactoryTest {

	private TestSettings testSettings;

	@BeforeEach
	public void setUp() {
		testSettings = new TestSettings();
	}

	@AfterEach
	public void tearDown() throws BackingStoreException {
		testSettings.remove();
	}

	private ApplicationSettingsSection getSection() {
		return new ApplicationSettings(testSettings.getRoot()).getSection("AttributeTables");
	}

	@Test
	public void testInMemory() {
		AttributeTablePreferences preferences = AttributeTablePreferencesFactory.createInMemory();
		preferences.getLayoutProperties().put("list.columns.title.visible", "true");
		assertEquals("true", preferences.getLayoutProperties().get("list.columns.title.visible"));
		assertNotSame(preferences.getLayoutProperties(),
				AttributeTablePreferencesFactory.createInMemory().getLayoutProperties());
		assertTrue(AttributeTablePreferencesFactory.createInMemory().getLayoutProperties().isEmpty());
	}

	@Test
	public void testPersistentWritesThrough() {
		AttributeTablePreferences preferences = AttributeTablePreferencesFactory.createPersistent(getSection());
		Map<String, String> properties = preferences.getLayoutProperties();
		assertTrue(properties.isEmpty());

		properties.put("list.columns.title.visible", "true");
		properties.put("list.columns.title.width", "120");
		properties.put("list.columns.date.visible", "false");
		assertEquals(properties,
				AttributeTablePreferencesFactory.createPersistent(getSection()).getLayoutProperties());

		assertEquals("false", properties.remove("list.columns.date.visible"));
		assertEquals(null, properties.remove("list.columns.date.visible"));
		assertEquals(Map.of("list.columns.title.visible", "true", "list.columns.title.width", "120"),
				AttributeTablePreferencesFactory.createPersistent(getSection()).getLayoutProperties());

		properties.clear();
		assertTrue(AttributeTablePreferencesFactory.createPersistent(getSection()).getLayoutProperties().isEmpty());
	}

	@Test
	public void testPersistentViewsAreReadOnly() {
		Map<String, String> properties = AttributeTablePreferencesFactory.createPersistent(getSection())
				.getLayoutProperties();
		properties.put("key", "value");
		assertThrows(UnsupportedOperationException.class, () -> properties.keySet().remove("key"));
		assertThrows(UnsupportedOperationException.class, () -> properties.entrySet().clear());
		assertEquals("value", properties.get("key"));
	}

	@Test
	public void testNullSection() {
		assertThrows(IllegalArgumentException.class, () -> AttributeTablePreferencesFactory.createPersistent(null));
	}
}

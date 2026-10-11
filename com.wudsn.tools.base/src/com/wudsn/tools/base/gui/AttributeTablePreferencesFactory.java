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

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.wudsn.tools.base.common.ApplicationSettings;
import com.wudsn.tools.base.common.ApplicationSettingsSection;

/**
 * Creates {@link AttributeTablePreferences}, either kept in memory or kept in
 * an {@link ApplicationSettingsSection}.
 *
 * @author Peter Dell
 */
public final class AttributeTablePreferencesFactory {

	private AttributeTablePreferencesFactory() {
	}

	/**
	 * Creates preferences that are kept in memory only, e.g. for a table or
	 * dialog without stored settings.
	 *
	 * @return The preferences, not {@code null}.
	 */
	public static AttributeTablePreferences createInMemory() {
		return new MapPreferences(new HashMap<String, String>());
	}

	/**
	 * Creates preferences that are kept in the given section, one key per
	 * layout property.
	 * <p>
	 * The properties are read from the section now, so this must be called
	 * before the tables are created, because an {@link AttributeTable} reads
	 * its layout in its constructor. Every change of the properties, e.g. by
	 * {@link AttributeTable#saveLayout()}, is written through to the section;
	 * {@link ApplicationSettings#flush()} then writes it to the backing store,
	 * e.g. on exit.
	 * <p>
	 * The key and entry views of the properties are read-only; use
	 * {@code put}, {@code remove} and {@code clear} to change them.
	 *
	 * @param section
	 *            The section, not {@code null}. It holds only the layout
	 *            properties, because {@code clear} clears the whole section.
	 * @return The preferences, not {@code null}.
	 */
	public static AttributeTablePreferences createPersistent(ApplicationSettingsSection section) {
		if (section == null) {
			throw new IllegalArgumentException("Parameter 'section' must not be null.");
		}
		return new MapPreferences(new SectionMap(section));
	}

	private static final class MapPreferences implements AttributeTablePreferences {

		private final Map<String, String> layoutProperties;

		MapPreferences(Map<String, String> layoutProperties) {
			this.layoutProperties = layoutProperties;
		}

		@Override
		public Map<String, String> getLayoutProperties() {
			return layoutProperties;
		}
	}

	// The properties in memory, every change written through to the section.
	private static final class SectionMap extends AbstractMap<String, String> {

		private final ApplicationSettingsSection section;
		private final Map<String, String> properties;

		SectionMap(ApplicationSettingsSection section) {
			this.section = section;
			properties = new HashMap<String, String>();
			for (String key : section.getKeys()) {
				properties.put(key, section.getString(key, ""));
			}
		}

		@Override
		public Set<Entry<String, String>> entrySet() {
			return Collections.unmodifiableMap(properties).entrySet();
		}

		@Override
		public String get(Object key) {
			return properties.get(key);
		}

		@Override
		public boolean containsKey(Object key) {
			return properties.containsKey(key);
		}

		@Override
		public String put(String key, String value) {
			// Written first, so a rejected key or value leaves the map unchanged.
			section.writeString(key, value);
			return properties.put(key, value);
		}

		@Override
		public String remove(Object key) {
			if (!properties.containsKey(key)) {
				return null;
			}
			section.remove((String) key);
			return properties.remove(key);
		}

		@Override
		public void clear() {
			section.clear();
			properties.clear();
		}
	}
}

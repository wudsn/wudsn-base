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

import java.util.HashMap;
import java.util.Map;

import com.wudsn.tools.base.common.ApplicationSettingsSection;

/**
 * {@link AttributeTablePreferences} kept in one {@link ApplicationSettingsSection},
 * one key per layout property.
 * <p>
 * The properties are read from the section when this instance is created,
 * which must happen before the tables are created, because an
 * {@link AttributeTable} reads its layout in its constructor. The tables
 * write their layout into the map with {@link AttributeTable#saveLayout()};
 * {@link #save()} then writes the map back to the section, e.g. on exit.
 *
 * @author Peter Dell
 */
public final class SettingsAttributeTablePreferences implements AttributeTablePreferences {

	private final ApplicationSettingsSection section;
	private final Map<String, String> layoutProperties;

	/**
	 * @param section
	 *            The section, not {@code null}. It holds only the layout
	 *            properties, because {@link #save()} replaces its content.
	 */
	public SettingsAttributeTablePreferences(ApplicationSettingsSection section) {
		if (section == null) {
			throw new IllegalArgumentException("Parameter 'section' must not be null.");
		}
		this.section = section;
		layoutProperties = new HashMap<String, String>();
		for (String key : section.getKeys()) {
			layoutProperties.put(key, section.getString(key, ""));
		}
	}

	@Override
	public Map<String, String> getLayoutProperties() {
		return layoutProperties;
	}

	/** Replaces the content of the section with the current layout properties. */
	public void save() {
		section.clear();
		for (Map.Entry<String, String> entry : layoutProperties.entrySet()) {
			section.writeString(entry.getKey(), entry.getValue());
		}
	}
}

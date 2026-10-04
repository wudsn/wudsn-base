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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * One named section of persistent application settings (key/value pairs).
 * <p>
 * Backed by {@code java.util.prefs.Preferences} - the JDK-built-in,
 * cross-platform mechanism for small named sections of persistent
 * key/value settings - with one {@code Preferences} node per section,
 * created by {@link ApplicationSettings#getSection}.
 *
 * @author Peter Dell
 */
public final class ApplicationSettingsSection {

	private final Preferences preferences;

	ApplicationSettingsSection(Preferences preferences) {
		this.preferences = preferences;
	}

	public String getString(String keyName, String defaultValue) {
		return preferences.get(keyName, defaultValue);
	}

	public void writeString(String keyName, String value) {
		preferences.put(keyName, value);
	}

	public int getUnsignedInt(String keyName, int defaultValue) {
		return preferences.getInt(keyName, defaultValue);
	}

	public void writeUnsignedInt(String keyName, int value) {
		preferences.putInt(keyName, value);
	}

	/** Deletes every key in this section, so a later {@code getXxx} call falls back to its own coded default again. */
	public void clear() {
		try {
			preferences.clear();
		} catch (BackingStoreException ex) {
			throw new IllegalStateException(ex);
		}
	}
}

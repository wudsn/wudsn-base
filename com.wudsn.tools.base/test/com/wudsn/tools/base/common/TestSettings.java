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

import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * A throw-away {@link Preferences} node for a test, under the user root, so
 * that tests never touch an application's real settings. {@link #remove()}
 * deletes it.
 */
public final class TestSettings {

	private final Preferences root;

	public TestSettings() {
		root = Preferences.userRoot().node("com/wudsn/tools/base/test/" + UUID.randomUUID());
	}

	public Preferences getRoot() {
		return root;
	}

	public void remove() throws BackingStoreException {
		root.removeNode();
		root.flush();
	}
}

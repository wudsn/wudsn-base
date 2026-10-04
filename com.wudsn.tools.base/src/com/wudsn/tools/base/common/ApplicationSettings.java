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

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * The persistent settings of one application: named {@link
 * ApplicationSettingsSection}s, each a child node of the application's own
 * root {@link Preferences} node.
 * <p>
 * The application chooses the root - typically {@code
 * Preferences.userNodeForPackage(MyApplication.class)}, or a throw-away node
 * in a test. It must not be a node of this library's packages, which every
 * WUDSN tool would share.
 *
 * @author Peter Dell
 */
public final class ApplicationSettings {

	private final Preferences root;
	private final Map<String, ApplicationSettingsSection> sections = new HashMap<String, ApplicationSettingsSection>();

	public ApplicationSettings(Preferences root) {
		if (root == null) {
			throw new IllegalArgumentException("Parameter 'root' must not be null.");
		}
		this.root = root;
	}

	/** The root node the sections are kept under. */
	public Preferences getRoot() {
		return root;
	}

	/** The section {@code name}, created on first use and the same instance afterwards. */
	public ApplicationSettingsSection getSection(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' must not be empty.");
		}
		ApplicationSettingsSection section = sections.get(name);
		if (section == null) {
			section = new ApplicationSettingsSection(root.node(name));
			sections.put(name, section);
		}
		return section;
	}
}

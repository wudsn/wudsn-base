/**
 * Copyright (C) 2013 - 2018 <a href="http://www.wudsn.com" target="_top">Peter Dell</a>
 *
 * This file is part of The!Cart Studio distribution.
 * 
 * The!Cart Studio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * The!Cart Studio distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with The!Cart Studio. If not, see <http://www.gnu.org/licenses/>.
 */

package com.wudsn.tools.base.atari;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.wudsn.tools.base.repository.ValueSet;

/**
 * Value set representing all known content types, including all cartridge
 * types.
 */
public final class Platform extends ValueSet {
	public static final Platform UNKNOWN;
	public static final Platform ATARI_5200;
	public static final Platform ATARI_800;

	// Instances
	private static final Map<String, Platform> values;

	// Instance attributes
	private String cartridgeTypeSampleCreatorPath;

	static {

		values = new TreeMap<String, Platform>();

		UNKNOWN = add("UNKNOWN", "");
		ATARI_5200 = add("ATARI_5200",
				"data/CartridgeTypeSampleCreator-Atari5200.rom");
		ATARI_800 = add("ATARI_800",
				"data/CartridgeTypeSampleCreator-Atari800.rom");

		initializeClass(Platform.class, ValueSets.class);
	}

	private Platform(String id, String cartridgeTypeSampleCreatorPath) {
		super(id, id, 0);
		this.cartridgeTypeSampleCreatorPath = cartridgeTypeSampleCreatorPath;
	}

	private static Platform add(String id, String cartridgeTypeSampleCreatorPath) {
		if (id == null) {
			throw new IllegalArgumentException(
					"Parameter 'id' must not be null.");
		}
		if (cartridgeTypeSampleCreatorPath == null) {
			throw new IllegalArgumentException(
					"Parameter 'cartridgeTypeSampleCreatorPath' must not be null.");
		}
		Platform result;
		result = new Platform(id, cartridgeTypeSampleCreatorPath);
		values.put(result.getId(), result);
		return result;
	}

	/**
	 * Gets the unmodifiable list of all values
	 * 
	 * @return The unmodifiable list of all values, not <code>null</code>.
	 */
	public static List<Platform> getValues() {
		return Collections.unmodifiableList(new ArrayList<Platform>(values
				.values()));
	}

	/**
	 * Gets a value set instance by its id.
	 * 
	 * @param id
	 *            The id, not <code>null</code>.
	 * @return The value set instance or <code>null</code>.
	 */
	public static Platform getInstance(String id) {
		if (id == null) {
			throw new IllegalArgumentException(
					"Parameter 'id' must not be null.");
		}
		return values.get(id);
	}

	/**
	 * Gets the path to the same creator binary.
	 * 
	 * @return The cartridge type, not <code>null</code>.
	 */
	public String getCartridgeTypeSampleCreatorPath() {
		return cartridgeTypeSampleCreatorPath;
	}
}

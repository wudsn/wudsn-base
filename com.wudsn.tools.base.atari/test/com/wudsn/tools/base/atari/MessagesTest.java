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

package com.wudsn.tools.base.atari;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

/**
 * Checks that every {@link Messages} field is loaded and that the English and
 * German properties define exactly these fields' keys - independent of the
 * locale the test runs in.
 */
public class MessagesTest {

	@Test
	public void testMessages() throws Exception {
		Set<String> fieldNames = new TreeSet<String>();
		for (Field field : Messages.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				assertNotNull(field.get(null), field.getName());
				fieldNames.add(field.getName());
			}
		}
		assertEquals(fieldNames, loadKeys("Messages.properties"));
		assertEquals(fieldNames, loadKeys("Messages_de.properties"));
	}

	private static Set<String> loadKeys(String resourceName) throws IOException {
		Properties properties = new Properties();
		try (InputStream inputStream = Messages.class.getResourceAsStream(resourceName)) {
			assertNotNull(inputStream, resourceName);
			properties.load(inputStream);
		}
		return new TreeSet<String>(properties.stringPropertyNames());
	}
}

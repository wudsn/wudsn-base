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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The order, duplicates and limit of an {@link MRUList}, and how it is
 * stored: the keys, the round trip, unknown types, emptied slots.
 */
public class MRUListTest {

	private TestSettings testSettings;
	private ApplicationSettingsSection section;

	@BeforeEach
	public void setUp() {
		testSettings = new TestSettings();
		section = new ApplicationSettings(testSettings.getRoot()).getSection("RecentFiles");
	}

	@AfterEach
	public void tearDown() throws BackingStoreException {
		testSettings.remove();
	}

	private MRUList<TestFileType> createList() {
		return new MRUList<TestFileType>(TestFileType.class, section, 3);
	}

	private static List<String> getFilePaths(MRUList<TestFileType> list) {
		List<String> result = new ArrayList<String>();
		for (MRUEntry<TestFileType> entry : list.getEntries()) {
			result.add(entry.getFilePath());
		}
		return result;
	}

	@Test
	public void testOrderDuplicatesAndLimit() {
		MRUList<TestFileType> list = createList();
		list.addFile("a.xex", TestFileType.EXECUTABLE);
		list.addFile("b.wrk", TestFileType.WORKSPACE);
		list.addFile("c.xex", TestFileType.EXECUTABLE);
		assertEquals("[c.xex, b.wrk, a.xex]", getFilePaths(list).toString());

		// The same path again, also with different case, moves to the front with its new type.
		list.addFile("B.WRK", TestFileType.EXECUTABLE);
		assertEquals("[B.WRK, c.xex, a.xex]", getFilePaths(list).toString());
		assertSame(TestFileType.EXECUTABLE, list.getEntries().get(0).getFileType());

		// Beyond the limit, the oldest entry is dropped.
		list.addFile("d.wrk", TestFileType.WORKSPACE);
		assertEquals("[d.wrk, B.WRK, c.xex]", getFilePaths(list).toString());

		assertEquals("B.WRK", list.getLastFilePath(TestFileType.EXECUTABLE));
		assertEquals("d.wrk", list.getLastFilePath(TestFileType.WORKSPACE));
		list.clear();
		assertEquals("", list.getLastFilePath(TestFileType.EXECUTABLE));

		assertThrows(UnsupportedOperationException.class, () -> list.getEntries().clear());
		assertThrows(IllegalArgumentException.class,
				() -> new MRUList<TestFileType>(TestFileType.class, section, 0));
	}

	@Test
	public void testSaveAndLoad() {
		MRUList<TestFileType> list = createList();
		list.addFile("a.xex", TestFileType.EXECUTABLE);
		list.addFile("b.wrk", TestFileType.WORKSPACE);
		list.save();

		// The keys, as the applications' existing settings have them.
		assertEquals("b.wrk", section.getString("FilePath_1", ""));
		assertEquals("WORKSPACE", section.getString("FileType_1", ""));
		assertEquals("a.xex", section.getString("FilePath_2", ""));
		assertEquals("EXECUTABLE", section.getString("FileType_2", ""));

		MRUList<TestFileType> loaded = createList();
		loaded.load();
		assertEquals("[b.wrk, a.xex]", getFilePaths(loaded).toString());
		assertSame(TestFileType.WORKSPACE, loaded.getEntries().get(0).getFileType());
		assertSame(TestFileType.EXECUTABLE, loaded.getEntries().get(1).getFileType());
	}

	@Test
	public void testUnknownTypeIsSkipped() {
		section.writeString("FilePath_1", "old.cfg");
		section.writeString("FileType_1", "NO_LONGER_A_TYPE");
		section.writeString("FilePath_2", "a.xex");
		section.writeString("FileType_2", "EXECUTABLE");
		section.writeString("FilePath_3", "no-type.xex");

		MRUList<TestFileType> list = createList();
		list.load();
		assertEquals("[a.xex]", getFilePaths(list).toString());
	}

	@Test
	public void testShorterListEmptiesOldSlots() {
		MRUList<TestFileType> list = createList();
		list.addFile("a.xex", TestFileType.EXECUTABLE);
		list.addFile("b.xex", TestFileType.EXECUTABLE);
		list.save();

		list.clear();
		list.addFile("c.xex", TestFileType.EXECUTABLE);
		list.save();

		MRUList<TestFileType> loaded = createList();
		loaded.load();
		assertEquals("[c.xex]", getFilePaths(loaded).toString());
	}
}

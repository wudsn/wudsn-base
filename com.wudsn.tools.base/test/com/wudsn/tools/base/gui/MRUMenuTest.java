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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wudsn.tools.base.common.ApplicationSettings;
import com.wudsn.tools.base.common.MRUEntry;
import com.wudsn.tools.base.common.MRUList;
import com.wudsn.tools.base.common.TestFileType;
import com.wudsn.tools.base.common.TestSettings;

/**
 * {@link MRUMenu#fill}: the items' texts and order, the menu's enabled
 * state, refilling, and an item passing its entry to the listener. Swing
 * menus need no display, so this also runs headless.
 */
public class MRUMenuTest {

	private TestSettings testSettings;
	private MRUList<TestFileType> list;

	@BeforeEach
	public void setUp() {
		testSettings = new TestSettings();
		list = new MRUList<TestFileType>(TestFileType.class,
				new ApplicationSettings(testSettings.getRoot()).getSection("RecentFiles"), 5);
	}

	@AfterEach
	public void tearDown() throws BackingStoreException {
		testSettings.remove();
	}

	@Test
	public void testFill() {
		final List<MRUEntry<TestFileType>> selected = new ArrayList<MRUEntry<TestFileType>>();
		MRUMenu.SelectionListener<TestFileType> listener = new MRUMenu.SelectionListener<TestFileType>() {
			@Override
			public void onSelect(MRUEntry<TestFileType> entry) {
				selected.add(entry);
			}
		};

		JMenu menu = new JMenu("Recent Files");
		MRUMenu.fill(menu, list, listener);
		assertEquals(0, menu.getItemCount());
		assertFalse(menu.isEnabled());

		list.addFile("a.xex", TestFileType.EXECUTABLE);
		list.addFile("b.wrk", TestFileType.WORKSPACE);
		MRUMenu.fill(menu, list, listener);
		assertTrue(menu.isEnabled());
		assertEquals(2, menu.getItemCount());
		assertEquals("1 b.wrk", menu.getItem(0).getText());
		assertEquals("2 a.xex", menu.getItem(1).getText());

		JMenuItem item = menu.getItem(1);
		item.getActionListeners()[0].actionPerformed(new ActionEvent(item, ActionEvent.ACTION_PERFORMED, ""));
		assertEquals(1, selected.size());
		assertSame(list.getEntries().get(1), selected.get(0));

		// Refilling replaces the items; an empty list disables the menu again.
		list.addFile("c.xex", TestFileType.EXECUTABLE);
		MRUMenu.fill(menu, list, listener);
		assertEquals(3, menu.getItemCount());
		assertEquals("1 c.xex", menu.getItem(0).getText());
		list.clear();
		MRUMenu.fill(menu, list, listener);
		assertEquals(0, menu.getItemCount());
		assertFalse(menu.isEnabled());
	}
}

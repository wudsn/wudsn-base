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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.wudsn.tools.base.common.MRUEntry;
import com.wudsn.tools.base.common.MRUList;
import com.wudsn.tools.base.repository.ValueSet;

/**
 * Fills a menu, e.g. "Recent Files", with the entries of an {@link MRUList}.
 *
 * @author Peter Dell
 */
public final class MRUMenu {

	/** Called with the entry whose menu item was chosen. */
	public interface SelectionListener<T extends ValueSet> {
		void onSelect(MRUEntry<T> entry);
	}

	private MRUMenu() {
	}

	/**
	 * Replaces the items of {@code menu} by one item per entry of {@code
	 * list}, newest first and numbered "1 path", "2 path", ..., each calling
	 * {@code listener} with its entry. The menu is disabled if the list is
	 * empty, enabled otherwise.
	 */
	public static <T extends ValueSet> void fill(JMenu menu, MRUList<T> list, final SelectionListener<T> listener) {
		if (menu == null) {
			throw new IllegalArgumentException("Parameter 'menu' must not be null.");
		}
		if (list == null) {
			throw new IllegalArgumentException("Parameter 'list' must not be null.");
		}
		if (listener == null) {
			throw new IllegalArgumentException("Parameter 'listener' must not be null.");
		}
		menu.removeAll();
		menu.setEnabled(!list.getEntries().isEmpty());

		int index = 1;
		for (final MRUEntry<T> entry : list.getEntries()) {
			JMenuItem item = new JMenuItem(index + " " + entry.getFilePath());
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					listener.onSelect(entry);
				}
			});
			menu.add(item);
			index++;
		}
	}
}

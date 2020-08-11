/**
 * Copyright (C) 2013 - 2014 <a href="http://www.wudsn.com" target="_top">Peter Dell</a>
 *
 * This file is part of a WUDSN software distribution.
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
 * along with the WUDSN software distribution. If not, see <http://www.gnu.org/licenses/>.
 */

package com.wudsn.tools.base.gui;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public final class DynamicMenuItemList {

    private JMenu menu;
    private int startIndex;
    private int itemCount;

    /**
     * Creation is public.
     * 
     * @param menu
     */
    public DynamicMenuItemList(JMenu menu) {
	if (menu == null) {
	    throw new IllegalArgumentException("Parameter 'menu' must not be null.");
	}
	this.menu = menu;
	startIndex = menu.getItemCount();
	itemCount = 0;
    }

    public void setMenuItems(List<JMenuItem> menuItems, boolean enabled) {
	if (menuItems == null) {
	    throw new IllegalArgumentException("Parameter 'menuItems' must not be null.");
	}
	if (itemCount > 0) {
	    for (int i = 0; i < itemCount + 1; i++) {
		menu.remove(startIndex);
	    }
	}
	itemCount = menuItems.size();
	if (itemCount > 0) {
	    menu.insertSeparator(startIndex);
	    for (int i = 0; i < itemCount; i++) {
		JMenuItem menuItem = menuItems.get(i);
		menuItem.setEnabled(enabled);
		menu.insert(menuItem, startIndex + 1 + i);
	    }
	}
    }

}
/**
 * Copyright (C) 2014 <a href="http://www.wudsn.com" target="_top">Peter Dell</a>
 *
 * This file is part of ROM Checker.
 * 
 * ROM Checker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * ROM Checker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ROM Checker.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wudsn.tools.base.gui;

import javax.swing.CellEditor;
import javax.swing.JScrollPane;

import com.wudsn.tools.base.common.Log;

@SuppressWarnings("serial")
public abstract class AttributeTablePanel extends JScrollPane {

    protected AttributeTableModel tableModel;
    protected AttributeTable table;

    public AttributeTablePanel(AttributeTableModel tableModel, AttributeTablePreferences preferences,
	    String preferencesPrefix) {
	if (tableModel == null) {
	    throw new IllegalArgumentException("Parameter 'tableModel' must not be null.");
	}
	if (preferences == null) {
	    throw new IllegalArgumentException("Parameter 'preferences' must not be null.");
	}
	if (preferencesPrefix == null) {
	    throw new IllegalArgumentException("Parameter 'preferencesPrefix' must not be null.");
	}

	table = new AttributeTable(tableModel, preferences, preferencesPrefix);
	tableModel.setTable(table);

	table.setSurrendersFocusOnKeystroke(true);

	setViewportView(table);
    }

    /**
     * Gets the entries table.
     * 
     * @return The entries table, not <code>null</code>.
     */
    public final AttributeTable getTable() {
	return table;
    }

    /**
     * Update columns and value helps which depend of the workbook root.
     */
    public void dataToUI() {
    }

    /**
     * Stop editing and transfer cell contents to model.
     */
    public final void dataFromUI() {
	CellEditor cellEditor = table.getCellEditor();
	if (cellEditor != null) {
	    try {
		cellEditor.stopCellEditing();
	    } catch (RuntimeException ignore) {
		Log.logError("Inconsistent entries table", null, ignore);
	    }
	}
    }

}

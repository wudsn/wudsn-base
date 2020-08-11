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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.wudsn.tools.base.repository.Attribute;

@SuppressWarnings("serial")
public abstract class AttributeTableModel extends AbstractTableModel {

    public static final class Column {

	public static final int HIDDEN = 0x00;
	public static final int FIXED = 0x01;
	public static final int VISIBLE = 0x02;
	public static final int SORTABLE = 0x04;

	private Attribute attribute;
	private int flags;
	private Comparator<?> comparator;
	private DefaultTableCellRenderer defaultTableCellRenderer;
	private DefaultCellEditor defaultCellEditor;

	Column(Attribute attribute, int flags, Comparator<?> comparator,
		DefaultTableCellRenderer defaultTableCellRenderer,
		DefaultCellEditor defaultCellEditor) {
	    this.attribute = attribute;
	    this.flags = flags;
	    this.comparator = comparator;
	    this.defaultTableCellRenderer = defaultTableCellRenderer;
	    this.defaultCellEditor = defaultCellEditor;
	}

	public Attribute getAttribute() {
	    return attribute;
	}

	public boolean isFixed() {
	    return (flags & FIXED) == FIXED;
	}

	public boolean isVisible() {
	    return isFixed() || (flags & VISIBLE) == VISIBLE;
	}

	public boolean isSortable() {
	    return (flags & SORTABLE) == SORTABLE;
	}

	public Comparator<?> getComparator() {
	    return comparator;
	}

	public DefaultTableCellRenderer getDefaultTableCellRenderer() {
	    return defaultTableCellRenderer;
	}

	public DefaultCellEditor getDefaultCellEditor() {
	    return defaultCellEditor;
	}
    }

    private final List<Column> columns;
    protected AttributeTable table;

    protected AttributeTableModel() {
	columns = new ArrayList<Column>();
    }

    protected final void addColumn(Attribute attribute, int flags) {
	addColumn(attribute, flags, null, null, null);
    }

    protected final void addColumn(Attribute attribute, int flags,
	    Comparator<?> comparator,
	    DefaultTableCellRenderer defaultTableCellRenderer,
	    DefaultCellEditor defaultCellEditor) {
	columns.add(new Column(attribute, flags, comparator,
		defaultTableCellRenderer, defaultCellEditor));
    }

    @Override
    public final int getColumnCount() {
	return columns.size();
    }

    // Used by the JTable object to set the column names
    @Override
    public final String getColumnName(int column) {
	return columns.get(column).getAttribute().getDataType()
		.getLabelWithoutMnemonics();
    }

    // Used by the JTable object to render different
    // functionality based on the data type
    @Override
    public final Class<? extends Object> getColumnClass(int column) {
	return columns.get(column).getAttribute().getDataType().getValueClass();
    }

    /**
     * Gets the column definition at an index.
     * 
     * @param index
     *            The index, a non-negative integer..
     * @return The column, not <code>null</code>.
     */
    public final Column getColumn(int index) {
	return columns.get(index);
    }

    /**
     * Gets the column index of an attribute.
     * 
     * @param attribute
     *            The attribute, not <code>null</code>.
     * @return The column index of an attribute or <code>-1</code> if there is
     *         no column for that attribute.
     */
    public final int getColumnIndex(Attribute attribute) {
	if (attribute == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'attribute' must not be null.");
	}
	int index = -1;
	for (int i = 0; i < columns.size() && index == -1; i++) {
	    if (columns.get(i).getAttribute() == attribute) {
		index = i;
	    }
	}
	return index;
    }

    /**
     * Sets the attribute table used to display the table. This is used to
     * compute the line numbering.
     * 
     * @param table
     *            The table, not <code>null</code>.
     */
    public final void setTable(AttributeTable table) {
	if (table == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'table' must not be null.");
	}
	this.table = table;
    }
}
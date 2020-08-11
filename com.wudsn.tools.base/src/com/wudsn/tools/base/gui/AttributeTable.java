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

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.wudsn.tools.base.common.ASCIIString;
import com.wudsn.tools.base.common.MemorySize;
import com.wudsn.tools.base.gui.AttributeTableModel.Column;
import com.wudsn.tools.base.repository.Attribute;
import com.wudsn.tools.base.repository.DataType;
import com.wudsn.tools.base.repository.ValueSet;

@SuppressWarnings("serial")
public final class AttributeTable extends JTable {

    private AttributeTableModel model;
    private AttributeTablePreferences attributeTablePreferences;
    private String prefix;
    private List<TableColumn> allTableColumns;

    /**
     * Creates a new attribute table.
     * 
     * @param model
     *            The table model, not <code>null</code>.
     * @param attributeTablePreferences
     *            The attributeTablePreferences to initialize the column state
     *            from and to save the column state to, or <code>null</code>.
     * @param prefix
     *            The attributeTablePreferences prefix, not <code>null</code>.
     */
    public AttributeTable(AttributeTableModel model,
	    AttributeTablePreferences attributeTablePreferences, String prefix) {
	super();
	if (model == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'model' must not be null.");
	}
	if (attributeTablePreferences == null && prefix != null) {
	    throw new IllegalArgumentException(
		    "Parameter 'attributeTablePreferences' must not be null if prefix is specified.");
	}
	if (attributeTablePreferences != null && prefix == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'prefix' must not be null if preferecces are specified.");
	}
	this.model = model;
	this.attributeTablePreferences = attributeTablePreferences;
	this.prefix = prefix;
	allTableColumns = new ArrayList<TableColumn>();

	setDefaultRenderer(MemorySize.class,
		ElementFactory.createMemorySizeTableCellRenderer());
	setDefaultRenderer(Number.class,
		ElementFactory.createNumberTableCellRenderer());

	TableRowSorter<AttributeTableModel> rowSorter = new TableRowSorter<AttributeTableModel>(
		model);
	for (int i = 0; i < model.getColumnCount(); i++) {

	    Class<?> columnClass = model.getColumnClass(i);
	    if (columnClass.isAssignableFrom(ValueSet.class)) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ValueSetField<?> field = new ValueSetField(columnClass);
		setDefaultEditor(columnClass, new DefaultCellEditor(field));
	    }

	    Column column = model.getColumn(i);
	    Comparator<?> comparator = column.getComparator();
	    if (comparator != null) {
		rowSorter.setComparator(i, comparator);
	    }
	}

	DefaultCellEditor editor;
	editor = (DefaultCellEditor) getDefaultEditor(String.class);
	setDefaultEditor(ASCIIString.class, editor);
	editor.setClickCountToStart(1);
	editor = (DefaultCellEditor) getDefaultEditor(Number.class);
	editor.setClickCountToStart(1);

	setModel(model);
	setRowSorter(rowSorter);

	AttributeTableHeader.install(this);
	AttributeTableColumnChooser.install(this);

	loadLayout();

    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
	TableColumn tableColumn = columnModel.getColumn(column);
	TableCellRenderer renderer = model.getColumn(
		tableColumn.getModelIndex()).getDefaultTableCellRenderer();
	if (renderer == null) {
	    renderer = getDefaultRenderer(getColumnClass(column));
	}
	return renderer;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
	TableColumn tableColumn = getColumnModel().getColumn(column);
	TableCellEditor editor = model.getColumn(tableColumn.getModelIndex())
		.getDefaultCellEditor();
	if (editor == null) {
	    editor = getDefaultEditor(getColumnClass(column));
	}
	return editor;
    }

    final void setColumnDefaults() {

	for (int modelColumnIndex = 0; modelColumnIndex < model
		.getColumnCount(); modelColumnIndex++) {
	    setColumnVisible(modelColumnIndex, false);
	}
	for (int modelColumnIndex = 0; modelColumnIndex < model
		.getColumnCount(); modelColumnIndex++) {
	    Column column = model.getColumn(modelColumnIndex);
	    setColumnVisible(modelColumnIndex, column.isVisible());
	}
    }

    public void setColumnVisible(int modelColumnIndex, boolean visible) {
	TableColumnModel columnModel = getColumnModel();
	int viewColumnIndex = convertColumnIndexToView(modelColumnIndex);
	TableColumn column = null;
	if (viewColumnIndex != -1) {
	    column = columnModel.getColumn(viewColumnIndex);
	}
	if (!visible) {
	    if (column != null) {
		columnModel.removeColumn(column);
	    }
	} else {
	    if (column == null) {
		// As the column is added at the end of the view, we have to
		// move it to the correct index
		column = allTableColumns.get(modelColumnIndex);
		addColumn(column);

		// Find the correct view index for the added column
		int closestBefore = Integer.MIN_VALUE;
		int closestAfter = Integer.MAX_VALUE;
		for (int i = 0; i < getColumnCount() - 1; i++) {
		    TableColumn tc = columnModel.getColumn(i);
		    if (modelColumnIndex > tc.getModelIndex()) {
			closestBefore = Math.max(closestBefore,
				tc.getModelIndex());
		    }
		    if (modelColumnIndex < tc.getModelIndex()) {
			closestAfter = Math.min(closestAfter,
				tc.getModelIndex());
		    }
		}

		int targetViewIndex = -1;
		if (closestBefore != Integer.MIN_VALUE) {
		    targetViewIndex = convertColumnIndexToView(closestBefore) + 1;
		} else {
		    targetViewIndex = convertColumnIndexToView(closestAfter);
		}

		if (targetViewIndex != -1) {
		    moveColumn(getColumnCount() - 1, targetViewIndex);
		}
	    }
	}

    }

    private void loadLayout() {

	// Remove current columns.
	while (columnModel.getColumnCount() > 0) {
	    columnModel.removeColumn(columnModel.getColumn(0));
	}
	allTableColumns.clear();

	// Load layout properties.
	Map<String, String> properties;
	if (attributeTablePreferences != null) {
	    properties = attributeTablePreferences.getLayoutProperties();
	} else {
	    properties = null;
	}
	List<TableColumn> tableColumns = new ArrayList<TableColumn>();
	for (int modelColumnIndex = 0; modelColumnIndex < model
		.getColumnCount(); modelColumnIndex++) {
	    TableColumn tableColumn = new TableColumn(modelColumnIndex);
	    Column column = model.getColumn(modelColumnIndex);
	    boolean visible = column.isVisible();
	    int viewColumnIndex = Integer.MAX_VALUE;
	    if (properties != null) {
		String columnKey = prefix + ".columns."
			+ column.getAttribute().getName();
		String value = properties.get(columnKey + ".visible");

		int width = -1;
		if (value != null) {
		    visible = Boolean.parseBoolean(value) || column.isFixed();

		    try {
			viewColumnIndex = Integer.parseInt(properties
				.get(columnKey + ".index"));
		    } catch (NumberFormatException ignore) {
		    }
		    try {
			width = Integer.parseInt(properties.get(columnKey
				+ ".width"));
			if (width > 0) {
			    tableColumn.setPreferredWidth(width);
			}
		    } catch (NumberFormatException ignore) {
		    }

		}
	    }
	    if (visible) {
		tableColumn.setIdentifier(Integer.valueOf(viewColumnIndex));

		tableColumns.add(tableColumn);
	    }
	    allTableColumns.add(tableColumn);

	    // Create dedicated header renderer for the tooltip.
	    TableCellRenderer parent = tableColumn.getHeaderRenderer();
	    if (parent == null) {
		parent = getTableHeader().getDefaultRenderer();
	    }
	    final TableCellRenderer finalParent = parent;
	    final TableColumnModel columnModel = this.columnModel;
	    final AttributeTableModel model = this.model;
	    TableCellRenderer headerRenderer = new TableCellRenderer() {

		@Override
		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		    Component result = finalParent
			    .getTableCellRendererComponent(table, value,
				    isSelected, hasFocus, row, column);
		    if (result instanceof JComponent) {
			TableColumn tableColumn = columnModel.getColumn(column);
			DataType dataType = model
				.getColumn(tableColumn.getModelIndex())
				.getAttribute().getDataType();
			String toolTip = dataType.getToolTip();
			((JComponent) result).setToolTipText(toolTip);
		    }
		    return result;
		}
	    };
	    tableColumn.setHeaderRenderer(headerRenderer);

	}

	// Order column sequence.
	Collections.sort(tableColumns, new Comparator<TableColumn>() {
	    @Override
	    public int compare(TableColumn o1, TableColumn o2) {
		Integer i1 = (Integer) o1.getIdentifier();
		Integer i2 = (Integer) o2.getIdentifier();
		return i1.compareTo(i2);
	    }
	});

	// Set attribute name as identifier for all columns.
	for (TableColumn tableColumn : allTableColumns) {
	    tableColumn.setIdentifier(model
		    .getColumn(tableColumn.getModelIndex()).getAttribute()
		    .getName());
	}

	// Create visible columns in order.
	for (TableColumn tableColumn : tableColumns) {
	    addColumn(tableColumn);
	}
    }

    /**
     * Saves the current layout of the layout properties in the
     * attributeTablePreferences.
     */
    public void saveLayout() {
	if (attributeTablePreferences == null) {
	    throw new RuntimeException(
		    "This table has not attributeTablePreferences assigned.");
	}
	Map<String, String> properties = attributeTablePreferences
		.getLayoutProperties();
	List<String> keys = new ArrayList<String>(properties.keySet());
	for (String key : keys) {
	    if (key.startsWith(prefix)) {
		properties.remove(key);
	    }
	}
	for (int modelIndex = 0; modelIndex < model.getColumnCount(); modelIndex++) {
	    Column column = model.getColumn(modelIndex);
	    TableColumn tableColumn = allTableColumns.get(modelIndex);
	    int viewIndex = convertColumnIndexToView(modelIndex);
	    boolean visible = (viewIndex != -1);
	    String columnKey = prefix + ".columns."
		    + column.getAttribute().getName();
	    properties.put(columnKey + ".visible", Boolean.toString(visible));
	    properties.put(columnKey + ".index", Integer.toString(viewIndex));
	    properties.put(columnKey + ".width",
		    Integer.toString(tableColumn.getWidth()));

	}

    }

    private void scrollToVisible(int rowIndex, int columnIndex) {
	Rectangle rect = getCellRect(rowIndex, columnIndex, true);
	scrollRectToVisible(rect);
    }

    /**
     * Selects a table row (or cell) based on the model row and attribute.
     * 
     * @param modelRowIndex
     *            The model index of the row to be selected.
     * @param attribute
     *            The attribute representing the column or <code>null</code>.
     */
    public void selectCell(int modelRowIndex, Attribute attribute) {
	if (modelRowIndex != -1) {

	    int row = convertRowIndexToView(modelRowIndex);
	    getSelectionModel().setSelectionInterval(row, row);
	    if (attribute != null) {
		AttributeTableModel tableModel = (AttributeTableModel) getModel();
		int column = tableModel.getColumnIndex(attribute);
		if (column < 0) {
		    column = 0;
		}
		scrollToVisible(row, column);
		editCellAt(row, column);
	    }
	}
    }

    public boolean print(String headerText, String subHeaderText,
	    String footerText) throws PrinterException {
	if (headerText == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'headerText' must not be null.");
	}
	if (subHeaderText == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'subHeaderText' must not be null.");
	}
	if (footerText == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'footerText' must not be null.");
	}
	AttributeTablePrinter printer = new AttributeTablePrinter(this);
	return printer.print(headerText, subHeaderText, footerText);
    }
}
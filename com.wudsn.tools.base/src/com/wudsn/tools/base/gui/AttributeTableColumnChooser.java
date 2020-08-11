/*
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.wudsn.tools.base.Actions;
import com.wudsn.tools.base.gui.AttributeTableModel.Column;
import com.wudsn.tools.base.repository.Action;
import com.wudsn.tools.base.repository.DataType;

/**
 * This class is based on the <a href=
 * "http://www.jroller.com/santhosh/entry/let_user_choose_their_favourite"
 * >excellent blog</a> by Santhosh Kumar.
 * 
 * @author Santhosh Kumar
 * @author Peter Dell
 * 
 */
final class AttributeTableColumnChooser extends MouseAdapter implements ActionListener {

    // Client property used to specify fixed columns which cannot be toggled.
    // The value is of type int[]
    // which is sorted.
    public static final String FIXED_COLUMNS = "FixedColumns"; // NOI18N

    // Client property used to pass column index to action listener
    private static final String COLUMN_INDEX = "ColumnIndex";

    // Client property used to pass the AttributeTable to the action listener.
    private static final String JTABLE = "AttributeTable";

    // Action command for menu.
    private static final String SET_COLUMN_DEFAULTS = "setColumnDefaults";

    /*-------------------------------------------------[ Singleton ]---------------------------------------------------*/

    private static WeakReference<AttributeTableColumnChooser> ref = null; // favour
									  // gc

    private AttributeTableColumnChooser() {
    }

    private static AttributeTableColumnChooser getInstance() {
	if (ref == null || ref.get() == null) {
	    ref = new WeakReference<AttributeTableColumnChooser>(new AttributeTableColumnChooser());
	}
	return ref.get();
    }

    /*-------------------------------------------------[ Usage ]---------------------------------------------------*/

    public static void install(AttributeTable table) {
	table.getTableHeader().addMouseListener(getInstance());
    }

    public static void uninstall(AttributeTable table) {
	table.getTableHeader().removeMouseListener(getInstance());
    }

    public static void hideColumns(TableColumnModel columnModel, int modelColumnIndexes[]) {
	if (columnModel == null) {
	    throw new IllegalArgumentException("Parameter 'columnModel' must not be null.");
	}
	if (modelColumnIndexes == null) {
	    throw new IllegalArgumentException("Parameter 'modelColumnIndexes' must not be null.");
	}
	TableColumn column[] = new TableColumn[modelColumnIndexes.length];
	for (int i = 0, j = 0; i < columnModel.getColumnCount(); i++) {
	    TableColumn col = columnModel.getColumn(i);
	    if (col.getModelIndex() == modelColumnIndexes[j]) {
		column[j++] = col;
		if (j >= modelColumnIndexes.length) {
		    break;
		}
	    }
	}
	for (int i = 0; i < column.length; i++) {
	    columnModel.removeColumn(column[i]);
	}
    }

    private static boolean isVisibleColumn(TableColumnModel model, int modelCol) {
	for (int i = 0; i < model.getColumnCount(); i++) {
	    if (model.getColumn(i).getModelIndex() == modelCol) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public void mouseReleased(MouseEvent me) {
	maybePopup(me);
    }

    @Override
    public void mousePressed(MouseEvent e) {
	maybePopup(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	maybePopup(e);
    }

    private void maybePopup(MouseEvent me) {
	if (!me.isPopupTrigger()) {
	    return;
	}

	JTableHeader header = (JTableHeader) me.getComponent();
	AttributeTable table = (AttributeTable) header.getTable();
	AttributeTableModel tableModel = (AttributeTableModel) table.getModel();
	TableColumnModel columnModel = table.getColumnModel();

	JPopupMenu popup = new JPopupMenu();
	for (int i = 0; i < tableModel.getColumnCount(); i++) {
	    Column column = tableModel.getColumn(i);
	    DataType dataType = column.getAttribute().getDataType();
	    JCheckBoxMenuItem item = new JCheckBoxMenuItem();
	    Action action = new Action(dataType.getLabel(), dataType.getToolTip(), null);
	    ElementFactory.setButtonTextAndMnemonic(item, action);
	    item.setSelected(isVisibleColumn(columnModel, i));
	    item.setEnabled(!column.isFixed());
	    item.putClientProperty(COLUMN_INDEX, Integer.valueOf((i)));
	    item.putClientProperty(JTABLE, header.getTable());
	    item.addActionListener(this);
	    popup.add(item);
	}
	popup.addSeparator();
	JMenuItem item = ElementFactory.createMenuItem(Actions.AttributeTable_HeaderPopupMenuSetColumnDefaults,
		SET_COLUMN_DEFAULTS);
	item.putClientProperty(JTABLE, header.getTable());

	item.addActionListener(this);
	popup.add(item);

	popup.show(header, me.getX(), me.getY());
    }

    /**
     * Menu item clicked.
     * 
     * @param actionEvent
     *            The action event, not <code>null</code>.
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
	JMenuItem item = (JMenuItem) actionEvent.getSource();
	AttributeTable table = (AttributeTable) item.getClientProperty(JTABLE);

	if (actionEvent.getActionCommand().equals(SET_COLUMN_DEFAULTS)) {
	    table.setColumnDefaults();
	} else {
	    Integer columnIndex = (Integer) item.getClientProperty(COLUMN_INDEX);
	    table.setColumnVisible(columnIndex.intValue(), item.isSelected());
	}

    }
}

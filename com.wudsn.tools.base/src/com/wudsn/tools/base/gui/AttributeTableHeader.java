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

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Utility class to add multi-column sort support to a JTable.
 * 
 * @author Peter Dell
 */
final class AttributeTableHeader {

	private final static class AttributeSortKey extends SortKey {
		/**
		 * Creates a <code>SortKey</code> for the specified column with the
		 * specified sort order.
		 * 
		 * @param column
		 *            index of the column, in terms of the model
		 * @param sortOrder
		 *            the sorter order
		 * @throws IllegalArgumentException
		 *             if <code>sortOrder</code> is <code>null</code>
		 */
		public AttributeSortKey(int column, SortOrder sortOrder) {
			super(column, sortOrder);
		}

		@Override
		public String toString() {
			return "column=" + getColumn() + ", sortOrder="
					+ getSortOrder().toString();
		}
	}

	public static void install(JTable table) {
		if (table == null) {
			throw new IllegalArgumentException(
					"Parameter 'table' must not be null.");
		}
		// React on view column changes.
		table.getColumnModel().addColumnModelListener(
				new CustomColumnModelListener(table));

		// Replace the header's mouse listener.
		JTableHeader header = table.getTableHeader();
		for (MouseListener ml : header.getMouseListeners()) {
			if (ml instanceof BasicTableHeaderUI.MouseInputHandler) {
				MouseListener altered = new CustomTableHeaderListener(table,
						(BasicTableHeaderUI.MouseInputHandler) ml);
				header.removeMouseListener(ml);
				header.addMouseListener(altered);
			}
		}

		// Set a new header renderer that can display multiple sort columns.
		// header.setDefaultRenderer(new CustomTableHeaderRenderer(header));
		header.setDefaultRenderer(new CustomerTableHeaderRenderer());
	}

	private final static class CustomColumnModelListener implements
			TableColumnModelListener {
		private final JTable table;

		public CustomColumnModelListener(JTable table) {
			this.table = table;
		}

		@Override
		public void columnMoved(TableColumnModelEvent e) {
			int from = e.getFromIndex();
			int to = e.getToIndex();
			if (from != to) {
				int fromModel = table.convertColumnIndexToModel(from);
				int toModel = table.convertColumnIndexToModel(to);

				// Swap sort key of the swapped columns.
				RowSorter<?> sorter = table.getRowSorter();
				List<? extends SortKey> oldKeys = sorter.getSortKeys();
				SortKey fromKey = getSortKeyWithColumn(fromModel, oldKeys);
				SortKey toKey = getSortKeyWithColumn(toModel, oldKeys);

				if (fromKey != null && toKey != null) {
					List<SortKey> newKeys = new ArrayList<SortKey>();
					for (SortKey k : oldKeys) {
						if (fromModel == k.getColumn()) {
							// from -> to
							newKeys.add(getSortKeyWithColumn(toModel, oldKeys));
						} else if (toModel == k.getColumn()) {
							// to -> from
							newKeys.add(getSortKeyWithColumn(fromModel, oldKeys));
						} else {
							// copy
							newKeys.add(k);
						}
					}
					// update
					sorter.setSortKeys(newKeys);
				}
			}
		}

		private static SortKey getSortKeyWithColumn(int col,
				List<? extends SortKey> oldKeys) {
			if (oldKeys == null) {
				throw new IllegalArgumentException(
						"Parameter 'oldKeys' must not be null.");
			}
			for (SortKey k : oldKeys) {
				if (k.getColumn() == col) {
					return k;
				}
			}
			return null;
		}

		@Override
		public void columnAdded(TableColumnModelEvent e) {
		}

		@Override
		public void columnMarginChanged(ChangeEvent e) {
		}

		@Override
		public void columnRemoved(TableColumnModelEvent e) {
		}

		@Override
		public void columnSelectionChanged(ListSelectionEvent e) {
		}

	}

	private final static class CustomTableHeaderListener implements
			MouseListener, MouseMotionListener {

		// Remember owning table.
		private JTable table;
		// Remember delegate implementations.
		private MouseListener original;
		private MouseMotionListener originalMotion;

		public CustomTableHeaderListener(JTable table,
				BasicTableHeaderUI.MouseInputHandler original) {
			if (table == null) {
				throw new IllegalArgumentException(
						"Parameter 'table' must not be null.");
			}
			if (original == null) {
				throw new IllegalArgumentException(
						"Parameter 'original' must not be null.");
			}
			this.table = table;
			this.original = original;
			this.originalMotion = original;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// Determine view and model column number.
			int viewColumn = table.columnAtPoint(e.getPoint());
			int modelColumn = table.convertColumnIndexToModel(viewColumn);
			RowSorter<?> sorter = table.getRowSorter();

			// Create new list of sort keys.
			List<? extends SortKey> oldSortKeys = sorter.getSortKeys();
			boolean extend = e.isShiftDown() || e.isControlDown();
			List<SortKey> newSortKeys = new ArrayList<SortKey>();
			AttributeTableModel tableModel = (AttributeTableModel) table
					.getModel();
			// Toggle sort order for this column?
			if (tableModel.getColumn(modelColumn).isSortable()) {
				boolean toggled = false;
				for (SortKey sortKey : oldSortKeys) {
					if (sortKey.getColumn() == modelColumn) {

						SortOrder oldSortOrder = sortKey.getSortOrder();
						SortOrder newSortOrder = (oldSortOrder == SortOrder.ASCENDING ? SortOrder.DESCENDING
								: SortOrder.ASCENDING);
						newSortKeys.add(new AttributeSortKey(modelColumn,
								newSortOrder));
						toggled = true;
					} else {
						// Copy as it is?
						if (extend) {
							newSortKeys.add(sortKey);
						}
					}
				}
				if (!toggled) {
					newSortKeys.add(new AttributeSortKey(modelColumn,
							SortOrder.ASCENDING));
				}

				// Update sorter.
				sorter.setSortKeys(newSortKeys);
			}

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			original.mouseEntered(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			original.mouseExited(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			original.mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			original.mouseReleased(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			originalMotion.mouseDragged(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			originalMotion.mouseMoved(e);
		}

	}

	@SuppressWarnings("serial")
	private final static class CustomerTableHeaderRenderer extends
			DefaultTableCellRenderer implements javax.swing.plaf.UIResource {

		private final static class EmptyIcon implements Icon, Serializable {
			int width = 0;
			int height = 0;

			public EmptyIcon() {
			}

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
			}

			@Override
			public int getIconWidth() {
				return width;
			}

			@Override
			public int getIconHeight() {
				return height;
			}
		}

		private Icon ascendingSortIcon;
		private Icon descendingSortIcon;
		private Icon unsortedSortIcon;
		private Icon sortIcon;
		private EmptyIcon emptyIcon;

		public CustomerTableHeaderRenderer() {
			setHorizontalAlignment(SwingConstants.CENTER);
			emptyIcon = new EmptyIcon();
			ascendingSortIcon = UIManager.getIcon("Table.ascendingSortIcon");
			descendingSortIcon = UIManager.getIcon("Table.descendingSortIcon");
			unsortedSortIcon = UIManager.getIcon("Table.naturalSortIcon");
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Icon sortIcon = null;

			boolean isPaintingForPrint = false;

			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					Color fgColor = null;
					Color bgColor = null;
					if (hasFocus) {
						fgColor = UIManager
								.getColor("TableHeader.focusCellForeground");
						bgColor = UIManager
								.getColor("TableHeader.focusCellBackground");
					}
					if (fgColor == null) {
						fgColor = header.getForeground();
					}
					if (bgColor == null) {
						bgColor = header.getBackground();
					}
					setForeground(fgColor);
					setBackground(bgColor);

					setFont(header.getFont());

					isPaintingForPrint = header.isPaintingForPrint();
				}

				if (!isPaintingForPrint && table.getRowSorter() != null) {

					SortOrder sortOrder = getColumnSortOrder(table, column);
					if (sortOrder != null) {
						switch (sortOrder) {
						case ASCENDING:
							sortIcon = ascendingSortIcon;
							break;
						case DESCENDING:
							sortIcon = descendingSortIcon;
							break;
						case UNSORTED:
							sortIcon = unsortedSortIcon;
							break;
						}
					}
				}
			}

			setText(value == null ? "" : value.toString());
			setIcon(sortIcon);

			Border border = null;
			if (hasFocus) {
				border = UIManager.getBorder("TableHeader.focusCellBorder");
			}
			if (border == null) {
				border = UIManager.getBorder("TableHeader.cellBorder");
			}
			setBorder(border);

			return this;
		}

		private static SortOrder getColumnSortOrder(JTable table, int viewColumn) {
			int modelColumn = table.convertColumnIndexToModel(viewColumn);
			List<? extends SortKey> sortKeys = table.getRowSorter()
					.getSortKeys();
			SortOrder sortOrder = SortOrder.UNSORTED;
			for (SortKey sortKey : sortKeys) {
				if (sortKey.getColumn() == modelColumn) {
					sortOrder = sortKey.getSortOrder();
				}
			}
			return sortOrder;
		}

		@Override
		public void paintComponent(Graphics g) {
			boolean b = UIManager.getBoolean("TableHeader.rightAlignSortArrow");
			if (b && sortIcon != null) {
				emptyIcon.width = sortIcon.getIconWidth();
				emptyIcon.height = sortIcon.getIconHeight();
				setIcon(emptyIcon);
				super.paintComponent(g);
				Point position = computeIconPosition(g);
				sortIcon.paintIcon(this, g, position.x, position.y);
			} else {
				super.paintComponent(g);
			}
		}

		private Point computeIconPosition(Graphics g) {
			FontMetrics fontMetrics = g.getFontMetrics();
			Rectangle viewR = new Rectangle();
			Rectangle textR = new Rectangle();
			Rectangle iconR = new Rectangle();
			Insets i = getInsets();
			viewR.x = i.left;
			viewR.y = i.top;
			viewR.width = getWidth() - (i.left + i.right);
			viewR.height = getHeight() - (i.top + i.bottom);
			SwingUtilities.layoutCompoundLabel(this, fontMetrics, getText(),
					sortIcon, getVerticalAlignment(), getHorizontalAlignment(),
					getVerticalTextPosition(), getHorizontalTextPosition(),
					viewR, iconR, textR, getIconTextGap());
			int x = getWidth() - i.right - sortIcon.getIconWidth();
			int y = iconR.y;
			return new Point(x, y);
		}
	}
}

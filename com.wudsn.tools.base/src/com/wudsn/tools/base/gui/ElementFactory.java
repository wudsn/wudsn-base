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

import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.wudsn.tools.base.common.ResourceUtility;
import com.wudsn.tools.base.common.StringUtility;
import com.wudsn.tools.base.common.TextUtility;
import com.wudsn.tools.base.repository.Action;
import com.wudsn.tools.base.repository.DataType;

/**
 * Factory for loading icons from the class path.
 * 
 * @author Peter Dell
 * 
 */
public final class ElementFactory {

    /**
     * Creation is private.
     */
    private ElementFactory() {

    }

    /**
     * Creates a new image Icon.
     * 
     * @param path
     *            The path to the image icon, using '/' as path separator, not
     *            empty and not <code>null</code>.
     * @return The icon image, not <code>null</code>.
     */
    public static ImageIcon createImageIcon(String path) {
	if (path == null) {
	    throw new IllegalArgumentException("Parameter 'path' must not be null.");
	}
	byte[] imageData = ResourceUtility.loadResourceAsByteArray(path);
	if (imageData == null) {
	    throw new IllegalArgumentException("No icon found for path '" + path + "'.");
	}
	ImageIcon imageIcon = new ImageIcon(imageData);
	return imageIcon;

    }

    /**
     * Creates a new button
     * 
     * @param action
     *            The action, not <code>null</code>.
     * @param withMnemonic
     *            <code>true</code> if the mnemonic shall use added,
     *            <code>false</code> otherwise.
     * @return The new menu item, not <code>null</code>.
     */
    public static JButton createButton(Action action, boolean withMnemonic) {
	if (action == null) {
	    throw new IllegalArgumentException("Parameter 'action' must not be null.");
	}
	JButton result = new JButton();
	setButtonTextAndMnemonic(result, action.getLabel(), withMnemonic, action.getToolTip());
	return result;
    }

    /**
     * Creates a new menu item.
     * 
     * @param action
     *            The action, not <code>null</code>.
     * @return The new menu item, not <code>null</code>.
     */
    public static JMenu createMenu(Action action) {
	if (action == null) {
	    throw new IllegalArgumentException("Parameter 'action' must not be null.");
	}
	JMenu result = new JMenu();
	setButtonTextAndMnemonic(result, action);
	return result;
    }

    /**
     * Creates a new menu item.
     * 
     * @param action
     *            The action, not <code>null</code>.
     * @param actionCommand
     *            The action command, not <code>null</code>.
     * @return The new menu item, not <code>null</code>.
     */
    public static JMenuItem createMenuItem(Action action, String actionCommand) {
	if (action == null) {
	    throw new IllegalArgumentException("Parameter 'action' must not be null.");
	}
	if (actionCommand == null) {
	    throw new IllegalArgumentException("Parameter 'actionCommand' must not be null.");
	}
	JMenuItem result = new JMenuItem();
	result.setIconTextGap(0);
	// Add some spaces to ensure the accelerator key text is separated
	String label = action.getLabel();
	if (action.getAccelerator() != null) {
	    label += "   ";
	}
	setButtonTextAndMnemonic(result, label, true, action.getToolTip());
	result.setActionCommand(actionCommand);
	result.setAccelerator(action.getAccelerator());
	return result;
    }

    /**
     * Creates a new tab in a tabbed pane.
     * 
     * @param tabbedPane
     *            The tabbed pane, not <code>null</code>.
     * @param action
     *            The action, not <code>null</code>.
     * @param component
     *            The component for the tab content, not <code>null</code>.
     */
    public static void createTab(JTabbedPane tabbedPane, Action action, JComponent component) {
	if (tabbedPane == null) {
	    throw new IllegalArgumentException("Parameter 'tabbedPane' must not be null.");
	}
	if (action == null) {
	    throw new IllegalArgumentException("Parameter 'action' must not be null.");
	}
	if (component == null) {
	    throw new IllegalArgumentException("Parameter 'component' must not be null.");
	}
	int index = tabbedPane.getTabCount();

	// Having no tool tip is representing by null.
	String toolTip = action.getToolTip();
	if (StringUtility.isEmpty(toolTip)) {
	    toolTip = null;
	}
	tabbedPane.insertTab(action.getLabel(), null, component, toolTip, index);

    }

    private static void setButtonTextAndMnemonic(AbstractButton abstractButton, String label, boolean withMnemonic,
	    String toolTip) {
	if (abstractButton == null) {
	    throw new IllegalArgumentException("Parameter 'abstractButton' must not be null.");
	}
	if (label == null) {
	    throw new IllegalArgumentException("Parameter 'label' must not be null.");
	}
	if (toolTip == null) {
	    throw new IllegalArgumentException("Parameter 'toolTip' must not be null.");
	}

	int index = label.indexOf('&');
	if (withMnemonic) {
	    if (index == -1) {
		throw new RuntimeException("No '&' contained in label text '" + label + "'.");
	    }
	    char c = label.charAt(index + 1);
	    c = Character.toUpperCase(c);
	    if (!((c >= KeyEvent.VK_A && c <= KeyEvent.VK_Z) || (c >= KeyEvent.VK_0 && c <= KeyEvent.VK_9))) {
		throw new RuntimeException("Mmnemonic character '" + c + "' contained in menu text '" + label
			+ "' if not between 'A' and 'Z' or '0' and '9'.");
	    }
	    label = label.substring(0, index) + label.substring(index + 1, label.length());
	    abstractButton.setMnemonic(c);
	} else {
	    if (index >= 0) {
		label = label.substring(0, index) + label.substring(index + 1, label.length());
	    }
	}
	abstractButton.setText(label);

	// Having no tool tip is represented by null.
	if (StringUtility.isEmpty(toolTip)) {
	    toolTip = null;
	}
	abstractButton.setToolTipText(toolTip);
    }

    public static void setButtonText(AbstractButton abstractButton, Action action) {
	if (abstractButton == null) {
	    throw new IllegalArgumentException("Parameter 'abstractButton' must not be null.");
	}
	if (action == null) {
	    throw new IllegalArgumentException("Parameter 'action' must not be null.");
	}
	setButtonTextAndMnemonic(abstractButton, action.getLabel(), false, action.getToolTip());
    }

    public static void setButtonTextAndMnemonic(AbstractButton abstractButton, Action action) {
	if (abstractButton == null) {
	    throw new IllegalArgumentException("Parameter 'abstractButton' must not be null.");
	}
	if (action == null) {
	    throw new IllegalArgumentException("Parameter 'action' must not be null.");
	}
	setButtonTextAndMnemonic(abstractButton, action.getLabel(), true, action.getToolTip());
    }

    public static JLabel createLabel(DataType dataType, JComponent field) {
	if (dataType == null) {
	    throw new IllegalArgumentException("Parameter 'dataType' must not be null.");
	}
	if (field == null) {
	    throw new IllegalArgumentException("Parameter 'field' must not be null.");
	}
	String text = dataType.getLabel();
	int index = text.indexOf('&');
	if (index == -1) {
	    throw new RuntimeException("No '&' contained in menu text '" + text + "'.");
	}
	char c = text.charAt(index + 1);
	c = Character.toUpperCase(c);
	if (c < KeyEvent.VK_A || c > KeyEvent.VK_Z) {
	    throw new RuntimeException("Mmnemonic character '" + c + "' contained in menu text '" + text
		    + "' if not between 'A' and 'Z'.");
	}

	text = dataType.getLabelWithoutMnemonics();
	JLabel result = new JLabel(text);
	result.setDisplayedMnemonic(c);
	result.setDisplayedMnemonicIndex(index);
	result.setLabelFor(field);

	String toolTip = dataType.getToolTip();
	if (StringUtility.isSpecified(toolTip)) {
	    result.setToolTipText(dataType.getToolTip());
	}
	return result;
    }

    public static Box createButtonBar() {
	Box buttonBar = Box.createHorizontalBox();
	buttonBar.add(Box.createHorizontalGlue());
	buttonBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

	return buttonBar;
    }

    /**
     * Sets the default "OK" button and "Cancel" action for a dialog.
     * 
     * @param rootPane
     *            The root pane of the dialog, not <code>null</code>.
     * @param okButton
     *            The OK button, not <code>null</code>.
     * @param cancelAction
     *            The cancel action, not <code>null</code>.
     */
    public static void setDialogDefaultButtons(JRootPane rootPane, JButton okButton, javax.swing.Action cancelAction) {
	if (rootPane == null) {
	    throw new IllegalArgumentException("Parameter 'rootPane' must not be null.");
	}
	if (okButton == null) {
	    throw new IllegalArgumentException("Parameter 'okButton' must not be null.");
	}
	if (cancelAction == null) {
	    throw new IllegalArgumentException("Parameter 'cancelAction' must not be null.");
	}
	rootPane.setDefaultButton(okButton);
	rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
		KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
	rootPane.getActionMap().put("CANCEL", cancelAction);

    }

    @SuppressWarnings("serial")
    public static TableCellRenderer createNumberTableCellRenderer() {
	DefaultTableCellRenderer renderer;
	renderer = new DefaultTableCellRenderer() {
	    @Override
	    protected void setValue(Object value) {
		if (value instanceof Number) {
		    long longValue = ((Number) value).longValue();
		    setText(TextUtility.formatAsDecimal(longValue));
		} else {
		    setText("");
		}
	    }
	};
	renderer.setHorizontalAlignment(SwingConstants.RIGHT);
	return renderer;
    }

    @SuppressWarnings("serial")
    public static TableCellRenderer createMemorySizeTableCellRenderer() {
	DefaultTableCellRenderer renderer;
	renderer = new DefaultTableCellRenderer() {
	    @Override
	    protected void setValue(Object value) {
		if (value instanceof Number) {
		    long memorySize = ((Number) value).longValue();
		    setText(TextUtility.formatAsMemorySize(memorySize));
		} else {
		    setText("");
		}
	    }
	};
	renderer.setHorizontalAlignment(SwingConstants.RIGHT);
	return renderer;
    }

}
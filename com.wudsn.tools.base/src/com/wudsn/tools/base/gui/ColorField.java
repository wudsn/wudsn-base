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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public final class ColorField extends JButton {

    private JLabel label;
    private JColorChooser colorChooser;
    private JDialog dialog;

    /**
     * Creation is public.
     */
    public ColorField() {
	setContentAreaFilled(false);
	setOpaque(true);
	
	colorChooser = new JColorChooser();
	addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		showColorChooserDialog();
	    }
	});
    }

    /**
     * Sets the label for this field.
     * 
     * @param label
     *            The label or <code>null</code>
     */
    public void setLabel(JLabel label) {
	this.label = label;
    }

    @Override
    public void setVisible(boolean visible) {
	if (label != null) {
	    label.setVisible(visible);
	}
	super.setVisible(visible);
    }

    /**
     * Sets the numeric value with locale dependent formatting.
     * 
     * @param value
     *            The color, not <code>null</code>.
     */
    public void setValue(Color value) {
	if (value == null) {
	    throw new IllegalArgumentException("Parameter 'value' must not be null.");
	}
	colorChooser.setColor(value);
	setBackground(value);
    }

    /**
     * Gets the currently selected color.
     * 
     * @return value The color, not <code>null</code>.
     */
    public Color getValue() {
	Color result = colorChooser.getColor();
	return result;
    }

    final void showColorChooserDialog() {
	if (dialog == null) {
	    dialog = JColorChooser.createDialog(colorChooser, label.getText(), true, colorChooser, null, null);
	}
	dialog.setVisible(true);
	setBackground(colorChooser.getColor());
    }
}
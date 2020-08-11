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

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.wudsn.tools.base.common.TextUtility;

@SuppressWarnings("serial")
public final class IntegerField extends JTextField {

    private JLabel label;

    /**
     * Creation is public.
     */
    public IntegerField() {

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
     *            The integer value.
     */
    public void setValue(int value) {
	setText(TextUtility.formatAsDecimal(value));
    }

    /**
     * Gets the numeric value by parsing the input with locale dependent
     * formatting.
     * 
     * @return value The integer value, or 0 the the input could not be parsed..
     */
    public int getValue() {
	int result;
	try {
	    result = NumberFormat.getNumberInstance().parse(getText()).intValue();
	} catch (ParseException ex) {
	    result = 0;
	}
	return result;
    }

}
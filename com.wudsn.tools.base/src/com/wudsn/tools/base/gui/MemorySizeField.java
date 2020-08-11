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

import static com.wudsn.tools.base.common.ByteArrayUtility.KB;
import static com.wudsn.tools.base.common.ByteArrayUtility.MB;

import java.text.NumberFormat;
import java.text.ParsePosition;

import javax.swing.JTextField;

import com.wudsn.tools.base.common.TextUtility;

@SuppressWarnings("serial")
public final class MemorySizeField extends JTextField {

    /**
     * Creation is public.
     */
    public MemorySizeField() {

    }

    /**
     * Sets the numeric value with locale dependent formatting.
     * 
     * @param value
     *            The integer value.
     */
    public void setValue(int value) {
	setText(TextUtility.formatAsMemorySize(value));
    }

    /**
     * Gets the numeric value by parsing the input with locale dependent
     * formatting.
     * 
     * @return value The integer value, or 0 the the input could not be parsed..
     */
    public int getValue() {
	int result;

	String text = getText();
	NumberFormat numberFormat = NumberFormat.getNumberInstance();
	ParsePosition parsePosition = new ParsePosition(0);
	Number number = numberFormat.parse(text, parsePosition);
	if (number == null) {
	    return 0;
	}
	result = number.intValue();
	String suffix = text.substring(parsePosition.getIndex()).trim().toUpperCase();
	if (suffix.equals("KB")) {
	    result *= KB;
	} else if (suffix.equals("MB")) {
	    result *= MB;
	}
	return result;
    }
}
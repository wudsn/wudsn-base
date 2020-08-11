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
package com.wudsn.tools.base.repository;

import com.wudsn.tools.base.common.ASCIIString;

/**
 * DataType repository object.
 * 
 * @author Peter Dell
 */
public final class DataType {

    private Class<?> valueClass;
    private String label;
    private String toolTip;
    private int maximumLength;
    private String allowedCharacters;

    public DataType(Class<?> valueClass) {

	if (valueClass == null) {
	    throw new IllegalArgumentException("Parameter 'valueClass' must not be null.");
	}
	this.valueClass = valueClass;
	this.label = "";
	this.toolTip = "";
	maximumLength = -1;
	allowedCharacters = null;
    }

    public DataType(Class<String> valueClass, int maximumLength, String allowedCharacters) {

	this(valueClass);
	this.maximumLength = maximumLength;
	this.allowedCharacters = allowedCharacters;

    }

    public DataType(Class<ASCIIString> valueClass, int maximumLength, ASCIIString allowedCharacters) {

	this(valueClass);
	this.maximumLength = maximumLength;
	this.allowedCharacters = allowedCharacters.toString();

    }

    public Class<?> getValueClass() {
	return valueClass;
    }

    public String getLabel() {
	return label;
    }

    public String getLabelWithoutMnemonics() {
	return label.replaceAll("&", "");
    }

    public String getToolTip() {
	return toolTip;
    }

    /**
     * Sets the texts for the data type. This method is only public for the
     * cases where data types are created dynamically. it must not be used to change data type constants.
     * 
     * @param label The label text, not <code>null</code>.
     * @param toolTip The tool tip text, not <code>null</code>.
     */
    public void setTexts(String label, String toolTip) {
	if (label == null) {
	    throw new IllegalArgumentException("Parameter 'label' must not be null.");
	}
	if (toolTip == null) {
	    throw new IllegalArgumentException("Parameter 'toolTip' must not be null.");
	}
	this.label = label;
	this.toolTip = toolTip;
    }

    public String getAllowedCharacters() {
	return allowedCharacters;
    }

    public int getMaximumLength() {
	return maximumLength;
    }

    @Override
    public String toString() {
	return "label=" + label;
    }

}

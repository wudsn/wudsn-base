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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.wudsn.tools.base.common.JDK.DefaultComboBoxModel;
import com.wudsn.tools.base.common.JDK.JComboBox;
import com.wudsn.tools.base.repository.ValueSet;

@SuppressWarnings("serial")
public final class ValueSetField<T extends ValueSet> extends JComboBox<T> {

    boolean settingValue;

    /**
     * Creates a value set field with all values of a value set class.
     * 
     * @param valueSetClass
     *            The class of the value set, not <code>null</code>.
     */
    public ValueSetField(Class<T> valueSetClass) {
	this(ValueSet.getValues(valueSetClass));
    }

    /**
     * Creates a value set field with all values of a value set class.
     * 
     * @param values
     *            The list of value, may be empty, not <code>null</code>.
     */
    public ValueSetField(List<T> values) {
	if (values == null) {
	    throw new IllegalArgumentException("Parameter 'values' must not be null.");
	}
	DefaultComboBoxModel<T> model = new DefaultComboBoxModel<T>();
	for (T value : values) {
	    model.addElement(value);
	}
	setModel(model);

    }

    public void setValue(T value) {
	if (value == null) {
	    throw new IllegalArgumentException("Parameter 'value' must not be null.");
	}
	settingValue = true;
	setSelectedItem(value);
	settingValue = false;
    }

    @Override
    public void addActionListener(final ActionListener l) {
	super.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (!settingValue) {
		    l.actionPerformed(e);
		}

	    }
	});

    }

    @SuppressWarnings("unchecked")
    public T getValue() {
	return (T) getSelectedItem();
    }
}
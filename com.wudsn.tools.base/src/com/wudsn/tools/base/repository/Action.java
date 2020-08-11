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

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/**
 * DataType repository object.
 * 
 * @author Peter Dell
 */
public final class Action {

    private KeyStroke accelerator;
    private String label;
    private String toolTip;

    /**
     * Create an not yet initialized action with an accelerator.
     * 
     * @param keyCode
     *            The key code, see @link {@link KeyEvent}.
     * @param modifiers
     *            The modifiers, see {@link com.wudsn.tools.base.gui.KeyStroke}.
     */
    public Action(int keyCode, int modifiers) {
	label = "";
	toolTip = "";
	accelerator = KeyStroke.getKeyStroke(keyCode, modifiers);
    }

    public Action(String label, String toolTip, KeyStroke accelerator) {
	if (label == null) {
	    throw new IllegalArgumentException("Parameter 'label' must not be null.");
	}
	if (toolTip == null) {
	    throw new IllegalArgumentException("Parameter 'toolTip' must not be null.");
	}
	this.label = label;
	this.toolTip = toolTip;
	this.accelerator = accelerator;
    }

    /**
     * Gets the accelerator.
     * 
     * @return The accelerator or <code>null</code>.
     */
    public KeyStroke getAccelerator() {
	return accelerator;
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

    @Override
    public String toString() {
	return label;
    }
}

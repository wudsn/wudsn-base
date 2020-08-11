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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

/**
 * The "M" modifier keys are a platform-independent way of representing keys.
 * 
 * @author Peter Dell
 */
public final class KeyStroke {

    /**
     * M1 is the COMMAND key on MacOS X, and the CTRL key on most other
     * platforms.
     */
    public static final int M1;

    /**
     * M2 is the SHIFT key.
     */
    public static final int M2;

    /**
     * M3 is the Option key on MacOS X, and the ALT key on most other platforms.
     */
    public static final int M3;

    static {

	M1 = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	M2 = ActionEvent.SHIFT_MASK;
	M3 = ActionEvent.ALT_MASK;
    }

    public static javax.swing.KeyStroke getKeyStroke(int keyCode, int modifiers) {
	return javax.swing.KeyStroke.getKeyStroke(keyCode, modifiers);
    }
}
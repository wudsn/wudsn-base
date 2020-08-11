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

package com.wudsn.tools.base;

import java.awt.event.KeyEvent;

import com.wudsn.tools.base.repository.Action;
import com.wudsn.tools.base.repository.NLS;

/**
 * Action repository.
 * 
 * @author Peter Dell
 */
public final class Actions extends NLS {

    // Actions: Dialog Button Bar
    public static Action ButtonBar_OK;
    public static Action ButtonBar_Cancel;
    public static Action ButtonBar_Yes;
    public static Action ButtonBar_YesAll;
    public static Action ButtonBar_No;
    public static Action ButtonBar_NoAll;


    // Actions: AttributeTable
    public static Action AttributeTable_HeaderPopupMenuSetColumnDefaults;

    // Actions: Menu
    public static Action MainMenu_File;
    public static Action MainMenu_Edit;
    public static Action MainMenu_Tools;
    public static Action MainMenu_Help;
    public static Action MainMenu_Help_HelpContents = new Action(KeyEvent.VK_F1, 0);

    static {
	initializeClass(Actions.class, null);
    }
}

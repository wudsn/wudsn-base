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

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;

/**
 * Interface to the preferences store.
 * 
 * @author Peter Dell
 * 
 */
public interface MainWindowPreferences {

    /**
     * Sets the main window extended state.
     * 
     * @param mainWindowExtendedState
     *            The main window extended state, see
     *            {@link JFrame#getExtendedState()}.
     */
    public void setMainWindowExtendedState(int mainWindowExtendedState);

    /**
     * Gets the main window extended state.
     * 
     * @return The main window extended state.
     */
    public int getMainWindowExtendedState();

    /**
     * Sets the main window location.
     * 
     * @param mainWindowLocation
     *            The main window location, not <code>null</code>.
     */
    public void setMainWindowLocation(Point mainWindowLocation);

    /**
     * Gets the main window location.
     * 
     * @return The main window location or <code>null</code>.
     */
    public Point getMainWindowLocation();

    /**
     * Sets the main window size.
     * 
     * @param mainWindowSize
     *            The main window size, not <code>null</code>.
     */
    public void setMainWindowSize(Dimension mainWindowSize);

    /**
     * Gets the main window size.
     * 
     * @return The main window size or <code>null</code>.
     */
    public Dimension getMainWindowSize();
}

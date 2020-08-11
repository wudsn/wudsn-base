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
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;

/**
 * Main window class that keeps track of the actual size of the window,
 * irrespective of maximizing/minimizing.
 * 
 * @author Peter Dell
 */
public final class MainWindow {

    JFrame frame;
    int state;
    Point actualLocation;
    Dimension actualSize;

    public MainWindow() {
	frame = new JFrame();
	state = Frame.NORMAL;
	actualLocation = null;
	actualSize = new Dimension();

	frame.addWindowStateListener(new WindowStateListener() {
	    @Override
	    public void windowStateChanged(WindowEvent e) {
		state = e.getNewState();
	    }
	});

	frame.addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentMoved(ComponentEvent e) {
		// If location was never determined, we use it as it is.
		if (actualLocation == null) {
		    actualLocation = frame.getLocation();
		}
		// Ignore moves in iconfied state.
		if ((state & Frame.ICONIFIED) == 1) {
		    return;
		}
		// Ignore move event that occurs prior to "maximize".
		// Works on windows, not sure about other OS types.
		if (frame.getLocation().x < 0 || frame.getLocation().y < 0) {
		    return;
		}
		// Take over horizontal resizing, if not maximized horizontally.
		if ((state & Frame.MAXIMIZED_HORIZ) == 0) {
		    actualLocation.x = frame.getLocation().x;
		}
		// Take over horizontal resizing, if not maximized horizontally.
		if ((state & Frame.MAXIMIZED_VERT) == 0) {
		    actualLocation.y = frame.getLocation().y;
		}

	    }

	    @Override
	    public void componentResized(ComponentEvent e) {
		// Ignore size changes in iconified state.
		if ((state & Frame.ICONIFIED) == 1) {
		    return;
		}
		// Take over horizontal resizing, if not maximized horizontally.
		if ((state & Frame.MAXIMIZED_HORIZ) == 0) {
		    actualSize.width = frame.getWidth();
		}
		// Take over horizontal resizing, if not maximized horizontally.
		if ((state & Frame.MAXIMIZED_VERT) == 0) {
		    actualSize.height = frame.getHeight();
		}
	    }
	});
    }

    public JFrame getFrame() {
	return frame;
    }

    public void dispose() {
	frame.dispose();

    }

    public void setWindowFromPreferences(MainWindowPreferences preferences) {
	if (preferences == null) {
	    throw new IllegalArgumentException("Parameter 'preferences' must not be null.");
	}
	// Set window extended state, but ignore "iconfied".
	frame.setExtendedState(preferences.getMainWindowExtendedState() & ~Frame.ICONIFIED);

	// Set size first, so relative positions works correctly.
	actualLocation = preferences.getMainWindowLocation();
	actualSize = preferences.getMainWindowSize();
	frame.setSize(actualSize);
	if (actualLocation != null) {
	    frame.setLocation(preferences.getMainWindowLocation());
	} else {
	    // This will center the JFrame in the middle of the screen
	    frame.setLocationRelativeTo(null);
	}

    }

    public void setPreferencesFromWindow(MainWindowPreferences preferences) {
	if (preferences == null) {
	    throw new IllegalArgumentException("Parameter 'preferences' must not be null.");
	}
	preferences.setMainWindowExtendedState(frame.getExtendedState());
	preferences.setMainWindowLocation(actualLocation);
	preferences.setMainWindowSize(actualSize);

    }

}
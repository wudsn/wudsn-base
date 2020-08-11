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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public final class Desktop {
    public static boolean openBrowser(String url) {
	if (url == null) {
	    throw new IllegalArgumentException("Parameter 'url' must not be null.");
	}

	try {
	    java.awt.Desktop.getDesktop().browse(new URI(url));
	    return true;
	} catch (IOException ignore) {
	} catch (URISyntaxException ignore) {
	}
	return false;
    }

    public static void openFile(File file) throws IOException {
	if (file == null) {
	    throw new IllegalArgumentException("Parameter 'file' must not be null.");
	}
	java.awt.Desktop.getDesktop().open(file);
    }
}
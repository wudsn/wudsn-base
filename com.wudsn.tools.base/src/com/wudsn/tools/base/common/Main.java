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
package com.wudsn.tools.base.common;

/**
 * Base class for command line commands.
 * 
 * @author Peter Dell
 * 
 */
public abstract class Main {


    public static void logInfo(String message) {
	if (message == null) {
	    throw new IllegalArgumentException("Parameter 'message' must not be null.");
	}
	println("INFO : " + message);

    }

    public static void logError(String message) {
	if (message == null) {
	    throw new IllegalArgumentException("Parameter 'message' must not be null.");
	}
	println("ERROR: " + message);

    }
    
    public static void println(Object o) {
	System.out.println(o);
    }
}
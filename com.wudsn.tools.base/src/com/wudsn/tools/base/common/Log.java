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

public abstract class Log {

    /**
     * Creation is private.
     */
    private Log() {
    }

    /**
     * Log a debug info message.
     * 
     * @param message
     *            The message, not <code>null</code>.
     * 
     * @param parameters
     *            The message parameters, may be empty or <code>null</code>.
     */
    public static void logInfo(String message, Object[] parameters) {
	if (message == null) {
	    throw new IllegalArgumentException("Parameter 'message' must not be null.");
	}
	message = format(message, parameters);
	System.out.println(message);

    }

    /**
     * Logs an error message and an exception to the plugin log and the standard
     * error stream.
     * 
     * @param message
     *            The message, not <code>null</code>.
     * @param parameters
     *            The message parameters, may be empty or <code>null</code>.
     * @param th
     *            The throwable or <code>null</code>.
     */
    public static void logError(String message, Object[] parameters, Throwable th) {
	if (message == null) {
	    throw new IllegalArgumentException("Parameter 'message' must not be null.");
	}

	message = format(message, parameters);
	if (th != null) {
	    message = message + "\n" + th.getMessage();
	}

	System.err.println("ERROR: " + message);
	if (th != null) {
	    th.printStackTrace(System.err);
	    System.err.flush();
	}

    }

    private static String format(String message, Object... parameters) {
	if (parameters == null) {
	    parameters = new String[0];
	}
	String[] stringParameters = new String[parameters.length];
	for (int i = 0; i < parameters.length; i++) {
	    Object parameter = parameters[i];
	    String stringParameter;
	    if (parameter == null) {
		stringParameter = "null";
	    } else {
		stringParameter = parameter.toString();
	    }
	    stringParameters[i] = stringParameter;
	}
	message = TextUtility.format(message, stringParameters);
	return message;
    }

}

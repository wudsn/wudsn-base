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
package com.wudsn.tools.base.console;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.wudsn.tools.base.Messages;
import com.wudsn.tools.base.common.CoreException;
import com.wudsn.tools.base.common.StringUtility;
import com.wudsn.tools.base.repository.Attribute;

public final class ConsoleCommandExecution {

    private ConsoleCommand consoleCommand;
    private Map<String, List<String>> parameterValues;

    ConsoleCommandExecution(ConsoleCommand consoleCommand, Map<String, List<String>> parameterValues) {
	if (consoleCommand == null) {
	    throw new IllegalArgumentException("Parameter 'consoleCommand' must not be null.");
	}
	if (parameterValues == null) {
	    throw new IllegalArgumentException("Parameter 'parameterValues' must not be null.");
	}
	this.consoleCommand = consoleCommand;
	this.parameterValues = Collections.unmodifiableMap(parameterValues);
    }

    public ConsoleCommand getConsoleCommand() {
	return consoleCommand;
    }

    public List<String> getParameterValuesAsString(Attribute attribute) {
	if (attribute == null) {
	    throw new IllegalArgumentException("Parameter 'attribute' must not be null.");
	}
	List<String> result = parameterValues.get(attribute.getName());
	if (result == null) {
	    result = Collections.emptyList();
	}
	return result;
    }

    public String getParameterValueAsString(Attribute attribute, boolean mandatory) throws CoreException {
	if (attribute == null) {
	    throw new IllegalArgumentException("Parameter 'attribute' must not be null.");
	}
	if (getParameterValuesAsString(attribute).isEmpty()) {
	    if (mandatory) {
		// ERROR: No value specified for parameter '{0}' of command
		// '{1}'.
		throw new CoreException(Messages.E255, attribute.getName(), consoleCommand.getActionCommand());
	    }
	    return "";
	}
	String result = getParameterValuesAsString(attribute).get(0);
	if (StringUtility.isEmpty(result)) {
	    if (mandatory) {
		// ERROR: No value specified for parameter '{0}' of command
		// '{1}'.
		throw new CoreException(Messages.E255, attribute.getName(), consoleCommand.getActionCommand());
	    }
	}
	return result;
    }

    public File getParameterValueAsFile(Attribute attribute, boolean mandatory) throws CoreException {
	if (attribute == null) {
	    throw new IllegalArgumentException("Parameter 'attribute' must not be null.");
	}
	if (getParameterValuesAsString(attribute).isEmpty()) {
	    if (mandatory) {
		// ERROR: No value specified for parameter '{0}' of command
		// '{1}'.
		throw new CoreException(Messages.E255, attribute.getName(), consoleCommand.getActionCommand());
	    }
	    return null;
	}
	return new File(getParameterValuesAsString(attribute).get(0));
    }

    public Integer getParameterValueAsInteger(Attribute attribute, boolean mandatory) throws CoreException {
	if (attribute == null) {
	    throw new IllegalArgumentException("Parameter 'attribute' must not be null.");
	}
	if (getParameterValuesAsString(attribute).isEmpty()) {
	    if (mandatory) {
		// ERROR: No value specified for parameter '{0}' of command
		// '{1}'.
		throw new CoreException(Messages.E255, attribute.getName(), consoleCommand.getActionCommand());
	    }
	    return null;
	}
	String stringValue = getParameterValuesAsString(attribute).get(0);
	try {
	    int intValue = Integer.parseInt(stringValue);
	    return Integer.valueOf(intValue);
	} catch (NumberFormatException ex) {
	    // ERROR: Specified value '{0}' for parameter '{1}' of command '{2}'
	    // is not a number.
	    throw new CoreException(Messages.E256, stringValue, attribute.getName(), consoleCommand.getActionCommand());
	}
    }

    @Override
    public String toString() {
	String result = "-" + consoleCommand.getActionCommand();
	if (!parameterValues.isEmpty()) {
	    result += ":" + parameterValues.toString();
	}
	return result;
    }

}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.wudsn.tools.base.console.ConsoleCommandParameter.Cardinality;
import com.wudsn.tools.base.repository.Action;
import com.wudsn.tools.base.repository.Attribute;

public final class ConsoleCommand {

    private Action action;
    private String actionCommand;
    private List<ConsoleCommandParameter> parameters;
    private List<ConsoleCommandParameter> unmodifiableParameters;

    /**
     * Used by
     * {@link ConsoleCommandParser#addCommand(Action, String)}.
     * 
     * @param action
     *            The action, not <code>null</code>.
     * @param actionCommand
     *            The console action command, not <code>null</code>.
     */
    ConsoleCommand(Action action, String actionCommand) {
	if (action == null) {
	    throw new IllegalArgumentException("Parameter 'action' must not be null.");
	}
	if (actionCommand == null) {
	    throw new IllegalArgumentException("Parameter 'actionCommand' must not be null.");
	}
	this.action = action;
	this.actionCommand = actionCommand;
	this.parameters = new ArrayList<ConsoleCommandParameter>();
	this.unmodifiableParameters = Collections.unmodifiableList(parameters);
    }

    /**
     * Add a parameter to the command.
     * 
     * @param attribute
     *            The defining attribute of the parameter, not <code>null</code>
     *            .
     * @param cardinality
     *            The cardinality of the parameter, see {@link Cardinality}.
     */
    public void addParameter(Attribute attribute, int cardinality) {
	if (attribute == null) {
	    throw new IllegalArgumentException("Parameter 'attribute' must not be null.");
	}
	parameters.add(new ConsoleCommandParameter(attribute, cardinality));
    }

    public Action getAction() {
	return action;
    }

    public String getActionCommand() {
	return actionCommand;
    }

    public List<ConsoleCommandParameter> getParameters() {
	return unmodifiableParameters;
    }

}

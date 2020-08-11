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

import com.wudsn.tools.base.repository.Attribute;

public final class ConsoleCommandParameter {

    public static final class Cardinality {
	/**
	 * Creation is private.
	 */
	private Cardinality() {
	}

	public static final int OPTIONAL = 1;
	public static final int MANDATORY = 2;
	public static final int MULTIPLE = 2;
    }

    private Attribute attribute;
    private int cardinality;

    /**
     * Used by {@link ConsoleCommand#addParameter(Attribute, int)}.
     * 
     * @param attribute
     *            The defining attribute or <code>null</code>.
     * @param cardinality
     *            The cardinality of the parameter, see {@link Cardinality}
     */
    ConsoleCommandParameter(Attribute attribute, int cardinality) {
	this.attribute = attribute;
	this.cardinality = cardinality;

    }

    /**
     * Gets the defining attribute of the parameter.
     * 
     * @return The defining attribute, not <code>null</code>.
     */
    public Attribute getAttribute() {
	return attribute;
    }

    /**
     * Gets the cardinality of the parameter.
     * 
     * @return See {@link Cardinality}.
     */
    public int getCardinality() {
	return cardinality;
    }

}

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

import com.wudsn.tools.base.repository.Attribute;
import com.wudsn.tools.base.repository.Message;

@SuppressWarnings("serial")
public class CoreException extends Exception {

    private Message message;
    private String[] parameters;

    public CoreException(Message message, String... parameters) {
	super(message.format(parameters));
	this.message = message;
	this.parameters = parameters;
    }

    public int getSeverity(){
	return message.getSeverity();
    }
    public MessageQueueEntry createMessageQueueEntry(Object owner, Attribute attribute) {
	return new MessageQueueEntry(owner, attribute, message, parameters);
    }
}

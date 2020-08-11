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

public final class MessageQueueEntry {

    private Object owner;
    private Attribute attribute;
    private int severity;
    private Message message;
    private String[] parameters;
    private Exception cause;

    public MessageQueueEntry(Object owner, Attribute attribute, Message message, String... parameters) {

	if (message == null) {
	    throw new IllegalArgumentException("Parameter 'message' must not be null.");
	}

	this.owner = owner;
	this.attribute = attribute;
	this.message = message;
	this.parameters = parameters;
	this.cause = null;
    }

    public Object getOwner() {
	return owner;
    }

    public Attribute getAttribute() {
	return attribute;
    }

    public Message getMessage() {
	return message;
    }

    public String getMessageText() {
	return message.format(parameters);
    }

    public Exception getCause() {
	return cause;
    }

    @Override
    public String toString() {
	String messageText = getMessageText();
	switch (severity) {
	case Message.STATUS:
	    return "INFO: " + messageText;
	case Message.INFO:
	    return "INFO: " + messageText;
	case Message.ERROR:
	    return "ERROR: " + messageText;
	}
	throw new IllegalStateException("Field 'severity' has illegal value " + severity + ".");
    }
}

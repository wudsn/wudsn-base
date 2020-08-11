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

import java.util.ArrayList;
import java.util.List;

import com.wudsn.tools.base.repository.Attribute;
import com.wudsn.tools.base.repository.Message;

public final class MessageQueue {

    private MessageQueueRenderer renderer;
    private List<MessageQueueEntry> entries;
    private boolean entriesChanged;
    private MessageQueueEntry firstErrorStatus;
    private MessageQueueEntry firstInfoStatus;

    public MessageQueue() {
	renderer = null;
	entries = new ArrayList<MessageQueueEntry>();
    }

    /**
     * Sets the renderer for status messages.
     * 
     * @param renderer
     *            The status receiver or <code>null</code>.
     */
    public void setMessageQueueRenderer(MessageQueueRenderer renderer) {
	this.renderer = renderer;
    }

    public void clear() {
	entries.clear();
	entriesChanged = false;
	firstErrorStatus = null;
	firstInfoStatus = null;
    }

    public void sendMessage(Object owner, Attribute attribute, Message message, String... parameters) {
	if (message == null) {
	    throw new IllegalArgumentException("Parameter 'message' must not be null.");
	}
	MessageQueueEntry messageQueueEntry = new MessageQueueEntry(owner, attribute, message, parameters);
	sendMessage(messageQueueEntry);
    }

    public void sendMessage(MessageQueueEntry messageQueueEntry) {
	if (messageQueueEntry == null) {
	    throw new IllegalArgumentException("Parameter 'status' must not be null.");
	}

	if (renderer != null && messageQueueEntry.getMessage().getSeverity() == Message.STATUS) {
	    renderer.displayStatusMessage(messageQueueEntry);
	} else {
	    entries.add(messageQueueEntry);
	    entriesChanged = true;
	    if (firstErrorStatus == null && messageQueueEntry.getMessage().getSeverity() == Message.ERROR) {
		firstErrorStatus = messageQueueEntry;
	    }
	    if (firstInfoStatus == null && messageQueueEntry.getMessage().getSeverity() == Message.INFO) {
		firstInfoStatus = messageQueueEntry;
	    }
	}
    }

    public boolean containsError() {
	return firstErrorStatus != null;
    }

    public boolean areEntriesChanged() {
	boolean result = entriesChanged;
	entriesChanged = false;
	return result;
    }

    public MessageQueueEntry getFirstError() {
	return firstErrorStatus;
    }

    public boolean containsInfo() {
	return firstInfoStatus != null;
    }

    public MessageQueueEntry getFirstInfo() {
	return firstInfoStatus;
    }

    public List<MessageQueueEntry> getEntries() {
	return entries;
    }

}

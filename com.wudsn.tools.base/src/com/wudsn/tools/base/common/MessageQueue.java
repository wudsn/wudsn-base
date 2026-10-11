/**
 * Copyright (C) 2013 - 2014 <a href="https://www.wudsn.com" target="_top">Peter Dell</a>
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
 * along with the WUDSN software distribution. If not, see <https://www.gnu.org/licenses/>.
 */

package com.wudsn.tools.base.common;

import java.util.ArrayList;
import java.util.List;

import com.wudsn.tools.base.repository.Attribute;
import com.wudsn.tools.base.repository.Message;

public final class MessageQueue {

	private MessageQueueRenderer renderer;
	private MessageQueueListener listener;
	private List<MessageQueueEntry> entries;
	private boolean entriesChanged;
	private MessageQueueEntry firstErrorStatus;
	private MessageQueueEntry firstWarningStatus;
	private MessageQueueEntry firstInfoStatus;
	// The number of entries per severity, indexed by the severity.
	private int[] counts;

	public MessageQueue() {
		renderer = null;
		listener = null;
		entries = new ArrayList<MessageQueueEntry>();
		counts = new int[Message.WARNING + 1];
	}

	/**
	 * Sets the listener that receives every message when it is sent.
	 *
	 * @param listener
	 *            The listener or <code>null</code>.
	 */
	public void setMessageQueueListener(MessageQueueListener listener) {
		this.listener = listener;
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
		firstWarningStatus = null;
		firstInfoStatus = null;
		counts = new int[Message.WARNING + 1];
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

		if (listener != null) {
			listener.messageSent(messageQueueEntry);
		}
		int severity = messageQueueEntry.getMessage().getSeverity();
		if (renderer != null && severity == Message.STATUS) {
			renderer.displayStatusMessage(messageQueueEntry);
		} else {
			entries.add(messageQueueEntry);
			entriesChanged = true;
			counts[severity]++;
			if (firstErrorStatus == null && severity == Message.ERROR) {
				firstErrorStatus = messageQueueEntry;
			}
			if (firstWarningStatus == null && severity == Message.WARNING) {
				firstWarningStatus = messageQueueEntry;
			}
			if (firstInfoStatus == null && severity == Message.INFO) {
				firstInfoStatus = messageQueueEntry;
			}
		}
	}

	/**
	 * Adds the entries of another queue in their order, as if they were sent
	 * to this queue, e.g. to collect the results of parallel workers. The
	 * listener receives them as well.
	 *
	 * @param messageQueue
	 *            The other queue, not <code>null</code> and not this queue.
	 */
	public void addAll(MessageQueue messageQueue) {
		if (messageQueue == null) {
			throw new IllegalArgumentException("Parameter 'messageQueue' must not be null.");
		}
		if (messageQueue == this) {
			throw new IllegalArgumentException("Parameter 'messageQueue' must not be this queue.");
		}
		for (MessageQueueEntry messageQueueEntry : messageQueue.getEntries()) {
			sendMessage(messageQueueEntry);
		}
	}

	/**
	 * Gets the number of entries with the given severity.
	 *
	 * @param severity
	 *            A severity of {@link Message}, e.g. {@link Message#WARNING}.
	 *            Status messages shown by the renderer are not entries.
	 * @return The number of entries, 0 or more.
	 */
	public int getCount(int severity) {
		if (severity < Message.STATUS || severity >= counts.length) {
			throw new IllegalArgumentException("Parameter 'severity' has illegal value " + severity + ".");
		}
		return counts[severity];
	}

	/**
	 * Determines whether an entry with the given message was sent.
	 *
	 * @param message
	 *            The message, not <code>null</code>.
	 * @return <code>true</code> if there is such an entry.
	 */
	public boolean contains(Message message) {
		if (message == null) {
			throw new IllegalArgumentException("Parameter 'message' must not be null.");
		}
		for (MessageQueueEntry messageQueueEntry : entries) {
			if (messageQueueEntry.getMessage() == message) {
				return true;
			}
		}
		return false;
	}

	public boolean containsError() {
		return firstErrorStatus != null;
	}

	public boolean containsWarning() {
		return firstWarningStatus != null;
	}

	public MessageQueueEntry getFirstWarning() {
		return firstWarningStatus;
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

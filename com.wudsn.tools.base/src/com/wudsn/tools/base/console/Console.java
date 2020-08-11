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

import com.wudsn.tools.base.common.MessageQueue;
import com.wudsn.tools.base.common.MessageQueueEntry;
import com.wudsn.tools.base.common.MessageQueueRenderer;
import com.wudsn.tools.base.repository.Message;

public final class Console implements MessageQueueRenderer {

	public Console() {

	}

	@Override
	public void displayMessageQueue(MessageQueue messageQueue) {
		if (messageQueue == null) {
			throw new IllegalArgumentException(
					"Parameter 'messageQueue' must not be null.");
		}
		for (MessageQueueEntry messageQueueEntry : messageQueue.getEntries()) {

			setStatus(messageQueueEntry);
		}
	}

	@Override
	public void displayStatusMessage(MessageQueueEntry messageQueueEntry) {
		if (messageQueueEntry == null) {
			throw new IllegalArgumentException(
					"Parameter 'messageQueueEntry' must not be null.");
		}
		setStatus(messageQueueEntry);

	}

	private void setStatus(MessageQueueEntry messageQueueEntry) {
		int severity = messageQueueEntry.getMessage().getSeverity();
		String messageText = messageQueueEntry.getMessage().getId() + " - "
				+ messageQueueEntry.getMessageText();
		switch (severity) {
		case Message.STATUS:
		case Message.INFO:
			println("INFO:  " + messageText);
			break;
		case Message.ERROR:
			System.err.println("ERROR: " + messageText);
			System.err.flush();
			break;
		default:
			throw new IllegalStateException(
					"Field 'severity' has illegal value " + severity + ".");
		}
	}

	@SuppressWarnings("static-method")
	public void println(String string) {
		if (string == null) {
			throw new IllegalArgumentException(
					"Parameter 'string' must not be null.");
		}
		System.out.println(string);
		System.out.flush();
	}

	@SuppressWarnings("static-method")
	public void exit(boolean errorOccurred) {
		System.exit(errorOccurred ? 1 : 0);
	}

}

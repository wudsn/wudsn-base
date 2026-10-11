/**
 * Copyright (C) 2026 <a href="https://www.wudsn.com" target="_top">Peter Dell</a>
 *
 * This file is part of a WUDSN software distribution.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the WUDSN software distribution. If not, see <https://www.gnu.org/licenses/>.
 */

package com.wudsn.tools.base.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.wudsn.tools.base.repository.Message;
import com.wudsn.tools.base.repository.TestMessages;

/**
 * Counts per severity, the first entry per severity, lookup by message,
 * merging and the listener.
 */
public class MessageQueueTest {

	@Test
	public void testCountsAndFirstEntries() {
		MessageQueue messageQueue = new MessageQueue();
		messageQueue.sendMessage(this, null, TestMessages.I002, "1");
		messageQueue.sendMessage(this, null, TestMessages.W004, "a");
		messageQueue.sendMessage(this, null, TestMessages.W004, "b");
		messageQueue.sendMessage(this, null, TestMessages.E001, "x.rom");

		assertEquals(1, messageQueue.getCount(Message.INFO));
		assertEquals(2, messageQueue.getCount(Message.WARNING));
		assertEquals(1, messageQueue.getCount(Message.ERROR));
		assertEquals(0, messageQueue.getCount(Message.STATUS));
		assertTrue(messageQueue.containsWarning());
		assertEquals("Entry a should be checked.", messageQueue.getFirstWarning().getMessageText());
		assertEquals("WARNING: Entry a should be checked.", messageQueue.getFirstWarning().toString());
		assertEquals("ERROR: File 'x.rom' cannot be read.", messageQueue.getFirstError().toString());

		assertTrue(messageQueue.contains(TestMessages.W004));
		assertFalse(messageQueue.contains(TestMessages.S003));

		messageQueue.clear();
		assertEquals(0, messageQueue.getCount(Message.WARNING));
		assertFalse(messageQueue.containsWarning());
		assertFalse(messageQueue.contains(TestMessages.W004));

		assertThrows(IllegalArgumentException.class, () -> messageQueue.getCount(0));
		assertThrows(IllegalArgumentException.class, () -> messageQueue.getCount(5));
		assertThrows(IllegalArgumentException.class, () -> messageQueue.contains(null));
	}

	@Test
	public void testAddAllAndListener() {
		MessageQueue worker = new MessageQueue();
		worker.sendMessage(this, null, TestMessages.W004, "a");
		worker.sendMessage(this, null, TestMessages.E001, "x.rom");

		List<MessageQueueEntry> sent = new ArrayList<MessageQueueEntry>();
		MessageQueue messageQueue = new MessageQueue();
		messageQueue.setMessageQueueListener(sent::add);
		messageQueue.sendMessage(this, null, TestMessages.I002, "1");
		messageQueue.addAll(worker);

		assertEquals(3, messageQueue.getEntries().size());
		assertEquals(1, messageQueue.getCount(Message.WARNING));
		assertEquals(1, messageQueue.getCount(Message.ERROR));
		assertEquals(messageQueue.getEntries(), sent);
		assertSame(worker.getEntries().get(0), messageQueue.getFirstWarning());

		assertThrows(IllegalArgumentException.class, () -> messageQueue.addAll(messageQueue));
		assertThrows(IllegalArgumentException.class, () -> messageQueue.addAll(null));
	}
}

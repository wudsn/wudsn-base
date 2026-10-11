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

/**
 * Receives every message when it is sent to a {@link MessageQueue}, whatever
 * its severity, e.g. to print it immediately.
 *
 * @author Peter Dell
 */
public interface MessageQueueListener {

	/**
	 * @param messageQueueEntry
	 *            The entry that was sent, not <code>null</code>.
	 */
	public void messageSent(MessageQueueEntry messageQueueEntry);
}

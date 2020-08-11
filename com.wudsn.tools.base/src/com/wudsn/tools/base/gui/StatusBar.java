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

package com.wudsn.tools.base.gui;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.wudsn.tools.base.common.MessageQueue;
import com.wudsn.tools.base.common.MessageQueueEntry;
import com.wudsn.tools.base.common.MessageQueueRenderer;
import com.wudsn.tools.base.repository.Message;

public final class StatusBar implements MessageQueueRenderer {

    private Icon iconInfo;
    private Icon iconError;
    private JLabel label;

    /**
     * Creation is public.
     */
    public StatusBar() {

	iconInfo = ElementFactory.createImageIcon("icons/status-info-16x16.png");
	iconError = ElementFactory.createImageIcon("icons/status-error-16x16.png");
	label = new JLabel();
	label.setHorizontalAlignment(SwingConstants.LEADING);
    }

    /**
     * Gets the component to be added to the container.
     * 
     * @return The component, not <code>null</code>.
     */
    public JComponent getComponent() {
	return label;
    }

     @Override
    public void displayMessageQueue(MessageQueue messageQueue) {
	if (messageQueue == null) {
	    throw new IllegalArgumentException("Parameter 'messageQueue' must not be null.");
	}

	if (messageQueue.containsError()) {
	    setStatus(messageQueue.getFirstError());
	} else if (messageQueue.containsInfo()) {
	    setStatus(messageQueue.getFirstInfo());
	} else {
	    setStatus(null);
	}
    }

     @Override
    public void displayStatusMessage(MessageQueueEntry messageQueueEntry) {
	if (messageQueueEntry == null) {
	    throw new IllegalArgumentException("Parameter 'messageQueueEntry' must not be null.");
	}

	setStatus(messageQueueEntry);
    }

    /**
     * Sets the status.
     * 
     * @param messageQueueEntry
     *            The status or <code>null</code>.
     */
    private void setStatus(MessageQueueEntry messageQueueEntry) {

	if (messageQueueEntry == null) {
	    label.setIcon(null);
	    label.setText(" ");
	} else {
	    int severity = messageQueueEntry.getMessage().getSeverity();
	    Icon icon;
	    switch (severity) {
	    case Message.STATUS:
		icon = iconInfo;
		break;
	    case Message.INFO:
		icon = iconInfo;
		break;
	    case Message.ERROR:
		icon = iconError;
		break;
	    default:
		throw new IllegalStateException("Field 'severity' has illegal value " + severity + ".");
	    }
	    label.setIcon(icon);
	    label.setText(messageQueueEntry.getMessageText());
	    label.setToolTipText(messageQueueEntry.getMessage().getId() + ": " + label.getText());
	}

    }

}
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

import org.w3c.dom.Document;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML handler for {@link XMLUtility}.
 * 
 * @author Peter Dell
 * 
 */
public abstract class XMLHandler extends DefaultHandler {

    /**
     * Creation is protected.
     */
    protected XMLHandler() {

    }

    /**
     * Initialize before open.
     * 
     * @param messageQueue
     *            The message, queue, not <code>null</code>.
     */
    public void startOpen(MessageQueue messageQueue) {

    }

    /**
     * Initialize before save.
     * 
     * @param document
     *            The document to serialize to, not <code>null</code>.
     * 
     * @param messageQueue
     *            The message, queue, not <code>null</code>.
     */
    public abstract void startSave(Document document, MessageQueue messageQueue);

}

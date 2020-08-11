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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.wudsn.tools.base.Messages;

/**
 * XML utility.
 * 
 * @author Peter Dell
 * 
 */
public final class XMLUtility {

    /**
     * Creation is private.
     */
    private XMLUtility() {

    }

    /**
     * Opens (deserializes) an XML file.
     * 
     * @param file
     *            The file to open, not <code>null</code>.
     * @param xmlHandler
     *            The XML handler to deserialize the file content to an object.
     * @param messageQueue
     *            The message queue, not <code>null</code>. Will contain error
     *            messages if the file does not exist or cannot be read.
     */
    public static void open(File file, XMLHandler xmlHandler, MessageQueue messageQueue) {
	if (file == null) {
	    throw new IllegalArgumentException("Parameter 'file' must not be null.");
	}
	if (xmlHandler == null) {
	    throw new IllegalArgumentException("Parameter 'xmlHandler' must not be null.");
	}
	if (messageQueue == null) {
	    throw new IllegalArgumentException("Parameter 'messageQueue' must not be null.");
	}

	InputStream inputStream = null;
	try {
	    inputStream = FileUtility.openInputStream(file);
	} catch (CoreException ex) {
	    messageQueue.sendMessage(ex.createMessageQueueEntry(file, null));
	    return;
	}

	xmlHandler.startOpen(messageQueue);
	if (messageQueue.containsError()) {
	    return;
	}

	SAXParser parser;
	try {
	    parser = SAXParserFactory.newInstance().newSAXParser();
	} catch (ParserConfigurationException ex) {
	    throw new RuntimeException("Cannot create parser.", ex);
	} catch (SAXException ex) {
	    throw new RuntimeException("Cannot create parser.", ex);
	}

	try {
	    parser.parse(inputStream, xmlHandler);
	} catch (SAXParseException ex) {
	    // ERROR: Cannot create parser for file '{0}'. Error in line
	    // {1}, column {2}.
	    messageQueue.sendMessage(file, null, Messages.E218, file.getAbsolutePath(),
		    TextUtility.formatAsDecimal(ex.getLineNumber()), TextUtility.formatAsDecimal(ex.getColumnNumber()));
	    return;
	} catch (SAXException ex) {
	    // ERROR: Cannot create parser for file '{0}': {1}.
	    messageQueue.sendMessage(file, null, Messages.E217, file.getAbsolutePath(), ex.getMessage());
	    return;
	} catch (IOException ex) {
	    // ERROR: Cannot create parser for file '{0}': {1}.
	    messageQueue.sendMessage(file, null, Messages.E217, file.getAbsolutePath(), ex.getMessage());
	    return;
	} finally {
	    try {
		FileUtility.closeInputStream(file, inputStream);
	    } catch (CoreException ignore) {
	    }
	}
    }

    private static Document createDocument() {
	// Create new file content
	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder documentBuilder;
	try {
	    documentBuilder = docFactory.newDocumentBuilder();
	} catch (ParserConfigurationException ex) {
	    throw new RuntimeException(ex);
	}

	// Root elements
	Document document = documentBuilder.newDocument();
	return document;
    }

    /**
     * Saves (serializes) an XML file.
     * 
     * @param file
     *            The file to open, not <code>null</code>.
     * @param xmlHandler
     *            The XML handler to serialize the object to the file content.
     * @param messageQueue
     *            The message queue, not <code>null</code>. Will contain error
     *            messages if the file does not exist or cannot be read.
     */
    public static void save(File file, XMLHandler xmlHandler, MessageQueue messageQueue) {
	if (file == null) {
	    throw new IllegalArgumentException("Parameter 'file' must not be null.");
	}
	if (xmlHandler == null) {
	    throw new IllegalArgumentException("Parameter 'xmlHandler' must not be null.");
	}
	if (messageQueue == null) {
	    throw new IllegalArgumentException("Parameter 'messageQueue' must not be null.");
	}

	// Ensure the folder and the file exists and can be written to.
	File folder = file.getParentFile();
	if (!folder.exists()) {
	    if (!folder.mkdirs()) {
		// ERROR: Cannot create folder '{0}'
		messageQueue.sendMessage(file, null, Messages.E202, folder.getAbsolutePath());
		return;
	    }
	} else {
	    if (!folder.isDirectory()) {
		// ERROR: '{0}' is no folder but a file.
		messageQueue.sendMessage(file, null, Messages.E201, folder.getAbsolutePath());
		return;

	    }
	}
	Document document = createDocument();
	xmlHandler.startSave(document, messageQueue);
	if (messageQueue.containsError()) {
	    return;
	}

	// Write document to file.
	OutputStream outputStream = null;
	try {
	    outputStream = FileUtility.openOutputStream(file);
	    // Write the content into XML file
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    try {
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(outputStream);
		transformer.transform(source, result);
	    } catch (TransformerException ex) {
		throw new RuntimeException(ex);
	    }

	} catch (CoreException ex) {
	    messageQueue.sendMessage(ex.createMessageQueueEntry(file, null));
	} finally {
	    if (outputStream != null) {
		try {
		    FileUtility.closeOutputStream(file, outputStream);
		} catch (CoreException ex) {
		    messageQueue.sendMessage(ex.createMessageQueueEntry(file, null));
		}
	    }
	}

    }

    /**
     * Copies the values from one XML handler to another XML handler in memory.
     * 
     * @param fromXMLHandler The source XML handler, not <code>null</code>.
     * @param toXMLHandler The target XML handler, not <code>null</code>.
     */
    public static void copyTo(XMLHandler fromXMLHandler, XMLHandler toXMLHandler) {
	if (fromXMLHandler == null) {
	    throw new IllegalArgumentException("Parameter 'fromXMLHandler' must not be null.");
	}
	if (toXMLHandler == null) {
	    throw new IllegalArgumentException("Parameter 'toXMLHandler' must not be null.");
	}
	Document document = createDocument();
	MessageQueue messageQueue = new MessageQueue();
	fromXMLHandler.startSave(document, messageQueue);
	if (messageQueue.containsError()) {
	    return;
	}
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	// Write the content into XML file
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	try {
	    Transformer transformer = transformerFactory.newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    DOMSource source = new DOMSource(document);
	    StreamResult result = new StreamResult(outputStream);
	    transformer.transform(source, result);
	} catch (TransformerException ex) {
	    throw new RuntimeException(ex);
	}

	toXMLHandler.startOpen(messageQueue);
	if (messageQueue.containsError()) {
	    return;
	}

	ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
	SAXParser parser;
	try {
	    parser = SAXParserFactory.newInstance().newSAXParser();
	} catch (ParserConfigurationException ex) {
	    throw new RuntimeException("Cannot create parser.", ex);
	} catch (SAXException ex) {
	    throw new RuntimeException("Cannot create parser.", ex);
	}

	try {
	    parser.parse(inputStream, toXMLHandler);
	} catch (SAXException ex) {

	} catch (IOException ex) {

	}
    }
}

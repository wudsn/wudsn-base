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

import static com.wudsn.tools.base.common.ByteArrayUtility.KB;
import static com.wudsn.tools.base.common.ByteArrayUtility.MB;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.filechooser.FileFilter;

import com.wudsn.tools.base.Messages;

/**
 * Utility class to access files and their content.
 * 
 * @author Peter Dell
 */
public final class FileUtility {

    /**
     * Extended file filter that provides access to the filter extension.
     * 
     */
    public final static class FileExtensionFileFilter extends javax.swing.filechooser.FileFilter {
	private String fileExtension;
	private String fullFilterDescription;

	FileExtensionFileFilter(String fileExtension, String fullFilterDescription) {
	    if (fileExtension == null) {
		throw new IllegalArgumentException("Parameter 'fileExtension' must not be null.");
	    }
	    if (fullFilterDescription == null) {
		throw new IllegalArgumentException("Parameter 'fullFilterDescription' must not be null.");
	    }
	    this.fileExtension = fileExtension;
	    this.fullFilterDescription = fullFilterDescription;
	}

	public String getFileExtension() {
	    return fileExtension;
	}

	@Override
	public boolean accept(File file) {
	    return file.isDirectory() || file.getName().endsWith(fileExtension);
	}

	@Override
	public String getDescription() {
	    return fullFilterDescription;
	}
    }

    /**
     * Intentionally read an unlimited amount of bytes.
     */
    public static final int MAX_SIZE_UNLIMITED = -1;

    /**
     * Intentionally read at most 1MB of bytes or chars.
     */
    public static final int MAX_SIZE_1MB = 1 * MB;

    /**
     * Buffer size for the bytes or chars.
     */
    private static final int BUFFER_SIZE = 1 * KB;

    /**
     * Creation is private,
     */
    private FileUtility() {
    }

    /**
     * Gets the (canonical) absolute path of a file.
     * 
     * @param filePath
     *            The file path, not <code>null</code>.
     * @return The canonical) absolute path of the file
     */
    public static String getAbsolutePath(String filePath) {
	if (filePath == null) {
	    throw new IllegalArgumentException("Parameter 'filePath' must not be null.");
	}
	File file = new File(filePath);
	String result;
	try {
	    result = file.getCanonicalPath();
	} catch (IOException ignore) {
	    result = file.getAbsolutePath();
	}
	return result;

    }

    /**
     * Creates a simple file extension based filter.
     * 
     * @param fileExtension
     *            The file extension, not empty and not <code>null</code>. Must
     *            start with a period.
     * @param filterDescription
     *            The filter description, not empty and not <code>null</code>.
     * @return
     */
    public static FileFilter createFileExtensionFileFilter(final String fileExtension, final String filterDescription) {
	if (fileExtension == null) {
	    throw new IllegalArgumentException("Parameter 'fileExtension' must not be null.");
	}
	if (StringUtility.isEmpty(fileExtension)) {
	    throw new IllegalArgumentException("Parameter 'fileExtension' must not be empty.");
	}
	if (fileExtension.charAt(0) != '.') {
	    throw new IllegalArgumentException("Parameter 'fileExtension' must start with a '.'");
	}
	if (filterDescription == null) {
	    throw new IllegalArgumentException("Parameter 'filterDescription' must not be null.");
	}
	if (StringUtility.isEmpty(filterDescription)) {
	    throw new IllegalArgumentException("Parameter 'filterDescription' must not be empty.");

	}
	final String fullFilterDescription = filterDescription + " (*" + fileExtension + ")";
	FileExtensionFileFilter fileExtensionFilefilter = new FileExtensionFileFilter(fileExtension,
		fullFilterDescription);
	return fileExtensionFilefilter;

    }

    /**
     * Normalizes a file extension to a given standard extension.
     * 
     * @param file
     *            The file, not <code>null</code>.
     * @param fileExtension
     *            The file extension, not empty and not <code>null</code>. Must
     *            start with a period.
     * @return The new file with normalized file extension.
     */
    public static File normalizeFileExtension(File file, String fileExtension) {
	if (file == null) {
	    throw new IllegalArgumentException("Parameter 'file' must not be null.");
	}
	if (fileExtension == null) {
	    throw new IllegalArgumentException("Parameter 'fileExtension' must not be null.");
	}
	if (StringUtility.isEmpty(fileExtension)) {
	    throw new IllegalArgumentException("Parameter 'fileExtension' must not be empty.");
	}
	if (fileExtension.charAt(0) != '.') {
	    throw new IllegalArgumentException("Parameter 'fileExtension' must start with a '.'");
	}
	if (!file.getName().toLowerCase().endsWith(fileExtension)) {
	    String filePath = file.getAbsolutePath();
	    int index = file.getName().lastIndexOf('.');
	    if (index > 0) {
		int strip = file.getName().length() - index;
		filePath = filePath.substring(0, filePath.length() - strip);
	    }
	    file = new File(filePath + fileExtension);
	}
	return file;
    }

    /**
     * Opens an input stream for reading.
     * 
     * @param ioFile
     *            The file, not <code>null</code>.
     * @return The input stream, not <code>null</code>.
     * @throws CoreException
     *             If the file does not exist or cannot be read.
     */
    public static InputStream openInputStream(File ioFile) throws CoreException {
	InputStream inputStream;
	String filePath;

	if (ioFile == null) {
	    throw new IllegalArgumentException("Parameter 'ioFile' must not be null.");
	}

	filePath = ioFile.getAbsolutePath();

	if (!ioFile.exists()) {
	    // ERROR: File '{0}' does not exist.
	    throw new CoreException(Messages.E203, filePath);
	}
	if (!ioFile.isFile()) {
	    // ERROR: '{0}' is no file but a folder.
	    throw new CoreException(Messages.E204, filePath);
	}
	try {
	    inputStream = new FileInputStream(ioFile);
	} catch (FileNotFoundException ex) {
	    // ERROR: Cannot open file '{0}' for reading. Original error
	    // message: {1}
	    throw new CoreException(Messages.E205, filePath, ex.getLocalizedMessage());
	}
	return inputStream;
    }

    /**
     * Reads the content of a file as byte array.
     * 
     * @param ioFile
     *            The file, not <code>null</code>.
     * @param maxSize
     *            The maximum number of character to read or
     *            {@link #MAX_SIZE_UNLIMITED}.
     * @param errorOnMaxSizeExceeded
     *            If <code>true</code>, an error will be thrown in case the
     *            specified maximum number of bytes is exceeded. If
     *            <code>false</code>, the content will be truncated to the
     *            specified maximum size (plus the internal buffer size).
     * 
     * @return The content of the file, may be empty, not <code>null</code>.
     * 
     * @throws CoreException
     *             If the file does not exist or cannot be read.
     */
    public static byte[] readBytes(File ioFile, long maxSize, boolean errorOnMaxSizeExceeded) throws CoreException {
	InputStream inputStream;

	if (ioFile == null) {
	    throw new IllegalArgumentException("Parameter 'ioFile' must not be null.");
	}

	inputStream = openInputStream(ioFile);

	return readBytes(ioFile.getAbsolutePath(), inputStream, maxSize, errorOnMaxSizeExceeded);

    }

    public static byte[] readBytes(String filePath, InputStream inputStream, long maxSize,
	    boolean errrorOnMaxSizeExceeded) throws CoreException {

	if (filePath == null) {
	    throw new IllegalArgumentException("Parameter 'filePath' must not be null.");
	}
	if (inputStream == null) {
	    throw new IllegalArgumentException("Parameter 'inputStream' must not be null.");
	}
	if (maxSize < MAX_SIZE_UNLIMITED) {
	    throw new IllegalArgumentException("Parameter 'maxSize' must not be less than " + MAX_SIZE_UNLIMITED + ".");
	}

	byte[] result;
	try {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();

	    int size = 0;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int count = 0;

	    do {
		count = inputStream.read(buffer);

		if (count > 0) {
		    bos.write(buffer, 0, count);

		    size += count;
		}
	    } while ((count > -1) && (maxSize == MAX_SIZE_UNLIMITED || size <= maxSize));

	    // Specified maximum size exceeded?
	    if (maxSize != MAX_SIZE_UNLIMITED && size > maxSize && errrorOnMaxSizeExceeded) {
		// ERROR: Content of file '{0}' exceeds the specified maximum
		// size of {1}.
		throw new CoreException(Messages.E207, filePath, TextUtility.formatAsMemorySize(maxSize));
	    }
	    bos.close();

	    result = bos.toByteArray();

	} catch (IOException ex) {
	    // ERROR: Cannot read content of file '{0}'. Original error message:
	    // {1}
	    throw new CoreException(Messages.E206, filePath, ex.getLocalizedMessage());

	} finally {
	    closeInputStream(new File(filePath), inputStream);
	}
	return result;
    }

    /**
     * Close an input stream.
     * 
     * @param ioFile
     *            The file, not <code>null</code>.
     * @param inputStream
     *            The input stream,not <code>null</code>.
     * @throws CoreException
     *             If the output stream cannot be closed.
     */
    public static void closeInputStream(File ioFile, InputStream inputStream) throws CoreException {

	if (ioFile == null) {
	    throw new IllegalArgumentException("Parameter 'ioFile' must not be null.");
	}
	if (inputStream == null) {
	    throw new IllegalArgumentException("Parameter 'inputStream' must not be null.");
	}

	try {
	    inputStream.close();
	} catch (IOException ex) {
	    // ERROR: Cannot close input stream of '{0}'. Original error
	    // message: {1}
	    throw new CoreException(Messages.E209, ioFile.getAbsolutePath(), ex.getLocalizedMessage());
	}
    }

    /**
     * Opens an output stream for writing.
     * 
     * @param ioFile
     *            The file, not <code>null</code>.
     * @return The output stream, not <code>null</code>.
     * @throws CoreException
     *             If the file does not exist or cannot be created or
     *             overwritten.
     */
    public static OutputStream openOutputStream(File ioFile) throws CoreException {
	OutputStream outputStream;
	String filePath;

	if (ioFile == null) {
	    throw new IllegalArgumentException("Parameter 'ioFile' must not be null.");
	}

	filePath = ioFile.getAbsolutePath();

	if (!ioFile.exists()) {
	    try {
		ioFile.createNewFile();
	    } catch (IOException ex) {
		// ERROR: Cannot create file '{0}'. Original error message: {1}
		throw new CoreException(Messages.E210, filePath, ex.getLocalizedMessage());
	    }
	}

	if (!ioFile.isFile()) {
	    // ERROR: '{0}' is no file but a folder.
	    throw new CoreException(Messages.E204, filePath);
	}
	try {
	    outputStream = new FileOutputStream(ioFile);
	} catch (FileNotFoundException ex) {
	    // ERROR: Cannot open file '{0}' for writing. Original error
	    // message: {1}
	    throw new CoreException(Messages.E211, filePath, ex.getLocalizedMessage());
	}
	return outputStream;
    }

    /**
     * Write a byte array to a file.
     * 
     * @param ioFile
     *            The file, not <code>null</code>.
     * @param content
     *            The content of the file, may be empty, not <code>null</code>.
     * @throws CoreException
     *             If the file does not exist or cannot be read.
     */
    public static void writeBytes(File ioFile, byte[] content) throws CoreException {
	OutputStream outputStream;

	if (ioFile == null) {
	    throw new IllegalArgumentException("Parameter 'ioFile' must not be null.");
	}
	if (content == null) {
	    throw new IllegalArgumentException("Parameter 'content' must not be null.");
	}

	outputStream = openOutputStream(ioFile);
	try {
	    writeBytes(ioFile, outputStream, content, 0, content.length);
	} finally {
	    closeOutputStream(ioFile, outputStream);
	}

    }

    /**
     * Write a byte array to a file.
     * 
     * @param ioFile
     *            The file, not <code>null</code>.
     * @param outputStream
     *            The output stream,not <code>null</code>.
     * @param content
     *            The content, not <code>null</code>.
     * @param offset
     *            The start offset from which the content is written, a
     *            non-negative integer.
     * @param length
     *            The number of bytes to write, a non-negative integer.
     * @throws CoreException
     *             If the file cannot be created or written.
     */
    public static void writeBytes(File ioFile, OutputStream outputStream, byte[] content, int offset, int length)
	    throws CoreException {

	if (ioFile == null) {
	    throw new IllegalArgumentException("Parameter 'ioFile' must not be null.");
	}
	if (outputStream == null) {
	    throw new IllegalArgumentException("Parameter 'outputStream' must not be null.");
	}
	if (content == null) {
	    throw new IllegalArgumentException("Parameter 'content' must not be null.");
	}
	if (offset < 0) {
	    throw new IllegalArgumentException("Parameter 'offset' must not be negative. Specified value is " + offset
		    + ".");
	}
	if (length < 0) {
	    throw new IllegalArgumentException("Parameter 'length' must not be negative. Specified value is " + length
		    + ".");
	}

	try {
	    outputStream.write(content, offset, length);
	} catch (IOException ex) {
	    // ERROR: Cannot write content of file '{0}'. Original error
	    // message: {1}
	    throw new CoreException(Messages.E212, ioFile.getAbsolutePath(), ex.getLocalizedMessage());
	}
    }

    /**
     * Close an output stream.
     * 
     * @param ioFile
     *            The file, not <code>null</code>.
     * @param outputStream
     *            The output stream,not <code>null</code>.
     * @throws CoreException
     *             If the output stream cannot be closed.
     */
    public static void closeOutputStream(File ioFile, OutputStream outputStream) throws CoreException {

	if (ioFile == null) {
	    throw new IllegalArgumentException("Parameter 'ioFile' must not be null.");
	}
	if (outputStream == null) {
	    throw new IllegalArgumentException("Parameter 'outputStream' must not be null.");
	}

	try {
	    outputStream.close();
	} catch (IOException ex) {
	    // ERROR: Cannot close output stream of '{0}'. Original error
	    // message: {1}
	    throw new CoreException(Messages.E213, ioFile.getAbsolutePath(), ex.getLocalizedMessage());
	}
    }

    /**
     * Reads the content of a file as string.
     * 
     * @param ioFile
     *            The file, not <code>null</code>.
     * @param maxSize
     *            The maximum number of character to read or
     *            {@link #MAX_SIZE_UNLIMITED}.
     * @return The content of the file, may be empty, not <code>null</code>.
     * @throws CoreException
     *             If the file does not exist or cannot be read.
     */
    public static String readString(File ioFile, long maxSize) throws CoreException {
	InputStream inputStream;
	String filePath;

	if (ioFile == null) {
	    throw new IllegalArgumentException("Parameter 'ioFile' must not be null.");
	}

	filePath = ioFile.getAbsolutePath();

	if (!ioFile.exists()) {
	    // ERROR: File '{0}' does not exist.
	    throw new CoreException(Messages.E203, filePath);
	}
	if (!ioFile.isFile()) {
	    // ERROR: '{0}' is no file but a folder.
	    throw new CoreException(Messages.E204, filePath);
	}
	try {
	    inputStream = new FileInputStream(ioFile);
	} catch (FileNotFoundException ex) {
	    // ERROR: Cannot open file '{0}' for reading. Original error
	    // message: {1}
	    throw new CoreException(Messages.E205, filePath, ex.getLocalizedMessage());
	}
	return readString(filePath, inputStream, maxSize);

    }

    /**
     * Reads a string from an input stream.
     * 
     * @param filePath
     *            The file path to be used in error messages.
     * @param inputStream
     *            The input stream, not <code>null</code>.
     * @param maxSize
     *            The maximum number of bytes to read, a non-negative integer or @link
     *            #MAX_SIZE_UNLIMITED}, see also {@link #MAX_SIZE_1MB}.
     * @return The string, may be empty, not <code>null</code>.
     * @throws CoreException
     *             in case the file cannot be read. the iNput stream has been
     *             closed in this case.
     */
    public static String readString(String filePath, InputStream inputStream, long maxSize) throws CoreException {

	if (filePath == null) {
	    throw new IllegalArgumentException("Parameter 'filePath' must not be null.");
	}
	if (inputStream == null) {
	    throw new IllegalArgumentException("Parameter 'inputStream' must not be null.");
	}
	if (maxSize < MAX_SIZE_UNLIMITED) {
	    throw new IllegalArgumentException("Parameter 'maxSize' must not be less than " + MAX_SIZE_UNLIMITED + ".");
	}

	StringBuilder result;
	result = new StringBuilder();
	try {
	    InputStreamReader reader = new InputStreamReader(inputStream);

	    int size = 0;
	    char[] buffer = new char[BUFFER_SIZE];
	    int count = 0;

	    do {
		count = reader.read(buffer);

		if (count > 0) {
		    result.append(buffer, 0, count);

		    size += count;
		}
	    } while ((count > -1) && (maxSize == MAX_SIZE_UNLIMITED || size <= maxSize));

	    // Specified maximum size exceeded?
	    if (maxSize != MAX_SIZE_UNLIMITED && size > maxSize) {
		// ERROR: Content of file '{0}' exceeds the specified maximum
		// size of {1} characters.
		throw new CoreException(Messages.E208, filePath, TextUtility.formatAsDecimal(maxSize));
	    }

	    reader.close();

	} catch (IOException ex) {
	    // ERROR: Cannot read content of file '{0}'. Original error message:
	    // {1}
	    throw new CoreException(Messages.E206, filePath, ex.getLocalizedMessage());
	} finally {
	    closeInputStream(new File(filePath), inputStream);
	}
	return result.toString();
    }

    /**
     * Write a string to a file.
     * 
     * @param ioFile
     *            The file, not <code>null</code>.
     * @param content
     *            The content of the file, may be empty, not <code>null</code>.
     * @throws CoreException
     *             If the file does not exist or cannot be read.
     */
    public static void writeString(File ioFile, String content) throws CoreException {
	OutputStream outputStream;
	String filePath;

	if (ioFile == null) {
	    throw new IllegalArgumentException("Parameter 'ioFile' must not be null.");
	}
	if (content == null) {
	    throw new IllegalArgumentException("Parameter 'content' must not be null.");
	}

	outputStream = openOutputStream(ioFile);
	filePath = ioFile.getAbsolutePath();
	writeString(filePath, outputStream, content);

    }

    private static void writeString(String filePath, OutputStream outputStream, String content) throws CoreException {

	if (filePath == null) {
	    throw new IllegalArgumentException("Parameter 'filePath' must not be null.");
	}
	if (outputStream == null) {
	    throw new IllegalArgumentException("Parameter 'outputStream' must not be null.");
	}
	if (content == null) {
	    throw new IllegalArgumentException("Parameter 'content' must not be null.");
	}

	try {
	    OutputStreamWriter writer = new OutputStreamWriter(outputStream);
	    writer.write(content);

	    writer.close();

	} catch (IOException ex) {
	    // ERROR: Cannot write content of file '{0}'. Original error
	    // message: {1}
	    throw new CoreException(Messages.E212, filePath, ex.getLocalizedMessage());
	} finally {
	    try {
		outputStream.close();
	    } catch (IOException ex) {
		// ERROR: Cannot close output stream of '{0}'. Original error
		// message: {1}
		throw new CoreException(Messages.E213, filePath, ex.getLocalizedMessage());
	    }
	}
	return;
    }

    public static void copyFile(File sourceFile, File targetFile) throws CoreException {
	if (sourceFile == null) {
	    throw new IllegalArgumentException("Parameter 'sourceFile' must not be null.");
	}
	if (targetFile == null) {
	    throw new IllegalArgumentException("Parameter 'targetFile' must not be null.");
	}
	InputStream inputStream = null;
	OutputStream outputStream = null;
	try {
	    inputStream = openInputStream(sourceFile);
	    outputStream = openOutputStream(targetFile);

	    byte[] buffer = new byte[BUFFER_SIZE];
	    int count = 0;
	    do {
		try {
		    count = inputStream.read(buffer);
		} catch (IOException ex) {
		    // ERROR: Cannot read content of file '{0}'. Original error
		    // message: {1}
		    throw new CoreException(Messages.E206, sourceFile.getAbsolutePath(), ex.getLocalizedMessage());
		}

		if (count > 0) {
		    try {
			outputStream.write(buffer, 0, count);
		    } catch (IOException ex) {
			// ERROR Cannot write content of file '{0}'. Original
			// error message: {1}
			throw new CoreException(Messages.E212, sourceFile.getAbsolutePath(), ex.getLocalizedMessage());
		    }
		}
	    } while (count > -1);

	    if (outputStream != null) {
		try {
		    closeOutputStream(targetFile, outputStream);
		} catch (CoreException ignore) {

		}
	    }

	    try {
		closeInputStream(sourceFile, inputStream);
	    } catch (CoreException ignore) {

	    }
	} catch (CoreException ex) {

	    if (outputStream != null) {
		try {
		    closeOutputStream(targetFile, outputStream);
		} catch (CoreException ignore) {

		}
	    }

	    if (inputStream != null) {
		try {
		    closeInputStream(sourceFile, inputStream);
		} catch (CoreException ignore) {

		}
	    }
	    throw ex;
	}
    }
}

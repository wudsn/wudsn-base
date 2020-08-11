package com.wudsn.tools.base.io;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wudsn.tools.base.common.ByteArrayUtility;

public final class CSVReader {

    public static final int MAX_LINE_SIZE = ByteArrayUtility.KB;
    private static final int CR = 13;
    private static final int LF = 10;

    private char separatorChar;
    private Reader reader;
    private int newLineChars;
    private Map<String, Integer> headerMap;
    private String[] columnHeaders;
    private String[] columnValues;
    private int rowNumber;

    public void open(File file, char separatorChar, String charsetName) {
	if (!file.exists()) {
	    throw new RuntimeException("File does not exist: "
		    + file.getAbsolutePath());
	}
	if (charsetName == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'charsetName' must not be null.");
	}
	try {
	    open(new FileInputStream(file), new FileInputStream(file),
		    separatorChar, charsetName);
	} catch (FileNotFoundException ex) {
	    throw new RuntimeException("File not found", ex);
	}
    }

    public void open(InputStream headerInputStream,
	    InputStream bodyInputStream, char separatorChar, String charsetName) {
	if (headerInputStream == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'headerInputStream' must not be null.");
	}
	if (bodyInputStream == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'bodyInputStream' must not be null.");
	}
	this.separatorChar = separatorChar;
	if (charsetName == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'charsetName' must not be null.");
	}

	newLineChars = 1;
	// Read one line first to determine newLineChars
	reader = new InputStreamReader(headerInputStream,
		Charset.forName(charsetName));
	try {
	    int c = reader.read();
	    while (c != -1) {
		if (c == CR || c == LF) {
		    c = reader.read();
		    if (c == CR || c == LF) {
			newLineChars++;
		    }
		    c = -1;
		} else {
		    c = reader.read();
		}
	    }
	} catch (IOException ex) {
	    throw new RuntimeException("Cannot read file", ex);

	}
	reader = new InputStreamReader(bodyInputStream,
		Charset.forName(charsetName));

	headerMap = new HashMap<String, Integer>();
	int columnIndex = 0;
	List<String> columnHeaderList = new ArrayList<String>();
	try {
	    StringBuilder builder = new StringBuilder();
	    boolean completed = false;
	    do {
		int c = reader.read();
		if (c == -1) {
		    completed = true;
		    reader.close();
		} else if (c == CR || c == LF) {
		    completed = true;
		    consumeNewLineChars();
		} else if (c == separatorChar) {
		    String columnHeader = builder.toString();
		    columnHeaderList.add(columnHeader);
		    headerMap.put(columnHeader, Integer.valueOf(columnIndex));
		    columnIndex++;
		    builder.setLength(0);
		} else {
		    builder.append((char) c);
		}
	    } while (!completed);
	    if (builder.length() > 0) {
		String columnHeader = builder.toString();
		columnHeaderList.add(columnHeader);
		headerMap.put(columnHeader, Integer.valueOf(columnIndex));
		columnIndex++;
	    }

	} catch (IOException ex) {
	    throw new RuntimeException("Cannot read file", ex);

	}
	columnHeaders = columnHeaderList.toArray(new String[columnIndex]);
	columnValues = new String[columnIndex];
	rowNumber = 1;
    }

    private void consumeNewLineChars() throws IOException {
	int c;
	if (newLineChars == 2) {
	    c = reader.read();
	    if (c != CR && c != LF) {
		throw new RuntimeException(
			"Inconsistent newlines in rowNumber " + rowNumber + ".");
	    }
	}
    }

    public int getRowNumber() {
	return rowNumber;
    }

    public int getColumnIndex(String columnName) {
	if (columnName == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'columnName' must not be null.");
	}
	Integer index = headerMap.get(columnName);
	if (index == null) {
	    throw new RuntimeException("Illegal column '" + columnName
		    + "'. Defined column names are: "
		    + headerMap.keySet().toString());
	}
	return index.intValue();
    }

    public boolean readNextRow() {
	StringBuilder builder = new StringBuilder();
	boolean completed = false;
	boolean eof = false;
	for (int i = 0; i < columnValues.length; i++) {
	    columnValues[i] = "";
	}
	int columnIndex = 0;
	boolean quoteMode = false;
	try {
	    do {
		int c = reader.read();
		if (c == -1) {
		    completed = true;
		    eof = true;
		} else {
		    if (quoteMode) {
			if (c == '"') {
			    quoteMode = false;
			} else {
			    builder.append((char) c);
			}
		    } else {
			if (c == CR || c == LF) {
			    completed = true;
			    consumeNewLineChars();
			    rowNumber++;
			} else if (c == separatorChar) {
			    String columnValue = builder.toString();
			    if (quoteMode) {
				columnValue = columnValue.substring(columnValue
					.length() - 1);
			    }
			    columnValues[columnIndex++] = columnValue;
			    quoteMode = false;
			    builder.setLength(0);
			} else {
			    if (builder.length() == 0 && c == '"') {
				quoteMode = true;
			    } else {
				builder.append((char) c);
			    }
			}
		    }
		}

	    } while (!completed);

	} catch (IOException ex) {
	    throw new RuntimeException("Cannot read file", ex);

	}
	if (builder.length() > 0) {
	    String columnValue = builder.toString();
	    if (quoteMode) {
		columnValue = columnValue.substring(columnValue.length() - 1);
	    }
	    if (columnIndex > columnValues.length - 1) {
		columnValues[columnIndex] = columnValue;
	    }
	    columnValues[columnIndex] = columnValue;
	}
	return !eof;
    }

    public String getColumnHeader(int index) {
	return columnHeaders[index];
    }

    public String getColumnValue(int index) {
	return columnValues[index];
    }

    public String getColumnValue(String columnName) {
	if (columnName == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'columnName' must not be null.");
	}
	int index = getColumnIndex(columnName);
	return columnValues[index];
    }

}

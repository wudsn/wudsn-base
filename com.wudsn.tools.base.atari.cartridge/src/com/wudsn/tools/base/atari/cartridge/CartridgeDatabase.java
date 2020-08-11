/**
 * Copyright (C) 2013 - 2018 <a href="http://www.wudsn.com" target="_top">Peter Dell</a>
 *
 * This file is part of The!Cart Studio distribution.
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
 * along with The!Cart Studio. If not, see <http://www.gnu.org/licenses/>.
 */

package com.wudsn.tools.base.atari.cartridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import com.wudsn.tools.base.atari.CartridgeType;
import com.wudsn.tools.base.common.CoreException;
import com.wudsn.tools.base.common.FileUtility;
import com.wudsn.tools.base.common.ResourceUtility;
import com.wudsn.tools.base.common.StringUtility;
import com.wudsn.tools.base.atari.cartridge.CartridgeDatabaseEntry.Key;

/**
 * Cartridge Database.
 * 
 * @author Peter Dell
 */
public final class CartridgeDatabase {

    private static final char QUOTE_CHAR = '\"';
    public final static String FILE_PATH = "data/CartridgeDatabase.csv";
    private final static char SEPARATOR_CHAR = ';';
    private final static String NEW_LINE_CHARS = "\r\n";

    private List<CartridgeDatabaseEntry> entries;
    private TreeMap<Key, List<CartridgeDatabaseEntry>> entriesBySizeAndCRC32;
    private TreeMap<String, CartridgeDatabaseEntry> entriesByMD5;
    private int knownCartridgeTypesCount;

    public CartridgeDatabase() {
	entries = new ArrayList<CartridgeDatabaseEntry>();
	entriesBySizeAndCRC32 = new TreeMap<Key, List<CartridgeDatabaseEntry>>();
	entriesByMD5 = new TreeMap<String, CartridgeDatabaseEntry>();
    }

    private void clear() {
	entries.clear();
	entriesBySizeAndCRC32.clear();
	entriesByMD5.clear();
	knownCartridgeTypesCount = 0;
    }

    public List<CartridgeDatabaseEntry> getEntries() {
	return Collections.unmodifiableList(entries);
    }

    public int getKnownTitelsCount() {
	return entries.size();
    }

    public int getKnownCartridgeTypesCount() {
	return knownCartridgeTypesCount;
    }

    public List<CartridgeDatabaseEntry> getEntriesBySizeAndCRC32(int sizeInKB,
	    long crc32) {
	Key key = new Key(sizeInKB, crc32);
	List<CartridgeDatabaseEntry> result = entriesBySizeAndCRC32.get(key);
	if (result != null) {
	    result = Collections.unmodifiableList(result);
	} else {
	    result = Collections.emptyList();
	}
	return result;
    }

    public CartridgeDatabaseEntry getEntryByMD5(String md5) {
	if (md5 == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'md5' must not be null.");
	}
	return entriesByMD5.get(md5);
    }

    public CartridgeDatabaseEntry addEntry(int sizeInKB, boolean hasCRC32,
	    long crc32, String md5, String title, String publisher,
	    String date, CartridgeType cartridgeType, String source) {
	CartridgeDatabaseEntry entry = new CartridgeDatabaseEntry(sizeInKB,
		crc32, md5, title, publisher, date, cartridgeType, source);
	entries.add(entry);
	if (hasCRC32) {
	    Key key = entry.getKey();
	    List<CartridgeDatabaseEntry> entries = entriesBySizeAndCRC32
		    .get(key);
	    if (entries == null) {
		entries = new ArrayList<CartridgeDatabaseEntry>();
		entriesBySizeAndCRC32.put(key, entries);
	    }
	    entries.add(entry);
	    // System.out.println("List " + System.identityHashCode(entries)
	    // + " for " + key + " as " + entries.size() + " entries");

	}
	if (StringUtility.isSpecified(md5)) {
	    entriesByMD5.put(entry.getMD5HexString(), entry);
	}
	if (entry.getCartridgeType() != CartridgeType.UNKNOWN) {
	    knownCartridgeTypesCount++;
	}

	return entry;
    }

    public void load() {
	clear();

	String content = ResourceUtility.loadResourceAsString(FILE_PATH);
	if (content == null) {
	    return;
	}
	BufferedReader reader = new BufferedReader(new StringReader(content));
	String line = "";
	try {
	    reader.readLine(); // Skip "sep="
	    while ((line = reader.readLine()) != null) {
		int index1 = line.indexOf(SEPARATOR_CHAR);
		int index2 = line.indexOf(SEPARATOR_CHAR, index1 + 1);
		int index3 = line.indexOf(SEPARATOR_CHAR, index2 + 1);
		int index4 = line.indexOf(QUOTE_CHAR, index3 + 2);
		int index5 = line.indexOf(QUOTE_CHAR, index4 + 3);
		int index6 = line.indexOf(QUOTE_CHAR, index5 + 3);
		int index7 = line.indexOf(SEPARATOR_CHAR, index6 + 2);

		String sizeInKBValue = line.substring(0, index1);
		int sizeInKB = Integer.parseInt(sizeInKBValue);
		String crc32Value = line.substring(index1 + 2, index2);
		boolean hasCRC32 = StringUtility.isSpecified(crc32Value)
			&& crc32Value.startsWith("0x");
		long crc32 = 0;
		if (hasCRC32) {
		    crc32 = Long.parseLong(crc32Value.substring(2), 16);
		}
		String md5 = line.substring(index2 + 2, index3);
		String title = line.substring(index3 + 2, index4);
		String publisher = line.substring(index4 + 3, index5);
		String date = line.substring(index5 + 3, index6);

		String cartridgeTypeId = line.substring(index6 + 2, index7);
		CartridgeType cartridgeType = CartridgeType
			.getInstance(cartridgeTypeId);
		if (cartridgeType == null) {
		    throw new IllegalStateException(
			    "No cartrigde type for numeric ID "
				    + cartridgeTypeId);
		}
		String source = line.substring(index5 + 1);
		addEntry(sizeInKB, hasCRC32, crc32, md5, title, publisher,
			date, cartridgeType, source);

	    }
	} catch (StringIndexOutOfBoundsException ex) {
	    throw new RuntimeException("Error while processing line: '" + line
		    + "'", ex);
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}
	// System.out.println(entriesByCRC32.values().toString());
    }

    public void save(File file) throws CoreException {
	if (file == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'file' must not be null.");
	}

	StringWriter writer = new StringWriter();
	PrintWriter printWriter = new PrintWriter(writer);
	printWriter.println("sep=" + SEPARATOR_CHAR);
	try {
	    for (CartridgeDatabaseEntry entry : entries) {
		printWriter.print(entry.getSizeInKB());
		printWriter.print(SEPARATOR_CHAR);
		printWriter.print("'");
		printWriter.print(entry.getCRC32HexString());
		printWriter.print(SEPARATOR_CHAR);
		printWriter.print("'");
		printWriter.print(entry.getMD5HexString());
		printWriter.print(SEPARATOR_CHAR);
		printWriter.print(QUOTE_CHAR);
		printWriter.print(entry.getTitle());
		printWriter.print(QUOTE_CHAR);
		printWriter.print(SEPARATOR_CHAR);
		printWriter.print(QUOTE_CHAR);
		printWriter.print(entry.getPublisher());
		printWriter.print(QUOTE_CHAR);
		printWriter.print(SEPARATOR_CHAR);
		printWriter.print(QUOTE_CHAR);
		printWriter.print(entry.getDate());
		printWriter.print(QUOTE_CHAR);
		printWriter.print(SEPARATOR_CHAR);
		printWriter.print(entry.getCartridgeType().getId());
		printWriter.print(SEPARATOR_CHAR);
		printWriter.print(entry.getSource());
		printWriter.print(NEW_LINE_CHARS);
	    }
	    writer.close();
	} catch (IOException ex) {
	    throw new RuntimeException("Error during serialization", ex);
	}
	FileUtility.writeString(file, writer.toString());
    }
}

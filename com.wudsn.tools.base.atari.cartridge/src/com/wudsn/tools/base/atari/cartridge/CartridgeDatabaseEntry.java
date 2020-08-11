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

import com.wudsn.tools.base.atari.CartridgeType;
import com.wudsn.tools.base.common.HexUtility;
import com.wudsn.tools.base.repository.Attribute;

public final class CartridgeDatabaseEntry {

    public static final class Attributes {
	private Attributes() {
	}

	public static final String ELEMENT_NAME = "entry";

	public static final Attribute TITLE = new Attribute("title",
		DataTypes.CartridgeDatabaseEntry_Title);

    }

    public static final class Key implements Comparable<Key> {
	private final int sizeInKB;
	private final long crc32;

	public Key(int sizeInKB, long crc32) {
	    this.sizeInKB = sizeInKB;
	    this.crc32 = crc32;
	}

	@Override
	public int hashCode() {
	    return sizeInKB + (int) crc32;
	}

	@Override
	public int compareTo(Key o) {
	    int result;
	    result = sizeInKB - o.sizeInKB;
	    if (result == 0) {
		if (crc32 != o.crc32) {
		    if (crc32 > o.crc32) {
			result = 1;
		    } else {
			result = -1;
		    }
		} else {
		    return 0;
		}
	    }
	    return result;
	}

	@Override
	public boolean equals(Object o) {
	    if (o instanceof Key) {
		Key other = (Key) o;
		return sizeInKB == other.sizeInKB && crc32 == other.crc32;
	    }
	    return false;
	}

	@Override
	public String toString() {
	    return "sizeInKB=" + sizeInKB + " crc32=0x"
		    + HexUtility.getLongValueHexString(crc32, 8);
	}
    }

    private int sizeInKB;
    private long crc32;
    private String md5HexString;
    private String title;
    private String publisher;
    private String date;
    private CartridgeType cartridgeType;
    private String source;

    CartridgeDatabaseEntry(int sizeInKB, long crc32, String md5, String title,
	    String publisher, String date, CartridgeType cartridgeType,
	    String source) {
	if (sizeInKB < 0) {
	    throw new IllegalArgumentException(
		    "Parameter 'sizeInK' must not be negative. Specified value is "
			    + sizeInKB + ".");
	}
	if (md5 == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'md5' must not be null.");
	}
	if (title == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'title' must not be null.");
	}
	if (publisher == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'publisher' must not be null.");
	}
	if (date == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'date' must not be null.");
	}
	if (cartridgeType == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'cartridgeType' must not be null.");
	}
	if (source == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'source' must not be null.");
	}
	this.sizeInKB = sizeInKB;
	this.crc32 = crc32;
	this.md5HexString = md5;
	this.title = title;
	this.publisher = publisher;
	this.date = date;
	this.cartridgeType = cartridgeType;
	this.source = source;
    }

    public int getSizeInKB() {
	return sizeInKB;
    }

    public long getCRC32() {
	return crc32;
    }

    public Key getKey() {
	return new Key(sizeInKB, crc32);
    }

    public String getCRC32HexString() {
	return "0x" + HexUtility.getLongValueHexString(crc32, 8);
    }

    public String getMD5HexString() {
	return md5HexString;
    }

    /**
     * Gets the title of the entry. In the database the title as no length
     * restriction, so it has to be truncated if required.
     * 
     * @return The title of the entry, may be empty, not <code>null</code>.
     */
    public String getTitle() {
	return title;
    }

    public String getPublisher() {

	return publisher;
    }

    public String getDate() {

	return date;
    }

    public CartridgeType getCartridgeType() {
	return cartridgeType;
    }

    public String getSource() {
	return source;
    }

    @Override
    public String toString() {
	return "sizeInKB=" + sizeInKB + " crc32=" + getCRC32HexString()
		+ " md5=" + md5HexString + " title=" + title
		+ " cartridgeType=" + cartridgeType.getId() + " source="
		+ source;
    }

}
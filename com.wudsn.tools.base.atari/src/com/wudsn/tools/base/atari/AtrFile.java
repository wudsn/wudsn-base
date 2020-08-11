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

package com.wudsn.tools.base.atari;

import static com.wudsn.tools.base.common.ByteArrayUtility.MASK_FF;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.wudsn.tools.base.common.StringUtility;

/**
 * Utility class to handle ATR image files.<br/>
 */
public final class AtrFile {

    public static enum DosVersion {
	UNKNOWN, ATARI_DOS_10, ATARI_DOS_20S, ATARI_DOS_25, ATARI_DOS_30, MYDOS, SPARTADOS, REALDOS
    }

    @SuppressWarnings("serial")
    public final static class AtrException extends Exception {
	public AtrException(String text) {
	    super(text);
	}
    }

    public static final class DirectoryEntry {
	private String directoryFileName;
	private String dosFileName;
	private int startSector;

	/**
	 * Creates a new directory entry.
	 * 
	 * @param directoryFileName
	 *            The file name as in the directory, i.e. 11 characters, not
	 *            trimmed, no ".", not <code>null</code>.
	 * @param dosFileName
	 *            The file in "8.3" notation, i.e. 1..12 characters,
	 *            trimmed, with ".", not <code>null</code>.
	 * @param startSector
	 */
	DirectoryEntry(String directoryFileName, String dosFileName,
		int startSector) {
	    this.directoryFileName = directoryFileName;
	    this.dosFileName = dosFileName;
	    this.startSector = startSector;
	}

	@Override
	public String toString() {
	    return "directoryFileName=" + directoryFileName + ", dosFileName="
		    + dosFileName + " startSector=" + startSector;
	}

	public int getStartSector() {
	    return startSector;
	}
    }

    // Sector sizes of the actual disk sectors.
    public static final int SECTOR_SIZE_8K = 8192;
    public static final int SECTOR_SIZE_SD = 128;
    public static final int SECTOR_SIZE_DD = 256;

    // Size of the ATR header and the ATR paragraph.
    public static final int HEADER_SIZE = 16;
    public static final int PARAGRAPH_SIZE = 16;
    public static final int MAXIMUM_SIZE = 65536 * SECTOR_SIZE_8K;

    // First 3 single density sectors, 128 bytes each
    public static final int BOOT_SECTORS = 3;
    public static final int BOOT_SECTORS_SIZE_SD = BOOT_SECTORS
	    * SECTOR_SIZE_SD;

    private static final byte[] bootCode = { 0, 1, // 1 sector
	    0, 7, // load address $0700
	    7, 7, // return address $0707
	    0x38, // sec
	    0x60 // rts
    };

    // Instance variables.
    private final byte[] data;
    private final int sectorSize;
    private final int sectorCount;
    private final DosVersion dosVersion;

    /**
     * Creates an ATR file proxy.
     * 
     * @param data
     *            The content of a valid ATR file, not <code>null</code>.
     */
    private AtrFile(byte[] data) {
	if (data == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'data' must not be null.");
	}
	if (!isHeader(data)) {
	    throw new IllegalArgumentException(
		    "Parameter 'data' is not a valid ATR file");
	}
	this.data = data;

	sectorSize = (data[4] & MASK_FF) + ((data[5] & MASK_FF) << 8);

	int paragraphs = (data[2] & MASK_FF);
	paragraphs += (data[3] & MASK_FF) << 8;
	paragraphs += (data[6] & MASK_FF) << 16;
	paragraphs += (data[7] & MASK_FF) << 24;
	int dataSize = paragraphs * PARAGRAPH_SIZE;
	if (data.length >= BOOT_SECTORS_SIZE_SD) {
	    sectorCount = (data.length - BOOT_SECTORS_SIZE_SD) / sectorSize
		    + BOOT_SECTORS;
	} else {
	    sectorCount = dataSize / SECTOR_SIZE_SD;
	}

	// From http://atari.kensclassics.org/dos.htm
	int vtocSector = 0x168;
	DosVersion dosVersion = DosVersion.UNKNOWN;
	if (sectorCount > vtocSector) {

	    int bootSectorOffset;
	    int vtocSectorOffset;
	    try {
		bootSectorOffset = getSectorStartOffset(1);
		vtocSectorOffset = getSectorStartOffset(vtocSector);
	    } catch (AtrException ex) {
		throw new RuntimeException(ex);
	    }
	    int vtoc0 = data[vtocSectorOffset] & MASK_FF;

	    // From http://guryus.tripod.com/hints.htm, 1995 = 0x7cb
	    int offset1995 = HEADER_SIZE + 0x7cb - 0x700;
	    int peek1995 = data[offset1995] & MASK_FF;

	    switch (vtoc0) {
	    case 1:
		dosVersion = DosVersion.ATARI_DOS_10;
		break;
	    case 2:
		switch (peek1995) {
		case 170:
		    dosVersion = DosVersion.ATARI_DOS_20S;
		    break;
		case 100:
		    dosVersion = DosVersion.ATARI_DOS_25;
		    break;
		default:
		    if (sectorCount == 720) {
			dosVersion = DosVersion.ATARI_DOS_20S;
		    } else if (sectorCount == 1040) {
			dosVersion = DosVersion.ATARI_DOS_25;
		    }
		    break;
		}
		break;
	    default:
		if (startsWith(bootSectorOffset, new byte[] { 0x01, 0x09, 0x00,
			0x32, 0x06, 0x32 })) {
		    dosVersion = DosVersion.ATARI_DOS_30;
		} else if (startsWith(bootSectorOffset, new byte[] { 0x4d,
			0x03, 0x00, 0x07 })) {
		    dosVersion = DosVersion.MYDOS;
		} else if (startsWith(bootSectorOffset, new byte[] { 0x00,
			0x03, 0x00, 0x07, 0x40, 0x07, 0x4c, (byte) 0x80, 0x07 })) {
		    dosVersion = DosVersion.SPARTADOS;
		} else if (startsWith(bootSectorOffset, new byte[] { 0x00,
			0x03, 0x00, 0x030, (byte) 0xe0, 0x07, 0x4c,
			(byte) 0x80, 0x30 })) {
		    dosVersion = DosVersion.REALDOS;
		}

	    }

	}
	this.dosVersion = dosVersion;
    }

    private boolean startsWith(int offset, byte[] data) {
	if (data == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'data' must not be null.");
	}
	for (int i = 0; i < data.length; i++) {
	    if (this.data[offset + i] != data[i]) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Creates an ATR file header. The following data sizes are used according
     * to SIO2PC readme:
     * <ul>
     * <li>WORD - 16bit unsigned short (little endian)</li>
     * <li>BYTE - 8bit unsigned char</li>
     * </ul>
     * 
     * The header has the following structure:
     * <ul>
     * <li>WORD wMagic $0296 (sum of 'NICKATARI')
     * <li>WORD = special code* indicating this is an Atari disk file</li>
     * <li>WORD = size of this disk image, in paragraphs (size/16)</li>
     * <li>WORD = sector size. (128 or 256) bytes/sector</li>
     * <li>WORD = high part of size, in paragraphs (added by REV 3.00)</li>
     * <li>BYTE = disk flags such as copy protection and write protect; see copy
     * protection chapter.</li>
     * <li>WORD =1st (or typical) bad sector; see copy protection chapter.</li>
     * <li>SPARES 5 unused (spare) header bytes (contain zeroes)</li>
     * 
     * </ul>
     * 
     * @param dataSize
     *            The content length. A positive integer which must be a
     *            multiple of the sector size and a multiple of 16.
     * @param sectorSize
     *            The sector size, a positive integer greater or equal to 128.
     * 
     * @return The file header, not <code>null</code>.
     */
    public static byte[] createHeader(int dataSize, int sectorSize) {

	if (sectorSize < 128) {
	    throw new IllegalArgumentException(
		    "Parameter 'sectorSize' must not be less than 128, actual value was "
			    + sectorSize + ".");
	}
	if (sectorSize > 65535) {
	    throw new IllegalArgumentException(
		    "Parameter 'sectorSize' must not be largen than 65535, actual value was "
			    + sectorSize + ".");
	}

	// Round everything except the boot sectors up to full sectors.
	if (sectorSize == SECTOR_SIZE_DD && dataSize >= BOOT_SECTORS_SIZE_SD) {
	    dataSize = ((dataSize - BOOT_SECTORS_SIZE_SD + sectorSize - 1) / sectorSize)
		    * sectorSize + BOOT_SECTORS_SIZE_SD;
	} else {
	    dataSize = ((dataSize + sectorSize - 1) / sectorSize) * sectorSize;
	}
	if ((dataSize % PARAGRAPH_SIZE) != 0) {
	    throw new IllegalArgumentException(
		    "Parameter 'content' has effective size " + dataSize
			    + " which is not a multiple of the paragraph size "
			    + PARAGRAPH_SIZE + ".");
	}
	int paragraphs = dataSize / PARAGRAPH_SIZE;
	byte[] header = new byte[HEADER_SIZE];
	header[0] = (byte) 0x96;
	header[1] = (byte) 0x02;

	header[4] = (byte) (sectorSize & 0xff);
	header[5] = (byte) ((sectorSize >>> 8) & 0xff);

	header[2] = (byte) (paragraphs & 0xff);
	header[3] = (byte) ((paragraphs >>> 8) & 0xff);
	header[6] = (byte) ((paragraphs >>> 16) & 0xff);
	header[7] = (byte) ((paragraphs >>> 24) & 0xff);

	header[8] = (byte) 0x00;
	header[9] = (byte) 0x00;
	header[10] = (byte) 0x00;

	header[11] = (byte) 0x00;
	header[12] = (byte) 0x00;
	header[13] = (byte) 0x00;
	header[14] = (byte) 0x00;
	header[15] = (byte) 0x00;
	return header;
    }

    /**
     * Determines if a byte array contains a valid ATR header
     * 
     * @param data
     *            The data, not <code>null</code>.
     * @return <code>true</code> If data has the required length and starts with
     *         the ATR magic bytes, <code>false</code> otherwise.
     */
    public static boolean isHeader(byte[] data) {
	if (data == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'data' must not be null.");
	}
	if (data.length >= 16 && data[0] == (byte) 0x96
		&& data[1] == (byte) 0x02) {
	    return true;
	}
	return false;
    }

    /**
     * Create default boot sectors. Boot sectors are always in single density.
     * 
     * @return The default boot sectors, not <code>null</code>.
     */
    public static byte[] createBootSectors() {
	byte[] result = new byte[BOOT_SECTORS_SIZE_SD];
	System.arraycopy(bootCode, 0, result, 0, bootCode.length);
	return result;
    }

    /**
     * Creates an ATR file proxy.
     * 
     * @param data
     *            The content of a valid ATR file, not <code>null</code>.
     * @return The instance, not <code>null</code>.
     * @throws AtrException
     *             If the data contains an invalid ATR.
     */
    public static AtrFile createInstance(byte[] data) throws AtrException {
	if (data == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'data' must not be null.");
	}
	if (!isHeader(data)) {
	    throw new AtrException("Parameter 'data' is not a valid ATR file");
	}
	AtrFile result = new AtrFile(data);
	return result;
    }

    /**
     * Gets the sector count of the disk.
     * 
     * @return The sector count of the disk, a positive integer.
     */
    public int getSectorCount() {
	return sectorCount;
    }

    /**
     * Gets the size of a sector in bytes.
     * 
     * @param sectorNumber
     *            The sector number, a positive integer.
     * @return The size of a sector in bytes, a positive integer.
     * @throws AtrException
     *             If the sector number is invalid.
     */
    public int getSectorSize(int sectorNumber) throws AtrException {
	if (sectorNumber <= 0) {
	    throw new AtrException(
		    "Parameter 'sectorNumber' must be positive. Specified value is "
			    + sectorNumber + ".");
	}
	if (sectorNumber > sectorCount) {
	    throw new AtrException(
		    "Parameter 'sectorNumber' must be less than the maximum sector "
			    + sectorCount + ". Specified value is "
			    + sectorNumber + ".");
	}
	int sectorSize;
	if (sectorNumber <= BOOT_SECTORS) {
	    sectorSize = SECTOR_SIZE_SD;
	} else {
	    sectorSize = this.sectorSize;

	}
	return sectorSize;
    }

    public int getSectorStartOffset(int sectorNumber) throws AtrException {
	if (sectorNumber <= 0) {
	    throw new AtrException(
		    "Parameter 'sectorNumber' must be positive. Specified value is "
			    + sectorNumber + ".");
	}
	if (sectorNumber > sectorCount) {
	    throw new AtrException(
		    "Parameter 'sectorNumber' must be less than the maximum sector "
			    + sectorCount + ". Specified value is "
			    + sectorNumber + ".");
	}
	int sectorSize = getSectorSize(sectorNumber);
	int offset = HEADER_SIZE;
	if (sectorNumber <= BOOT_SECTORS) {
	    offset += (sectorNumber - 1) * SECTOR_SIZE_SD;
	} else {
	    offset += (sectorNumber - 1 - BOOT_SECTORS) * sectorSize
		    + BOOT_SECTORS_SIZE_SD;
	}
	return offset;
    }

    public byte[] getSector(int sectorNumber) throws AtrException {
	if (sectorNumber <= 0) {
	    throw new AtrException(
		    "Parameter 'sectorNumber' must be positive. Specified value is "
			    + sectorNumber + ".");
	}
	if (sectorNumber > sectorCount) {
	    throw new AtrException(
		    "Parameter 'sectorNumber' must be less than the maximum sector "
			    + sectorCount + ". Specified value is "
			    + sectorNumber + ".");
	}
	int sectorSize = getSectorSize(sectorNumber);
	byte[] sector = new byte[sectorSize];
	int offset = getSectorStartOffset(sectorNumber);
	System.arraycopy(data, offset, sector, 0, sectorSize);
	return sector;
    }

    public byte[] getSectors(int startSectorNumber, int endSectorNumber,
	    int sectorSize) throws AtrException {
	if (startSectorNumber <= 0) {
	    throw new AtrException(
		    "Parameter 'sectorNumber' must be positive. Specified value is "
			    + startSectorNumber + ".");
	}
	if (startSectorNumber > sectorCount) {
	    throw new AtrException(
		    "Parameter 'sectorNumber' must be less than the maximum sector "
			    + sectorCount + ". Specified value is "
			    + startSectorNumber + ".");
	}
	if (endSectorNumber <= 0) {
	    throw new AtrException(
		    "Parameter 'endSectorNumber' must be positive. Specified value is "
			    + endSectorNumber + ".");
	}
	if (endSectorNumber > sectorCount) {
	    throw new AtrException(
		    "Parameter 'sectorNumber' must be less than the maximum sector "
			    + sectorCount + ". Specified value is "
			    + endSectorNumber + ".");
	}

	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	for (int sectorNumber = startSectorNumber; sectorNumber <= endSectorNumber; sectorNumber++) {
	    bos.write(getSector(sectorNumber), 0, sectorSize);
	}
	return bos.toByteArray();
    }

    public boolean hasDirectory() {
	switch (dosVersion) {
	case ATARI_DOS_20S:
	case ATARI_DOS_25:
	case MYDOS:
	    return true;
	default:
	    return false;
	}
    }

    public List<DirectoryEntry> getDirectory() throws AtrException {
	return getDirectory("");

    }

    /**
     * Gets the binary content of a file.
     * 
     * @param fileName
     *            The file name in "8.3" notation, not <code>null</code>.
     * @param usedSectors
     *            The list of sector numbers for used sectors or
     *            <code>null</code> .
     * @return The binary content of the file or <code>null</code> if the file
     *         does not exist.
     * @throws AtrException
     *             If an error during reading occurs.
     */
    public byte[] getFileContent(String fileName, List<Integer> usedSectors)
	    throws AtrException {
	if (StringUtility.isEmpty(fileName)) {
	    return null;
	}
	List<DirectoryEntry> entries = getDirectory(fileName);
	if (entries.isEmpty()) {
	    return null;
	}
	DirectoryEntry directoryEntry = entries.get(0);
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	int sectorNumber = directoryEntry.getStartSector();
	while (sectorNumber != 0) {
	    if (usedSectors != null) {
		usedSectors.add(Integer.valueOf(sectorNumber));
	    }

	    byte[] sector = getSector(sectorNumber);
	    int sectorSize = sector.length;
	    int bytesUsed = sector[sectorSize - 1] & MASK_FF;
	    // The upper 6 bits of the high byte are the file
	    // number. Only the lower 2 bits are part of the
	    // sector number.
	    int nextSectorLow = (sector[sectorSize - 2] & MASK_FF);
	    int nextSectorHigh = (sector[sectorSize - 3] & MASK_FF & 0x03);
	    int nextSector = nextSectorLow + (nextSectorHigh << 8);

	    // For a 128 bytes byte, the structure is 125 data bytes and the
	    // last 3 contain the link to next sector
	    // and file # (which is 6 bits), limiting to 64 files per disk

	    bos.write(sector, 0, bytesUsed);
	    sectorNumber = nextSector;
	}
	return bos.toByteArray();
    }

    private List<DirectoryEntry> getDirectory(String fileNameFilter)
	    throws AtrException {
	if (fileNameFilter == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'fileNameFilter' must not be null.");
	}
	if (!hasDirectory()) {
	    throw new AtrException(
		    "Cannot get directory of unknown or unsupported DOS version "
			    + dosVersion + ".");
	}

	List<DirectoryEntry> result = new ArrayList<DirectoryEntry>();
	int sectorNumber = 361;
	int entryWidth = 16;

	boolean complete = false;
	while (!complete) {
	    byte[] sector = getSector(sectorNumber);
	    int sectorSize = getSectorSize(sectorNumber);
	    // Only the 128 bytes of the directory sectors are use, irrespective
	    // of the density.
	    int relevantSectorSize = Math.min(AtrFile.SECTOR_SIZE_SD,
		    sectorSize);
	    int offset = 0;
	    while (offset < relevantSectorSize && !complete) {
		byte status = sector[offset];
		if (status == 0x00) {
		    complete = true;
		} else {
		    // Entry not deleted and in use?
		    if ((status & 0x80) == 0x00 && (status & 0x40) != 0x00) {
			byte[] fileNameArray = new byte[8];
			System.arraycopy(sector, offset + 5, fileNameArray, 0,
				8);
			byte[] extensionArray = new byte[3];
			String fileName = new String(fileNameArray);
			System.arraycopy(sector, offset + 5 + 8,
				extensionArray, 0, 3);
			String extension = new String(extensionArray);
			String dosFileName = fileName.trim() + "."
				+ extension.trim();

			if (StringUtility.isEmpty(fileNameFilter)
				|| dosFileName.equals(fileNameFilter)) {
			    int startSectorLow = (sector[offset + 3] & MASK_FF);
			    int startSectorHigh = (sector[offset + 4] & MASK_FF);
			    int startSector = startSectorLow
				    + (startSectorHigh << 8);

			    DirectoryEntry directoryEntry = new DirectoryEntry(
				    fileName + extension, dosFileName,
				    startSector);
			    result.add(directoryEntry);
			}
		    }
		}
		offset += entryWidth;
	    }
	    // Skip unused parts.
	    offset += (sectorSize - relevantSectorSize);
	    sectorNumber++;

	}
	return result;
    }

    @Override
    public String toString() {
	return "sectorSize=" + sectorSize + ", sectorCount=" + sectorCount
		+ ", DOS=" + dosVersion;
    }
}

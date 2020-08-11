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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.wudsn.tools.base.atari.CartridgeType;
import com.wudsn.tools.base.common.CoreException;
import com.wudsn.tools.base.common.HexUtility;
import com.wudsn.tools.base.common.Main;
import com.wudsn.tools.base.common.StringUtility;
import com.wudsn.tools.base.common.TextUtility;
import com.wudsn.tools.base.io.CSVReader;

/**
 * CRC Database Creator.
 * 
 * @author Peter Dell
 */
public final class CartridgeDatabaseCreator extends Main {

    private boolean logSkipped;

    /**
     * XML handler
     * 
     * @author Peter Dell
     * 
     */
    private final static class XMLHandler extends DefaultHandler {
	private int nesting = 0;

	public XMLHandler() {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
		org.xml.sax.Attributes attributes) throws SAXException {
	    if (qName == null) {
		throw new IllegalArgumentException(
			"Parameter 'qName' must not be null.");
	    }
	    if (attributes == null) {
		throw new IllegalArgumentException(
			"Parameter 'attributes' must not be null.");
	    }
	    if (qName.equals("table")) {
		nesting++;
		println("Table " + attributes.getValue("id") + " at " + nesting);

	    }
	}

	@Override
	public void endElement(String uri, String localName, String qName)
		throws SAXException {
	    if (qName.equals("table")) {
		nesting--;
	    }
	}

    }

    private final static String BASE_DIR = "C:\\jac\\system\\Java\\Programming\\Workspaces\\Productions\\com.wudsn.tools.base.atari.cartridge\\";
    private final static String KROTKI_LIST = BASE_DIR
	    + "tst\\crc\\Atari 8-bit ROM list\\List - Comparison.csv";
    private final static String MAXFLASH_LIST = BASE_DIR
	    + "tst\\crc\\Maxflash\\romlib.ini";
    private final static String DATABASE_FILE = BASE_DIR + "src\\"
	    + CartridgeDatabase.FILE_PATH;

    private Map<String, CartridgeType> mappings;

    public static void main(String[] args) {
	new CartridgeDatabaseCreator().run();
    }

    private CartridgeDatabaseCreator() {
	mappings = new TreeMap<String, CartridgeType>();
	createMappings();
    }

    private void addMapping(String mappingValue, CartridgeType cartridgeType) {
	if (mappingValue == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'mappingValue' must not be null.");
	}
	if (cartridgeType == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'cartridgeType' must not be null.");
	}
	mappings.put(mappingValue, cartridgeType);
    }

    private void createMappings() {
	addMapping("1024KB/ATARIMAX", CartridgeType.CARTRIDGE_ATMAX_1024);
	addMapping("1024KB/ULTIMATE1MB", CartridgeType.UNKNOWN);
	addMapping("1032KB/CORINA EEPROM", CartridgeType.UNKNOWN);
	addMapping("128KB/-", CartridgeType.UNKNOWN);
	addMapping("128KB/ATARIMAX", CartridgeType.CARTRIDGE_ATMAX_128);
	addMapping("128KB/ATRAX", CartridgeType.CARTRIDGE_ATRAX_128);
	addMapping("128KB/ATRAX SDX", CartridgeType.CARTRIDGE_ATRAX_SDX_128);
	addMapping("128KB/DECODED ATRAX", CartridgeType.CARTRIDGE_ATRAX_DEC_128);
	addMapping("128KB/FOOCART", CartridgeType.UNKNOWN);
	addMapping("128KB/MEGACART", CartridgeType.CARTRIDGE_MEGA_128);
	addMapping("128KB/MYIDE+", CartridgeType.UNKNOWN);
	addMapping("128KB/MYIDE+FLASH", CartridgeType.UNKNOWN);
	addMapping("128KB/MYIDE-II", CartridgeType.UNKNOWN);
	addMapping("128KB/SIC!", CartridgeType.CARTRIDGE_SIC_128);
	addMapping("128KB/SPARTADOS X", CartridgeType.CARTRIDGE_SDX_128);
	addMapping("128KB/SWITCHABLE XEGS", CartridgeType.CARTRIDGE_SWXEGS_128);
	addMapping("128KB/TURBO FREEZER 2005", CartridgeType.UNKNOWN);
	addMapping("128KB/TURBOSOFT", CartridgeType.CARTRIDGE_TURBOSOFT_128);
	addMapping("128KB/XEGS", CartridgeType.CARTRIDGE_XEGS_128);
	addMapping("16KB/-", CartridgeType.CARTRIDGE_STD_16);
	addMapping("16KB/?", CartridgeType.UNKNOWN);
	addMapping("16KB/BLIZZARD", CartridgeType.CARTRIDGE_BLIZZARD_16);
	addMapping("16KB/BUTTON SWITCH", CartridgeType.CARTRIDGE_STD_16);
	addMapping("16KB/OSS 034M", CartridgeType.CARTRIDGE_OSS_034M_16);
	addMapping("16KB/OSS 043M", CartridgeType.CARTRIDGE_OSS_043M_16);
	addMapping("16KB/OSS M091", CartridgeType.CARTRIDGE_OSS_M091_16);
	addMapping("16KB/STANDARD", CartridgeType.CARTRIDGE_STD_16);
	addMapping("2KB/AUTO SWITCHOFF", CartridgeType.UNKNOWN);
	addMapping("256KB/SIC!", CartridgeType.CARTRIDGE_SIC_256);
	addMapping("256KB/SIDE", CartridgeType.UNKNOWN);
	addMapping("256KB/TURBO FREEZER 2005", CartridgeType.UNKNOWN);
	addMapping("256KB/ULTIMATE1MB", CartridgeType.UNKNOWN);
	addMapping("2KB/STANDARD", CartridgeType.CARTRIDGE_STD_2);
	addMapping("32KB/-", CartridgeType.UNKNOWN);
	addMapping("32KB/ADAWLIAH", CartridgeType.CARTRIDGE_ADAWLIAH_32);
	addMapping("32KB/AST", CartridgeType.CARTRIDGE_AST_32);
	addMapping("32KB/BLIZZARD", CartridgeType.CARTRIDGE_BLIZZARD_32);
	addMapping("32KB/ULTRACART", CartridgeType.CARTRIDGE_ULTRACART_32);
	addMapping("32KB/WILLIAMS", CartridgeType.CARTRIDGE_WILL_32);
	addMapping("32KB/XEGS", CartridgeType.CARTRIDGE_XEGS_32);
	addMapping("40KB/BOUNTY BOB", CartridgeType.CARTRIDGE_BBSB_40);
	addMapping("4KB/", CartridgeType.CARTRIDGE_STD_4);
	addMapping("4KB/AUTO SWITCHOFF", CartridgeType.CARTRIDGE_STD_4);
	addMapping("4KB/BLIZZARD", CartridgeType.CARTRIDGE_BLIZZARD_4);
	addMapping("4KB/BUTTON SWITCH", CartridgeType.UNKNOWN);
	/**
	 * Kr0tki: The "ElectroSmith" and "ElectroSmith Right" mappings were
	 * used by the "ACE-80XL" and "ACE-80" cartridges, respectively.
	 * According to ClausB, ElectroSmith was a company that manufactured
	 * cartridges both for ACE and for OSS, so the mappings' names I chosen
	 * are actually imprecise. I have to rename them in the next update of
	 * the spreadsheet. The "ElectroSmith" mapping is a switchable 4 KB
	 * cartridge that controls its visibility with the A3 line when
	 * accessing page $D5. Accessing an $D5xx address that has its bit 3 set
	 * to 1 disables the cartridge, while bit 3 = 0 enables it. The
	 * "ElectroSmith Right" is similar, only it is a right cartridge for
	 * Atari 800 only.
	 **/

	addMapping("4KB/ELECTROSMITH", CartridgeType.UNKNOWN);
	addMapping("4KB/ELECTROSMITH RIGHT", CartridgeType.UNKNOWN);
	addMapping("4KB/RIGHT", CartridgeType.CARTRIDGE_RIGHT_4);
	addMapping("4KB/STANDARD", CartridgeType.CARTRIDGE_STD_4);
	addMapping("512KB/IDE PLUS 2.0", CartridgeType.UNKNOWN);
	addMapping("512KB/MEGACART", CartridgeType.CARTRIDGE_MEGA_512);
	addMapping("520KB/CORINA EEPROM", CartridgeType.UNKNOWN);
	addMapping("64KB/?", CartridgeType.UNKNOWN);
	addMapping("64KB/ATRAX SDX", CartridgeType.CARTRIDGE_ATRAX_SDX_64);
	addMapping("64KB/DIAMOND", CartridgeType.CARTRIDGE_DIAMOND_64);
	addMapping("64KB/EXPRESS", CartridgeType.CARTRIDGE_EXP_64);
	addMapping("64KB/SPARTADOS X", CartridgeType.CARTRIDGE_SDX_64);
	addMapping("64KB/TURBOSOFT", CartridgeType.CARTRIDGE_TURBOSOFT_64);
	addMapping("64KB/WILLIAMS", CartridgeType.CARTRIDGE_WILL_64);
	addMapping("64KB/XEGS", CartridgeType.CARTRIDGE_XEGS_64);
	addMapping("8KB/", CartridgeType.CARTRIDGE_STD_8);
	addMapping("8KB/-", CartridgeType.CARTRIDGE_STD_8);
	addMapping("8KB/?", CartridgeType.UNKNOWN);
	addMapping("8KB/BUTTON SWITCH", CartridgeType.CARTRIDGE_STD_8);
	addMapping("8KB/LOW BANK", CartridgeType.CARTRIDGE_RIGHT_8);
	addMapping("8KB/OSS", CartridgeType.CARTRIDGE_OSS_8);
	addMapping("8KB/PHOENIX", CartridgeType.CARTRIDGE_PHOENIX_8);
	addMapping("8KB/RIGHT", CartridgeType.CARTRIDGE_RIGHT_8);
	addMapping("8KB/STANDARD", CartridgeType.CARTRIDGE_STD_8);
	// The "8KB/TELELINK II" has 256 bytes RAM on it, so do not default for
	// standard 8k ROMs
	addMapping("8KB/TELELINK II", CartridgeType.UNKNOWN);
    }

    private void run() {
	logSkipped = true;

	// Kro0ki's list is the best.
	boolean krotkiList = true;

	// The others are currently not used.
	boolean atariManiaList = false;
	boolean maxFlashList = false;

	CartridgeDatabase database = new CartridgeDatabase();
	if (atariManiaList) {
	    addAtariManiaList(database);
	}
	if (krotkiList) {
	    addKrotkiList(database);
	}
	if (maxFlashList) {
	    addMaxflashList(database);
	}
	try {
	    File file = new File(DATABASE_FILE);
	    database.save(file);
	    logInfo("CRC database saved to '"
		    + file
		    + "' with "
		    + TextUtility.formatAsDecimal(file.length())
		    + " bytes, "
		    + TextUtility.formatAsDecimal(database
			    .getKnownTitelsCount())
		    + " known entries and "
		    + TextUtility.formatAsDecimal(database
			    .getKnownCartridgeTypesCount())
		    + " known content types.");
	} catch (CoreException ex) {
	    logError(ex.getMessage());
	}
    }

    private static void addEntry(CartridgeDatabase database, int sizeInKB,
	    boolean hasCRC32, long crc32, String md5, String title,
	    String publisher, String date, CartridgeType cartridgeType,
	    String source) {
	if (database == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'database' must not be null.");
	}
	if ((crc32 & 0xffffffff) != crc32) {
	    throw new IllegalArgumentException(
		    "Parameter 'crc32' must not be larger than 32 bit.");
	}

	database.addEntry(sizeInKB, hasCRC32, crc32, md5, title, publisher,
		date, cartridgeType, source);
    }

    @SuppressWarnings("static-method")
    private void addAtariManiaList(CartridgeDatabase database) {
	if (database == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'database' must not be null.");
	}
	URL url;
	try {
	    url = new URL(
		    "http://www.atarimania.com/list_games_atari-400-800-xl-xe-p_total-page-step-cartridge_380-2-200-1_8_G.html");
	} catch (MalformedURLException ex) {
	    throw new RuntimeException(ex);
	}
	logInfo("Reading '" + url.toString() + "'.");
	try {
	    InputStream inputStream = url.openStream();
	    XMLHandler xmlHandler = new XMLHandler();
	    XMLReader reader;
	    SAXParser parser;
	    try {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		parser = factory.newSAXParser();
		reader = parser.getXMLReader();

		reader.setFeature("http://xml.org/sax/features/validation",
			false);
		reader.setFeature(
			"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
			false);
		reader.setFeature(
			"http://apache.org/xml/features/nonvalidating/load-external-dtd",
			false);
		reader.setFeature(
			"http://xml.org/sax/features/external-general-entities",
			false);
		reader.setFeature(
			"http://xml.org/sax/features/external-parameter-entities",
			false);
		reader.setFeature(
			"http://xml.org/sax/features/use-entity-resolver2",
			false);
		// reader.setFeature("http://apache.org/xml/features/validation/unparsed-entity-checking",
		// false);
		reader.setFeature(
			"http://xml.org/sax/features/resolve-dtd-uris", false);
		reader.setFeature(
			"http://apache.org/xml/features/validation/dynamic",
			false);
		reader.setFeature(
			"http://apache.org/xml/features/validation/schema/augment-psvi",
			false);

	    } catch (ParserConfigurationException ex) {
		throw new RuntimeException("Cannot create parser.", ex);
	    } catch (SAXException ex) {
		throw new RuntimeException("Cannot create parser.", ex);
	    }

	    try {
		parser.parse(new InputSource(inputStream), xmlHandler);

	    } catch (SAXParseException ex) {
		throw new RuntimeException(ex);
	    }

	    catch (SAXException ex) {
		throw new RuntimeException(ex);
	    }
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}
    }

    private void addKrotkiList(CartridgeDatabase database) {
	if (database == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'database' must not be null.");
	}
	File file = new File(KROTKI_LIST);
	logInfo("Reading '" + file.getAbsolutePath() + "'.");
	CSVReader csvReader = new CSVReader();
	csvReader.open(file, ',', Charset.defaultCharset().name());
	String titleHeader = "Title";
	int titleIndex = csvReader.getColumnIndex(titleHeader);
	String publisherHeader = "Publisher";
	int publisherIndex = csvReader.getColumnIndex(publisherHeader);
	String dateHeader = "Date";
	int dateIndex = csvReader.getColumnIndex(dateHeader);
	String crc32Header = "CRC-32";
	int crc32Index = csvReader.getColumnIndex(crc32Header);
	String sizeHeader = ("Size");
	int sizeIndex = csvReader.getColumnIndex(sizeHeader);
	String mappingHeader = ("Mapping");
	int mappingIndex = csvReader.getColumnIndex(mappingHeader);
	int addedCount = 0;
	int errorCount = 0;
	int skippedCount = 0;
	int skippedCRC32Count = 0;

	while (csvReader.readNextRow()) {
	    String title = csvReader.getColumnValue(titleIndex).trim();
	    String publisher = csvReader.getColumnValue(publisherIndex).trim();
	    String date = csvReader.getColumnValue(dateIndex).trim();

	    String crc32Value = csvReader.getColumnValue(crc32Index).trim()
		    .toUpperCase();
	    String sizeValue = csvReader.getColumnValue(sizeIndex).trim()
		    .toUpperCase();
	    String mappingValue = sizeValue
		    + "/"
		    + csvReader.getColumnValue(mappingIndex).trim()
			    .toUpperCase();
	    if (StringUtility.isEmpty(title)) {
		if (StringUtility.isSpecified(publisher)
			|| StringUtility.isSpecified(date)
			|| StringUtility.isSpecified(crc32Value)) {
		    if (logSkipped) {
			logInfo("Skipping: Column '" + titleHeader
				+ "' empty in row " + csvReader.getRowNumber());
		    }
		    skippedCount++;
		}
		continue;
	    }
	    if (StringUtility.isEmpty(crc32Value)) {
		if (logSkipped) {
		    logInfo("Skipping: " + title + ", column '" + crc32Header
			    + "' empty in row " + csvReader.getRowNumber());
		}
		skippedCRC32Count++;
		continue;
	    }
	    if (StringUtility.isEmpty(sizeValue)) {
		if (logSkipped) {
		    logInfo("Skipping: " + title + ", column '" + sizeHeader
			    + "' empty in row " + csvReader.getRowNumber());
		}
		skippedCount++;
		continue;
	    }

	    boolean hasCRC32 = StringUtility.isSpecified(crc32Value);
	    long crc32 = Long.parseLong(crc32Value, 16);
	    int sizeInKB;
	    final String KB_SUFFIX = "KB";
	    if (sizeValue.endsWith(KB_SUFFIX)) {
		sizeInKB = Integer.parseInt(sizeValue.substring(0,
			sizeValue.length() - KB_SUFFIX.length()));
	    } else {
		throw new RuntimeException("Unknown size suffix in "
			+ sizeValue + ".");
	    }

	    CartridgeType cartridgeType = mappings.get(mappingValue);
	    if (cartridgeType == null) {
		throw new RuntimeException("Unknown mapping value "
			+ mappingValue + " for " + title
			+ ". Add a mapping definition.");
	    }
	    if (cartridgeType == CartridgeType.UNKNOWN) {
		logInfo("Value " + mappingValue + " for '" + title
			+ "' has been mapped to the UNKNOWN content type.");
		errorCount++;
	    } else {
		if (cartridgeType.getSizeInKB() != sizeInKB) {
		    throw new RuntimeException(
			    "Size mismatch for mapping value " + mappingValue
				    + ". Check mapping definition.");
		}
	    }
	    addEntry(database, sizeInKB, hasCRC32, crc32, "", title, publisher,
		    date, cartridgeType, "K");
	    addedCount++;

	}
	logInfo(addedCount + " entries added, " + skippedCount
		+ " entries skipped with empty fields, " + skippedCRC32Count
		+ " entries skipped without CRC32, " + errorCount
		+ " errors from Krotki's list.");
    }

    private void addMaxflashList(CartridgeDatabase database) {
	if (database == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'database' must not be null.");
	}

	File file = new File(MAXFLASH_LIST);
	logInfo("Reading '" + file.getAbsolutePath() + "'.");
	FileInputStream fis;
	try {
	    fis = new FileInputStream(file);
	} catch (FileNotFoundException ex) {
	    throw new RuntimeException(ex);
	}
	BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
	int addedCount = 0;
	int errorCount = 0;
	int skippedCount = 0;
	String line = null;
	try {
	    String md5;
	    String title;
	    boolean hasCRC32;
	    long crc32 = 0;
	    String crc32String;
	    String formatValue;
	    int format;

	    md5 = "";
	    title = "";
	    hasCRC32 = false;
	    crc32 = 0;
	    crc32String = "";
	    formatValue = "";
	    format = 0;

	    while ((line = reader.readLine()) != null) {

		if (line.startsWith("[")) {
		    md5 = line.substring(1, 32).toUpperCase();
		} else if (line.startsWith("DESCRIPTION=")) {
		    title = line.substring(12);
		} else if (line.startsWith("CRC32=")) {
		    String crc32Value = line.substring(6);
		    hasCRC32 = StringUtility.isSpecified(crc32Value);
		    crc32 = Long.parseLong(crc32Value);
		    crc32String = getCRC32String(crc32);
		} else if (line.startsWith("FORMAT=")) {
		    formatValue = line.substring(7);
		    format = Integer.parseInt(formatValue);
		}

		// End of INI section reached=
		if (StringUtility.isEmpty(line)) {
		    if (format == 0) {
			if (logSkipped) {
			    logInfo("Skipping entry " + formatValue + "/"
				    + crc32String + "/" + title
				    + " as it is unsupported.");
			}
		    } else if (format == 0 || hasCRC32 == false
			    || StringUtility.isEmpty(title)) {
			logError("Skipping entry " + formatValue + "/"
				+ crc32String + "/" + title
				+ " as it is incomplete.");
		    } else {
			int sizeInKB = 0;

			CartridgeType cartridgeType = CartridgeType.UNKNOWN;
			switch (format) {
			case 1:
			    // cartridgeType = CartridgeType.FILE_EXECUTABLE;
			    // not relevant
			    break;
			case 2:
			    cartridgeType = CartridgeType.CARTRIDGE_STD_8;
			    break;
			case 3:
			    // cartridgeType = CartridgeType.FILE_ATR; no
			    // relevant
			    break;
			}
			addEntry(database, sizeInKB, hasCRC32, crc32, md5,
				title, "", "", cartridgeType, "M");
			addedCount++;

		    }
		    md5 = "";
		    format = 0;
		    crc32 = 0;
		    title = "";
		}

	    }
	    reader.close();
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}

	logInfo(addedCount + " entries added, " + skippedCount
		+ " entries skipped, " + errorCount
		+ " errors from Maxflash list.");

    }

    private static String getCRC32String(long crc32) {
	return HexUtility.getLongValueHexString(crc32, 8);
    }

}

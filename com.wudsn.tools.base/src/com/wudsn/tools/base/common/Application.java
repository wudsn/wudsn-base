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

import static com.wudsn.tools.base.common.ByteArrayUtility.MB;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

import com.wudsn.tools.base.Texts;
import com.wudsn.tools.base.gui.Desktop;
import com.wudsn.tools.base.gui.UIManager;

/**
 * Application which has a version and can be downloaded from the web.
 * 
 * @author Peter Dell
 * 
 */
public final class Application {

    public final class MemoryInfo {
	public final long totalMemory;
	public final long maxMemory;
	public final long freeMemory;

	MemoryInfo() {
	    totalMemory = Runtime.getRuntime().totalMemory();
	    maxMemory = Runtime.getRuntime().maxMemory();
	    freeMemory = Runtime.getRuntime().freeMemory();
	}

	/**
	 * Gets the maximum amount of memory reserved for Java in MB.
	 * 
	 * @return The maximum amount of memory reserved for Java in MB, not
	 *         empty, not <code>null</code>.
	 */
	public String getMaximumMemoryMB() {
	    long maximumMemoryMB = (maxMemory / MB) * MB;
	    String result = TextUtility.formatAsMemorySize(maximumMemoryMB);
	    return result;
	}
    }

    public static final String UNKNOWN_VERSION = "????-??-?? ??:??:??";

    private static Application instance;
    private String urlString;
    private String jarFileName;
    private String versionPath;

    /**
     * Creation is private.
     */
    private Application() {

    }

    public static void createInstance(String urlString, String jarFileName,
	    String versionPath) {
	if (urlString == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'urlString' must not be null.");
	}
	if (jarFileName == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'jarFileName' must not be null.");
	}
	if (versionPath == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'versionPath' must not be null.");
	}
	if (instance != null) {
	    throw new IllegalStateException("An application with URL '"
		    + urlString + "' was already created.");
	}
	instance = new Application();
	instance.urlString = urlString;
	instance.jarFileName = jarFileName;
	instance.versionPath = versionPath;

	// Ensure native look and feel also for popups resulting from early
	// errors.
	UIManager.init();
    }

    /**
     * Gets the current application instance.
     * 
     * @return The current application instance, not <code>null</code>.
     */
    public static Application getInstance() {
	if (instance == null) {
	    throw new IllegalStateException("No application instance created.");
	}
	return instance;
    }

    /**
     * Reads the version file from the JAR in the ZIP archive on the web.
     * 
     * @return The version string, not empty and not <code>null</code>.
     */
    public String getWebVersion() {
	String result = UNKNOWN_VERSION;
	URL url;
	try {
	    url = new URL(urlString);
	} catch (MalformedURLException ex) {
	    throw new RuntimeException(ex);
	}
	InputStream urlInputStream = null;
	ZipInputStream zipInputStream = null;
	try {
	    urlInputStream = url.openStream();
	    zipInputStream = new ZipInputStream(urlInputStream);
	    ZipEntry jarZipEntry = null;
	    ZipEntry zipEntry;
	    while (jarZipEntry == null
		    && (zipEntry = zipInputStream.getNextEntry()) != null) {
		if (zipEntry.getName().equals(jarFileName)) {
		    jarZipEntry = zipEntry;
		}
	    }
	    if (jarZipEntry != null) {
		JarInputStream jarInputStream = new JarInputStream(
			zipInputStream);
		String versionResource = null;
		while (versionResource == null
			&& (zipEntry = jarInputStream.getNextEntry()) != null) {
		    if (zipEntry.getName().equals(versionPath)) {
			try {
			    versionResource = FileUtility.readString(
				    versionPath, jarInputStream,
				    FileUtility.MAX_SIZE_1MB);
			    result = versionResource.trim();
			} catch (CoreException igore) {
			}
		    }
		}

	    }
	} catch (IOException ex) {
	    if (zipInputStream != null) {
		try {
		    zipInputStream.close();

		} catch (IOException ignore) {
		}
	    }
	    if (urlInputStream != null) {
		try {
		    urlInputStream.close();

		} catch (IOException ignore) {
		}
	    }
	}
	return result;
    }

    /**
     * Gets the version of the tool based on the version file created the ANT
     * script.
     * 
     * @return The version in the format "YYYY-MM-DD HH:MM:SS", not
     *         <code>null</code>.
     */
    public String getLocalVersion() {
	String result = ResourceUtility.loadResourceAsString(versionPath);
	if (result == null) {
	    result = UNKNOWN_VERSION;
	}
	result = result.trim();
	return result;
    }

    /**
     * Asynchronously checks for an update and display a notification dialog if
     * an update is available.
     * 
     * @param exitForUpdateRunnable
     *            Callback when the user as selected to download the update and
     *            the application should exit, so it can be replaced. Can be
     *            <code>null</code>.
     */
    public void checkForUpdate(final Runnable exitForUpdateRunnable) {
	Thread thread = new Thread(new Runnable() {

	    @Override
	    public void run() {
		checkForUpdateSync(exitForUpdateRunnable);
	    }
	});
	thread.start();

    }

    void checkForUpdateSync(Runnable exitForUpdateRunnable) {
	String localVersion = getLocalVersion();
	String webVersion = getWebVersion();

	// Both versions defined and local version less than web version?
	if (!localVersion.equals(UNKNOWN_VERSION)
		&& !webVersion.equals(UNKNOWN_VERSION)
		&& localVersion.compareTo(webVersion) < 0) {
	    String message = TextUtility.format(Texts.UpdateDialog_Text,
		    webVersion);
	    int dialogResult = JOptionPane.showConfirmDialog(null, message,
		    Texts.UpdateDialog_Title, JOptionPane.YES_NO_OPTION);

	    if (dialogResult == JOptionPane.YES_OPTION) {
		Desktop.openBrowser(urlString);
		if (exitForUpdateRunnable != null) {
		    exitForUpdateRunnable.run();
		}
	    }
	}

    }

    /**
     * Gets the memory info after performing a garbage collection.
     * 
     * @return The memory info, not <code>null</code>.
     */
    public MemoryInfo getMemoryInfo() {
	return new MemoryInfo();
    }
}
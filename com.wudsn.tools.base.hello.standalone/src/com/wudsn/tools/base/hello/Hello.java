/*
 * Hello, a Java test program
 * Copyright (C) 2020 Peter Dell
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.wudsn.tools.base.hello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

import javax.swing.JOptionPane;

/**
 * Stand-alone test program.
 *
 * @author Peter Dell
 */
public final class Hello {

	/**
	 * Point of entry to the stand-alone version
	 *
	 * @param args
	 *            optional first String: name of a file to edit
	 */
	public static void main(String[] args) throws Throwable {
		Hello instance = new Hello();
		try {
			instance.run(args);
		} catch (Throwable th) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			printWriter.println(th.getMessage());
			th.printStackTrace(printWriter);
			JOptionPane.showMessageDialog(null, stringWriter.toString(), "Unexpected Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			throw th;
		}
	}

	/**
	 * Creation is private.
	 */
	private Hello() {
	}

	/**
	 * Point of entry to the stand-alone version
	 *
	 * @param args
	 *            The command line arguments, not <code>null</code>.
	 */
	private void run(String[] args) {
		Class<?> clazz = getClass();
		String osResourcePath = clazz.getName().replace('.', '/') + ".os";
		String osVersion = "Unknown version";

		InputStream is = clazz.getClassLoader().getResourceAsStream(osResourcePath);
		if (is != null) {
			InputStreamReader isReader = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isReader);
			StringBuffer sb = new StringBuffer();
			String str;
			try {
				while ((str = reader.readLine()) != null) {
					sb.append(str);
				}
				osVersion = sb.toString();
			} catch (IOException e) {
			}
		}
		String text = clazz.getName() + " on " + osVersion + " at " + (new Date()).toString();
		println(text);
		println();

		println("System Class Loader");
		printClassLoader(ClassLoader.getSystemClassLoader());
		println();

		println("Class Loader");
		printClassLoader(clazz.getClassLoader());
		println();

		println("Current Stack");
		new Exception().printStackTrace(System.out);
		println();

		println("System Properties");
		printSystemProperties();
		println();

		println("OSGI Classes");
		String title = "";

		try {
			title = Texts.APPLICATION_TITLE;
			text = Texts.APPLICATION_TITLE + " - " + text;
			println(Texts.class.getName() + " loaded");
		} catch (NoClassDefFoundError ex) {
			ex.printStackTrace(System.out);
		}
		JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE);
	}

	private static void println() {
		System.out.println();
	}

	private static void println(String message) {
		System.out.println(message);
	}

	private ClassLoader printClassLoader(ClassLoader classLoader) {
		while (classLoader != null) {
			String classPath = "?";
			if (classLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) classLoader).getURLs();
				classPath = Arrays.toString(urls);
			}
			println("Loaded by " + classLoader.getClass().getName() + " with class path " + classPath);
			classLoader = classLoader.getParent();
		}
		return classLoader;
	}

	private void printSystemProperties() {
		Properties properties = System.getProperties();
		TreeSet<String> keys = new TreeSet<String>(properties.stringPropertyNames());
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			String key = i.next();
			println(key + "=" + String.valueOf(properties.getProperty(key)));
		}
	}

}
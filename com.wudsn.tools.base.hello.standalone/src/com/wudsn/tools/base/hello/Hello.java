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

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;

/**
 * Stand-alone test program.
 *
 * @author Peter Dell
 */
public final class Hello {

	private ByteArrayOutputStream bos;
	private PrintStream printStream;

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
		bos = new ByteArrayOutputStream();
		printStream = new PrintStream(bos) {
			public void write(int b) {
				super.write(b);
				System.out.write(b);
			}

			public void write(byte buf[], int off, int len) {
				super.write(buf, off, len);
				System.out.write(buf, off, len);
			}
		};
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
		new Exception().printStackTrace(printStream);
		println();

		println("System Properties");
		printSystemProperties();
		println();

		println("OSGI Classes");
		String title = "";

		try {
			title = Texts.APPLICATION_TITLE;
			text = Texts.APPLICATION_TITLE + " - " + text;
			println(Texts.class.getName() + " loaded from org.eclipse.osgi.jar");
		} catch (NoClassDefFoundError ex) {
			ex.printStackTrace(printStream);
		} catch (UnsupportedClassVersionError ex) {
			ex.printStackTrace(printStream);
		}

		try {
			println(CoreException.class.getName() + " loaded from org.eclipse.equinox.common.jar");
		} catch (NoClassDefFoundError ex) {
			ex.printStackTrace(printStream);
		} catch (UnsupportedClassVersionError ex) {
			ex.printStackTrace(printStream);
		}
		try {

			println(SWT.class.getName() + " loaded from swt.jar");

		} catch (NoClassDefFoundError ex) {
			ex.printStackTrace(printStream);
		} catch (UnsupportedClassVersionError ex) {
			ex.printStackTrace(printStream);
		}
		try {
			text = bos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		showLongTextMessageInDialog(null, text, title);
	}

	private void println() {
		printStream.println();
	}

	private void println(String message) {
		printStream.print(message);
		println();
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

	private void showLongTextMessageInDialog(Frame frame, String longMessage, String title) {
		JTextArea textArea = new JTextArea(32, 64);
		textArea.setText(longMessage);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		JOptionPane.showMessageDialog(frame, scrollPane);
	}

}
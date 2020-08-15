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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

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
		String text = Texts.APPLICATION_TITLE + " " + this.getClass().getName() + " at " + (new Date()).toString();
		System.out.println(text);
		JOptionPane.showMessageDialog(null, text, Texts.APPLICATION_TITLE, JOptionPane.INFORMATION_MESSAGE);
	}

}
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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Proxy for functions that are only available in JDK 1.7.
 * 
 * @author Peter Dell
 * 
 */
public final class JDK {

    public static final class Boolean {
	/**
	 * Compares two {@code boolean} values. The value returned is identical
	 * to what would be returned by:
	 * 
	 * <pre>
	 * Boolean.valueOf(x).compareTo(Boolean.valueOf(y))
	 * </pre>
	 * 
	 * @param x
	 *            the first {@code boolean} to compare
	 * @param y
	 *            the second {@code boolean} to compare
	 * @return the value {@code 0} if {@code x == y}; a value less than
	 *         {@code 0} if {@code !x && y}; and a value greater than
	 *         {@code 0} if {@code x && !y}
	 * @since 1.7
	 */
	public static int compare(boolean x, boolean y) {
	    return (x == y) ? 0 : (x ? 1 : -1);
	}
    }

    public static final class Long {
	/**
	 * Compares two {@code long} values numerically. The value returned is
	 * identical to what would be returned by:
	 * 
	 * <pre>
	 * Long.valueOf(x).compareTo(Long.valueOf(y))
	 * </pre>
	 * 
	 * @param x
	 *            the first {@code long} to compare
	 * @param y
	 *            the second {@code long} to compare
	 * @return the value {@code 0} if {@code x == y}; a value less than
	 *         {@code 0} if {@code x < y}; and a value greater than
	 *         {@code 0} if {@code x > y}
	 * @since 1.7
	 */
	public static int compare(long x, long y) {
	    return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}
    }

    public static final class FileDialog {

	public static File[] getFiles(java.awt.FileDialog fileDialog) {
	    Method getFiles = null;
	    try {
		getFiles = java.awt.FileDialog.class.getMethod("getFiles", new Class[] {});
	    } catch (SecurityException ex) {

	    } catch (NoSuchMethodException ex) {

	    }
	    File[] result;
	    if (getFiles != null) {
		try {
		    result = (File[]) getFiles.invoke(fileDialog);
		} catch (IllegalArgumentException ex) {
		    throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
		    throw new RuntimeException(ex);
		} catch (InvocationTargetException ex) {
		    throw new RuntimeException(ex);
		}
	    } else {
		String directory = fileDialog.getDirectory();
		String file = fileDialog.getFile();
		if (file != null) {
		    result = new File[] { new File(directory, file) };
		} else {
		    result = new File[0];
		}
	    }
	    return result;
	}

	public static void setMultipleMode(java.awt.FileDialog fileDialog, boolean multiSelectionEnabled) {
	    if (fileDialog == null) {
		throw new IllegalArgumentException("Parameter 'fileDialog' must not be null.");
	    }
	    Method setMultipleMode = null;
	    try {
		setMultipleMode = java.awt.FileDialog.class.getMethod("setMultipleMode",
			new Class[] { java.lang.Boolean.TYPE });
	    } catch (SecurityException ex) {

	    } catch (NoSuchMethodException ex) {

	    }
	    if (setMultipleMode != null) {
		try {
		    setMultipleMode.invoke(fileDialog, java.lang.Boolean.valueOf(multiSelectionEnabled));
		} catch (IllegalArgumentException ex) {
		    throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
		    throw new RuntimeException(ex);
		} catch (InvocationTargetException ex) {
		    throw new RuntimeException(ex);
		}
	    }
	}

    }

    public interface ComboBoxModel<E> extends javax.swing.ComboBoxModel {

    }

    @SuppressWarnings("serial")
    public static class DefaultComboBoxModel<E> extends javax.swing.DefaultComboBoxModel implements ComboBoxModel<E> {

	public DefaultComboBoxModel() {
	    super();
	}

	public DefaultComboBoxModel(final E items[]) {
	    super(items);
	}

	public void addElementTyped(E anObject) {
	    super.addElement(anObject);
	}

    }

    @SuppressWarnings("serial")
    public static class JComboBox<E> extends javax.swing.JComboBox {
	public JComboBox() {
	    super();
	}

	public JComboBox(ComboBoxModel<E> comboboxmodel) {
	    super(comboboxmodel);
	}

	public void setModel(ComboBoxModel<E> aModel) {
	    super.setModel(aModel);
	}
    }
}
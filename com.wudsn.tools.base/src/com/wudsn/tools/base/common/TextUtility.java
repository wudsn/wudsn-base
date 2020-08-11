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

import static com.wudsn.tools.base.common.ByteArrayUtility.KB;
import static com.wudsn.tools.base.common.ByteArrayUtility.MB;

import java.text.NumberFormat;

/**
 * Utility class for processing text.
 * 
 * @author Peter Dell
 */
public final class TextUtility {

    /**
     * Parameter variable tokens.
     */
    private static final String[] PARAMETERS = { "{0}", "{1}", "{2}", "{3}", "{4}", "{5}", "{6}", "{7}", "{8}", "{9}" };

    /**
     * Creation is private.
     */
    private TextUtility() {

    }

    /**
     * Formats a text with parameters "{0}" to "{9}".
     * 
     * @param text
     *            The text with the parameters "{0}" to "{9}", may be empty, not
     *            <code>null</code>.
     * @param parameters
     *            The parameters, may be empty or <code>null</code>.
     * 
     * @return The formatted text, may be empty, not <code>null</code>.
     */
    public static String format(String text, String... parameters) {
	if (text == null) {
	    throw new IllegalArgumentException("Parameter 'text' must not be null.");
	}
	if (parameters == null) {
	    parameters = new String[0];
	}
	for (int i = 0; i < parameters.length; i++) {
	    String parameter = parameters[i];
	    if (parameter == null) {
		parameter = "";
	    }
	    text = text.replace(PARAMETERS[i], parameter);
	}
	return text;
    }

    /**
     * Formats a long value as a decimal number of the current locale settings.
     * 
     * @param number
     *            The number.
     * @return The formatted number text, not empty, not <code>null</code>.
     */
    public static String formatAsDecimal(long number) {
	return NumberFormat.getNumberInstance().format(number);
    }

    /**
     * Formats a long value as a percent value of a total long number of the
     * current locale settings.
     * 
     * @param number
     *            The number, a non-negative integer.
     * @param totalNumber
     *            The total number, a non-negative integer.
     * @return The formatted number text, not empty, not <code>null</code>.
     */
    public static String formatAsDecimalPercent(long number, long totalNumber) {

	int percent = 0;
	if (totalNumber > 0) {
	    percent = (int) ((1.0d * number) / totalNumber * 100);
	    if (number > 0 && percent == 0) {
		percent = 1;
	    }
	}
	return formatAsDecimal(percent)+"%";
    }

    /**
     * Formats a long value as a decimal number of the current locale settings.
     * 
     * @param number
     *            The number.
     * @return The formatted number text, not empty, not <code>null</code>.
     */
    public static String formatAsMemorySize(long number) {
	String suffix = "B";
	if (number > 0) {
	    if (number % MB == 0) {
		number = number / MB;
		suffix = "MB";
	    } else if (number % KB == 0) {
		number = number / KB;
		suffix = "KB";

	    }
	}
	return NumberFormat.getNumberInstance().format(number) + " " + suffix;
    }

    /**
     * Formats a long value as a hexa-decimal number of the current locale
     * settings.
     * 
     * @param number
     *            The number.
     * @param maxValue
     *            The maximum value to be assumed, it determines the number of
     *            leading zeros that will be added.
     * @return The formatted number text, not empty, not <code>null</code>.
     */
    public static String formatAsHexaDecimal(long number, long maxValue) {
	String result;
	int length = Long.toHexString(maxValue).length();
	result = "$" + HexUtility.getLongValueHexString(number, length);
	return result;
    }

}

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

package com.wudsn.tools.base;

import com.wudsn.tools.base.repository.Message;
import com.wudsn.tools.base.repository.NLS;

/**
 * Message repository.
 * 
 * @author Peter Dell
 */
public final class Messages extends NLS {

    // General file operations
    public static Message E200;
    public static Message E201;
    public static Message E202;
    public static Message E203;
    public static Message E204;
    public static Message E205;
    public static Message E206;
    public static Message E207;
    public static Message E208;
    public static Message E209;
    public static Message E210;
    public static Message E211;
    public static Message E212;
    public static Message E213;
    public static Message E214;
    public static Message E215;
    public static Message E216;
    public static Message E217;
    public static Message E218;
    public static Message E219;

    // Console and parser
    public static Message E250;
    public static Message E251;
    public static Message E252;
    public static Message E253;
    public static Message S254;
    public static Message E255;
    public static Message E256;
    
    // Repository validation
    public static Message E300;
    public static Message E301;
    public static Message E302;
    public static Message E303;
    public static Message E304;

    static {
	initializeClass(Messages.class, null);
    }
}

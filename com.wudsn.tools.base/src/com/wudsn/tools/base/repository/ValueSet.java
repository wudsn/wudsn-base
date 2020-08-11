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
package com.wudsn.tools.base.repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.wudsn.tools.base.ValueSets;

public abstract class ValueSet extends NLS implements Comparable<ValueSet> {

    protected final String id;
    protected String text;
    protected final int sortKey;

    /**
     * Initialize the given class from its properties files.
     * 
     * @param clazz
     *            The class, not <code>null</code>.
     * @param containerClazz
     *            The contain class where the NLS information is stored.
     */
    protected static void initializeClass(Class<? extends ValueSet> clazz, Class<? extends ValueSets> containerClazz) {
	NLS.initializeClass(clazz, containerClazz);
    }

    protected ValueSet(String id, int sortKey) {
	this(id, id, sortKey);
    }

    protected ValueSet(String id, String text, int sortKey) {
	if (id == null) {
	    throw new IllegalArgumentException("Parameter 'id' must not be null.");
	}
	if (text == null) {
	    throw new IllegalArgumentException("Parameter 'text' must not be null.");
	}
	this.id = id;
	this.text = text;
	this.sortKey = sortKey;
    }

    public final String getId() {
	return id;
    }

    public final String getText() {
	return text;
    }

    final void setText(String text) {
	if (text == null) {
	    throw new IllegalArgumentException("Parameter 'text' must not be null.");
	}
	this.text = text;
    }

    @Override
    public final boolean equals(Object o) {
	return this == o;
    }

    @Override
    public final int hashCode() {
	return id.hashCode();
    }

    @Override
    public final String toString() {
	return text; // For use in value set fields and for debugging.
    }

    @Override
    public final int compareTo(ValueSet o) {
	int result = sortKey - o.sortKey;
	if (result == 0) {
	    result = text.compareToIgnoreCase(o.text);
	}
	return result;
    }

    public static final <T extends ValueSet> List<T> getValues(Class<T> valueSetClass) {
	try {
	    Method method = valueSetClass.getMethod("getValues");
	    Object object = method.invoke(null);
	    @SuppressWarnings("unchecked")
	    List<T> result = (List<T>) object;
	    result = new ArrayList<T>(result); // Mutable copy
	    sort(result);
	    return result;

	} catch (NoSuchMethodException ex) {
	    throw new RuntimeException(ex);
	} catch (SecurityException ex) {
	    throw new RuntimeException(ex);
	} catch (IllegalAccessException ex) {
	    throw new RuntimeException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new RuntimeException(ex);
	} catch (InvocationTargetException ex) {
	    throw new RuntimeException(ex);
	}
    }

    public static final void sort(List<? extends ValueSet> list) {
	Collections.sort(list);
    }

}
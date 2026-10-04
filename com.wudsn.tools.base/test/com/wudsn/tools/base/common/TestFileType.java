/**
 * Copyright (C) 2026 <a href="https://www.wudsn.com" target="_top">Peter Dell</a>
 *
 * This file is part of a WUDSN software distribution.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the WUDSN software distribution. If not, see <https://www.gnu.org/licenses/>.
 */

package com.wudsn.tools.base.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.wudsn.tools.base.repository.ValueSet;

/**
 * A value set of file types for the MRU tests, the way an application
 * defines its own: public, with a static {@code getValues()}.
 */
public final class TestFileType extends ValueSet {

	public static final TestFileType WORKSPACE = new TestFileType("WORKSPACE", 0);
	public static final TestFileType EXECUTABLE = new TestFileType("EXECUTABLE", 1);

	private TestFileType(String id, int sortKey) {
		super(id, sortKey);
	}

	public static List<TestFileType> getValues() {
		return Collections.unmodifiableList(Arrays.asList(WORKSPACE, EXECUTABLE));
	}
}

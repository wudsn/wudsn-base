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

import com.wudsn.tools.base.repository.ValueSet;

/**
 * One "most recently used" file entry: its path plus the type it was opened
 * as, a value of the application's own value set of file types.
 *
 * @param <T> the application's value set of file types
 * @author Peter Dell
 */
public final class MRUEntry<T extends ValueSet> {

	private final String filePath;
	private final T fileType;

	public MRUEntry(String filePath, T fileType) {
		if (filePath == null) {
			throw new IllegalArgumentException("Parameter 'filePath' must not be null.");
		}
		if (fileType == null) {
			throw new IllegalArgumentException("Parameter 'fileType' must not be null.");
		}
		this.filePath = filePath;
		this.fileType = fileType;
	}

	public String getFilePath() {
		return filePath;
	}

	public T getFileType() {
		return fileType;
	}
}

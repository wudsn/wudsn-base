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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wudsn.tools.base.repository.ValueSet;

/**
 * A "most recently used" file list, persisted in an {@link
 * ApplicationSettingsSection}: the newest entry first, a path at most once
 * (compared ignoring case), at most {@code maxEntries} entries.
 * <p>
 * {@link #save()} writes entry {@code n} (counting from 1) under the keys
 * {@code "FilePath_" + n} and {@code "FileType_" + n}, the latter holding
 * the file type's {@link ValueSet#getId() ID}; {@link #load()} reads them
 * back. The value set of file types must have a static {@code getValues()}
 * method, which {@link ValueSet#getValues(Class)} calls to find a type by
 * its ID. An entry whose stored ID is no value (any more) is skipped.
 *
 * @param <T> the application's value set of file types
 * @author Peter Dell
 */
public final class MRUList<T extends ValueSet> {

	private static final String FILE_PATH_KEY = "FilePath_";
	private static final String FILE_TYPE_KEY = "FileType_";

	private final Class<T> fileTypeClass;
	private final ApplicationSettingsSection settingsSection;
	private final int maxEntries;
	private final List<MRUEntry<T>> entries = new ArrayList<MRUEntry<T>>();

	public MRUList(Class<T> fileTypeClass, ApplicationSettingsSection settingsSection, int maxEntries) {
		if (fileTypeClass == null) {
			throw new IllegalArgumentException("Parameter 'fileTypeClass' must not be null.");
		}
		if (settingsSection == null) {
			throw new IllegalArgumentException("Parameter 'settingsSection' must not be null.");
		}
		if (maxEntries < 1) {
			throw new IllegalArgumentException("Parameter 'maxEntries' must be at least 1.");
		}
		this.fileTypeClass = fileTypeClass;
		this.settingsSection = settingsSection;
		this.maxEntries = maxEntries;
	}

	public int getMaxEntries() {
		return maxEntries;
	}

	public void clear() {
		entries.clear();
	}

	/** Puts the file first; an entry with the same path (ignoring case) is replaced, the oldest one dropped beyond the limit. */
	public void addFile(String filePath, T fileType) {
		MRUEntry<T> newEntry = new MRUEntry<T>(filePath, fileType);
		Iterator<MRUEntry<T>> it = entries.iterator();
		while (it.hasNext()) {
			if (it.next().getFilePath().equalsIgnoreCase(filePath)) {
				it.remove();
				break;
			}
		}

		if (entries.size() >= maxEntries) {
			entries.remove(entries.size() - 1);
		}

		entries.add(0, newEntry);
	}

	/** The entries, newest first. */
	public List<MRUEntry<T>> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	/** The path of the newest entry of {@code fileType}, or {@code ""}. */
	public String getLastFilePath(T fileType) {
		for (MRUEntry<T> entry : entries) {
			if (entry.getFileType().equals(fileType)) {
				return entry.getFilePath();
			}
		}
		return "";
	}

	public void load() {
		clear();

		Map<String, T> fileTypes = new HashMap<String, T>();
		for (T fileType : ValueSet.getValues(fileTypeClass)) {
			fileTypes.put(fileType.getId(), fileType);
		}

		for (int i = 1; i <= maxEntries; i++) {
			String filePath = settingsSection.getString(FILE_PATH_KEY + i, "");
			T fileType = fileTypes.get(settingsSection.getString(FILE_TYPE_KEY + i, ""));
			if (!filePath.isEmpty() && fileType != null) {
				entries.add(new MRUEntry<T>(filePath, fileType));
			}
		}
	}

	/** Writes the entries; the slots after the last one up to the limit are emptied, so that {@link #load()} skips them. */
	public void save() {
		for (int i = 1; i <= maxEntries; i++) {
			if (i <= entries.size()) {
				MRUEntry<T> entry = entries.get(i - 1);
				settingsSection.writeString(FILE_PATH_KEY + i, entry.getFilePath());
				settingsSection.writeString(FILE_TYPE_KEY + i, entry.getFileType().getId());
			} else {
				settingsSection.writeString(FILE_PATH_KEY + i, "");
				settingsSection.writeString(FILE_TYPE_KEY + i, "");
			}
		}
	}
}

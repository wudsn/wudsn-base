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

package com.wudsn.tools.base.gui;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import com.wudsn.tools.base.common.JDK;
import com.wudsn.tools.base.common.StringUtility;
import com.wudsn.tools.base.common.FileUtility.FileExtensionFileFilter;

/**
 * File chooser that uses JFileChooser or JFileDialog depending on which looks
 * best on the active native platform.
 * 
 * @author Peter Dell
 */
public abstract class FileChooser {

    /**
     * Wrapper class for standard JFileChooser.
     */
    private static final class FileChooserWrapper extends FileChooser {
	private JFileChooser fileChooser;

	public FileChooserWrapper() {
	    fileChooser = new JFileChooser();
	}

	@Override
	public void setDialogTitle(String dialogTitle) {
	    fileChooser.setDialogTitle(dialogTitle);
	}

	@Override
	public void setMultiSelectionEnabled(boolean multiSelectionEnabled) {
	    fileChooser.setMultiSelectionEnabled(multiSelectionEnabled);

	}

	@Override
	public void setCurrentDirectory(File dir) {
	    fileChooser.setCurrentDirectory(dir);
	}

	@Override
	public File getCurrentDirectory() {
	    File result;
	    result = fileChooser.getCurrentDirectory();
	    return result;
	}

	@Override
	public void setSelectedFile(File file) {
	    fileChooser.setSelectedFile(file);

	}

	@Override
	public void setFileFilter(FileFilter filefilter) {
	    fileChooser.setFileFilter(filefilter);
	}

	@Override
	public File getSelectedFile() {
	    File result;

	    result = fileChooser.getSelectedFile();
	    return result;
	}

	@Override
	public File[] getSelectedFiles() {
	    File result[];

	    result = fileChooser.getSelectedFiles();
	    return result;
	}

	@Override
	public int showOpenDialog(JFrame parent) {
	    int result = fileChooser.showOpenDialog(parent);
	    return result;
	}

	@Override
	public int showSaveDialog(JFrame parent) {
	    int result;
	    result = fileChooser.showSaveDialog(parent);
	    return result;
	}
    }

    /**
     * Wrapper class for standard JFileChooser.
     */
    private static final class FileDialogWrapper extends FileChooser {

	private String dialogTitle;
	private boolean multiSelectionEnabled;
	private File currentDirectory;
	FileFilter fileFilter;
	private File selectedFile;
	private File[] selectedFiles;

	public FileDialogWrapper() {

	}

	@Override
	public void setDialogTitle(String dialogTitle) {
	    this.dialogTitle = dialogTitle;
	}

	@Override
	public void setMultiSelectionEnabled(boolean multiSelectionEnabled) {
	    this.multiSelectionEnabled = multiSelectionEnabled;

	}

	@Override
	public void setCurrentDirectory(File currentDirectory) {
	    this.currentDirectory = currentDirectory;
	}

	@Override
	public File getCurrentDirectory() {
	    if (currentDirectory == null) {
		currentDirectory = new File(System.getProperty("user.home"));
	    }
	    return currentDirectory;
	}

	@Override
	public void setSelectedFile(File selectedFile) {
	    this.selectedFile = selectedFile;

	}

	@Override
	public void setFileFilter(FileFilter fileFilter) {
	    this.fileFilter = fileFilter;
	}

	@Override
	public File getSelectedFile() {
	    return selectedFile;
	}

	@Override
	public File[] getSelectedFiles() {
	    return selectedFiles;

	}

	@Override
	public int showOpenDialog(JFrame parent) {
	    return showDialog(parent, FileDialog.LOAD);
	}

	@Override
	public int showSaveDialog(JFrame parent) {
	    return showDialog(parent, FileDialog.SAVE);
	}

	private int showDialog(JFrame parent, int mode) {
	    FileDialog fileDialog = new FileDialog(parent, dialogTitle, mode);

	    JDK.FileDialog.setMultipleMode(fileDialog, multiSelectionEnabled);

	    
	    // Create file name file wrapper. Note that this does not work in
	    // Windows. Therefore the file will be called, but no filtering will
	    // be active. Therefore the additional file extension filter support
	    // (see below) was added.
	    FilenameFilter fileNameFilter = null;
	    if (fileFilter != null) {
		fileNameFilter = new FilenameFilter() {

		    @Override
		    public boolean accept(File dir, String name) {
			boolean result = fileFilter.accept(new File(dir, name));
			return result;

		    }
		};
	    }
	    fileDialog.setFilenameFilter(fileNameFilter);

	    // Preset current directory.
	    File currentDirectory = getCurrentDirectory();
	    if (currentDirectory != null) {
		fileDialog.setDirectory(currentDirectory.getAbsolutePath());
	    } else {
		fileDialog.setDirectory(null);
	    }

	    // Preset current select file.
	    if (selectedFile != null) {
		fileDialog.setFile(selectedFile.getName());
	    } else {
		if (fileFilter instanceof FileExtensionFileFilter) {
		    FileExtensionFileFilter fileExtensionFileFilter = (FileExtensionFileFilter) fileFilter;
		    fileDialog.setFile("*" + fileExtensionFileFilter.getFileExtension());
		} else {
		    fileDialog.setFile(null);
		}
	    }

	    int result = JFileChooser.CANCEL_OPTION;
	    fileDialog.setVisible(true);

	    // Take over new current directory.
	    String currentDirectoryPath = fileDialog.getDirectory();
	    if (currentDirectoryPath == null || StringUtility.isEmpty(currentDirectoryPath)) {
		setCurrentDirectory(null);

	    } else {
		setCurrentDirectory(new File(currentDirectoryPath));
	    }

	    // Take over new select file(s).
	    selectedFile = null;
	    selectedFiles = null;
	    if (multiSelectionEnabled) {
		selectedFiles = JDK.FileDialog.getFiles(fileDialog);
		if (selectedFiles.length > 0) {
		    result = JFileChooser.APPROVE_OPTION;
		}
	    } else {
		String selectedFileName = fileDialog.getFile();
		if (selectedFileName != null) {
		    result = JFileChooser.APPROVE_OPTION;
		    selectedFile = new File(currentDirectoryPath, selectedFileName);
		}
	    }

	    return result;
	}
    }

    /**
     * Factory method the returns the correct implementation.
     * 
     * @return The new instance, not <code>null</code>.
     */
    public static FileChooser createInstance() {
	FileChooser result;

	boolean useFileChooser = false;
	if (useFileChooser) {
	    result = new FileChooserWrapper();
	} else {
	    result = new FileDialogWrapper();
	}
	return result;
    }

    public abstract void setDialogTitle(String dialogTitle);

    public abstract void setMultiSelectionEnabled(boolean multiSelectionEnabled);

    public abstract void setCurrentDirectory(File dir);

    public abstract File getCurrentDirectory();

    public abstract void setSelectedFile(File file);

    public abstract void setFileFilter(FileFilter filefilter);

    public abstract File getSelectedFile();

    public abstract File[] getSelectedFiles();

    public abstract int showOpenDialog(JFrame parent);

    public abstract int showSaveDialog(JFrame parent);

}

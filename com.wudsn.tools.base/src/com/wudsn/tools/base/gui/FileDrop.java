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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * This class makes it easy to drag and drop files from the operating system to
 * a Java program. Any <tt>java.awt.Component</tt> can be dropped onto, but only
 * <tt>javax.swing.JComponent</tt>s will indicate the drop event with a changed
 * border.
 * <p/>
 * To use this class, construct a new <tt>FileDrop</tt> by passing it the target
 * component and a <tt>Listener</tt> to receive notification when file(s) have
 * been dropped. Here is an example:
 * <p/>
 * <code><pre>
 *      JPanel myPanel = new JPanel();
 *      new FileDrop( myPanel, new FileDrop.Listener()
 *      {   public void filesDropped( File[] files )
 *          {   
 *             
 *              ...
 *          }   
 *      }); 
 * </pre></code>
 * 
 * @author Robert Harder (rharder@users.sf.net) - original version
 * @author Nathan Blomquist - Linux (KDE/Gnome) support added
 * @author Peter Dell - stripped version
 */
public final class FileDrop {

    private final class FileDropTargetListener implements DropTargetListener {
	private final Border dragBorder;
	private final Component c;
	private final Listener listener;

	public FileDropTargetListener(Border dragBorder, Component c, Listener listener) {
	    this.dragBorder = dragBorder;
	    this.c = c;
	    this.listener = listener;
	}

	 @Override
	public void dragEnter(DropTargetDragEvent event) {
	    // Is this an acceptable drag event?
	    if (isDraggingFileList(event) && listener.isDropAllowed()) {
		// If it's a Swing component, set its border
		if (c instanceof JComponent) {
		    JComponent jc = (JComponent) c;
		    normalBorder = jc.getBorder();
		    jc.setBorder(dragBorder);
		}

		event.acceptDrag(DnDConstants.ACTION_COPY);
	    } else {
		event.rejectDrag();
	    }
	}

	/*
	 * This is called continually as long as the mouse is over the drag
	 * target.
	 */
	 @Override
	public void dragOver(DropTargetDragEvent event) {
	}

	 @Override
	public void drop(DropTargetDropEvent event) {
	    try { // Get whatever was dropped
		Transferable transferable = event.getTransferable();

		// Is it a file list?
		if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
		    // Say we'll take it.
		    event.acceptDrop(DnDConstants.ACTION_COPY);

		    @SuppressWarnings("unchecked")
		    List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
		    File[] files = new File[fileList.size()];
		    fileList.toArray(files);

		    // Notify listener.
		    listener.filesDropped(files);

		    // Mark that drop is completed.
		    event.getDropTargetContext().dropComplete(true);
		} else {
		    // Check for a reader flavor.
		    DataFlavor[] flavors = transferable.getTransferDataFlavors();
		    boolean handled = false;
		    for (int zz = 0; zz < flavors.length; zz++) {
			if (flavors[zz].isRepresentationClassReader()) {
			    event.acceptDrop(DnDConstants.ACTION_COPY);

			    Reader reader = flavors[zz].getReaderForText(transferable);

			    BufferedReader br = new BufferedReader(reader);

			    listener.filesDropped(createFileArray(br));

			    // Mark that drop is completed.
			    event.getDropTargetContext().dropComplete(true);
			    handled = true;
			    break;
			}
		    }
		    if (!handled) {
			event.rejectDrop();
		    }
		}
	    } catch (IOException io) {
		throw new RuntimeException(io);
		// event.rejectDrop();
	    } catch (UnsupportedFlavorException ufe) {

		event.rejectDrop();
	    } finally {
		// If it's a Swing component, reset its border
		if (c instanceof JComponent) {
		    JComponent jc = (JComponent) c;
		    jc.setBorder(normalBorder);
		}
	    }
	}

	 @Override
	public void dragExit(DropTargetEvent event) {
	    // If it's a Swing component, reset its border
	    if (c instanceof JComponent) {
		JComponent jc = (JComponent) c;
		jc.setBorder(normalBorder);
	    }
	}

	 @Override
	public void dropActionChanged(DropTargetDragEvent event) {
	    // Is this an acceptable drag event?
	    if (isDraggingFileList(event)) {
		event.acceptDrag(DnDConstants.ACTION_COPY);
	    } else {
		event.rejectDrag();
	    }
	}
    }

    public static interface Listener {

	/**
	 * This method is called when file are about to be dropped.
	 * 
	 * @return <code>true</code> if the target allows dropping the files.
	 */
	public abstract boolean isDropAllowed();

	/**
	 * This method is called when files have been successfully dropped.
	 * 
	 * @param files
	 *            The array of files that were dropped, not empty and not
	 *            <code>null</code>.
	 */
	public abstract void filesDropped(File[] files);

    }

    private static String ZERO_CHAR_STRING = String.valueOf((char) 0);

    // Instance variables.
    DropTargetListener dropListener;
    Border normalBorder;

    /**
     * Create a new instance.
     * 
     * @param c
     *            Component on which files will be dropped, not
     *            <code>null</code>.
     * @param recursive
     *            <code>true</code> to recursively set children as drop targets.
     * @param listener
     *            Drop listener, not <code>null</code>.
     * @since 1.0
     */
    public FileDrop(Component c, boolean recursive, Listener listener) {
	if (c == null) {
	    throw new IllegalArgumentException("Parameter 'c' must not be null.");
	}
	if (listener == null) {
	    throw new IllegalArgumentException("Parameter 'listener' must not be null.");
	}
	Color borderColor = new Color(0f, 0f, 1f, 0.25f);
	Border dragBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor);

	dropListener = new FileDropTargetListener(dragBorder, c, listener);

	// Make the component (and possibly children) drop targets
	makeDropTarget(c, recursive);

    }

    private void makeDropTarget(final Component c, boolean recursive) {
	// Make drop target
	DropTarget dt = new DropTarget();
	try {
	    dt.addDropTargetListener(dropListener);
	} catch (TooManyListenersException e) {
	    throw new RuntimeException(e);
	}

	// Listen for hierarchy changes and remove the drop target when the
	// parent gets cleared out.
	c.addHierarchyListener(new HierarchyListener() {
	     @Override
	    public void hierarchyChanged(HierarchyEvent event) {
		Component parent = c.getParent();
		if (parent == null) {
		    c.setDropTarget(null);
		} else {
		    new DropTarget(c, dropListener);
		}
	    }
	});
	if (c.getParent() != null)
	    new DropTarget(c, dropListener);

	if (recursive && (c instanceof Container)) {
	    // Get the container and it's components
	    Container cont = (Container) c;
	    Component[] comps = cont.getComponents();

	    // Set it's components as listeners also
	    for (int i = 0; i < comps.length; i++)
		makeDropTarget(comps[i], recursive);
	}
    }

    static File[] createFileArray(BufferedReader bufferedReader) {
	if (bufferedReader == null) {
	    throw new IllegalArgumentException("Parameter 'bufferedReader' must not be null.");
	}
	try {
	    List<File> list = new ArrayList<File>();
	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) {
		try {
		    // KDE seems to append a 0 char to the end of the reader
		    if (ZERO_CHAR_STRING.equals(line))
			continue;

		    File file = new File(new URI(line));
		    list.add(file);
		} catch (URISyntaxException ex) {
		    throw new RuntimeException(ex);
		}
	    }

	    return list.toArray(new File[list.size()]);
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Determine if the dragged data is a file list.
     * 
     * @param event
     *            The event, not <code>null</code>.
     * @return <code>true</code> if the dragged data is a file list.
     */
    static boolean isDraggingFileList(final DropTargetDragEvent event) {
	if (event == null) {
	    throw new IllegalArgumentException("Parameter 'event' must not be null.");
	}

	DataFlavor[] flavors = event.getCurrentDataFlavors();
	boolean ok = false;
	for (int i = 0; !ok && i < flavors.length; i++) {
	    DataFlavor curFlavor = flavors[i];
	    if (curFlavor.equals(DataFlavor.javaFileListFlavor) || curFlavor.isRepresentationClassReader()) {
		ok = true;
	    }
	}
	return ok;
    }

}

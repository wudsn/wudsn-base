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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import com.wudsn.tools.base.Actions;

/**
 * Base class for simple dialogs.
 * 
 * @author Peter Dell
 */
@SuppressWarnings("serial")
public abstract class SimpleDialog {

    public static final String ACTION_COMMAND_OK = "OK";
    public static final String ACTION_COMMAND_CANCEL = "CANCEL";

    private final JFrame parent;
    private final String title;
    private final boolean modal;

    JDialog dialog;
    private JButton okButton;
    private Action cancelAction;

    protected SimpleDialog(JFrame parent, String title, boolean modal) {
	if (parent == null) {
	    throw new IllegalArgumentException("Parameter 'parent' must not be null.");
	}

	if (title == null) {
	    throw new IllegalArgumentException("Parameter 'title' must not be null.");
	}
	this.parent = parent;
	this.title = title;
	this.modal = modal;
    }

    public void show() {
	if (dialog == null) {
	    dialog = new JDialog(parent, title, modal);
	    dialog.setIconImage(parent.getIconImage());
	    initComponents(dialog);
	}
	dataToUI();
	dialog.getRootPane().getDefaultButton().requestFocusInWindow();
	dialog.setVisible(true);
    }

    protected void initComponents(JDialog dialog) {
    }
    
    protected void dataToUI(){
	
    }

    protected final void initButtonBar() {

	Container pane = dialog.getContentPane();

	okButton = ElementFactory.createButton(Actions.ButtonBar_OK, false);
	Box buttonBar = ElementFactory.createButtonBar();
	buttonBar.add(okButton);
	pane.add(buttonBar, BorderLayout.SOUTH);

	okButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent evt) {
		dialog.setVisible(false);
	    }
	});

	cancelAction = new AbstractAction() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		dialog.setVisible(false);
	    }
	};
	ElementFactory.setDialogDefaultButtons(dialog.getRootPane(), okButton, cancelAction);
	okButton.requestFocus();

	dialog.pack();
	dialog.setLocationRelativeTo(dialog.getParent());
    }
}
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.wudsn.tools.base.Actions;

/**
 * Simple modal dialog with "OK" and "Cancel" buttons.
 * 
 * @author Peter Dell
 * 
 */
@SuppressWarnings("serial")
public abstract class ModalDialog extends JDialog implements ActionListener {

	protected final JPanel fieldsPane;

	private Box buttonBar;
	private final JButton okButton;
	private final JButton cancelButton;

	protected transient boolean okPressed;

	public ModalDialog(JFrame parent, String title) {
		super(parent, title, true);

		Container pane = getContentPane();
		JPanel dataPane = new JPanel(new BorderLayout());
		pane.add(dataPane, BorderLayout.NORTH);
		fieldsPane = new JPanel(new SpringLayout());
		dataPane.add(fieldsPane, BorderLayout.NORTH);

		okButton = ElementFactory.createButton(Actions.ButtonBar_OK, false);
		Action cancelAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ModalDialog.this.actionPerformed(e);

			}
		};
		cancelButton = new JButton(cancelAction);
		ElementFactory.setButtonText(cancelButton, Actions.ButtonBar_Cancel);

		buttonBar = ElementFactory.createButtonBar();
		buttonBar.add(okButton);
		buttonBar.add(cancelButton);
		pane.add(buttonBar, BorderLayout.SOUTH);

		okButton.addActionListener(this);

		ElementFactory.setDialogDefaultButtons(getRootPane(), okButton,
				cancelButton.getAction());
	}

	protected final void addButtonBarButton(JButton button) {
		if (button == null) {
			throw new IllegalArgumentException(
					"Parameter 'button' must not be null.");
		}
		buttonBar.add(button, 0);
	}

	protected final void showModal(JComponent focusField) {
		if (focusField == null) {
			throw new IllegalArgumentException(
					"Parameter 'focusField' must not be null.");
		}
		dataToUi();
		okPressed = false;
		pack(); // Resize to fit content
		setLocationRelativeTo(getParent());
		focusField.requestFocus();
		setVisible(true);
	}

	protected void dataFromUi() {
	}

	protected void dataToUi() {

	}

	protected boolean validateOK() {
		return true;

	}

	@Override
	public final void actionPerformed(ActionEvent evt) {
		okPressed = evt.getSource() == okButton;
		if (okPressed) {
			dataFromUi();
			dataToUi();
			if (!validateOK()) {
				return;
			}
		}

		setVisible(false);

	}

}
/**
 * Copyright (C) 2013 - 2014 <a href="https://www.wudsn.com" target="_top">Peter Dell</a>
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
 * along with the WUDSN software distribution. If not, see <https://www.gnu.org/licenses/>.
 */

package com.wudsn.tools.base.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.wudsn.tools.base.Actions;

/**
 * A modal dialog with "OK" and "Cancel" buttons, shown once.
 * <p>
 * A subclass builds its form in the constructor, either in {@link
 * #fieldsPane} (a {@link SpringLayout} panel at the top, e.g. with {@link
 * SpringUtilities}) or as its own panel added at {@link BorderLayout#CENTER}
 * of the content pane. It shows itself with {@link #showModal(JComponent)}
 * and then reads {@link #okPressed}.
 * <ul>
 * <li>OK runs {@link #dataFromUi()}, {@link #dataToUi()} and {@link
 * #validateOK()}; if that returns <code>false</code>, the dialog stays
 * open.</li>
 * <li>Cancel, Escape and the window's close box close it with {@link
 * #okPressed} <code>false</code>, as does {@link #close()}, which a subclass
 * calls from a button of its own.</li>
 * <li>OK is the default button. OK and Cancel have their mnemonics.</li>
 * <li>{@link #addButtonBarButton(JButton)} adds buttons at the left of the
 * button bar.</li>
 * <li>{@link #showModal(JComponent)} disposes the dialog once it is closed: a
 * modal dialog is created for one use.</li>
 * </ul>
 *
 * @author Peter Dell
 */
@SuppressWarnings("serial")
public abstract class ModalDialog extends JDialog implements ActionListener {

	protected final JPanel fieldsPane;

	private Box buttonBar;
	private final JButton okButton;
	private final JButton cancelButton;

	protected transient boolean okPressed;

	/**
	 * Creates the dialog.
	 * 
	 * @param owner
	 *            The window the dialog belongs to and is centered on, or
	 *            <code>null</code>.
	 * @param title
	 *            The title, not <code>null</code>.
	 */
	public ModalDialog(Window owner, String title) {
		super(owner, title, ModalityType.APPLICATION_MODAL);

		Container pane = getContentPane();
		JPanel dataPane = new JPanel(new BorderLayout());
		pane.add(dataPane, BorderLayout.NORTH);
		fieldsPane = new JPanel(new SpringLayout());
		dataPane.add(fieldsPane, BorderLayout.NORTH);

		okButton = ElementFactory.createButton(Actions.ButtonBar_OK, true);
		Action cancelAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ModalDialog.this.actionPerformed(e);

			}
		};
		cancelButton = new JButton(cancelAction);
		ElementFactory.setButtonTextAndMnemonic(cancelButton, Actions.ButtonBar_Cancel);

		buttonBar = ElementFactory.createButtonBar();
		buttonBar.add(okButton);
		buttonBar.add(cancelButton);
		pane.add(buttonBar, BorderLayout.SOUTH);

		okButton.addActionListener(this);

		ElementFactory.setDialogDefaultButtons(getRootPane(), okButton, cancelButton.getAction());
	}

	protected final void addButtonBarButton(JButton button) {
		if (button == null) {
			throw new IllegalArgumentException("Parameter 'button' must not be null.");
		}
		buttonBar.add(button, 0);
	}

	/**
	 * Shows the dialog and blocks until it is closed, then disposes it.
	 * 
	 * @param focusField
	 *            The field to focus first, not <code>null</code>.
	 */
	protected final void showModal(JComponent focusField) {
		if (focusField == null) {
			throw new IllegalArgumentException("Parameter 'focusField' must not be null.");
		}
		dataToUi();
		okPressed = false;
		pack(); // Resize to fit content
		setLocationRelativeTo(getParent());
		focusField.requestFocus();
		setVisible(true);
		dispose();
	}

	/** Closes the dialog without OK, e.g. from a button of the subclass that ends the dialog with its own result. */
	protected final void close() {
		okPressed = false;
		setVisible(false);
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
				okPressed = false; // Still open: the close box must not count as OK.
				return;
			}
		}

		setVisible(false);

	}

}

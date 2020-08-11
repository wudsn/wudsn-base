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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.wudsn.tools.base.Actions;
import com.wudsn.tools.base.repository.Action;

/**
 * Wrapper for {@link JOptionPane}.
 * 
 * @author Peter Dell
 */

public final class StandardDialog {

    // Result array indexes.
    public static final int CHOICE_ACTION = 0;
    public static final int BUTTON_ACTION = 1;

    private final static class ButtonActionListener implements ActionListener {
	private JOptionPane optionsPane;
	private JDialog dialog;
	private Action action;

	public ButtonActionListener(JOptionPane optionsPane, JDialog dialog, Action action) {
	    if (optionsPane == null) {
		throw new IllegalArgumentException("Parameter 'optionsPane' must not be null.");
	    }
	    if (dialog == null) {
		throw new IllegalArgumentException("Parameter 'dialog' must not be null.");
	    }
	    if (action == null) {
		throw new IllegalArgumentException("Parameter 'action' must not be null.");
	    }

	    this.optionsPane = optionsPane;
	    this.dialog = dialog;
	    this.action = action;
	}

	 @Override
	public void actionPerformed(ActionEvent e) {
	    optionsPane.setValue(action);
	    dialog.setVisible(false);
	}
    }

    public static Action[] showConfirmation(Component parentComponent, String title, String message, Action[] choices,
	    Action[] actions, Action defaultAction, Action closeAction) {

	if (parentComponent == null) {
	    throw new IllegalArgumentException("Parameter 'parentComponent' must not be null.");
	}
	if (title == null) {
	    throw new IllegalArgumentException("Parameter 'title' must not be null.");
	}
	if (message == null) {
	    throw new IllegalArgumentException("Parameter 'message' must not be null.");
	}
	if (actions == null) {
	    throw new IllegalArgumentException("Parameter 'actions' must not be null.");
	}
	if (defaultAction == null) {
	    throw new IllegalArgumentException("Parameter 'defaultAction' must not be null.");
	}
	if (closeAction == null) {
	    throw new IllegalArgumentException("Parameter 'closeAction' must not be null.");
	}
	return showInternal(parentComponent, title, JOptionPane.QUESTION_MESSAGE, message, choices, actions,
		defaultAction, closeAction);
    }

    public static void showErrorMessage(Component parentComponent, String message, String title) {
	if (parentComponent == null) {
	    throw new IllegalArgumentException("Parameter 'parentComponent' must not be null.");
	}
	if (message == null) {
	    throw new IllegalArgumentException("Parameter 'message' must not be null.");
	}
	if (title == null) {
	    throw new IllegalArgumentException("Parameter 'title' must not be null.");
	}
	showInternal(parentComponent, title, JOptionPane.ERROR_MESSAGE, message, null,
		new Action[] { Actions.ButtonBar_OK }, Actions.ButtonBar_OK, Actions.ButtonBar_OK);

    }

    private static Action[] showInternal(Component parentComponent, String title, int messageType, String message,
	    Action[] choices, Action[] actions, Action defaultAction, Action closeAction) {

	JPanel panel = new JPanel(new BorderLayout());
	panel.add(new JLabel(message), BorderLayout.NORTH);
	ButtonGroup buttomGroup = new ButtonGroup();

	JRadioButton[] choiceButtons;
	if (choices != null) {
	    choiceButtons = new JRadioButton[choices.length];
	    Box box = Box.createVerticalBox();
	    panel.add(box, BorderLayout.WEST);
	    for (int i = 0; i < choices.length; i++) {
		JRadioButton button = new JRadioButton();
		button.setSelected(i == 0);
		ElementFactory.setButtonTextAndMnemonic(button, choices[i]);
		box.add(button);
		buttomGroup.add(button);
		choiceButtons[i] = button;
	    }
	} else {
	    choiceButtons = null;
	}

	JButton[] buttons = new JButton[actions.length];
	JButton defaultButton = null;
	for (int i = 0; i < actions.length; i++) {
	    Action action = actions[i];
	    boolean withMnemonic = (action != defaultAction && action != closeAction);
	    JButton button = ElementFactory.createButton(action, withMnemonic);
	    if (action == defaultAction) {
		defaultButton = button;
	    }
	    buttons[i] = button;
	}

	JOptionPane optionsPane = new JOptionPane(panel, messageType, JOptionPane.DEFAULT_OPTION, null, buttons,
		defaultButton);
	JDialog dialog = optionsPane.createDialog(parentComponent, title);
	for (int i = 0; i < buttons.length; i++) {
	    buttons[i].addActionListener(new ButtonActionListener(optionsPane, dialog, actions[i]));
	}

	dialog.setVisible(true);
	Object value = optionsPane.getValue();
	if (value == null || Integer.valueOf(-1).equals(value)) {
	    return new Action[] { null, closeAction };
	}
	Action choice = null;
	if (choices != null && choiceButtons != null) {
	    for (int i = 0; i < buttons.length && choice == null; i++) {
		if (choiceButtons[i].isSelected()) {
		    choice = choices[i];
		}
	    }
	}
	return new Action[] { choice, (Action) optionsPane.getValue() };
    }
}
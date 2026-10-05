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

package com.wudsn.tools.base.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wudsn.tools.base.Actions;

/**
 * Shows a {@link ModalDialog} and drives it while it is open - one step per
 * timer tick inside its modal loop - through OK with a failing and a passing
 * check, Cancel, Escape, the close box and {@link ModalDialog#close()} -
 * and the variant with "OK" only.
 * Needs a display; skipped when headless.
 */
public class ModalDialogTest {

	/** A dialog with one field, whose check passes only when {@link #valid} is set. */
	private static final class TestDialog extends ModalDialog {
		final JTextField field = new JTextField(10);
		boolean valid;
		int validateCount;

		TestDialog() {
			this(true);
		}

		TestDialog(boolean cancelButton) {
			super(null, "Test", cancelButton);
			getContentPane().add(field, BorderLayout.CENTER);
		}

		@Override
		protected boolean validateOK() {
			validateCount++;
			return valid;
		}

		boolean showDialog() {
			showModal(field);
			return okPressed;
		}

		void addButton(JButton button) {
			addButtonBarButton(button);
		}

		void closeFromSubclass() {
			close();
		}

		JButton okButtonOfSubclass() {
			return getOKButton();
		}
	}

	private TestDialog dialog;

	@BeforeEach
	public void setUp() {
		assumeFalse(GraphicsEnvironment.isHeadless(), "ModalDialogTest needs a display");
	}

	/** Shows the dialog on the event dispatch thread and runs the steps while it is open; returns {@code okPressed}. */
	private boolean showWith(final Runnable... steps) throws Exception {
		final boolean[] result = new boolean[1];
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				final int[] next = { 0 };
				final Timer timer = new Timer(150, null);
				timer.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (next[0] < steps.length) {
							steps[next[0]++].run();
						} else {
							timer.stop();
						}
					}
				});
				timer.start();
				result[0] = dialog.showDialog();
				timer.stop();
			}
		});
		return result[0];
	}

	private static AbstractButton findButton(Container container, String label) {
		for (Component component : container.getComponents()) {
			if (component instanceof AbstractButton && label.equals(((AbstractButton) component).getText())) {
				return (AbstractButton) component;
			}
			if (component instanceof Container) {
				AbstractButton result = findButton((Container) component, label);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	private static String text(com.wudsn.tools.base.repository.Action action) {
		return action.getLabel().replace("&", "");
	}

	private AbstractButton okButton() {
		return findButton(dialog.getContentPane(), text(Actions.ButtonBar_OK));
	}

	private AbstractButton cancelButton() {
		return findButton(dialog.getContentPane(), text(Actions.ButtonBar_Cancel));
	}

	@Test
	public void testOKWithCheck() throws Exception {
		dialog = new TestDialog();
		final List<Boolean> visibleAfterFailedCheck = new ArrayList<Boolean>();
		boolean ok = showWith(new Runnable() {
			@Override
			public void run() {
				okButton().doClick();
				visibleAfterFailedCheck.add(Boolean.valueOf(dialog.isVisible()));
			}
		}, new Runnable() {
			@Override
			public void run() {
				dialog.valid = true;
				okButton().doClick();
			}
		});
		assertEquals("[true]", visibleAfterFailedCheck.toString());
		assertEquals(2, dialog.validateCount);
		assertTrue(ok);
		assertFalse(dialog.isDisplayable()); // Disposed.
	}

	@Test
	public void testCancel() throws Exception {
		dialog = new TestDialog();
		dialog.valid = true;
		assertFalse(showWith(new Runnable() {
			@Override
			public void run() {
				cancelButton().doClick();
			}
		}));
		assertEquals(0, dialog.validateCount);
		assertFalse(dialog.isDisplayable());
	}

	@Test
	public void testEscape() throws Exception {
		dialog = new TestDialog();
		dialog.valid = true;
		assertFalse(showWith(new Runnable() {
			@Override
			public void run() {
				dialog.getRootPane().getActionMap().get("CANCEL")
						.actionPerformed(new ActionEvent(dialog, ActionEvent.ACTION_PERFORMED, "CANCEL"));
			}
		}));
	}

	@Test
	public void testCloseBoxAfterFailedCheck() throws Exception {
		dialog = new TestDialog();
		assertFalse(showWith(new Runnable() {
			@Override
			public void run() {
				okButton().doClick(); // The check fails, the dialog stays open.
			}
		}, new Runnable() {
			@Override
			public void run() {
				dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
			}
		}));
		assertFalse(dialog.isDisplayable());
	}

	@Test
	public void testClose() throws Exception {
		dialog = new TestDialog();
		dialog.valid = true;
		assertFalse(showWith(new Runnable() {
			@Override
			public void run() {
				dialog.closeFromSubclass();
			}
		}));
	}

	@Test
	public void testOKOnly() throws Exception {
		dialog = new TestDialog(false);
		assertTrue(cancelButton() == null);
		assertTrue(okButton() == dialog.getRootPane().getDefaultButton());
		int buttons = 0;
		for (Component component : okButton().getParent().getComponents()) {
			if (component instanceof AbstractButton) {
				buttons++;
			}
		}
		assertEquals(1, buttons);
		dialog.dispose();

		// OK closes with okPressed.
		dialog = new TestDialog(false);
		dialog.valid = true;
		assertTrue(showWith(new Runnable() {
			@Override
			public void run() {
				okButton().doClick();
			}
		}));

		// Escape and the close box still close it, without.
		dialog = new TestDialog(false);
		dialog.valid = true;
		assertFalse(showWith(new Runnable() {
			@Override
			public void run() {
				dialog.getRootPane().getActionMap().get("CANCEL")
						.actionPerformed(new ActionEvent(dialog, ActionEvent.ACTION_PERFORMED, "CANCEL"));
			}
		}));
		dialog = new TestDialog(false);
		dialog.valid = true;
		assertFalse(showWith(new Runnable() {
			@Override
			public void run() {
				dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
			}
		}));
		assertFalse(dialog.isDisplayable());
	}

	@Test
	public void testButtons() throws Exception {
		dialog = new TestDialog();
		assertTrue(okButton() == dialog.getRootPane().getDefaultButton());
		assertTrue(okButton() == dialog.okButtonOfSubclass());
		assertNotEquals(0, okButton().getMnemonic());
		assertNotEquals(0, cancelButton().getMnemonic());

		JButton first = new JButton("First");
		JButton second = new JButton("Second");
		dialog.addButton(second);
		dialog.addButton(first); // Each one goes to the far left.
		Box buttonBar = (Box) okButton().getParent();
		List<String> order = new ArrayList<String>();
		for (Component component : buttonBar.getComponents()) {
			if (component instanceof AbstractButton) {
				order.add(((AbstractButton) component).getText());
			}
		}
		assertEquals("[First, Second, " + text(Actions.ButtonBar_OK) + ", " + text(Actions.ButtonBar_Cancel) + "]",
				order.toString());

		// Neighboring buttons are BUTTON_GAP apart.
		dialog.pack();
		AbstractButton firstButton = findButton(dialog.getContentPane(), "First");
		AbstractButton secondButton = findButton(dialog.getContentPane(), "Second");
		assertEquals(ModalDialog.BUTTON_GAP, secondButton.getX() - (firstButton.getX() + firstButton.getWidth()));
		assertEquals(ModalDialog.BUTTON_GAP, cancelButton().getX() - (okButton().getX() + okButton().getWidth()));
		dialog.dispose();
	}
}

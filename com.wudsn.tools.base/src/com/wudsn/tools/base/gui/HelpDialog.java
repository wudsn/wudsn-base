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
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import com.wudsn.tools.base.Texts;
import com.wudsn.tools.base.common.Application;
import com.wudsn.tools.base.common.ClassPathUtility;
import com.wudsn.tools.base.common.ResourceUtility;
import com.wudsn.tools.base.common.ResourceUtility.ResourceModifier;
import com.wudsn.tools.base.repository.NLS;

/**
 * Help contents dialog.
 * 
 * @author Peter Dell
 */
public final class HelpDialog extends SimpleDialog {

	private final static class VersionResourceModifier implements ResourceModifier {
		public VersionResourceModifier() {

		}

		@Override
		public byte[] modifyResource(URL url, byte[] data) {
			if (url.getFile().toLowerCase().endsWith(".html")) {
				try {
					Charset charset = Charset.forName("UTF-8");
					String html = new String(data, charset);
					String localVersion = Application.getInstance().getLocalVersion();
					html = html.replaceAll("\\$\\{version\\}", localVersion);
					data = html.getBytes(charset);
				} catch (UnsupportedCharsetException ex) {
					throw new RuntimeException(ex);
				}

			}
			return data;
		}
	}

	private String path;
	private int width;
	private int height;
	private List<ResourceModifier> resourceModifierList;

	JEditorPane jep;

	public HelpDialog(JFrame parent, String path, int width, int height, List<ResourceModifier> resourceModifierList) {
		super(parent, Texts.HelpDialog_Title, false);
		if (path == null) {
			throw new IllegalArgumentException("Parameter 'path' must not be null.");
		}
		this.path = path;
		this.width = width;
		this.height = height;
		this.resourceModifierList = new ArrayList<ResourceModifier>(2);
		this.resourceModifierList.add(new VersionResourceModifier());

		if (resourceModifierList != null) {
			this.resourceModifierList.addAll(resourceModifierList);
		}
		;
	}

	@Override
	protected void initComponents(JDialog dialog) {

		Container pane = dialog.getContentPane();

		jep = new JEditorPane();
		jep.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(jep);
		pane.add(scrollPane, BorderLayout.CENTER);

		String fullPath = NLS.getResourcePath(path);
		if (fullPath == null) {
			throw new RuntimeException("Help resource '" + path + "' not found in path.");
		}
		URL url = ClassPathUtility.class.getClassLoader().getResource(fullPath);
		setPage(url);
		jep.setPreferredSize(new Dimension(width, height));

		// Add listener for hyperlinks in the text.
		jep.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == EventType.ACTIVATED) {
					URL url = e.getURL();
					String protocol = url.getProtocol();
					if (protocol.equals("file") || protocol.equals("jar")) {
						setPage(url);
						jep.scrollToReference(e.getDescription());
						return;
					} else if (protocol.equals("http") || protocol.equals("https")) {
						if (Desktop.isDesktopSupported()) {
							try {
								Desktop.getDesktop().browse(url.toURI());
							} catch (IOException ex) {
								throw new RuntimeException("Cannot load page '" + url + "'.", ex);
							} catch (URISyntaxException ex) {
								throw new RuntimeException("Cannot load page '" + url + "'.", ex);
							}
						}
					}
				}

			}
		});
		initButtonBar();
	}

	/**
	 * Sets the page content based on an URL.
	 * 
	 * @param url The URL, not <code>null</code>.
	 */
	void setPage(URL url) {
		if (url == null) {
			throw new IllegalArgumentException("Parameter 'url' must not be null.");
		}
		try {
			url = new URL(url, "", ResourceUtility.createStreamHandler(resourceModifierList));
			jep.setPage(url);
		} catch (IOException ex) {
			jep.setText(ex.getMessage());
		}
	}
}
/* OpenLogViewer
 *
 * Copyright 2012
 *
 * This file is part of the OpenLogViewer project.
 *
 * OpenLogViewer software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenLogViewer software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any OpenLogViewer software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
 */
package org.diyefi.openlogviewer.subframes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.diyefi.openlogviewer.OpenLogViewer;

public class AboutFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int FRAME_WIDTH = 500;
	private static final int FRAME_HEIGHT = 320;
	private static final int SPACER_HEIGHT = 20;

	private static final HyperlinkListener HLL = new HyperlinkListener() {
		public void hyperlinkUpdate(final HyperlinkEvent hle) {
			if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(hle.getDescription()));
				} catch (IOException e) {
					OpenLogViewer.getInstance().defaultBrowserNotFound();
				}
			}
		}
	};

	private String appName;
	
	public AboutFrame(final String newAppName) {
		appName = newAppName;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setLayout(new BorderLayout());
		OpenLogViewer.setupWindowKeyBindings(this);
		setupNorthPanel();
		setupCenterPanel();
		setupSouthPanel();
		setTitle("About - " + appName);
		pack();
	}
	
	private void setupNorthPanel() {
		final JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

		final ImageIcon icon = createImageIcon("logo64x64.png", appName + " Logo");
		final JLabel logo = new JLabel(icon);
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		northPanel.add(logo);

		addTextToPanel(northPanel, appName);
		final String appVersion = "0.0.3-SNAPSHOT";
		addTextToPanel(northPanel, "Version: " + appVersion);

		add(northPanel, BorderLayout.NORTH);
	}

	private void setupCenterPanel() {
		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		final JTextPane description = new JTextPane();
		final SimpleAttributeSet simpleAttrib = new SimpleAttributeSet();
		StyleConstants.setAlignment(simpleAttrib, StyleConstants.ALIGN_JUSTIFIED);
		description.setParagraphAttributes(simpleAttrib, false);
		description.setText("OpenLogViewer is a free, open source, cross-platform"
				+ " data-log visualization application that currently supports many"
				+ " CSV data-log formats (including MegaSquirt style), and FreeEMS"
				+ " binary data-log formats. OpenLogViewer is currently in its"
				+ " infancy. While in the future it will be amazing, right now it"
				+ " has all of the basics that you need.");
		description.setEditable(false);
		description.setOpaque(false);
		description.setBorder(BorderFactory.createEmptyBorder(SPACER_HEIGHT,
				SPACER_HEIGHT,
				SPACER_HEIGHT,
				SPACER_HEIGHT));
		description.setAlignmentX(Component.CENTER_ALIGNMENT);
		centerPanel.add(description);
		addTextToPanel(centerPanel, "Website: <a href='http://olv.diyefi.org'>http://olv.diyefi.org</a>");
		addTextToPanel(centerPanel, "Support: <a href='http://forum.diyefi.org/viewforum.php?f=32'>http://forum.diyefi.org/viewforum.php?f=32</a>");
		addTextToPanel(centerPanel, "Issues: <a href='http://issues.freeems.org'>http://issues.freeems.org</a>");
		centerPanel.add(Box.createVerticalStrut(SPACER_HEIGHT));
		add(centerPanel, BorderLayout.CENTER);
	}

	private void setupSouthPanel() {
		final JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		addTextToPanel(southPanel, "Built by <a href='http://maven.apache.org/'>Maven</a> 3");
		add(southPanel, BorderLayout.SOUTH);
	}

	private void addTextToPanel(final JPanel panel, final String text) {
		JEditorPane newPane = null;
		final Font font = UIManager.getFont("Label.font");
		final StringBuilder bodyRule = new StringBuilder("body { font-family: ");
		bodyRule.append(font.getFamily());
		bodyRule.append("; font-size: ");
		bodyRule.append(font.getSize());
		bodyRule.append("pt; text-align: center;}");
		newPane = new JEditorPane("text/html", text);
		((javax.swing.text.html.HTMLDocument) newPane.getDocument()).getStyleSheet().addRule(bodyRule.toString());
		newPane.addHyperlinkListener(HLL);
		newPane.setEditable(false);
		newPane.setBorder(null);
		newPane.setOpaque(false);
		newPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(newPane);
	}

	private ImageIcon createImageIcon(final String path, final String description) {
		final java.net.URL imgURL = OpenLogViewer.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
}

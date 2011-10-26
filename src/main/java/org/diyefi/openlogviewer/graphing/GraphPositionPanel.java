/* OpenLogViewer
 *
 * Copyright 2011
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
package org.diyefi.openlogviewer.graphing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.diyefi.openlogviewer.OpenLogViewerApp;
import org.diyefi.openlogviewer.genericlog.GenericLog;

public class GraphPositionPanel extends JPanel {
	private static final long serialVersionUID = -7808475370693818838L;
	private GenericLog genLog;
	private Color majorGraduationColor;
	private Color positionDataColor;
	private Color backgroundColor;
	private int majorGraduationSpacing;
	private boolean[] validSnappingPositions;

	public GraphPositionPanel() {
		super();
		init();
	}

	private void init() {
		this.setOpaque(true);
		this.setLayout(null);
		genLog = new GenericLog();
		majorGraduationColor = Color.GRAY;
		positionDataColor = majorGraduationColor;
		backgroundColor = Color.BLACK;
		setGraduationSpacing();
		validSnappingPositions = new boolean[this.getWidth()];
	}

	@Override
	public final void paint(final Graphics g) { // override paint because there will be no components in this pane
		if (!this.getSize().equals(this.getParent().getSize())) {
			this.setSize(this.getParent().getSize());
		}
		setGraduationSpacing();
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (genLog.getLogStatus() == GenericLog.LOG_NOT_LOADED) {
			paintPositionBar(g2d, false);
		} else if (genLog.getLogStatus() == GenericLog.LOG_LOADING) {
			paintPositionBar(g2d, false);
		} else if (genLog.getLogStatus() == GenericLog.LOG_LOADED) {
			final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
			final boolean zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedBeyondOneToOne();
			paintPositionBar(g2d, zoomedOut);
			paintPositionData(g2d, zoomedOut);
			if(!zoomedOut && zoom > 1){
				setupMouseCursorLineSnappingPositions();
			}
		}
	}

	private void paintPositionBar(final Graphics2D g2d, final boolean zoomedOut) {
		final int center = this.getWidth() / 2;
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		g2d.setColor(majorGraduationColor);
		double count = -1.0;
		if(zoomedOut){
			count = Math.round(graphPosition / zoom);
		} else {
			count = Math.round(graphPosition * zoom);
		}
		for (int i = center; i > 0; i--) { //paint left of center
			if ((count % (majorGraduationSpacing * zoom) == 0) ||
					(zoomedOut && count % (majorGraduationSpacing / zoom) == 0)) {
				g2d.drawLine(i, 0, i, 6);
			}
			count--;
		}
		if(zoomedOut){
			count = Math.round(graphPosition / zoom);
		} else {
			count = Math.round(graphPosition * zoom);
		}
		for (int i = center; i < this.getWidth(); i++) { // Paint right of center
			if ((count % (majorGraduationSpacing * zoom) == 0) ||
					(zoomedOut && count % (majorGraduationSpacing / zoom) == 0)) {
				g2d.drawLine(i, 0, i, 6);
			}
			count++;
		}
		g2d.drawLine(0, 0, this.getWidth(), 0);
	}

	private void paintPositionData(final Graphics2D g2d, final boolean zoomedOut) {
		final int center = this.getWidth() / 2;
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		g2d.setColor(positionDataColor);

		// Setup count.
		double count = -1.0;
		if(zoomedOut){
			count = Math.round(graphPosition / zoom);
		} else {
			count = Math.round(graphPosition * zoom);
		}

		// Paint left of center.
		for (int i = center; i > 0; i--) {
			if ((count % (majorGraduationSpacing * zoom) == 0) ||
					(zoomedOut && count % (majorGraduationSpacing / zoom) == 0)) {
				String positionDataString = "";
				if(zoomedOut){
					positionDataString = Integer.toString((int) (count * zoom));
				} else {
					positionDataString = Integer.toString((int) (count / zoom));
				}
				g2d.drawString(positionDataString, i - 10, 18);
			}
			count--;
		}

		// Reset count.
		if(zoomedOut){
			count = Math.round(graphPosition / zoom);
		} else {
			count = Math.round(graphPosition * zoom);
		}

		 // Paint right of center.
		for (int i = center; i < this.getWidth(); i++) {
			if ((count % (majorGraduationSpacing * zoom) == 0) ||
					(zoomedOut && count % (majorGraduationSpacing / zoom) == 0)) {
				String positionDataString = "";
				if(zoomedOut){
					positionDataString = Integer.toString((int) (count * zoom));
				} else {
					positionDataString = Integer.toString((int) (count / zoom));
				}
				g2d.drawString(positionDataString, i - 10, 18);
			}
			count++;
		}
	}

	private void setupMouseCursorLineSnappingPositions() {
		final int center = this.getWidth() / 2;
		validSnappingPositions = new boolean[this.getWidth()];
		final double graphPosition = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getGraphPosition();
		final int zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
		double count = Math.round(graphPosition * zoom);
		// Fill array with valid snapping points that are left of center
		for (int i = center; i > 0; i--) {
			if (count % zoom == 0) {
				validSnappingPositions[i] = true;
			}
			count--;
		}
		count = Math.round(graphPosition * zoom);
		// Fill array with valid snapping points that are right of center
		for (int i = center; i < this.getWidth(); i++) {
			if (count % zoom == 0) {
				validSnappingPositions[i] = true;
			}
			count++;
		}
	}

	public final void setLog(final GenericLog log) {
		genLog = log;
		repaint();
	}

	private void setGraduationSpacing() {
		int zoom = 1;
		boolean zoomedOut = false;
		if (OpenLogViewerApp.getInstance() != null) {
			zoom = OpenLogViewerApp.getInstance().getEntireGraphingPanel().getZoom();
			zoomedOut = OpenLogViewerApp.getInstance().getEntireGraphingPanel().isZoomedBeyondOneToOne();
		}
		if (zoomedOut){
			majorGraduationSpacing = zoom * 100;
		} else{
			if (zoom > 64) {
				majorGraduationSpacing = 1;
			} else if (zoom > 32) {
				majorGraduationSpacing = 2;
			} else if (zoom > 16) {
				majorGraduationSpacing = 5;
			} else if (zoom > 8) {
				majorGraduationSpacing = 10;
			} else if (zoom > 4) {
				majorGraduationSpacing = 20;
			} else if (zoom > 2) {
				majorGraduationSpacing = 25; // Hmmmmmmm :-)
			} else if (zoom > 1) {
				majorGraduationSpacing = 50;
			} else {
				majorGraduationSpacing = 100;
			}
		}
	}

	public final int getBestSnappingPosition(final int xMouseCoord) {
		int bestPosition = 0;
		if (validSnappingPositions[xMouseCoord]) {
			bestPosition = xMouseCoord;
		} else {
			boolean found = false;
			final int startPosition = xMouseCoord;
			for (int distance = 1; !found; distance++) {
				final int next = startPosition + distance;
				final int prev = startPosition - distance;
				if (next > validSnappingPositions.length - 1 || prev < 0) {
					bestPosition = xMouseCoord;
					found = true;
				} else if (validSnappingPositions[next]) {
					bestPosition = next;
					found = true;
				} else if (validSnappingPositions[prev]) {
					bestPosition = prev;
					found = true;
				}
			}
		}
		return bestPosition;
	}
}

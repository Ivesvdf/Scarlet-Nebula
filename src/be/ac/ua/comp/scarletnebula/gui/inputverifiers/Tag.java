/*
 * Copyright (C) 2011  Ives van der Flaas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package be.ac.ua.comp.scarletnebula.gui.inputverifiers;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tag extends JLabel {
	private static final long serialVersionUID = 1L;

	public Tag(final String s) {
		super(s);

		// We must be non-opaque since we won't fill all pixels.
		// This will also stop the UI from filling our background.
		setOpaque(false);

		// Add an empty border around us to compensate for
		// the rounded corners.
		setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final int width = getWidth();
		final int height = getHeight();

		// Paint a rounded rectangle in the background.
		g.setColor(new Color(180, 180, 180));
		g.fillRoundRect(0, 0, width, height, 8, 8);

		// Now call the superclass behavior to paint the foreground.
		super.paintComponent(g);
	}

	static public void main(final String[] args) {
		final JFrame f = new JFrame();
		f.setLayout(new FlowLayout());
		f.getContentPane().add(new Tag("Webserver"));
		f.getContentPane().add(new Tag("DNS"));
		f.getContentPane().add(new Tag("Tags kunnen ook meer tekst bevatten"));

		f.setSize(300, 300);
		f.setVisible(true);
	}
}
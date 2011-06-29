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

package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

/**
 * A component that contains exactly one other component. A CollapsablePanel can
 * be "collapsed", after which it takes op no space.
 * 
 * @author ives
 * 
 */
public class CollapsablePanel extends JPanel implements Collapsable {
	private static final long serialVersionUID = 1L;
	private Component storedComponent;

	/**
	 * Constructs a CollapsablePanel
	 * 
	 * @param containedComponent
	 *            The component that will be contained
	 * @param originallyVisible
	 *            True if the component should be visible to start with, false
	 *            otherwise
	 */
	public CollapsablePanel(final Component containedComponent,
			final boolean originallyVisible) {
		super(new BorderLayout());
		this.storedComponent = containedComponent;

		if (originallyVisible) {
			uncollapse();
		}
	}

	/*
	 * @see be.ac.ua.comp.scarletnebula.gui.Collapsable#uncollapse()
	 */
	@Override
	public void uncollapse() {
		if (getComponentCount() == 0) {
			add(storedComponent, BorderLayout.CENTER);
			revalidate();
		}
	}

	/*
	 * @see be.ac.ua.comp.scarletnebula.gui.Collapsable#collapse()
	 */
	@Override
	public void collapse() {
		if (getComponentCount() > 0) {
			storedComponent = getComponent(0);
			removeAll();
			revalidate();
		}
	}

}

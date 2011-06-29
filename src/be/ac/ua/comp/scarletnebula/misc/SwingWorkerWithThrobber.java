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

package be.ac.ua.comp.scarletnebula.misc;

import java.beans.PropertyChangeEvent;

import javax.swing.SwingWorker;

import be.ac.ua.comp.scarletnebula.gui.Collapsable;

/**
 * A partial implementation of a SwingWorker that displays a throbber while its
 * working.
 * 
 * @author ives
 * @see SwingWorker
 * @param <T>
 *            the result type returned by this SwingWorkerWithThrobber's
 *            doInBackground and get methods
 * @param <V>
 *            the type used for carrying out intermediate results by this
 *            SwingWorkerWithThrobber's publish and process methods
 */
public abstract class SwingWorkerWithThrobber<T, V> extends SwingWorker<T, V> {
	final Collapsable throbber;

	/**
	 * Constructor.
	 * 
	 * @param throbber
	 *            The component that will be uncollapsed while the
	 *            SwingWorkerWithThrobber is working. Give it to me while
	 *            collapsed and obviously make sure it's added to some panel.
	 */
	public SwingWorkerWithThrobber(final Collapsable throbber) {
		super();
		this.throbber = throbber;

		addPropertyChangeListener(new WorkerPropertyChangeListener() {
			@Override
			public void progressChanged(final Object source,
					final int newProgress, final PropertyChangeEvent evt) {
			}

			@Override
			public void taskIsFinished(final PropertyChangeEvent evt) {
				throbber.collapse();
			}

			@Override
			public void taskStarted(final PropertyChangeEvent evt) {
				throbber.uncollapse();
			}
		});
	}
}

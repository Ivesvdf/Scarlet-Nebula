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
import java.beans.PropertyChangeListener;

import javax.swing.SwingWorker;

/**
 * Implementation of PropertyChangeListener to be used in conjunction with a
 * SwingWorker. Extend one of these babies and pass it as a
 * PropertyChangeListener to the SwingWorker.
 * 
 * @author ives
 * 
 */
public abstract class WorkerPropertyChangeListener implements
		PropertyChangeListener {

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			progressChanged(evt.getSource(), (Integer) evt.getNewValue(), evt);
		}

		if ("state".equals(evt.getPropertyName())
				&& SwingWorker.StateValue.DONE == evt.getNewValue()) {
			taskIsFinished(evt);
		}

		if ("state".equals(evt.getPropertyName())
				&& SwingWorker.StateValue.STARTED == evt.getNewValue()) {
			taskStarted(evt);
		}
	}

	/**
	 * Method that will be called when setProgress() is called in the
	 * SwingWorker.
	 * 
	 * @param source
	 *            The SwingWorker whose progress changed
	 * @param newProgress
	 *            The current progress
	 * @param evt
	 *            The event itself that notified this class of the change
	 */
	public abstract void progressChanged(Object source, int newProgress,
			PropertyChangeEvent evt);

	/**
	 * Method that will be called when the SwingWorker's work is done ie when
	 * doInBackground finishes.
	 * 
	 * @param evt
	 *            The event itself that notified this class of the change
	 */
	public abstract void taskIsFinished(PropertyChangeEvent evt);

	/**
	 * Method that will be called when the SwingWorker's starts the actual work
	 * 
	 * @param evt
	 *            The event itself that notified this class of the change
	 */
	public abstract void taskStarted(PropertyChangeEvent evt);

}

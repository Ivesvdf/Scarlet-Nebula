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
		PropertyChangeListener
{

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if ("progress".equals(evt.getPropertyName()))
		{
			progressChanged(evt.getSource(), (Integer) evt.getNewValue(), evt);
		}

		if ("state".equals(evt.getPropertyName())
				&& SwingWorker.StateValue.DONE == evt.getNewValue())
		{
			taskIsFinished(evt);
		}

		if ("state".equals(evt.getPropertyName())
				&& SwingWorker.StateValue.STARTED == evt.getNewValue())
		{
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

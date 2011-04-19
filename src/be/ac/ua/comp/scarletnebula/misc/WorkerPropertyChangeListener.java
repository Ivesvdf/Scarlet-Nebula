package be.ac.ua.comp.scarletnebula.misc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingWorker;

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
	}

	public abstract void progressChanged(Object source, int newProgress,
			PropertyChangeEvent evt);

	public abstract void taskIsFinished(PropertyChangeEvent evt);

}

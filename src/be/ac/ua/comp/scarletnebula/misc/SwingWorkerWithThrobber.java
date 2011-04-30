package be.ac.ua.comp.scarletnebula.misc;

import java.beans.PropertyChangeEvent;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public abstract class SwingWorkerWithThrobber<T, V> extends SwingWorker<T, V>
{
	final Collapsable throbber;
	private static final Log log = LogFactory
			.getLog(SwingWorkerWithThrobber.class);

	/**
	 * Constructor.
	 * 
	 * @param throbber
	 *            The component that will be uncollapsed while the
	 *            SwingWorkerWithThrobber is working. Give it to me while
	 *            collapsed and obviously make sure it's added to some panel.
	 */
	public SwingWorkerWithThrobber(final Collapsable throbber)
	{
		super();
		this.throbber = throbber;

		addPropertyChangeListener(new WorkerPropertyChangeListener()
		{
			@Override
			public void progressChanged(Object source, int newProgress,
					PropertyChangeEvent evt)
			{
			}

			@Override
			public void taskIsFinished(PropertyChangeEvent evt)
			{
				throbber.collapse();
			}

			@Override
			public void taskStarted(PropertyChangeEvent evt)
			{
				throbber.uncollapse();
			}
		});
	}
}

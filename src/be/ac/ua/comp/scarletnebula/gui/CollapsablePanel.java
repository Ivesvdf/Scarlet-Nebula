package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.jdesktop.core.animation.timing.Animator;

/**
 * A component that contains exactly one other component. A CollapsablePanel can
 * be "collapsed", after which it takes op no space.
 * 
 * @author ives
 * 
 */
public class CollapsablePanel extends JPanel implements Collapsable
{
	private static final long serialVersionUID = 1L;
	private Component storedComponent;
	private Animator collapseAnimation = null;

	/**
	 * Constructs a CollapsablePanel
	 * 
	 * @param containedComponent
	 *            The component that will be contained
	 * @param originallyVisible
	 *            True if the component should be visible to start with, false
	 *            otherwise
	 */
	public CollapsablePanel(Component containedComponent,
			boolean originallyVisible)
	{
		super(new BorderLayout());
		this.storedComponent = containedComponent;

		if (originallyVisible)
			uncollapse();
	}

	/*
	 * @see be.ac.ua.comp.scarletnebula.gui.Collapsable#uncollapse()
	 */
	@Override
	public void uncollapse()
	{
		if (getComponentCount() == 0)
		{
			add(storedComponent, BorderLayout.CENTER);
			revalidate();
		}
	}

	/*
	 * @see be.ac.ua.comp.scarletnebula.gui.Collapsable#collapse()
	 */
	@Override
	public void collapse()
	{
		if (getComponentCount() > 0)
		{
			storedComponent = getComponent(0);
			removeAll();
			revalidate();
		}
	}

}

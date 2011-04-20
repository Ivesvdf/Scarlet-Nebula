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
public class CollapsablePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	Component storedComponent;

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

	/**
	 * Adjusts this component to make it display the contained component
	 */
	public void uncollapse()
	{
		if (getComponentCount() == 0)
		{
			System.out.println("uncollapsing");
			add(storedComponent, BorderLayout.CENTER);
			revalidate();
			repaint();
		}
	}

	/**
	 * Adjusts this component to assure it no longer takes up any space
	 */
	public void collapse()
	{
		if (getComponentCount() > 0)
		{
			storedComponent = getComponent(0);
			removeAll();
			revalidate();
			repaint();
		}
	}
}

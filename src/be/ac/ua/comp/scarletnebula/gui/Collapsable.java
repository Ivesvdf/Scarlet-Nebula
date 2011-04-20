package be.ac.ua.comp.scarletnebula.gui;

public interface Collapsable
{

	/**
	 * Adjusts this component to make it display the contained component
	 */
	public abstract void uncollapse();

	/**
	 * Adjusts this component to assure it no longer takes up any space
	 */
	public abstract void collapse();

}
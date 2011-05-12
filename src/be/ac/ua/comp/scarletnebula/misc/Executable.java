package be.ac.ua.comp.scarletnebula.misc;

/**
 * An interface that requires only one method, which takes a single parameter
 * and returns no result. A bit like Callable and Runnable, only slightly
 * different.
 * 
 * @author ives
 * 
 * @param <Paramtype>
 *            The parameter that will be given to the run() method.
 */
public interface Executable<Paramtype> {
	/**
	 * Method to be executed.
	 * 
	 * @param param
	 *            The templated parameter given.
	 */
	void run(Paramtype param);
}
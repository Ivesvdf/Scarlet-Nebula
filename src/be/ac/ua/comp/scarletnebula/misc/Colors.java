package be.ac.ua.comp.scarletnebula.misc;

import java.awt.Color;

/**
 * Colors is an enumeration class that makes it easier to work with colors.
 * Methods are provided for conversion to hex strings, and for getting alpha
 * channel colors.
 * 
 * @author Nazmul Idris
 * @version 1.0
 * @since Apr 21, 2007, 12:55:24 PM
 */
public enum Colors {

	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	// various colors in the pallete
	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	Pink(255, 175, 175), Green(159, 205, 20), Orange(213, 113, 13), Yellow(
			Color.yellow), Red(189, 67, 67), LightBlue(208, 223, 245), Blue(
			Color.blue), Black(0, 0, 0), White(255, 255, 255), Gray(Color.gray
			.getRed(), Color.gray.getGreen(), Color.gray.getBlue());

	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	// constructors
	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

	Colors(final Color c) {
		_myColor = c;
	}

	Colors(final int r, final int g, final int b) {
		_myColor = new Color(r, g, b);
	}

	Colors(final int r, final int g, final int b, final int alpha) {
		_myColor = new Color(r, g, b, alpha);
	}

	Colors(final float r, final float g, final float b, final float alpha) {
		_myColor = new Color(r, g, b, alpha);
	}

	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	// data
	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

	private Color _myColor;

	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	// methods
	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

	public Color alpha(final float t) {
		return new Color(_myColor.getRed(), _myColor.getGreen(),
				_myColor.getBlue(), (int) (t * 255f));
	}

	public static Color alpha(final Color c, final float t) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(),
				(int) (t * 255f));
	}

	public Color color() {
		return _myColor;
	}

	public Color color(final float f) {
		return alpha(f);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("r=").append(_myColor.getRed()).append(", g=")
				.append(_myColor.getGreen()).append(", b=")
				.append(_myColor.getBlue()).append("\n");
		return sb.toString();
	}

	public String toHexString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("#");
		sb.append(Integer.toHexString(_myColor.getRed()));
		sb.append(Integer.toHexString(_myColor.getGreen()));
		sb.append(Integer.toHexString(_myColor.getBlue()));
		return sb.toString();
	}

}// end enum Colors

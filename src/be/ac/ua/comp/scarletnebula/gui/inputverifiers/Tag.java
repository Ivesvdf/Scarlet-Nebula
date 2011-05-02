package be.ac.ua.comp.scarletnebula.gui.inputverifiers;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tag extends JLabel
{
	private static final long serialVersionUID = 1L;

	public Tag(final String s)
	{
		super(s);

		// We must be non-opaque since we won't fill all pixels.
		// This will also stop the UI from filling our background.
		setOpaque(false);

		// Add an empty border around us to compensate for
		// the rounded corners.
		setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
	}

	@Override
	protected void paintComponent(final Graphics g)
	{
		final int width = getWidth();
		final int height = getHeight();

		// Paint a rounded rectangle in the background.
		g.setColor(new Color(180, 180, 180));
		g.fillRoundRect(0, 0, width, height, 8, 8);

		// Now call the superclass behavior to paint the foreground.
		super.paintComponent(g);
	}

	static public void main(final String[] args)
	{
		final JFrame f = new JFrame();
		f.setLayout(new FlowLayout());
		f.getContentPane().add(new Tag("Webserver"));
		f.getContentPane().add(new Tag("DNS"));
		f.getContentPane().add(new Tag("Tags kunnen ook meer tekst bevatten"));

		f.setSize(300, 300);
		f.setVisible(true);
	}
}
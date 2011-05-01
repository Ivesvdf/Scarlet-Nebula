package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIDefaults;

import org.jdesktop.swingx.painter.Painter;

public class ToolbarStyleButton extends JButton
{
	private static final long serialVersionUID = 1L;

	public ToolbarStyleButton(final Icon icon)
	{
		super(icon);
		setBorderPainted(false);
		setRolloverEnabled(true);
		setRolloverIcon(icon);
		// setBounds(10, 10, icon.getIconWidth(), icon.getIconHeight() - 2);
		setMargin(new Insets(0, 0, 0, 0));
		setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		final UIDefaults def = new UIDefaults();
		final Painter<Object> notAPainter = new Painter<Object>()
		{
			@Override
			public void paint(final Graphics2D g, final Object c, final int w, final int h)
			{
			}
		};
		def.put("Button[Enabled].backgroundPainter", notAPainter);
		def.put("Button[Focused].backgroundPainter", notAPainter);
		putClientProperty("Nimbus.Overrides", def);
		putClientProperty("Nimbus.Overrides.InheritDefaults", false);

	}
}

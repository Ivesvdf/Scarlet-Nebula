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

	ToolbarStyleButton(Icon icon)
	{
		super(icon);
		setBorderPainted(false);
		setRolloverEnabled(true);
		setRolloverIcon(icon);
		setBounds(10, 10, icon.getIconWidth(), icon.getIconHeight());
		setMargin(new Insets(0, 0, 0, 0));
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		UIDefaults def = new UIDefaults();
		def.put("Button[Enabled].backgroundPainter", new Painter<Object>()
		{
			@Override
			public void paint(Graphics2D g, Object c, int w, int h)
			{
			}
		});
		putClientProperty("Nimbus.Overrides", def);
		putClientProperty("Nimbus.Overrides.InheritDefaults", false);

	}
}

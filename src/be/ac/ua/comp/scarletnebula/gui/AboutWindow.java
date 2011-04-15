package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AboutWindow extends JDialog
{
	private static final long serialVersionUID = 1L;

	AboutWindow(JFrame parent)
	{
		super(parent, "About...", true);
		setMaximumSize(new Dimension(400, 400));
		setMinimumSize(new Dimension(400, 400));

		setSize(400, 400);
		setLocationByPlatform(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JLabel compLogo = new JLabel(new ImageIcon(getClass().getResource(
				"/images/comp.gif")));
		compLogo.setBorder(new EmptyBorder(0, 20, 20, 20));

		JLabel progName = new JLabel("Scarlet Nebula");
		progName.setFont(new Font("SansSerif", Font.PLAIN, 30));
		progName.setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel upperPane = new JPanel();
		upperPane.setLayout(new BorderLayout());
		upperPane.add(compLogo, BorderLayout.EAST);
		upperPane.add(progName, BorderLayout.WEST);

		setLayout(new BorderLayout());
		add(upperPane, BorderLayout.NORTH);
	}
}

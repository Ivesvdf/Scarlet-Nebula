package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import be.ac.ua.comp.scarletnebula.misc.Utils;

public abstract class LoudInputVerifier extends InputVerifier implements
		KeyListener, DocumentListener
{

	private JDialog popup;
	private JLabel messageLabel;
	final private JLabel image;
	private Point point;
	private Dimension cDim;
	private Color color = new Color(243, 255, 159);
	final private JTextField textField;

	public LoudInputVerifier(JTextField textField, String message)
	{
		this.textField = textField;
		if (textField != null)
		{
			textField.addKeyListener(this);
			textField.getDocument().addDocumentListener(this);
		}

		messageLabel = new JLabel(message + " ");
		image = new JLabel(Utils.icon("warning16.png"));
	}

	@Override
	public boolean shouldYieldFocus(JComponent c)
	{
		JDialog parentDialog = (JDialog) SwingUtilities.getAncestorOfClass(
				JDialog.class, c);

		if (!verify(c) && parentDialog != null)
		{
			if (popup == null)
			{
				popup = new JDialog(parentDialog, "");
				initComponents(popup);
			}
			c.setBackground(Color.PINK);
			popup.setSize(0, 0);
			popup.setLocationRelativeTo(c);
			point = popup.getLocation();
			cDim = c.getSize();
			popup.setLocation(point.x - (int) cDim.getWidth() / 2, point.y
					+ (int) cDim.getHeight() / 2);
			popup.pack();
			popup.setVisible(true);
			return false;
		}
		if (popup != null)
			popup.setVisible(false);

		c.setBackground(Color.WHITE);

		return true;

	}

	protected void setMessage(String message)
	{
		messageLabel.setText(message);
	}

	/**
	 * @see KeyListener
	 */

	@Override
	public void keyPressed(KeyEvent e)
	{
		// popup.setVisible(false);
	}

	/**
	 * @see KeyListener
	 */

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	/**
	 * @see KeyListener
	 */

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	private void initComponents(JDialog popup)
	{
		JPanel content = new JPanel(new FlowLayout());
		popup.setUndecorated(true);
		content.setBackground(color);
		content.add(image);
		content.add(messageLabel);
		content.setBorder(BorderFactory.createEtchedBorder());
		popup.setLayout(new BorderLayout());
		popup.add(content, BorderLayout.CENTER);

		popup.setFocusableWindowState(false);
	}

	@Override
	public void changedUpdate(DocumentEvent e)
	{

	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		shouldYieldFocus(textField);
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		shouldYieldFocus(textField);
	}

}

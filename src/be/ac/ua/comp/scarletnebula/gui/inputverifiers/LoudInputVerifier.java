package be.ac.ua.comp.scarletnebula.gui.inputverifiers;

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
		KeyListener, DocumentListener {

	private JDialog popup;
	private final JLabel messageLabel;
	final private JLabel image;
	private Point point;
	private Dimension cDim;
	private final Color color = new Color(243, 255, 159);
	final private JTextField textField;

	/**
	 * Constructs a LoudInputVerifier
	 * 
	 * @param textField
	 *            The JTextField to be verified
	 * @param message
	 *            The message shown when textField does not pass verification
	 */
	public LoudInputVerifier(final JTextField textField, final String message) {
		this.textField = textField;
		if (textField != null) {
			textField.addKeyListener(this);
			textField.getDocument().addDocumentListener(this);
		}

		messageLabel = new JLabel(message + " ");
		image = new JLabel(Utils.icon("warning16.png"));
	}

	/**
	 * @see InputVerifier
	 */
	@Override
	public boolean shouldYieldFocus(final JComponent c) {
		final JDialog parentDialog = (JDialog) SwingUtilities
				.getAncestorOfClass(JDialog.class, c);

		if (!verify(c) && parentDialog != null) {
			if (popup == null) {
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
		if (popup != null) {
			popup.setVisible(false);
		}

		c.setBackground(Color.WHITE);

		return true;

	}

	/**
	 * Sets the text contained in the popup. Can be used by deriving classes to
	 * give failure-dependant error notices.
	 * 
	 * @param message
	 *            The message to be displayed.
	 */
	protected void setMessage(final String message) {
		messageLabel.setText(message);
	}

	/**
	 * @see KeyListener
	 */
	@Override
	public void keyPressed(final KeyEvent e) {
		// popup.setVisible(false);
	}

	/**
	 * @see KeyListener
	 */
	@Override
	public void keyTyped(final KeyEvent e) {
	}

	/**
	 * @see KeyListener
	 */
	@Override
	public void keyReleased(final KeyEvent e) {
	}

	/**
	 * Initialises the popup window (fills it with components)
	 * 
	 * @param popup
	 *            The popup that will be filled.
	 */
	private void initComponents(final JDialog popup) {
		final JPanel content = new JPanel(new FlowLayout());
		popup.setUndecorated(true);
		content.setBackground(color);
		content.add(image);
		content.add(messageLabel);
		content.setBorder(BorderFactory.createEtchedBorder());
		popup.setLayout(new BorderLayout());
		popup.add(content, BorderLayout.CENTER);

		popup.setFocusableWindowState(false);
	}

	/**
	 * @see DocumentListener
	 */
	@Override
	public void changedUpdate(final DocumentEvent e) {

	}

	/**
	 * @see DocumentListener
	 */
	@Override
	public void insertUpdate(final DocumentEvent e) {
		shouldYieldFocus(textField);
	}

	/**
	 * @see DocumentListener
	 */
	@Override
	public void removeUpdate(final DocumentEvent e) {
		shouldYieldFocus(textField);
	}

}

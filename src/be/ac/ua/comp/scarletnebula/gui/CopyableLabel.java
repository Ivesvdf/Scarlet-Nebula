package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import be.ac.ua.comp.scarletnebula.misc.Utils;

public class CopyableLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public CopyableLabel() {
		super();
		init();
	}

	public CopyableLabel(final String text) {
		super(text);
		init();
	}

	private void init() {
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(final MouseEvent e) {
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					final JPopupMenu popup = new JPopupMenu();
					final JMenuItem copy = new JMenuItem("Copy",
							Utils.icon("copy16.png"));
					copy.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							final StringSelection stringSelection = new StringSelection(
									CopyableLabel.this.getText());
							final Clipboard clipboard = Toolkit.getDefaultToolkit()
									.getSystemClipboard();
							clipboard.setContents(stringSelection, null);
						}
					});
					popup.add(copy);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseExited(final MouseEvent e) {
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
			}

			@Override
			public void mouseClicked(final MouseEvent e) {
			}
		});
	}
}

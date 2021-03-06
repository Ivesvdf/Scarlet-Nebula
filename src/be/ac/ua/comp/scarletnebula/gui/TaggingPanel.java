/*
 * Copyright (C) 2011  Ives van der Flaas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TaggingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final TagList taglist = new TagList();
	private static Log log = LogFactory.getLog(TaggingPanel.class);
	private final ActionListener addTagActionListener;

	public TaggingPanel() {
		this(new ArrayList<String>());
	}

	public TaggingPanel(final Collection<String> initialTags) {
		super(new BorderLayout());

		for (final String tag : initialTags) {
			taglist.addTag(new TagItem(tag));
		}
		final BetterTextField inputField = new BetterTextField();
		addTagActionListener = new AddTagActionListener(inputField);
		inputField.addActionListener(addTagActionListener);
		final String hint = "Type a tag and press enter";
		inputField.setPlaceHolder(hint);
		inputField.setToolTipText(hint);
		inputField.setInputVerifier(new TagInputVerifier(inputField));
		inputField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 0, 5, 0),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		taglist.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		final JScrollPane tagScrollPane = new JScrollPane(taglist);
		tagScrollPane.setBorder(null);

		final JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(inputField, BorderLayout.NORTH);
		centerPanel.add(tagScrollPane, BorderLayout.CENTER);
		centerPanel.setMaximumSize(new Dimension(250, 500));
		centerPanel.setPreferredSize(new Dimension(200, 200));

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(Box.createHorizontalGlue());
		add(centerPanel);
		add(Box.createHorizontalGlue());

	}

	public Collection<String> getTags() {
		return taglist.getTags();
	}

	private final class AddTagActionListener implements ActionListener {
		private final BetterTextField inputField;

		private AddTagActionListener(final BetterTextField inputField) {
			this.inputField = inputField;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final String tagTxt = inputField.getText();
			inputField.setText("");
			if (!Pattern.matches("^\\s*$", tagTxt)) {
				taglist.add(tagTxt);
			}
		}
	}

	public class TagList extends JPanel {
		private static final long serialVersionUID = 1L;

		TagList() {
			super();
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			setBackground(Color.WHITE);

		}

		void add(final String tag) {
			addTag(new TagItem(tag));
		}

		void addTag(final TagItem tag) {
			tag.setAlignmentX(LEFT_ALIGNMENT);

			// Only add if it's not yet in the list
			if (!getTags().contains(tag.getTag())) {
				add(tag);
			}
			revalidate();
			repaint();
		}

		public Collection<String> getTags() {
			final Collection<String> tags = new ArrayList<String>();
			for (final Component c : getComponents()) {
				final TagItem tag = (TagItem) c;
				tags.add(tag.getTagString());
				log.debug("Tag:" + tag.getTagString());
			}
			return tags;
		}
	}

	private class TagItem extends JPanel {
		private static final long serialVersionUID = 1L;
		private final String tag;

		public String getTag() {
			return tag;
		}

		TagItem(final String tag) {
			this.tag = tag;
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBackground(Color.WHITE);
			setFont(new Font(getFont().getName(), Font.BOLD, getFont()
					.getSize()));
			add(new JLabel(tag));
			add(Box.createHorizontalGlue());

			final JButton deleteButton = new JButton(new ImageIcon(getClass()
					.getResource("/images/remove16.png")));
			deleteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					final JPanel taglist = (JPanel) TagItem.this.getParent();
					taglist.remove(TagItem.this);
					taglist.revalidate();
					taglist.repaint();
				}
			});
			add(deleteButton);
		}

		public String getTagString() {
			return tag;
		}

	}

	public void simulateEnter() {
		log.debug("Simulating return");
		addTagActionListener.actionPerformed(null);
	}
}

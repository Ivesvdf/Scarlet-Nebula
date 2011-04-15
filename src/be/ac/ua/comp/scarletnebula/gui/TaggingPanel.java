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

public class TaggingPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final TagList taglist = new TagList();
	private static Log log = LogFactory.getLog(TaggingPanel.class);

	public TaggingPanel()
	{
		this(new ArrayList<String>());
	}

	public TaggingPanel(Collection<String> initialTags)
	{
		super(new BorderLayout());

		for (String tag : initialTags)
		{
			taglist.addTag(new TagItem(tag));
		}
		final BetterTextField inputField = new BetterTextField();
		ActionListener addTagActionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String tagTxt = inputField.getText();
				inputField.setText("");
				taglist.add(tagTxt);
			}
		};
		inputField.addActionListener(addTagActionListener);
		final String hint = "Type a tag and press enter";
		inputField.setPlaceHolder(hint);
		inputField.setToolTipText(hint);
		inputField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 0, 5, 0),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		taglist.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		JScrollPane tagScrollPane = new JScrollPane(taglist);
		tagScrollPane.setBorder(null);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(inputField, BorderLayout.NORTH);
		centerPanel.add(tagScrollPane, BorderLayout.CENTER);
		centerPanel.setMaximumSize(new Dimension(250, 500));
		centerPanel.setPreferredSize(new Dimension(200, 200));

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(Box.createHorizontalGlue());
		add(centerPanel);
		add(Box.createHorizontalGlue());

	}

	public Collection<String> getTags()
	{
		return taglist.getTags();
	}

	public class TagList extends JPanel
	{
		private static final long serialVersionUID = 1L;

		TagList()
		{
			super();
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			setBackground(Color.WHITE);

		}

		void add(String tag)
		{
			addTag(new TagItem(tag));
		}

		void addTag(TagItem tag)
		{
			tag.setAlignmentX(LEFT_ALIGNMENT);
			add(tag);
			revalidate();
			repaint();
		}

		public Collection<String> getTags()
		{
			Collection<String> tags = new ArrayList<String>();
			for (Component c : getComponents())
			{
				TagItem tag = (TagItem) c;
				tags.add(tag.getTagString());
				log.debug("Tag:" + tag.getTagString());
			}
			return tags;
		}
	}

	private class TagItem extends JPanel
	{
		private static final long serialVersionUID = 1L;
		String tag;

		TagItem(String tag)
		{
			this.tag = tag;
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBackground(Color.WHITE);
			setFont(new Font(getFont().getName(), Font.BOLD, getFont()
					.getSize()));
			add(new JLabel(tag));
			add(Box.createHorizontalGlue());

			JButton deleteButton = new JButton(new ImageIcon(getClass()
					.getResource("/images/remove16.png")));
			deleteButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JPanel taglist = (JPanel) TagItem.this.getParent();
					taglist.remove(TagItem.this);
					taglist.revalidate();
					taglist.repaint();
				}
			});
			add(deleteButton);
		}

		public String getTagString()
		{
			return tag;
		}

	}
}

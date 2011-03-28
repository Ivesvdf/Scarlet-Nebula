package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class TaggingPage extends WizardPage
{
	private static Log log = LogFactory.getLog(TaggingPage.class);
	private static final long serialVersionUID = 1L;
	private final TagList taglist = new TagList();

	TaggingPage()
	{
		setLayout(new BorderLayout());

		BetterTextLabel lbl = new BetterTextLabel(
				"Enter some labels that describe the functionality of this server. E.g. dns, webserver, ...");
		lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(lbl, BorderLayout.NORTH);

		JPanel bottomPanel = new JPanel();

		final JTextField inputField = new JTextField();
		inputField.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		inputField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String tagTxt = inputField.getText();
				inputField.setText("");
				taglist.addTag(new TagItem(tagTxt));
			}
		});
		bottomPanel.setLayout(new BorderLayout());

		taglist.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		JScrollPane tagScrollPane = new JScrollPane(taglist);
		bottomPanel.add(inputField, BorderLayout.NORTH);
		bottomPanel.add(tagScrollPane, BorderLayout.CENTER);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

		add(bottomPanel, BorderLayout.CENTER);

		inputField.requestFocus();
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		// Extract tags
		Collection<String> tags = new ArrayList<String>();
		for (Component c : taglist.getComponents())
		{
			TagItem tag = (TagItem) c;
			tags.add(tag.getTagString());
			log.debug("Tag:" + tag.getTagString());
		}
		return null;
	}

	@Override
	public boolean nextIsEnabled()
	{
		return false;
	}

	@Override
	public boolean finishIsEnabled()
	{
		return true;
	}

	class TagList extends JPanel
	{
		TagList()
		{
			super();
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			setBackground(Color.WHITE);

		}

		void addTag(TagItem tag)
		{
			tag.setAlignmentX(LEFT_ALIGNMENT);
			add(tag);
			revalidate();
			repaint();
		}
	}

	class TagItem extends JPanel
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

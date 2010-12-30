package be.ac.ua.comp.scarletnebula.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class Wizard
{
	Stack<WizardPage> visitedPages = new Stack<WizardPage>();
	DataRecorder recorder;
	JDialog window;
	WizardListener listener = null;

	final JButton previousButton = new JButton("< Prev");
	final JButton nextButton = new JButton("Next >");
	final JButton finishButton = new JButton("Finish");
	final JButton cancelButton = new JButton("Cancel");
	
	JPanel container = new JPanel();

	public Wizard(WizardPage startPage, DataRecorder recorder)
	{
		this.recorder = recorder;
		visitedPages.push(startPage);
	}
	
	public void start(JDialog window)
	{
		this.window = window;

		initializeButtons();
		displayPage();
	}

	private void displayPage()
	{
		renderPage(visitedPages.peek());
	}

	private void initializeButtons()
	{
		previousButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				previous();
			}
		});

		nextButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				next();
			}
		});

		finishButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				finish();
			}
		});

		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cancel();
			}
		});

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(previousButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(nextButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(finishButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(cancelButton);

		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		window.setLayout(new BorderLayout());

		container.setLayout(new BorderLayout());
		
		window.add(container, BorderLayout.CENTER);
		window.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void renderPage(WizardPage page)
	{
		if(page == null)
			return;
		
		nextButton.setEnabled(page.nextIsEnabled());
		finishButton.setEnabled(page.finishIsEnabled());
		previousButton.setEnabled(visitedPages.size() > 1);
		
		for(Component c : container.getComponents())
		{
			container.remove(c);
			System.out.println("Deleting one component"); 
		}

		for(Component c: page.getComponents())
			c.setVisible(true);
		
		container.add(page);
		System.out.println("This page has " + page.getComponentCount() + " components.");
		container.revalidate();
		container.repaint();
	}

	private void next()
	{
		WizardPage nextPage = visitedPages.peek().next(recorder);
		visitedPages.push(nextPage);
		displayPage();
	}

	private void previous()
	{
		visitedPages.pop();
		
		System.out.println(visitedPages.size());
		System.out.println(visitedPages.peek().toString());
		displayPage();
	}

	private void finish()
	{
		// Run this for the side effects to recorder
		visitedPages.peek().next(recorder);
		if (listener != null)
			listener.onFinish(recorder);
		window.dispose();
	}

	private void cancel()
	{
		window.dispose();
		if (listener != null)
			listener.onCancel(recorder);
	}

	public void addWizardListener(WizardListener l)
	{
		listener = l;
	}
}

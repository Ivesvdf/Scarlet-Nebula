package be.ac.ua.comp.scarletnebula.wizard;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.JDialog;

public class Wizard
{
	Stack<WizardPage> visitedPages = new Stack<WizardPage>();
	DataRecorder recorder;
	JDialog window;
	WizardListener listener = null;

	WizardTemplate wizardTemplate = null;

	public Wizard(WizardPage startPage, DataRecorder recorder, WizardTemplate wizardTemplate)
	{
		this.recorder = recorder;
		this.wizardTemplate = wizardTemplate;
		visitedPages.push(startPage);
	}
	
	public void start(JDialog window)
	{
		this.window = window;

		initializeButtons(window);
		displayPage();
	}

	private void displayPage()
	{
		renderPage(visitedPages.peek());
	}

	private void initializeButtons(JDialog window)
	{
		wizardTemplate.setupWindow(window);
		
		wizardTemplate.previousButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				previous();
			}
		});

		wizardTemplate.nextButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				next();
			}
		});

		wizardTemplate.finishButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				finish();
			}
		});

		wizardTemplate.cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cancel();
			}
		});
	}

	private void renderPage(WizardPage page)
	{
		if(page == null)
			return;
		
		wizardTemplate.nextButton.setEnabled(page.nextIsEnabled());
		wizardTemplate.finishButton.setEnabled(page.finishIsEnabled());
		wizardTemplate.previousButton.setEnabled(visitedPages.size() > 1);
		
		for(Component c : wizardTemplate.container.getComponents())
		{
			wizardTemplate.container.remove(c);
			System.out.println("Deleting one component"); 
		}

		for(Component c: page.getComponents())
			c.setVisible(true);
		
		wizardTemplate.container.add(page);
		wizardTemplate.container.revalidate();
		wizardTemplate.container.repaint();
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

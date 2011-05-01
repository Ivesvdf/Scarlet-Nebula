package be.ac.ua.comp.scarletnebula.wizard;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class SimpleWizardTemplate extends WizardTemplate
{
	@Override
	void setupWindow(final JDialog window)
	{
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

}

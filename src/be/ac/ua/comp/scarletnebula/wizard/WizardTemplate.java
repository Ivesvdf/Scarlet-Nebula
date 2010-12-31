package be.ac.ua.comp.scarletnebula.wizard;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public abstract class WizardTemplate
{
	public JButton previousButton = new JButton("< Prev");
	public JButton nextButton = new JButton("Next >");
	public JButton finishButton = new JButton("Finish");
	public JButton cancelButton = new JButton("Cancel");
	
	public JPanel container = new JPanel();
	
	abstract void setupWindow(JDialog window);
}

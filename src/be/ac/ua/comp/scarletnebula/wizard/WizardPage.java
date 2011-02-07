package be.ac.ua.comp.scarletnebula.wizard;

import javax.swing.JPanel;

public abstract class WizardPage extends JPanel
{
	private static final long serialVersionUID = 3157233847233312403L;

	public abstract WizardPage next(DataRecorder recorder);

	public boolean nextIsEnabled()
	{
		return true;
	}

	public boolean finishIsEnabled()
	{
		return false;
	}
}

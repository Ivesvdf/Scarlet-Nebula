package be.ac.ua.comp.scarletnebula.wizard;

import javax.swing.JPanel;

public abstract class WizardPage extends JPanel
{
	public abstract WizardPage next(DataRecorder recorder);
	public boolean nextIsEnabled() { return true; }
	public boolean finishIsEnabled() { return false; }
}

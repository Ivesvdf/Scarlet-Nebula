package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import javax.swing.BorderFactory;

import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class FinalServerAddPage extends WizardPage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FinalServerAddPage(final AddServerWizardDataRecorder rec)
	{
		final BetterTextLabel lbl = new BetterTextLabel(
				"<html>Press <b>Finish</b> to start the server.");
		lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(lbl);
	}

	@Override
	public WizardPage next(final DataRecorder recorder)
	{
		// TODO Auto-generated method stub
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
}

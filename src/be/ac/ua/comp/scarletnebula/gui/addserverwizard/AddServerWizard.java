package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;

import be.ac.ua.comp.scarletnebula.gui.GUI;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;

public class AddServerWizard extends JDialog implements WizardListener
{
	private final GUI gui;
	private static final long serialVersionUID = 1L;

	public AddServerWizard(JFrame parent, final GUI gui)
	{
		super(parent);
		this.gui = gui;
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setPreferredSize(new Dimension(400, 300));
		this.setLocation(0, 0);
		this.setLocationRelativeTo(null);
		this.pack();

		Wizard wiz = new Wizard(new ChooseProviderPage(), new AddServerWizardDataRecorder(), new SimpleWizardTemplate());
		wiz.addWizardListener(this);
		wiz.start(this);
	}


	@Override
	public void onFinish(DataRecorder recorder)
	{
		gui.addServerWizardClosed((AddServerWizardDataRecorder)recorder);
	}

	@Override
	public void onCancel(DataRecorder recorder)
	{
		// TODO Auto-generated method stub

	}
}

package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.gui.WrappableLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ChooseNamePage extends WizardPage
{
	JTextField name = new JTextField();

	ChooseNamePage(AddProviderWizardDataRecorder recorder)
	{
		setLayout(new BorderLayout());
		WrappableLabel text = new WrappableLabel(
				"What name would you like to use to describe this account with "
						+ recorder.getTemplate().getName() + "?");
		text.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
		add(text, BorderLayout.NORTH);

		// And the textfields below
		FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, max(50dlu;p):grow, 7dlu", "");

		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.append("Account Name", name);
		add(builder.getPanel());

		// Prefill the textfield with something useful

		name.setText(recorder.getTemplate().getName() + " ("
				+ recorder.getEndpoint().getName() + ")");
		name.setPreferredSize(name.getMinimumSize());

	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		rec.setName(name.getText());

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

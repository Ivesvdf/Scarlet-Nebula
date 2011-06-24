package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.ChangeableLabel;
import be.ac.ua.comp.scarletnebula.gui.LabelEditSwitcherPanel;
import be.ac.ua.comp.scarletnebula.gui.inputverifiers.NumberInputVerifier;
import be.ac.ua.comp.scarletnebula.gui.inputverifiers.PlainTextVerifier;
import be.ac.ua.comp.scarletnebula.gui.inputverifiers.ServernameInputVerifier;
import be.ac.ua.comp.scarletnebula.gui.windows.SelectKeyWindow;
import be.ac.ua.comp.scarletnebula.misc.Executable;
import be.ac.ua.comp.scarletnebula.misc.Utils;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class FinalServerAddPage extends WizardPage {

	final JTextField instanceNameField = new JTextField();
	private static final long serialVersionUID = 1L;
	private final JTextField instanceCountTextField = new JTextField();
	private final JTextField vlanField = new JTextField();
	private final JCheckBox addToFavoritesBox = new JCheckBox(
			"Add image to favorites");

	public FinalServerAddPage(final AddServerWizardDataRecorder rec) {
		super(new BorderLayout());
		final BetterTextLabel lbl = new BetterTextLabel(
				"<html><font size=\"4\">Press <b><font color=\"green\">Finish</font></b> to start the server.</font>");
		lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
		lbl.setAlignmentX(CENTER_ALIGNMENT);

		add(lbl, BorderLayout.NORTH);

		instanceNameField.setInputVerifier(new ServernameInputVerifier(
				instanceNameField));
		instanceNameField.setText(rec.provider.getName() + "-"
				+ rec.provider.listLinkedServers().size());
		instanceNameField.selectAll();

		final FormLayout layout = new FormLayout(
				"right:max(40dlu;p):grow, 7dlu, max(80dlu;p):grow", "");

		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.append("Instance Name", instanceNameField);
		builder.nextLine();
		instanceCountTextField.setInputVerifier(new NumberInputVerifier(
				instanceCountTextField, "Only number allowed."));

		builder.append("Nr of instances to start", new LabelEditSwitcherPanel(
				"1", instanceCountTextField));

		vlanField.setInputVerifier(new PlainTextVerifier(vlanField,
				"Only plain text allowed."));
		// builder.append("VLAN id", new LabelEditSwitcherPanel("(None)",
		// vlanField));

		if (rec.provider.supportsSSHKeys()) {
			rec.keypairOrPassword = rec.provider.getDefaultKeypair();
			final ChangeableLabel sshKeyLabel = new ChangeableLabel(
					rec.keypairOrPassword, new Executable<JLabel>() {

						@Override
						public void run(final JLabel param) {
							new SelectKeyWindow((JDialog) Utils
									.findWindow(FinalServerAddPage.this),
									rec.provider) {
								private static final long serialVersionUID = 1L;

								@Override
								public void onOk(final String keyname) {
									if (!keyname.isEmpty()) {
										rec.keypairOrPassword = keyname;
										param.setText(keyname);
										dispose();
									} else {
										JOptionPane.showMessageDialog(this,
												"Please select a key",
												"Select Key",
												JOptionPane.ERROR_MESSAGE);
									}
								}
							};
						}
					});

			builder.append("SSH Key", sshKeyLabel);
		}
		final JPanel panel = builder.getPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 80));
		add(panel, BorderLayout.CENTER);

		addToFavoritesBox.setBorder(BorderFactory.createEmptyBorder(0, 20, 10,
				20));
		if (rec.provider.imageInFavorites(rec.image)) {
			addToFavoritesBox.setSelected(false);
			addToFavoritesBox.setEnabled(false);
		} else {
			addToFavoritesBox.setSelected(true);
		}

		add(addToFavoritesBox, BorderLayout.SOUTH);
	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		final AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;

		final InputVerifier nameInputVerifier = instanceNameField
				.getInputVerifier();
		if (nameInputVerifier == null
				|| nameInputVerifier.verify(instanceNameField)) {
			rec.instanceName = instanceNameField.getText();
		} else {
			int i = 1;
			String name;
			do {
				name = "Nameless - " + i++;
			}
			while (CloudManager.get().serverExists(name));

			rec.instanceName = name;
		}

		final InputVerifier countInputVerifier = instanceCountTextField
				.getInputVerifier();

		if ((countInputVerifier == null)
				|| countInputVerifier.verify(instanceCountTextField)) {
			rec.instanceCount = Integer
					.decode(instanceCountTextField.getText());
		} else {
			rec.instanceCount = 1;
		}

		if (addToFavoritesBox.isSelected()) {
			rec.provider.addToFavorites(rec.image);
			rec.provider.store();
		}
		return null;
	}

	@Override
	public boolean nextIsEnabled() {
		return false;
	}

	@Override
	public boolean finishIsEnabled() {
		return true;
	}
}

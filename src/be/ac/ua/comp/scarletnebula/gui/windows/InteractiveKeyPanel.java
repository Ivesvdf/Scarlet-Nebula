package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.KeylistWithDefault;
import be.ac.ua.comp.scarletnebula.gui.keywizards.ImportKeyWizard;
import be.ac.ua.comp.scarletnebula.gui.keywizards.KeyRecorder;
import be.ac.ua.comp.scarletnebula.gui.keywizards.NewKeyWizard;
import be.ac.ua.comp.scarletnebula.misc.Utils;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;

public class InteractiveKeyPanel extends JPanel {
	private final class AddKeyActionListener implements ActionListener {
		private final CloudProvider provider;

		private AddKeyActionListener(final CloudProvider provider) {
			this.provider = provider;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final NewKeyWizard wiz = new NewKeyWizard(
					(JDialog) Utils.findWindow(InteractiveKeyPanel.this),
					provider);
			wiz.addWizardListener(new AddToListWizardListener());

			wiz.start();
		}
	}

	private final class ModifySelectedActionListener implements ActionListener {
		private final CloudProvider provider;

		private ModifySelectedActionListener(final CloudProvider provider) {
			this.provider = provider;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final ImportKeyWizard wiz = new ImportKeyWizard(
					(JDialog) Utils.findWindow(InteractiveKeyPanel.this),
					provider);

			wiz.addWizardListener(new AddToListWizardListener());

			wiz.start();

		}
	}

	private final class RemoveSelectedActionListener implements ActionListener {
		private final CloudProvider provider;

		private RemoveSelectedActionListener(final CloudProvider provider) {
			this.provider = provider;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Collection<String> keynames = keylist.getSelection();
			final String options[] = {
					"Delete key" + (keynames.size() > 1 ? "s" : ""), "Cancel" };

			if (!keynames.isEmpty()) {
				final int result = JOptionPane
						.showOptionDialog(
								InteractiveKeyPanel.this,
								"Deleting "
										+ (keynames.size() > 1 ? "keys"
												: "a key")
										+ " will permanently remove it both locally and on the remote CloudProvider.\nProceed?",
								"Remove key", JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.WARNING_MESSAGE, null, options,
								options[0]);

				if (result == JOptionPane.OK_OPTION) {
					for (final String key : keynames) {
						keylist.removeKey(key);
					}
					keylist.assureDefaultKey();

					(new SwingWorker<Object, Object>() {
						@Override
						protected Exception doInBackground() throws Exception {
							try {
								for (final String key : keynames) {
									provider.deleteKey(key);
								}
							} catch (final Exception ignore) {
								// If the key can't be deleted, just leave it...
							}
							return null;
						}
					}).execute();
				}
			}
		}
	}

	private final class AddToListWizardListener implements WizardListener {
		private AddToListWizardListener() {
		}

		@Override
		public void onFinish(final DataRecorder recorder) {
			final KeyRecorder rec = (KeyRecorder) recorder;
			keylist.add(rec.keyname, rec.makeDefault);
		}

		@Override
		public void onCancel(final DataRecorder recorder) {

		}
	}

	private static final long serialVersionUID = 1L;
	private KeylistWithDefault keylist;

	public InteractiveKeyPanel(final CloudProvider provider) {
		super(new BorderLayout());

		placeComponents(provider);
	}

	private final void placeComponents(final CloudProvider provider) {
		final JButton addButton = new JButton(Utils.icon("add22.png"));
		addButton.addActionListener(new AddKeyActionListener(provider));

		final JButton modifyButton = new JButton("Import Key");
		modifyButton.addActionListener(new ModifySelectedActionListener(
				provider));

		final JButton removeButton = new JButton(Utils.icon("remove22.png"));
		removeButton.addActionListener(new RemoveSelectedActionListener(
				provider));

		keylist = new KeylistWithDefault(provider);
		final JScrollPane keylistScrollPane = new JScrollPane(keylist);
		keylistScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(15, 20, 15, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
		add(keylistScrollPane, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

		final GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1.0;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;

		buttonPanel.add(addButton, c);

		c.gridx = 1;
		buttonPanel.add(modifyButton, c);

		c.gridx = 2;
		buttonPanel.add(removeButton, c);

		add(buttonPanel, BorderLayout.SOUTH);
	}
}

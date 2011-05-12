package be.ac.ua.comp.scarletnebula.gui.keywizards;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.KeyManager;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.Collapsable;
import be.ac.ua.comp.scarletnebula.gui.CollapsablePanel;
import be.ac.ua.comp.scarletnebula.gui.ThrobberBarWithText;
import be.ac.ua.comp.scarletnebula.misc.SwingWorkerWithThrobber;
import be.ac.ua.comp.scarletnebula.misc.WorkerPropertyChangeListener;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public abstract class AbstractFinalKeyWizardPage extends WizardPage {
	private final class TestKeyExistsWorker extends
			SwingWorkerWithThrobber<Boolean, String> {
		private final String checkKeyname;
		private final CloudProvider provider;

		private TestKeyExistsWorker(final Collapsable throbber,
				final String checkKeyname, final CloudProvider provider) {
			super(throbber);
			this.checkKeyname = checkKeyname;
			this.provider = provider;
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			final boolean exists = provider
					.linkedUnlinkedKeyExists(checkKeyname);
			return !exists;
		}
	}

	private final class SetWarningMessagePropertyListener extends
			WorkerPropertyChangeListener {
		private final SwingWorkerWithThrobber<Boolean, String> checkKeyWorker;

		private SetWarningMessagePropertyListener(
				final SwingWorkerWithThrobber<Boolean, String> checkKeyWorker) {
			this.checkKeyWorker = checkKeyWorker;
		}

		@Override
		public void taskStarted(final PropertyChangeEvent evt) {
		}

		@Override
		public void taskIsFinished(final PropertyChangeEvent evt) {
			try {
				final boolean goodkey = checkKeyWorker.get();

				if (goodkey) {
					keyValidityMessage
							.setText("<html><font color=\"green\">This keyname is not in use and the key is ready to be created. </font></html>");
				} else {
					keyValidityMessage
							.setText("<html><font color=\"red\"><b>Warning!</b> Keyname already in use! Proceed at your own risk.</font></html>");
				}
			} catch (final Exception e) {
				log.error("The impossible happened ", e);
			}

		}

		@Override
		public void progressChanged(final Object source, final int newProgress,
				final PropertyChangeEvent evt) {
		}
	}

	private static final long serialVersionUID = 1L;
	private final JCheckBox makeDefault;
	private static Log log = LogFactory.getLog(CloudProvider.class);
	private final JLabel keyValidityMessage = new JLabel();

	protected AbstractFinalKeyWizardPage(final CloudProvider provider,
			final String toptext, final String checkKeyname) {
		super(new BorderLayout());
		final BetterTextLabel toptextLabel = new BetterTextLabel(toptext);
		toptextLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

		makeDefault = new JCheckBox("Make this the default keypair.", true);

		if (KeyManager.getKeyNames(provider.getName()).isEmpty()
				|| provider.getDefaultKeypair().isEmpty()) {
			makeDefault.setEnabled(false);
		}
		makeDefault.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

		keyValidityMessage.setBorder(BorderFactory.createEmptyBorder(0, 20, 0,
				20));
		add(keyValidityMessage, BorderLayout.CENTER);
		add(makeDefault, BorderLayout.SOUTH);

		final JPanel textAndThrobber = new JPanel(new BorderLayout());

		textAndThrobber.add(toptextLabel, BorderLayout.NORTH);
		if (!checkKeyname.isEmpty()) {
			final ThrobberBarWithText throbber = new ThrobberBarWithText(
					"Verifying key uniqueness...");
			final CollapsablePanel throbberPanel = new CollapsablePanel(
					throbber, false);
			throbberPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10,
					0));
			final SwingWorkerWithThrobber<Boolean, String> checkKeyWorker = new TestKeyExistsWorker(
					throbberPanel, checkKeyname, provider);
			checkKeyWorker
					.addPropertyChangeListener(new SetWarningMessagePropertyListener(
							checkKeyWorker));
			checkKeyWorker.execute();
			textAndThrobber.add(throbberPanel, BorderLayout.SOUTH);
		}

		add(textAndThrobber, BorderLayout.NORTH);
	}

	protected boolean makeKeyDefault() {
		return makeDefault.isSelected();
	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		performAction((KeyRecorder) recorder);

		if (makeDefault.isSelected()) {
			// Make this key the default key
		}
		return null;
	}

	protected abstract void performAction(KeyRecorder recorder);

	@Override
	public boolean nextIsEnabled() {
		return false;
	}

	@Override
	public boolean finishIsEnabled() {
		return true;
	}

}

package be.ac.ua.comp.scarletnebula.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.dasein.cloud.compute.VmState;

import TightVNC.VncViewer;
import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.AddServerWizard;
import be.ac.ua.comp.scarletnebula.gui.windows.GUI;
import be.ac.ua.comp.scarletnebula.gui.windows.SSHTerminalWindow;
import be.ac.ua.comp.scarletnebula.gui.windows.ServerPropertiesWindow;
import be.ac.ua.comp.scarletnebula.gui.windows.StatisticsWindow;
import be.ac.ua.comp.scarletnebula.misc.Utils;

public class ServerListMouseListener implements MouseListener {
	private final class VNCActionListener implements ActionListener {
		private final Collection<Server> selectedServers;

		private VNCActionListener(final Collection<Server> selectedServers) {
			this.selectedServers = selectedServers;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final VncViewer v = new VncViewer();

			final Server firstServer = selectedServers.iterator().next();

			String address;

			if (firstServer.getPublicDnsAddress() != null) {
				address = firstServer.getPublicDnsAddress();
			} else if (firstServer.getPublicIpAddresses().length >= 1) {
				address = firstServer.getPublicIpAddresses()[0];
			} else {
				return;
			}

			v.mainArgs = Arrays.asList("HOST", address, "PASSWORD",
					firstServer.getVNCPassword(), "Scaling factor", "auto",
					"Show controls", "Yes").toArray(new String[0]);
			v.inAnApplet = false;
			v.inSeparateFrame = true;

			v.init(gui);
			v.start();
		}
	}

	private final class StartPropertiesActionListener implements ActionListener {
		private final Collection<Server> selectedServers;

		private StartPropertiesActionListener(
				final Collection<Server> selectedServers) {
			this.selectedServers = selectedServers;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			new ServerPropertiesWindow(gui, selectedServers);
		}
	}

	private final class StartStatisticsActionListener implements ActionListener {
		private final Collection<Server> selectedServers;

		private StartStatisticsActionListener(
				final Collection<Server> selectedServers) {
			this.selectedServers = selectedServers;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			new StatisticsWindow(gui, selectedServers);
		}
	}

	private final class StartTerminalActionListener implements ActionListener {
		private final Collection<Server> selectedServers;

		private StartTerminalActionListener(
				final Collection<Server> selectedServers) {
			this.selectedServers = selectedServers;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			new SSHTerminalWindow(gui, selectedServers);
		}
	}

	private final class UnlinkActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			gui.unlinkSelectedServers();
		}
	}

	private final class RefreshActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			gui.refreshSelectedServers();
		}
	}

	private final class TerminateActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			gui.terminateSelectedServers();
		}
	}

	private final class RebootActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			gui.rebootSelectedServers();
		}
	}

	private final class PauseActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			gui.pauseSelectedServers();
		}
	}

	private final class ResumeActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			gui.resumeSelectedServers();
		}
	}

	private final GUI gui;
	private final ServerListModel serverListModel;
	private final ServerList serverlist;

	public ServerListMouseListener(final GUI gui, final ServerList serverlist,
			final ServerListModel serverListModel) {
		super();
		this.gui = gui;
		this.serverlist = serverlist;
		this.serverListModel = serverListModel;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			final ServerList list = (ServerList) e.getSource();
			final int indexOfSelectedServer = list
					.locationToIndex(e.getPoint());

			// TODO:
			// When no servers OR a single server is selected, choose what
			// server this will take effect on based on the current mouse
			// position. If more servers are selected, apply action to all
			// selected servers!
			if (list.getSelectedIndices().length <= 1) {
				list.setSelectedIndex(indexOfSelectedServer);
			}
			final Server clickedServer = serverListModel
					.getVisibleServerAtIndex(indexOfSelectedServer);

			final Collection<Server> allSelectedServers = list
					.getSelectedServers();
			if (!allSelectedServers.contains(clickedServer)) {
				list.setSelectedIndices(new int[0]);
			}
			if (clickedServer == null) {
				return;
			}

			final Collection<Server> selectedServers = list
					.getSelectedServers();

			final VmState status = clickedServer.getStatus();

			final JPopupMenu popup = new JPopupMenu();
			final JMenuItem pause = new JMenuItem("Pause",
					Utils.icon("paused.png"));
			pause.addActionListener(new PauseActionListener());

			final JMenuItem resume = new JMenuItem("Resume",
					Utils.icon("resume16.png"));
			resume.addActionListener(new ResumeActionListener());

			final JMenuItem reboot = new JMenuItem("Reboot",
					Utils.icon("restarting.png"));
			reboot.addActionListener(new RebootActionListener());

			final JMenuItem terminate = new JMenuItem("Terminate",
					Utils.icon("terminated.png"));
			terminate.addActionListener(new TerminateActionListener());

			final JMenuItem refresh = new JMenuItem("Refresh",
					Utils.icon("refresh16.png"));
			refresh.addActionListener(new RefreshActionListener());

			final JMenuItem unlink = new JMenuItem("Unlink Instance",
					Utils.icon("unlink16.png"));
			unlink.addActionListener(new UnlinkActionListener());

			final JMenuItem console = new JMenuItem("Start terminal",
					Utils.icon("console16.png"));
			console.addActionListener(new StartTerminalActionListener(
					selectedServers));

			final JMenuItem vnc = new JMenuItem("Start VNC",
					Utils.icon("vnc16.png"));
			vnc.addActionListener(new VNCActionListener(selectedServers));

			final JMenuItem statistics = new JMenuItem("View statistics",
					Utils.icon("statistics16.png"));
			statistics.addActionListener(new StartStatisticsActionListener(
					selectedServers));

			final JMenuItem properties = new JMenuItem("Properties",
					Utils.icon("settings16.png"));
			properties.addActionListener(new StartPropertiesActionListener(
					selectedServers));

			if (status != VmState.RUNNING && status != VmState.PAUSED) {
				pause.setEnabled(false);
				reboot.setEnabled(false);
				terminate.setEnabled(false);
			}
			if (clickedServer.getServerStatistics() == null
					|| status != VmState.RUNNING) {
				statistics.setEnabled(false);
			}

			if (clickedServer.sshWillFail() || status != VmState.RUNNING) {
				console.setEnabled(false);
			}

			if (status != VmState.RUNNING) {
				vnc.setEnabled(false);
			}

			pause.setEnabled(clickedServer.isPausable()
					&& clickedServer.getStatus() == VmState.RUNNING);

			if (!clickedServer.isRebootable()) {
				reboot.setEnabled(false);
			}

			resume.setEnabled(clickedServer.getStatus() == VmState.PAUSED);

			popup.add(pause);
			popup.add(resume);
			popup.add(reboot);
			popup.add(terminate);
			popup.add(refresh);
			popup.addSeparator();
			popup.add(console);
			popup.add(vnc);
			popup.add(statistics);
			popup.addSeparator();
			popup.add(unlink);
			popup.addSeparator();
			popup.add(properties);

			popup.show(e.getComponent(), e.getX(), e.getY());
		} else if (e.getClickCount() >= 1
				&& serverlist.startNewServerServerSelected()) {
			// If the user clicked a null server, this is taken as a hint
			// to create a new server!
			serverlist.setSelectedIndices(new int[0]);
			new AddServerWizard(gui, gui);
		} else if (e.getClickCount() == 2
				&& serverlist.getSelectedServers().size() > 0)
		// Normal double click on a server
		{
			new ServerPropertiesWindow(gui, serverlist.getSelectedServers());
		}

	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

}
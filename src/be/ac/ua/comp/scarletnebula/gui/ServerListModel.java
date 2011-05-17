package be.ac.ua.comp.scarletnebula.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.AbstractListModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.misc.SearchHelper;

public class ServerListModel extends AbstractListModel {
	public static enum CreateNewServerServer {
		DISPLAY_NEW_SERVER, NO_NEW_SERVER
	};

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(ServerListModel.class);
	private final boolean displayNewServer;

	LinkedList<Server> visibleServers = new LinkedList<Server>();
	LinkedList<Server> invisibleServers = new LinkedList<Server>();

	public ServerListModel(final CreateNewServerServer displayNewServer) {
		this.displayNewServer = (displayNewServer == CreateNewServerServer.DISPLAY_NEW_SERVER);
	}

	@Override
	public int getSize() {
		// One extra element for the "create server" server
		return visibleServers.size() + (displayNewServer ? 1 : 0);
	}

	/**
	 * Returns the actual Server at index. This may be null if this is a
	 * "Create server" server.
	 */
	@Override
	public Object getElementAt(final int index) {
		// Returns null when returning the fake "create server" server, like it
		// should
		if (index >= visibleServers.size() || visibleServers.get(index) == null) {
			return null;
		}

		return visibleServers.get(index);
	}

	/**
	 * Refreshes the server at index "index".
	 * 
	 * @param index
	 */
	public void refreshIndex(final int index) {
		fireContentsChanged(this, index, index);
	}

	/**
	 * Searches for "server" in the list of visibleservers. If this server is
	 * found, this element is refreshed.
	 * 
	 * @param server
	 */
	public void refreshServer(final Server server) {
		for (int i = 0; i < visibleServers.size(); i++) {
			if (visibleServers.get(i) == server) {
				refreshIndex(i);
				return;
			}
		}
	}

	/**
	 * Adds a server to the model and makes it visible
	 * 
	 * @param server
	 */
	public void addServer(final Server server) {
		visibleServers.add(server);

		fireIntervalAdded(this, visibleServers.size() - 1,
				visibleServers.size() - 1);
	}

	/**
	 * Makes the server at "index" invisible
	 * 
	 * @param index
	 */
	public void makeInvisible(final int index) {
		final Server tmp = visibleServers.get(index);
		visibleServers.remove(index);
		invisibleServers.add(tmp);
		fireIntervalRemoved(this, index, index);
	}

	/**
	 * Sets the model filter to "filterString"
	 * 
	 * @param filterString
	 */
	public void filter(final String filterString) {
		final LinkedList<Server> allServers = new LinkedList<Server>();

		allServers.addAll(visibleServers);
		allServers.addAll(invisibleServers);

		final int oldVisibleCount = getSize();

		visibleServers.clear();
		invisibleServers.clear();

		fireIntervalRemoved(this, 0, oldVisibleCount - 1);

		// Make a collection of tokens from a filterString, pass that to each
		// server and ask it if matches.
		final Collection<String> filterTerms = SearchHelper
				.tokenize(filterString);
		for (final Server server : allServers) {
			if (server.match(filterTerms)) {
				visibleServers.add(server);
			} else {
				invisibleServers.add(server);
			}
		}

		fireIntervalAdded(this, 0, getSize() - 1);
	}

	/**
	 * Returns the label that represents the visible server at "index"
	 * 
	 * @param index
	 * @return
	 */
	public Server elementAt(final int index) {
		return (Server) getElementAt(index);
	}

	/**
	 * Returns the visible server at "index"
	 * 
	 * @param index
	 * @return
	 */
	public Server getVisibleServerAtIndex(final int index) {
		if (index >= visibleServers.size()) {
			return null;
		}

		return visibleServers.get(index);
	}

	/**
	 * Makes all servers invisible
	 */
	public void clear() {
		invisibleServers.addAll(visibleServers);
		final int visibleServerCount = getSize();
		visibleServers.clear();
		fireIntervalRemoved(this, 0, visibleServerCount - 1);
	}

	/**
	 * Returns the visible servers at "indices".
	 * 
	 * @param indices
	 * @return
	 */
	public Collection<Server> getVisibleServersAtIndices(final int[] indices) {
		final Collection<Server> servers = new ArrayList<Server>();

		for (final int i : indices) {
			final Server server = getVisibleServerAtIndex(i);
			if (server != null) {
				servers.add(server);
			}
		}

		return servers;
	}

	/**
	 * Returns the index the Server "server" can be found at. This comparison
	 * will be made with the == operator.
	 * 
	 * @param server
	 * @return
	 */
	private int visibleServerToIndex(final Server server) {
		for (int i = 0; i < visibleServers.size(); i++) {
			if (visibleServers.get(i) == server) {
				return i;
			}
		}
		// Todo: throw an exception when not found
		return -1;
	}

	/**
	 * Removes "server" from the model.
	 * 
	 * @param server
	 */
	public void removeServer(final Server server) {
		final int index = visibleServerToIndex(server);
		log.debug("Server we're removing is at index" + index);
		visibleServers.remove(server);

		if (index >= 0) {
			fireIntervalRemoved(this, index, index);
		}
	}

	public Collection<Server> getVisibleServers() {
		return visibleServers;
	}
}

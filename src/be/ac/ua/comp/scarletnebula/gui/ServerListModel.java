package be.ac.ua.comp.scarletnebula.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.Server;

public class ServerListModel extends AbstractListModel
{
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(ServerListModel.class);

	LinkedList<Server> visibleServers = new LinkedList<Server>();
	LinkedList<Server> invisibleServers = new LinkedList<Server>();

	@Override
	public int getSize()
	{
		return visibleServers.size();
	}

	/**
	 * This needs to return the string that will be visible in the arraylist.
	 */
	@Override
	public Object getElementAt(int index)
	{
		if (index >= visibleServers.size() || visibleServers.get(index) == null)
			return null;

		return new JLabel(visibleServers.get(index).getFriendlyName(),
				getServerIcon(getVisibleServerAtIndex(index)),
				javax.swing.SwingConstants.LEFT);
	}

	private ImageIcon getServerIcon(Server server)
	{
		String filename = new String("/images/add.png");

		switch (server.getStatus())
		{
			case PAUSED:
				filename = "/images/paused.png";
				break;
			case PENDING:
				filename = "/images/pending.png";
				break;
			case RUNNING: // This needs to be made load-dependent...
				filename = "/images/running_ok.png";
				break;
			case REBOOTING:
			case STOPPING:
				filename = "/images/stopping.png";
				break;
			case TERMINATED:
				filename = "/images/terminated.png";
				break;

		}

		ImageIcon icon = new ImageIcon(getClass().getResource(filename));
		return icon;
	}

	public void refreshIndex(int index)
	{
		fireContentsChanged(this, index, index);
	}

	/**
	 * Searches for "server" in the list of visibleservers. If this server is
	 * found, this element is refreshed.
	 * 
	 * @param server
	 */
	public void refreshServer(Server server)
	{
		for (int i = 0; i < visibleServers.size(); i++)
		{
			if (visibleServers.get(i) == server)
			{
				refreshIndex(i);
				return;
			}
		}
	}

	public void addServer(Server server)
	{
		visibleServers.add(server);

		fireIntervalAdded(this, visibleServers.size() -1,
				visibleServers.size() - 1);
	}

	public void makeInvisible(int index)
	{
		Server tmp = visibleServers.get(index);
		visibleServers.remove(index);
		invisibleServers.add(tmp);
		fireContentsChanged(this, index, index);
	}

	public void makeVisible(int index)
	{
		Server tmp = invisibleServers.get(index);
		visibleServers.add(tmp);
		fireContentsChanged(this, visibleServers.size() - 1,
				visibleServers.size() - 1);
	}

	public void filter(String filterString)
	{
		LinkedList<Server> allServers = new LinkedList<Server>();

		allServers.addAll(visibleServers);
		allServers.addAll(invisibleServers);

		int oldVisibleCount = visibleServers.size();
		
		visibleServers.clear();
		invisibleServers.clear();

		fireIntervalRemoved(this, 0, oldVisibleCount-1);
		
		for (Server server : allServers)
		{
			if (server.getFriendlyName().contains(filterString))
			{
				visibleServers.add(server);
			}
			else
			{
				invisibleServers.add(server);
			}
		}

		fireIntervalAdded(this, 0, visibleServers.size() - 1);
	}

	public JLabel elementAt(int index)
	{
		return (JLabel) getElementAt(index);
	}

	public Server getVisibleServerAtIndex(int index)
	{
		if(index >= visibleServers.size())
			return null;
		
		return visibleServers.get(index);
	}

	public void clear()
	{
		invisibleServers.addAll(visibleServers);
		int visibleServerCount = visibleServers.size();
		visibleServers.clear();
		fireIntervalRemoved(this, 0, visibleServerCount-1);
	}

	public Collection<Server> getVisibleServersAtIndices(int[] indices)
	{
		Collection<Server> servers = new ArrayList<Server>();

		for (int i : indices)
			servers.add(getVisibleServerAtIndex(i));

		return servers;
	}

	private int visibleServerToIndex(Server server)
	{
		for(int i = 0; i < visibleServers.size(); i++)
		{
			if(visibleServers.get(i) == server)
			{
				return i;
			}
		}
		// Todo: throw an exception when not found
		return -1;
	}
	public void removeServer(Server server)
	{
		int index = visibleServerToIndex(server);
		log.debug("Server we're removing is at index" + index);
		visibleServers.remove(server);
		fireIntervalRemoved(this, index, index);

	}

}

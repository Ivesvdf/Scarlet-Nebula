package be.ac.ua.comp.scarletnebula.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;

import be.ac.ua.comp.scarletnebula.core.Server;

public class ServerListModel extends AbstractListModel
{
	private static final long serialVersionUID = 1L;
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
		return visibleServers.get(index).getFriendlyName();
	}
	
	public void addServer(Server server)
	{
		visibleServers.add(server);
		
		fireContentsChanged(this, visibleServers.size()-2, visibleServers.size()-1);
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
		fireContentsChanged(this, visibleServers.size()-1, visibleServers.size()-1);
	}
	
	public void filter(String filterString)
	{
		LinkedList<Server> allServers = new LinkedList<Server>();
		
		allServers.addAll(visibleServers);
		allServers.addAll(invisibleServers);
		
		visibleServers.clear();
		invisibleServers.clear();
		
		for(Server server : allServers)
		{
			if(server.getFriendlyName().contains(filterString))
			{
				visibleServers.add(server);
			}
			else
			{
				invisibleServers.add(server);
			}
		}
		
		fireContentsChanged(this, 0, visibleServers.size()-1);
	}

	public String elementAt(int index)
	{
		return (String)getElementAt(index);
	}
	
	public void clear()
	{
		invisibleServers.addAll(visibleServers);
		visibleServers.clear();
		fireContentsChanged(this, 0, visibleServers.size()-1);
	}
	
}

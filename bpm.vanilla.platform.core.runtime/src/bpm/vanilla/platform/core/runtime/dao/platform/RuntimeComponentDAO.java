package bpm.vanilla.platform.core.runtime.dao.platform;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;

public class RuntimeComponentDAO {

	private IVanillaComponentListenerService listener;
	
	public RuntimeComponentDAO(IVanillaComponentListenerService listener) throws Exception{
		if (listener == null){
			throw new Exception("IVanillaComponentListenerService cannot be null");
		}
		this.listener = listener;
	}
	
	public void delete(Server server)throws Exception{
		throw new Exception("RuntimeComponent cannot be manually deleted for now. Needs implementations by being able to stop remotly OSGI Component");
	}
	
	public int add(Server server) throws Exception{
		throw new Exception("RuntimeComponent cannot be manually added for now. They must be registered by The VanillaListenerComponent when they start.");
	}
	
	public void update(Server server) throws Exception{
		throw new Exception("Not available");
	}
	
	public List<Server> getAll(boolean includeStoppedComponent)throws Exception{
		return getByType("*", includeStoppedComponent);
	}
	
	/**
	 * ere, added doc, this is what s actually called by Hypervision
	 * @param componentNature
	 * @return
	 * @throws Exception
	 */
	public List<Server> getByType(String componentNature, boolean includeStoppedComponent) throws Exception{
		List<Server> l  = new ArrayList<Server>();
		for(IVanillaComponentIdentifier id : listener.getRegisteredComponents(componentNature, includeStoppedComponent)){
			Server s = new Server();
			s.setName(id.getComponentId());
			s.setUrl(id.getComponentUrl());
			
			s.setComponentNature(id.getComponentNature());
			s.setComponentStatus(id.getComponentStatus());
			s.setDescription(id.getComponentNature());
			l.add(s);
		}
		
		return l;
	}
}

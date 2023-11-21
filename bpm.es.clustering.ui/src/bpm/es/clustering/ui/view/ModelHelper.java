package bpm.es.clustering.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.gef.GefModel;
import bpm.es.clustering.ui.model.VanillaPlatformModule;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.VanillaSetup;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;

public class ModelHelper {
	private static Server createServer(int serverType, String url) throws Exception{
		Server s = new Server();
		//s.setType(serverType);
		s.setUrl(url);
		switch(serverType){
		case Server.TYPE_BIRT:
			s.setName("BirtViewer"); //$NON-NLS-1$
			break;
		case Server.TYPE_FAWEB:
			s.setName("FreeAnalysisWeb"); //$NON-NLS-1$
			s.setComponentNature(VanillaComponentType.COMPONENT_FREEANALYSISWEB);
			break;
		case Server.TYPE_FWR:
			s.setName("FreeWebReport"); //$NON-NLS-1$
			s.setComponentNature(VanillaComponentType.COMPONENT_FREEWEBREPORT);
			break;
		case Server.TYPE_SCHEDULER:
			s.setName("BiProcessManager"); //$NON-NLS-1$
			break;
		case Server.TYPE_FMWEB:
			s.setName("FreeMetrics"); //$NON-NLS-1$
			s.setComponentNature(VanillaComponentType.COMPONENT_FREEMETRICS);
			break;
		case Server.TYPE_ORBEON:
			s.setName("Orbeon"); //$NON-NLS-1$
			break;
		default:
			return null;
		}
		return s;
	}
	
	public static GefModel createModel(){
		List<Server> clients = new ArrayList<Server>();
		List<VanillaPlatformModule> modules = new ArrayList<VanillaPlatformModule>();
		VanillaPlatformModule defaultModule = null;
		List<Repository> repositoris = new ArrayList<Repository>();
		
		try {
			//XXX : remove this once webapp are dynamically registered
//			VanillaSetup set = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVanillaSetup();
//			
//			if (set.getBirtViewer() != null){
//				Server s = createServer(Server.TYPE_BIRT, set.getBirtViewer());
//				if (s != null){
//					clients.add(s);
//				}
//				
//				s = createServer(Server.TYPE_FAWEB, set.getFreeAnalysisServer());
//				if (s != null){
//					clients.add(s);
//				}
//				s = createServer(Server.TYPE_FWR, set.getFreeWebReportServer());
//				if (s != null){
//					clients.add(s);
//				}
//				s = createServer(Server.TYPE_FMWEB,  set.getFreeMetricsServer());
//				if (s != null){
//					clients.add(s);
//				}
//				s = createServer(Server.TYPE_ORBEON, set.getOrbeonUrl());
//				if (s != null){
//					clients.add(s);
//				}
//				
//					
//			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			//add the repositories
			repositoris.addAll( Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		HashMap<String, List<Server>> serverByUrl = new HashMap<String, List<Server>>(); 
		
		try {
			
			for(Server s : Activator.getDefault().getVanillaApi().getVanillaSystemManager().getServerNodes(true)){
				if (serverByUrl.get(s.getUrl()) == null){
					serverByUrl.put(s.getUrl(), new ArrayList<Server>());
				}
				serverByUrl.get(s.getUrl()).add(s);
			}
	
			defaultModule = new VanillaPlatformModule(serverByUrl.get(Activator.getDefault().getVanillaContext().getVanillaUrl()));
			serverByUrl.remove(Activator.getDefault().getVanillaContext().getVanillaUrl());
			defaultModule.load();
			defaultModule.setName(Messages.ModelHelper_6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			for(String s : serverByUrl.keySet()){
				if (serverByUrl.get(s) == null || serverByUrl.get(s).isEmpty()){
					continue;
				}
				VanillaPlatformModule m = new VanillaPlatformModule(serverByUrl.get(s));
				m.setName(s);
				modules.add(m);
				try{
					m.load();
				}catch(Exception ex){
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new GefModel(clients, modules, defaultModule, repositoris);
	}
}

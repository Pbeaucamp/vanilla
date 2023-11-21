//package bpm.workflow.ui.tools.repository;
//
//import java.util.HashMap;
//
//import bpm.repository.api.model.FactoryRepository;
//import bpm.repository.api.model.IRepository;
//import bpm.repository.api.model.IRepositoryConnection;
//import bpm.workflow.runtime.resources.servers.BiRepository;
//import bpm.workflow.ui.Activator;
//
//
///**
// * Cache the repositopry Content to avoid multiple useless loading
// * @author ludo
// *
// */
//public class RepositoryCache {
////	private HashMap<BiRepository, IRepository> cache = new HashMap<BiRepository, IRepository>();
//	
//	private void addBIRepository() throws Exception{
//		Activator.getDefault().getVanillaContext();
//		
//		IRepositoryConnection r = FactoryRepository.getInstance().getConnection(FactoryRepository.AXIS_CLIENT, rep.getUrl(), rep.getLogin(), rep.getPassword(), null, rep.getGroupId());
//
//		IRepository o = r.getRepository();
//		
////		cache.put(rep, o);
//
//	}
//	
//	public void refresh() throws Exception{
//		addBIRepository();
//	}
//	
//	public IRepository getRepository(){
////		if (cache.get(rep) == null){
////			try {
////				addBIRepository(rep);
////			} catch (Exception e) {
////				e.printStackTrace();
////				return null;
////			}
////		}
////		
////		return cache.get(rep);
//	}
//}

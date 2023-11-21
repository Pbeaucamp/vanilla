package bpm.vanilla.platform.core.runtime.components.listener;

import java.util.List;

import org.apache.log4j.Logger;

import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.communication.IRuntimeService;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IUnitedOlapPreloadManager;
import bpm.vanilla.platform.core.IVanillaSystemManager;
import bpm.vanilla.platform.core.beans.UOlapPreloadBean;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.event.impl.RepositoryItemEvent;

public class RepositoryListener {}
//implements IVanillaListener{
//	private IUnitedOlapPreloadManager preloadManager;
//	private IVanillaSystemManager systemManager;
//	private IRuntimeService unitedOlapRuntime;
//	private IModelService unitedOlapModel;
//	
//	public RepositoryListener(IUnitedOlapPreloadManager preloadManager, IVanillaSystemManager systemManager, IRuntimeService unitedOlapRuntime, IModelService unitedOlapModel){
//		this.preloadManager = preloadManager;
//		this.systemManager = systemManager;
//		this.unitedOlapModel = unitedOlapModel;
//		this.unitedOlapRuntime = unitedOlapRuntime;
//	}
//	
//	
//	public void handleEvent(IVanillaEvent event){
//		RepositoryItemEvent ev = (RepositoryItemEvent)event;
////		if (event.getNewValue() == event.getOldValue()){
////			return;
////		}
//		if (ev.getPropertyChangedName().equals(RepositoryItemEvent.ITEM_STATE)){
//			handleChangeItemState(ev);
//		}
//		
//	}
//	
//	public void handleChangeItemState(RepositoryItemEvent event){
//		
//		
//	
//		IObjectIdentifier identifier = new ObjectIdentifier(event.getRepositoryId(), event.getDirectoryItemId());
//		
//		
//		VanillaSession session = null;
//		try{
//			session = systemManager.getSession(event.getSessionId());
//		}catch(Exception ex){
//			Logger.getLogger(getClass()).error("Failed to get vanillaSession - " + ex.getMessage(), ex);
//		}
//		User user = session.getUser();
//		
//		/*
//		 * UnitedOlap unload/Preload handle
//		 */
//		if (unitedOlapModel != null){
//			Schema s = null;
//			try{
//				//TODO
//				//s = unitedOlapModel.getSchema(identifier, null);
//			}catch(Exception ex){
//				Logger.getLogger(getClass()).error("Failed to find UNitedOlap schema for " + identifier.toString() + "- " + ex.getMessage(), ex); 
//			}
//			
//			if (event.getNewValue() == Boolean.FALSE){
//				if (s == null){
//					return;
//				}
//				Logger.getLogger(getClass()).info("Unload Schema because the Schema Model has been stopped");
//				try{
//					unitedOlapModel.unloadSchema(s.getId());
//				}catch(Exception ex){
//					Logger.getLogger(getClass()).error("Failed to Unload UnitedOlap Schema "+ s.getId()  +" - " + ex.getMessage(), ex);
//				}
//				
//			}
//			else if (event.getNewValue() == Boolean.TRUE){
//				Logger.getLogger(getClass()).info("Load Schema and preload because the Schema Model has been started");
//				//preload
//				List<UOlapPreloadBean> beans = null;
//				try{
//					beans = preloadManager.getPreloadForIdentifier(identifier);
//				}catch(Exception ex){
//					Logger.getLogger(getClass()).error("Failed to get UnitedOlap Preloads - " + ex.getMessage(), ex);
//				}
//				
//				for(UOlapPreloadBean b : beans){
//					
//					
//					
//					IRuntimeContext runtimeContext = new RuntimeContext(user.getName(), user.getPassword(), "", b.getVanillaGroupId());
//					try{
//						if (s == null){
//							//TODO
//							//relaodModel();
//						}
//						//TODO
//						//unitedOlapModel.loadSchema(s, identifier, runtimeContext);
//						int i = b.getMdxQuery().toLowerCase().indexOf(" from ");
//						String cubeName = b.getMdxQuery().substring(i + 6);
//						unitedOlapRuntime.preload(b.getMdxQuery(), s.getId(), cubeName, runtimeContext);
//					}catch(Exception ex){
//						Logger.getLogger(getClass()).error("Failed to perform preload - " + ex.getMessage(), ex);
//					}
//				}
//				
//			}
//			
//		}
//
//	}
//
//}

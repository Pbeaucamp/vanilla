package bpm.united.olap.runtime.preload;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;

import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.preload.IPreloadConfig;
import bpm.united.olap.api.preload.IPreloader;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.HierarchyExtractor;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.engine.IUnitedOlapContentManager;

public class Preloader implements IPreloader{

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private Schema schema;
	private IRuntimeContext runtimeContext;
	private IUnitedOlapContentManager contentManager;
	public Preloader(IUnitedOlapContentManager contentManager, Schema schema, IRuntimeContext runtimeContext){
		this.schema = schema;
		this.runtimeContext = runtimeContext;
		this.contentManager = contentManager;
	}
	
	@Override
	public void addPreloadListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}

	@Override
	public void preload(IModelService modelService, IPreloadConfig config) throws Exception {
		
		for(Dimension d : schema.getDimensions()){
			
			for(Hierarchy h : d.getHierarchies()){
				
				Integer lvlNumber = config.getLevelNumber(h.getUname());
				
				if (lvlNumber == null || lvlNumber == -1){
					continue;
				}
				else if (lvlNumber > h.getLevels().size()){
					Logger.getLogger(getClass()).warn("Preloader Configuration has a Level number higher than Hierarchy level Numbers for Hierarchy " + h.getUname());
				}
				else{
					try{
						String rootName = h.getUname() + ".[All " + h.getParentDimension().getName() + "]";
						Logger.getLogger(getClass()).info("Preloading Hierarchy " + h.getUname() + "...");
						preload(h, rootName, 0, lvlNumber);
						Logger.getLogger(getClass()).info("Preloaded Hierarchy " + h.getUname());
					}catch(Exception ex){
						Logger.getLogger(getClass()).error("Preload Hierarchy " + h.getUname() + " failed - " + ex.getMessage(), ex );
					}
					
					
				}
				
			}
			
		}
	}
	
	private void preload(Hierarchy hierarchy, String rootName , int currentLevel, int lastLevel) throws Exception{
//		List<Member> submems = modelService.getSubMembers(rootName, schema.getId());
//		
//		if (currentLevel == lastLevel){
////			listeners.firePropertyChange(IPreloader.LOADED_HIERARCHY, "", "Loaded");
//		}
//		else{
//			
//			for(Member m : submems){
//				preload(modelService, m.getUname(), currentLevel + 1, lastLevel);
//			}
//			
//		}
		
		//find a cube using this dimension to find a cubeInstance object
		String cubeName = null;
		for(Cube cube : schema.getCubes()) {
			for(Dimension dim : cube.getDimensions()) {
				for(Hierarchy hiera : dim.getHierarchies()) {
					if(hiera.getUname().equals(hierarchy.getUname())) {
						cubeName = cube.getName();
						break;
					}
				}
				if(cubeName != null) {
					break;
				}
			}
			if(cubeName != null) {
				break;
			}
		}
		
		if(cubeName == null) {
			throw new Exception("No cube uses the hierarchy : " + hierarchy.getUname() + " was found");
		}
		
		ICubeInstance cubeInstance = contentManager.getCubeInstance(schema.getId(), cubeName, runtimeContext);
		
		HierarchyExtractor extractor = cubeInstance.getHierarchyExtractor(hierarchy);
		
		extractor.preloadLevelDatas(currentLevel, lastLevel, runtimeContext);
	}

	@Override
	public void removePreloadListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}

}

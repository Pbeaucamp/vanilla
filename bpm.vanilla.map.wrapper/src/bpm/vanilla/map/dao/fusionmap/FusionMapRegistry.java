package bpm.vanilla.map.dao.fusionmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class FusionMapRegistry extends HibernateDaoSupport implements IFusionMapRegistry{

	
	private String mapFolderLocation;
	/**
	 * @return the mapFolderLocation
	 */
	public String getMapFolderLocation() {
		return mapFolderLocation;
	}

	/**
	 * @param mapFolderLocation the mapFolderLocation to set
	 */
	public void setMapFolderLocation(String mapFolderLocation) {
		this.mapFolderLocation = mapFolderLocation;
	}
	
	@Override
	public IFusionMapObject addFusionMapObject(IFusionMapObject fusionMap, InputStream swfFileStream) throws Exception {
		
		/*
		 * we set the relative to config SWF fileName on the fusionMapObject
		 */
		File file = new File(fusionMap.getName() + new Object().hashCode());
		fusionMap.setSwfFileName(file.getName());
		
		/*
		 * we save the SWF file
		 */
		saveSwfFile(fusionMap.getSwfFileName(), swfFileStream);
		
		
		
		/*
		 * we save the datas in the database
		 */
		
		try{
			long id = (Long)getHibernateTemplate().save(fusionMap);
			fusionMap.setId(id);	
			
			for(IFusionMapSpecificationEntity e : fusionMap.getSpecificationsEntities()){
				e.setFusionMapObjectId(id);
				long eid = (Long)getHibernateTemplate().save(e);
				e.setId(eid);
			}
			
			
			
			return fusionMap;
		}catch(Exception ex){
			
			deleteSwfFile(fusionMap);
			
			for(IFusionMapSpecificationEntity e : fusionMap.getSpecificationsEntities()){
				getHibernateTemplate().delete(e);
			}
			getHibernateTemplate().delete(fusionMap);
			throw ex;
		}
		
		
	}

	private void deleteSwfFile(IFusionMapObject fusionMap) {
		File folder = new File(mapFolderLocation);
		File file = new File(folder, fusionMap.getSwfFileName());
		
		if (file.exists()){
			file.delete();
		}
		
	}
	
	
	
	
	
	

	private void saveSwfFile(String swfFileName, InputStream swfFileStream) throws Exception{
		
		File folder = new File(mapFolderLocation);
		if (!folder.exists()){
			folder.mkdirs();
		}
		
		File file = new File(folder, swfFileName);
		if (file.exists()){
			throw new Exception("The file " + file.getAbsolutePath() + " already exists." );
		}
		
		
		try{
			FileOutputStream fos = new FileOutputStream(file);
			
			byte[] buffer = new byte[1024];
			int sz = 0;
			while( (sz = swfFileStream.read(buffer)) != -1){
				fos.write(buffer, 0, sz);
			}
			fos.close();
			swfFileStream.close();
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error when copying SWF file : " + ex.getMessage(), ex);
		}
		
		
		
	}

	@Override
	public IFusionMapObject getFusionMapObject(long fusionMapObjectId)throws Exception {
		
		List<IFusionMapObject> l  = getHibernateTemplate().find("from FusionMapObject where id=" + fusionMapObjectId);
		
		if (l.isEmpty()){
			return null;
		}
		
		IFusionMapObject fusionMap = l.get(0);
		
		List<IFusionMapSpecificationEntity> entities = getHibernateTemplate().find("from FusionMapSpecificationEntity where fusionMapObjectId=" + fusionMapObjectId);
		for(IFusionMapSpecificationEntity entity : entities){
			fusionMap.addSpecificationEntity(entity);
		}
		
		return fusionMap;
	}

	@Override
	public List<IFusionMapObject> getFusionMapObjects() throws Exception {
		List<IFusionMapObject> l = getHibernateTemplate().find("from FusionMapObject");
		for(IFusionMapObject fusionMap : l){
			List<IFusionMapSpecificationEntity> entities = getHibernateTemplate().find("from FusionMapSpecificationEntity where fusionMapObjectId=" + fusionMap.getId());
			for(IFusionMapSpecificationEntity entity : entities){
				fusionMap.addSpecificationEntity(entity);
			}
		}
		return l;
	}

	@Override
	public void removeFusionMapObject(IFusionMapObject fusionMap)throws Exception {
		for(IFusionMapSpecificationEntity entity : fusionMap.getSpecificationsEntities()){
			getHibernateTemplate().delete(entity);
		}
		
		getHibernateTemplate().delete(fusionMap);
		deleteSwfFile(fusionMap);
	}

	@Override
	public void removeFusionMapObject(long fusionMapObjectId) throws Exception {
		
		IFusionMapObject fusionMap = getFusionMapObject(fusionMapObjectId);
		
		if (fusionMap == null){
			return;
		}
		for(IFusionMapSpecificationEntity entity : fusionMap.getSpecificationsEntities()){
			getHibernateTemplate().delete(entity);
		}
		
		getHibernateTemplate().delete(fusionMap);
		deleteSwfFile(fusionMap);
		
	}

	@Override
	public void configure(Object object) {
			
	}

	@Override
	public List<IFusionMapObject> getFusionMapObjects(String type) throws Exception {
		if(type.equals(IFusionMapObject.VANILLAMAP_TYPE)) {
			List<IFusionMapObject> l = getHibernateTemplate().find("from FusionMapObject where type = '" + type + "'");
			for(IFusionMapObject fusionMap : l){
				List<IFusionMapSpecificationEntity> entities = getHibernateTemplate().find("from FusionMapSpecificationEntity where fusionMapObjectId=" + fusionMap.getId());
				for(IFusionMapSpecificationEntity entity : entities){
					fusionMap.addSpecificationEntity(entity);
				}
			}
			
			return l;
		}
		else {
			List<IFusionMapObject> l = getHibernateTemplate().find("from FusionMapObject where type = '" + type + "' or type is NULL");
			for(IFusionMapObject fusionMap : l){
				List<IFusionMapSpecificationEntity> entities = getHibernateTemplate().find("from FusionMapSpecificationEntity where fusionMapObjectId=" + fusionMap.getId());
				for(IFusionMapSpecificationEntity entity : entities){
					fusionMap.addSpecificationEntity(entity);
				}
			}
			
			return l;
		}
	}

}

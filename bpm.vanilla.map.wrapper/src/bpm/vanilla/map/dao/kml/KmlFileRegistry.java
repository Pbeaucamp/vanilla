package bpm.vanilla.map.dao.kml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import bpm.vanilla.map.core.design.kml.IKmlObject;
import bpm.vanilla.map.core.design.kml.IKmlRegistry;
import bpm.vanilla.map.core.design.kml.IKmlSpecificationEntity;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class KmlFileRegistry extends HibernateDaoSupport implements IKmlRegistry{
	private String kmlFolderLocation;
	
	/**
	 * @return the kmlFolderLocation
	 */
	@Override	
	public String getKmlFolderLocation() {
		return kmlFolderLocation;
	}

	/**
	 * @param kmlFolderLocation the kmlFolderLocation to set
	 */
	public void setKmlFolderLocation(String kmlFolderLocation) {
		this.kmlFolderLocation = kmlFolderLocation;
	}

	@Override
	public IKmlObject addKmlObject(IKmlObject kmlObject, InputStream kmlInputStream) throws Exception {
		/*
		 * we set the relative to config Kml fileName on the KmlObject
		 */
		File file = new File(kmlObject.getKmlFileName());
		kmlObject.setKmlFileName(file.getName());
		
		/*
		 * we save the KML file
		 */
		saveKmlFile(kmlObject.getKmlFileName(), kmlInputStream);
		
		/*
		 * we save the datas in the database
		 */
		
		try{
			int id = (Integer)getHibernateTemplate().save(kmlObject);
			kmlObject.setId(id);	
			
			for(IKmlSpecificationEntity kmlSpeEntity : kmlObject.getSpecificationsEntities()){
				kmlSpeEntity.setKmlObjectId(id);
				int kmlSpeEntityId = (Integer)getHibernateTemplate().save(kmlSpeEntity);
				kmlSpeEntity.setId(kmlSpeEntityId);
			}
			
			
			
			return kmlObject;
		}catch(Exception ex){
			
			deleteKmlFile(kmlObject);
			
			for(IKmlSpecificationEntity kmlSpeEntity : kmlObject.getSpecificationsEntities()){
				getHibernateTemplate().delete(kmlSpeEntity);
			}
			getHibernateTemplate().delete(kmlObject);
			throw ex;
		}
	}
	
	private void saveKmlFile(String kmlFileName, InputStream kmlInputStream) throws Exception{
		
		File folder = new File(kmlFolderLocation);
		if (!folder.exists()){
			folder.mkdirs();
		}
		
		File file = new File(folder, kmlFileName);
		if (file.exists()){
			throw new Exception("The file " + file.getAbsolutePath() + " already exists." );
		}
		
		
		try{
			FileOutputStream fos = new FileOutputStream(file);
			
			byte[] buffer = new byte[1024];
			int sz = 0;
			while( (sz = kmlInputStream.read(buffer)) != -1){
				fos.write(buffer, 0, sz);
			}
			fos.close();
			kmlInputStream.close();
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error when copying KML file : " + ex.getMessage(), ex);
		}
	}
	
	private void deleteKmlFile(IKmlObject kmlObject) {
		File folder = new File(kmlFolderLocation);
		File file = new File(folder, kmlObject.getKmlFileName());
		
		if (file.exists()){
			file.delete();
		}
		
	}

	@Override
	public IKmlObject getKmlObject(Integer kmlObjectId) throws Exception {
		List<IKmlObject> l  = getHibernateTemplate().find("from KmlObject where id=" + kmlObjectId);
		
		if (l.isEmpty()){
			return null;
		}
		
		IKmlObject kmlObject = l.get(0);
		
		List<IKmlSpecificationEntity> entities = getHibernateTemplate().find("from KmlSpecificationEntities where kmlObjectId=" + kmlObjectId);
		for(IKmlSpecificationEntity entity : entities){
			kmlObject.addSpecificationEntity(entity);
		}
		
		return kmlObject;
	}

	@Override
	public List<IKmlObject> getKmlObjects() throws Exception {
		List<IKmlObject> listKmlObjects = getHibernateTemplate().find("from KmlObject");
		for(IKmlObject kmlObject : listKmlObjects){
			List<IKmlSpecificationEntity> entities = getHibernateTemplate().find("from KmlSpecificationEntity where kmlObjectId=" + kmlObject.getId());
			for(IKmlSpecificationEntity entity : entities){
				kmlObject.addSpecificationEntity(entity);
			}
		}
		return listKmlObjects;
	}

	@Override
	public void removeKmlObject(IKmlObject kmlObject) throws Exception {
		for(IKmlSpecificationEntity entity : kmlObject.getSpecificationsEntities()){
			getHibernateTemplate().delete(entity);
		}
		
		getHibernateTemplate().delete(kmlObject);
	}

	@Override
	public void removeKmlObject(Integer kmlObjectId) throws Exception {
		IKmlObject kmlObject = getKmlObject(kmlObjectId);
		
		if (kmlObject == null){
			return;
		}
		for(IKmlSpecificationEntity entity : kmlObject.getSpecificationsEntities()){
			getHibernateTemplate().delete(entity);
		}
		
		getHibernateTemplate().delete(kmlObject);
	}

	@Override
	public void configure(Object object) {
		
		
	}
	
}

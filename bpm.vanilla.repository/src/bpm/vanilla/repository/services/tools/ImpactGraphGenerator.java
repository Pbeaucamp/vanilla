package bpm.vanilla.repository.services.tools;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.ImpactLevel;
import bpm.vanilla.repository.beans.RepositoryDaoComponent;
import bpm.vanilla.repository.services.ServiceBrowseImpl;
import bpm.vanilla.workplace.core.datasource.AbstractDatasource;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;

public class ImpactGraphGenerator {

	public static final int TYPE_ITEM = 0;
	public static final int TYPE_DATASOURCE = 1;
	
	/**
	 *
	 * @param id
	 * @param type either TYPE_ITEM or TYPE_DATASOURCE
	 * @param component
	 * @param serviceBrowseImpl 
	 * @return a list containing the next graph level. Can contains IDatasource or RepositoryItem
	 * @throws Exception 
	 */
	public static List<Object> getNextObjects(int id, int type, RepositoryDaoComponent component, ServiceBrowseImpl serviceBrowseImpl) throws Exception {
		List<Object> result = new ArrayList<Object>();
		//Get the items by dependancies
		if(type == TYPE_ITEM) {
			RepositoryItem thisItem = component.getItemDao().findByPrimaryKey(id);
			List<RepositoryItem> items = serviceBrowseImpl.getDependantItems(thisItem);
			for(RepositoryItem item : items) {
				result.add(item);
			}
		}
		
		//look in the datasources
		//XXX for now, we look only for jdbc and oda/jdbc datasources
		List<AbstractDatasource> datasources = new ArrayList<AbstractDatasource>();
		if(type == TYPE_ITEM) {
			datasources = component.getRelationalImpactDao().getDatasourcesByItemId(id);
		}
		else {
			datasources.add(component.getRelationalImpactDao().getDatasourceById(id));
		}
		List<AbstractDatasource> correspondingDatasources = new ArrayList<AbstractDatasource>();
		for(AbstractDatasource ds : datasources) {
			correspondingDatasources = getCorrespondingDatasources(ds, false, component);
		}
		
		//If it's an item, we add the datasources to the result
		if(type == TYPE_ITEM) {
			for(AbstractDatasource ds : correspondingDatasources) {
				if(!(ds.getItemId() == id)) {
					result.add(ds);
				}
			}
		}
		//if it's a datasource, we look for the original items
		else {
			AbstractDatasource current = component.getRelationalImpactDao().getDatasourceById(id);
			for(AbstractDatasource ds : correspondingDatasources) {
				if(!(ds.isOut() == current.isOut())) {
					RepositoryItem dsItem = component.getItemDao().findByPrimaryKey(ds.getItemId());
					if(!result.contains(dsItem)) {
						result.add(dsItem);
					}
				}
			}
		}
		
		
		return result;
	}
	
	/**
	 * 
	 * @param id
	 * @param type either TYPE_ITEM or TYPE_DATASOURCE
	 * @param component
	 * @param serviceBrowseImpl 
	 * @return a list containing the previous graph level. Can contains IDatasource or RepositoryItem
	 * @throws Exception 
	 */
	public static List<Object> getPreviousObjects(int id, int type, RepositoryDaoComponent component, ServiceBrowseImpl serviceBrowseImpl) throws Exception {
		List<Object> result = new ArrayList<Object>();
		//Get the items by dependancies
		if(type == TYPE_ITEM) {
			List<RepositoryItem> items = serviceBrowseImpl.getNeededItems(id);
			for(RepositoryItem item : items) {
				result.add(item);
			}
		}
		
		//look in the datasources
		//XXX for now, we look only for jdbc and oda/jdbc datasources
		List<AbstractDatasource> datasources = new ArrayList<AbstractDatasource>();
		if(type == TYPE_ITEM) {
			datasources = component.getRelationalImpactDao().getDatasourcesByItemId(id);
		}
		else {
			datasources.add(component.getRelationalImpactDao().getDatasourceById(id));
		}
		List<AbstractDatasource> correspondingDatasources = new ArrayList<AbstractDatasource>();
		for(AbstractDatasource ds : datasources) {
			correspondingDatasources.addAll(getCorrespondingDatasources(ds, true, component));
		}
		
		//If it's an item, we add the datasources to the result
		if(type == TYPE_ITEM) {
			for(AbstractDatasource ds : correspondingDatasources) {
				if(!(ds.getItemId() == id)) {
					result.add(ds);
				}
			}
		}
		//if it's a datasource, we look for the original items
		else {
			AbstractDatasource current = component.getRelationalImpactDao().getDatasourceById(id);
			for(AbstractDatasource ds : correspondingDatasources) {
				if(!(ds.isOut() == current.isOut())) {
					RepositoryItem dsItem = component.getItemDao().findByPrimaryKey(ds.getItemId());
					if(!result.contains(dsItem)) {
						result.add(dsItem);
					}
				}
			}
			RepositoryItem dsItem = component.getItemDao().findByPrimaryKey(current.getItemId());
			if(!result.contains(dsItem)) {
				result.add(dsItem);
			}
		}
		
		return result;
	}

	/**
	 * 
	 * @param ds 
	 * @param lookForOut
	 * @param component 
	 * @return
	 */
	private static List<AbstractDatasource> getCorrespondingDatasources(AbstractDatasource ds, boolean lookForOut, RepositoryDaoComponent component) {
		List<AbstractDatasource> result = new ArrayList<AbstractDatasource>();
		
		if(ds instanceof ModelDatasourceJDBC) {
			ModelDatasourceJDBC jdbcDs = (ModelDatasourceJDBC) ds;
			
			if(jdbcDs.isOut() != lookForOut) {
			
				List<AbstractDatasource> allDs = component.getRelationalImpactDao().getAllDatasources();
				for(AbstractDatasource current : allDs) {
					if(current instanceof ModelDatasourceJDBC) {
						ModelDatasourceJDBC currentJdbc = (ModelDatasourceJDBC) current;
						
						if(currentJdbc.isOut() == lookForOut) {
							if(getJdbcUrl(currentJdbc).equals(getJdbcUrl(jdbcDs))) {
								result.add(currentJdbc);
							}
						}
						
					}
				}
			}
			
		}
		
		return result;
	}
	
	private static String getJdbcUrl(ModelDatasourceJDBC jdbc) {
		if(jdbc.isUseFullUrl()) {
			return jdbc.getFullUrl();
		}
		else {
			return jdbc.getDriver() + jdbc.getHost() + jdbc.getPort() + jdbc.getDbName();
		}
	}

	/**
	 * Generate the impact graph
	 * @param itemId
	 * @param repositoryDao
	 * @param serviceBrowseImpl 
	 * @return
	 * @throws Exception 
	 */
	public static List<ImpactLevel> generateImpactGraph(int itemId, RepositoryDaoComponent repositoryDao, ServiceBrowseImpl serviceBrowseImpl) throws Exception {
		
		List<ImpactLevel> result = new ArrayList<ImpactLevel>();
		
		RepositoryItem item = repositoryDao.getItemDao().findByPrimaryKey(itemId);
		
		ImpactLevel level = new ImpactLevel();
		List<Object> elements = new ArrayList<Object>();
		elements.add(item);
		level.setElements(elements);
		
		
		List<ImpactLevel> previous = new ArrayList<ImpactLevel>();
		previous.add(level);
		//find previous
		while(previous != null && !previous.isEmpty()) {
			List<ImpactLevel> currents = new ArrayList<ImpactLevel>(previous);
			previous = new ArrayList<ImpactLevel>();
			for(ImpactLevel currentLevel : currents) {
				for(Object obj : currentLevel.getElements()) {
					int id = -1;
					int type = -1;
					if(obj instanceof RepositoryItem) {
						id = ((RepositoryItem)obj).getId();
						type = TYPE_ITEM;
					} 
					else {
						id = ((AbstractDatasource)obj).getDbId();
						type = TYPE_DATASOURCE;
					}
					
					List<Object> previousObjects = getPreviousObjects(id, type, repositoryDao, serviceBrowseImpl);
					if(previousObjects != null && !previousObjects.isEmpty()) {
						for(Object shit : previousObjects) {
							ImpactLevel newLevel = new ImpactLevel();
							newLevel.addChild(currentLevel);
							List<Object> listOfShit = new ArrayList<Object>();
							listOfShit.add(shit);
							newLevel.setElements(listOfShit);
							
							previous.add(newLevel);
						}
					}
					else {
						result.add(currentLevel);
					}
				}
				
			}
		}
		
		previous = new ArrayList<ImpactLevel>();
		previous.add(level);
		
		//find next
		while(previous != null && !previous.isEmpty()) {
			List<ImpactLevel> currents = new ArrayList<ImpactLevel>(previous);
			previous = new ArrayList<ImpactLevel>();
			for(ImpactLevel currentLevel : currents) {
				for(Object obj : currentLevel.getElements()) {
					int id = -1;
					int type = -1;
					if(obj instanceof RepositoryItem) {
						id = ((RepositoryItem)obj).getId();
						type = TYPE_ITEM;
					} 
					else {
						id = ((AbstractDatasource)obj).getDbId();
						type = TYPE_DATASOURCE;
					}
					
					List<Object> previousObjects = getNextObjects(id, type, repositoryDao, serviceBrowseImpl);
					if(previousObjects != null && !previousObjects.isEmpty()) {
						for(Object shit : previousObjects) {
							ImpactLevel newLevel = new ImpactLevel();
							currentLevel.addChild(newLevel);
							List<Object> listOfShit = new ArrayList<Object>();
							listOfShit.add(shit);
							newLevel.setElements(listOfShit);
							
							previous.add(newLevel);
						}

					}
					
				}
				
			}
		}
		
		return result;
	}
}

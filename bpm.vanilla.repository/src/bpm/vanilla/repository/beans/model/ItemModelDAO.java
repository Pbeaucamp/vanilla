package bpm.vanilla.repository.beans.model;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ItemModelDAO extends HibernateDaoSupport {
	
	private int limitNumberVersion;
	
	public void setLimitNumberVersion(int limitNumberVersion) {
		this.limitNumberVersion = limitNumberVersion;
	}

	public ItemModel getVersion(int itemId, int revisionNumber) {
		List<ItemModel> versions = getHibernateTemplate().find("from ItemModel where itemId = " + itemId + " and version = " + revisionNumber);
		return versions.get(0);
	}

	public ItemModel getLastVersion(int itemId) {
		List<ItemModel> versions = getAllVersion(itemId);
		ItemModel last = null;
		for(ItemModel version : versions) {
			if(last == null || last.getVersion() < version.getVersion()) {
				last = version;
			}
		}
		return last;
	}
	
	public List<ItemModel> getAllVersion(int itemId) {
		return getHibernateTemplate().find("from ItemModel where itemId = " + itemId);
	}
	
	public void deletePreviousVersion(int itemId) {
		if(limitNumberVersion > 0) {
			List<ItemModel> versions = getAllVersion(itemId);
			if(versions != null && versions.size() >= limitNumberVersion) {
				int numberOfDelete = (versions.size() + 1) - limitNumberVersion;
				for(int i=0; i<numberOfDelete; i++) {
					ItemModel first = null;
					for(ItemModel version : versions) {
						if(first == null || first.getVersion() > version.getVersion()) {
							first = version;
						}
					}
					if(first != null){
						delete(first);
						versions.remove(first);
					}
				}
			}
		}
	}
	
	public ItemModel save(ItemModel model) {
		deletePreviousVersion(model.getItemId());
		
		ItemModel last = getLastVersion(model.getItemId());
		if(last != null) {
			model.setVersion(last.getVersion() + 1);
		}
		else {
			model.setVersion(1);
		}
		model.setId((Integer)getHibernateTemplate().save(model));
		return model;
	}
	
	public void delete(ItemModel model) {
		getHibernateTemplate().delete(model);
	}

	public void deleteByItemId(int itemId) {
		List<ItemModel> versions = getAllVersion(itemId);
		if(versions != null){
			for (ItemModel model : versions) {
				delete(model);
			}
		}
	}
	 
}

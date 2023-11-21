package bpm.vanilla.repository.beans;

import java.util.List;

import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class GlobalDAO extends HibernateDaoSupport {

	public Object manageItem(Object item, ManageAction action) throws Exception {
		if (item != null) {
			switch (action) {
			case SAVE:
				item = saveItem(item);
				break;
			case SAVE_OR_UPDATE:
				item = saveOrUpdateItem(item);
				break;
			case UPDATE:
				item = updateItem(item);
				break;
			case DELETE:
				deleteItem(item);
				break;
			default:
				break;
			}
			return item;
		}
		return null;
	}
	
	private Object saveItem(Object item) throws Exception {
		setModificationInfos(item);

		manageItemDependancies(item, true);
		if (item instanceof ItemMetadataTableLink) {
			ItemMetadataTableLink itemInt = (ItemMetadataTableLink) item;

			Integer id = (Integer) save(itemInt);
			itemInt.setId(id);
			manageItemDependancies(item, false);
			return item;
		}

		throw new Exception("Unknow type item : " + item.getClass().toString());
	}

	/**
	 * Cette m�thode cr�e ou mets � jour un �l�ment en BDD et cr�e ou mets �
	 * jour ses d�pendances
	 * 
	 * @param item
	 * @return
	 * @throws Exception 
	 */
	private Object saveOrUpdateItem(Object item) throws Exception {
		setModificationInfos(item);
		manageItemDependancies(item, true);
		if (item instanceof ItemMetadataTableLink) {
			ItemMetadataTableLink itemInt = (ItemMetadataTableLink) item;
			if (itemInt.getId() <= 0) {
				Integer id = (Integer) save(itemInt);
				itemInt.setId(id);
				manageItemDependancies(item, false);
				return item;
			}
		}

		update(item);
		manageItemDependancies(item, false);
		return item;
	}

	/**
	 * Cette m�thode mets � jour un �l�ment en BDD et cr�e ou mets � jour ses
	 * d�pendances
	 * 
	 * @param item
	 * @return
	 * @throws Exception 
	 */
	private Object updateItem(Object item) throws Exception {
		setModificationInfos(item);
		manageItemDependancies(item, true);
		update(item);
		manageItemDependancies(item, false);
		return item;
	}

	public void deleteItem(Object item) {
		delete(item);
	}

	private void setModificationInfos(Object item) {
		
	}
	/**
	 * Gestion des d�pendances d'un �l�ment
	 * 
	 * beforeSave = true G�re les d�pendances qui doivent fournir leur ID a
	 * l'�l�ment principal <T>
	 * 
	 * beforeSave = false G�re les d�pendances qui doivent obtenir l'ID de
	 * l'�l�ment principal <T> avant d'�tre sauv� en BDD
	 * 
	 * @param item
	 * @param beforeSave
	 * @throws Exception 
	 * @throws OdessaaException
	 */
	private void manageItemDependancies(Object item, boolean beforeSave) throws Exception {
		if (!beforeSave) {

		}
	}

	@SuppressWarnings("unchecked")
	public List<ItemMetadataTableLink> getMetadataLinks(int itemId) {
		return (List<ItemMetadataTableLink>) getHibernateTemplate().find("SELECT m FROM ItemMetadataTableLink m WHERE m.itemId =" + itemId);
	}

}

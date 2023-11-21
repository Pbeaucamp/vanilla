package bpm.vanilla.repository.beans.meta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaForm;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class MetaDAO extends HibernateDaoSupport {
	public void manageItems(List<MetaLink> links, ManageAction action) throws Exception {
		if (links != null) {
			for (MetaLink link : links) {
				((MetaLink) manageItem(link, ManageAction.SAVE_OR_UPDATE)).getId();
			}
		}
	}

//	public void manageValues(List<MetaValue> values, ManageAction action) throws Exception {
//		if (values != null) {
//			for (MetaValue value : values) {
//				manageItem(value, action);
//			}
//		}
//	}

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
		if (item instanceof MetaValue) {
			MetaValue itemInt = (MetaValue) item;

			Integer id = (Integer) save(itemInt);
			itemInt.setId(id);
			manageItemDependancies(item, false);
			return item;
		}

		throw new Exception("Unknow type item : " + item.getClass().toString());
	}

	/**
	 * Cette méthode crée ou mets à jour un élément en BDD et crée ou mets à
	 * jour ses dépendances
	 * 
	 * @param item
	 * @return
	 * @throws Exception 
	 */
	private Object saveOrUpdateItem(Object item) throws Exception {
		setModificationInfos(item);
		manageItemDependancies(item, true);
		if (item instanceof MetaLink) {
			MetaLink itemInt = (MetaLink) item;
			if (itemInt.getId() <= 0) {
				Integer id = (Integer) save(itemInt);
				itemInt.setId(id);
				manageItemDependancies(item, false);
				return item;
			}
		}
		else if (item instanceof MetaValue) {
			MetaValue itemInt = (MetaValue) item;
			if (itemInt.getId() <= 0) {
				Integer id = (Integer) save(itemInt);
				itemInt.setId(id);
				manageItemDependancies(item, false);
				return item;
			}
		}
		else if (item instanceof Meta) {
			Meta itemInt = (Meta) item;
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
	 * Cette méthode mets à jour un élément en BDD et crée ou mets à jour ses
	 * dépendances
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
		if (item instanceof MetaValue) {
			((MetaValue) item).setModifyDate(new Date());
		}
	}
	/**
	 * Gestion des dépendances d'un élément
	 * 
	 * beforeSave = true Gère les dépendances qui doivent fournir leur ID a
	 * l'élément principal <T>
	 * 
	 * beforeSave = false Gère les dépendances qui doivent obtenir l'ID de
	 * l'élément principal <T> avant d'être sauvé en BDD
	 * 
	 * @param item
	 * @param beforeSave
	 * @throws Exception 
	 * @throws OdessaaException
	 */
	private void manageItemDependancies(Object item, boolean beforeSave) throws Exception {
		if (!beforeSave) {
			if (item instanceof MetaLink && ((MetaLink) item).getValue() != null) {
				MetaValue value = ((MetaLink) item).getValue();
				value.setMetaLinkId(((MetaLink) item).getId());
				manageItem(value, ManageAction.SAVE_OR_UPDATE);
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	public List<Meta> getMetaByForm(int formId) {
		return (List<Meta>) getHibernateTemplate().find("SELECT m FROM Meta m, MetaFormLink l WHERE m.id = l.metaId AND l.metaFormId=" + formId);
	}

	@SuppressWarnings("unchecked")
	public List<MetaLink> getMetaLinks(int itemId, TypeMetaLink type, boolean loadResponse) {
		List<MetaLink> metas = (List<MetaLink>) getHibernateTemplate().find("FROM MetaLink WHERE itemId=" + itemId + " AND type=" + type.ordinal());
		loadItems(metas, loadResponse);
		return metas;
	}

	private void loadItems(List<MetaLink> links, boolean loadResponse) {
		if (links != null) {
			for (MetaLink link : links) {
				Meta meta = getMeta(link.getMetaId());
				link.setMeta(meta);
				if (loadResponse) {
					MetaValue value = getMetaValue(link.getId());
					link.setValue(value);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private Meta getMeta(int metaId) {
		List<Meta> metas = (List<Meta>) getHibernateTemplate().find("FROM Meta WHERE id=" + metaId);
		return metas != null && !metas.isEmpty() ? metas.get(0) : null;
	}
	
	public Meta getMeta(String meta) {
		List<Meta> metas = getHibernateTemplate().find("FROM Meta WHERE key=?", meta);
		return metas != null && !metas.isEmpty() ? metas.get(0) : null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MetaForm> getMetaForms() {
		return (List<MetaForm>) getHibernateTemplate().find("FROM MetaForm");
	}

	@SuppressWarnings("unchecked")
	private MetaValue getMetaValue(int metaLinkId) {
		List<MetaValue> values = (List<MetaValue>) getHibernateTemplate().find("FROM MetaValue WHERE metaLinkId=" + metaLinkId);
		return values != null && !values.isEmpty() ? values.get(0) : null;
	}
	
	public List<Integer> getItemsByMeta(TypeMetaLink type, List<MetaValue> values) {
		List<Integer> items = new ArrayList<Integer>();
		if (values != null) {
			boolean first = true;
			for (MetaValue metaValue : values) {
				String value = metaValue.getValue();
				if (value == null || value.isEmpty()) {
					return new ArrayList<Integer>();
				}
				
				List<Integer> itemsFound = getItemsByMeta(type, metaValue.getMetaKey(), value);
				if (first) {
					items.addAll(itemsFound);
					first = false;
				}
				else {
					ListIterator<Integer> iter = items.listIterator();
					while(iter.hasNext()){
					    if (!itemsFound.contains(iter.next())){
					        iter.remove();
					    }
					}
				}
			}
		}
		return items;
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getItemsByMeta(TypeMetaLink type, String metaKey, String value) {
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT l.itemId ");
		buf.append("FROM Meta m, MetaLink l, MetaValue v ");
		buf.append("WHERE l.id = v.metaLinkId ");
		buf.append("AND l.metaId=m.id ");
		buf.append("AND m.key='" + metaKey + "' ");
		buf.append("AND l.type=" + type.ordinal() + " ");
		buf.append("AND v.value='" + value + "'");
//		buf.append("AND (");
//		buf.append("		(m.type=" + Meta.TypeMeta.DATE.ordinal() + " AND DATE_FORMAT(v.value,'" + MetaValue.DATE_FORMAT + "')='" + value + "')");
//		buf.append("	OR ");
//		buf.append("		(v.value='" + value + "')");
//		buf.append(")");
		return (List<Integer>) getHibernateTemplate().find(buf.toString());
	}
}

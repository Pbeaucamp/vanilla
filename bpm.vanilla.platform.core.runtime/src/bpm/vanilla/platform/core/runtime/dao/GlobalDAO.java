package bpm.vanilla.platform.core.runtime.dao;

import java.io.Serializable;

import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.resources.D4C;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.beans.resources.ValidationRuleResult;
import bpm.vanilla.platform.core.beans.resources.ValidationSchemaResult;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class GlobalDAO extends HibernateDaoSupport {

	public Serializable manageItem(Serializable item, ManageAction action) throws Exception {
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
	
	private Serializable saveItem(Serializable item) throws Exception {
		setModificationInfos(item);

		manageItemDependancies(item, true);
		if (item instanceof D4C) {
			D4C itemInt = (D4C) item;

			Integer id = (Integer) save(itemInt);
			itemInt.setId(id);
			manageItemDependancies(itemInt, false);
			return item;
		}
		else if (item instanceof ValidationDataResult) {
			ValidationDataResult itemInt = (ValidationDataResult) item;

			Integer id = (Integer) save(itemInt);
			itemInt.setId(id);
			manageItemDependancies(itemInt, false);
			return item;
		}
		else if (item instanceof ValidationSchemaResult) {
			ValidationSchemaResult itemInt = (ValidationSchemaResult) item;

			Integer id = (Integer) save(itemInt);
			itemInt.setId(id);
			manageItemDependancies(itemInt, false);
			return item;
		}
		else if (item instanceof ValidationRuleResult) {
			ValidationRuleResult itemInt = (ValidationRuleResult) item;

			Integer id = (Integer) save(itemInt);
			itemInt.setId(id);
			manageItemDependancies(itemInt, false);
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
	private Serializable saveOrUpdateItem(Serializable item) throws Exception {
		setModificationInfos(item);
		manageItemDependancies(item, true);
		if (item instanceof D4C) {
			D4C itemInt = (D4C) item;
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
	private Serializable updateItem(Serializable item) throws Exception {
		setModificationInfos(item);
		manageItemDependancies(item, true);
		update(item);
		manageItemDependancies(item, false);
		return item;
	}

	public void deleteItem(Serializable item) {
		delete(item);
	}

	private void setModificationInfos(Serializable item) {
		
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
	private void manageItemDependancies(Serializable item, boolean beforeSave) throws Exception {
		if (!beforeSave) {
			if (item instanceof ValidationDataResult) {
				ValidationDataResult itemParent = (ValidationDataResult) item;
				if (itemParent.getSchemaValidationResults() != null) {
					for (ValidationSchemaResult schemaResult : itemParent.getSchemaValidationResults()) {
						schemaResult.setValidationId(itemParent.getId());
						saveItem(schemaResult);
					}
				}
			}
			else if (item instanceof ValidationSchemaResult) {
				ValidationSchemaResult itemParent = (ValidationSchemaResult) item;
				if (itemParent.getRuleResults() != null) {
					for (ValidationRuleResult ruleResult : itemParent.getRuleResults()) {
						ruleResult.setValidationSchemaId(itemParent.getId());
						saveItem(ruleResult);
					}
				}
			}
		}
	}
}

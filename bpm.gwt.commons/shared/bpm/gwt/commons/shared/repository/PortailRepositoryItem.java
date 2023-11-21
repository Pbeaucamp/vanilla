package bpm.gwt.commons.shared.repository;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortailRepositoryItem implements IRepositoryObject {

	private static final long serialVersionUID = 1L;
	
	private IRepositoryObject parent;
	private RepositoryItem item;

	private String createdBy;
	private String modifiedBy;
	
	private String typeName;
	
	private boolean isViewable = false;
	private boolean isOwned = false;

	public PortailRepositoryItem() {
		super();
	}

	public PortailRepositoryItem(RepositoryItem item, String typeName) {
		this.item = item;
		this.typeName = typeName;
	}

	@Override
	public String getName() {
		return item.getName();
	}

	public int getId() {
		return item.getId();
	}
	
	public void setParent(IRepositoryObject parent) {
		this.parent = parent;
	}
	
	public PortailRepositoryDirectory getParentDirectory() {
		return parent != null && parent instanceof PortailRepositoryDirectory ? (PortailRepositoryDirectory)parent : null;
	}
	
	public PortailRepositoryItem getParentItem() {
		return parent != null && parent instanceof PortailRepositoryItem ? (PortailRepositoryItem)parent : null;
	}

	public boolean hasDefaultFormat() {
		if (item.getDefaultFormat() == null || item.getDefaultFormat().equals("")) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public int getType() {
		return item.getType();
	}
	
	public int getSubType() {
		return item.getSubtype();
	}

	public boolean isReport() {
		return item.getType() == IRepositoryApi.FWR_TYPE || (item.getType() == IRepositoryApi.CUST_TYPE && (item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE || item.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE));
	}

	public void setViewable(boolean isViewable) {
		this.isViewable = isViewable;
	}

	public boolean isViewable() {
		return isViewable;
	}

	public RepositoryItem getItem() {
		return item;
	}

	public int getDirectoryId() {
		if(parent == null || !(parent instanceof PortailRepositoryDirectory)) {
			return 0;
		}
		else {
			return ((PortailRepositoryDirectory)parent).getId();
		}
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setOwned(boolean isOwned) {
		this.isOwned = isOwned;
	}

	public boolean isOwned() {
		return isOwned;
	}

	public String getTypeName() {
		return typeName;
	}
}

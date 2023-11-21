package bpm.gwt.commons.shared.repository;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortailItemFmdt extends PortailRepositoryItem {

	private static final long serialVersionUID = 1L;
	
	private List<PortailItemFmdtDriller> drillers = new ArrayList<PortailItemFmdtDriller>();
	
	public PortailItemFmdt() { }

	public PortailItemFmdt(RepositoryItem item, String typeName) {
		super(item, typeName);
	}

	public List<PortailItemFmdtDriller> getDrillers() {
		return drillers;
	}

	public void addDriller(PortailItemFmdtDriller driller) {
		if(drillers == null) {
			this.drillers = new ArrayList<PortailItemFmdtDriller>();
		}
		
		driller.setParent(this);
		
		this.drillers.add(driller);
	}
}

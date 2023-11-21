package bpm.gwt.commons.shared;

import java.util.ArrayList;

import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;

public class InfoShareD4C extends InfoShare {
	
	private CkanPackage pack;
	
	public InfoShareD4C() { }
	
	public InfoShareD4C(TypeExport typeExport, CkanPackage pack) {
		super(TypeShare.CKAN, typeExport, "", "", new ArrayList<Group>(), new ArrayList<Email>(), "", false);
		this.pack = pack;
	}
	
	public CkanPackage getPack() {
		return pack;
	}
}

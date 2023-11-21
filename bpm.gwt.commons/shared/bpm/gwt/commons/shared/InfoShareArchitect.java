package bpm.gwt.commons.shared;

import java.util.ArrayList;

import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.Group;

public class InfoShareArchitect extends InfoShare {
	
	private Contract contract;
	
	public InfoShareArchitect() { }
	
	public InfoShareArchitect(TypeExport typeExport, String name, String format, Contract contract) {
		super(TypeShare.ARCHITECT, typeExport, name, format, new ArrayList<Group>(), new ArrayList<Email>(), "", false);
		this.contract = contract;
	}
	
	public Contract getContract() {
		return contract;
	}
}

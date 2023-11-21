package bpm.vanilla.map.design.ui.wizard.map;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.IMapDefinition;

public class AddressSelectionZonePage extends WizardPage  {
	private TreeViewer addressTree;
	
	private IMapDefinition mapDefinition;
	private IBuilding building;
	private List<IAddress> addresses;
	
	protected AddressSelectionZonePage(String pageName, IMapDefinition mapDef, List<IAddress> addresses, boolean isBuilding) {
		super(pageName);
		this.mapDefinition = mapDef;
		this.addresses = addresses;
	}

	@Override
	public void createControl(Composite parent) {
		
		
	}

}

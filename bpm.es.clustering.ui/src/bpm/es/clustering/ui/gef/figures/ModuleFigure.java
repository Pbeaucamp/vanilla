package bpm.es.clustering.ui.gef.figures;

import org.eclipse.draw2d.GridLayout;


public class ModuleFigure extends ContainerFigure{
	private boolean defaultModule = false;
	
	public ModuleFigure(String title, boolean isDefault) {
		super(title);
		this.defaultModule = isDefault;
		getClient().setLayoutManager(new GridLayout());
	}

	public boolean isDefault(){
		return defaultModule;
	}
	
}

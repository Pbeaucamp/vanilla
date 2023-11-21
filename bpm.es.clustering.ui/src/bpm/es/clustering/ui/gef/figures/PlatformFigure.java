package bpm.es.clustering.ui.gef.figures;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import bpm.es.clustering.ui.Messages;

public class PlatformFigure extends ContainerFigure{
	
	private static final Color col1 = new Color(Display.getDefault(), 232, 227 , 228);
	private static final Color colApps = new Color(Display.getDefault(), 158, 173 , 241);
	private static final Color colResp = new Color(Display.getDefault(), 247, 252 , 152);
	private static final Color colMod = new Color(Display.getDefault(), 225, 247 , 213);
	
	private ContainerFigure applicationsHolder;
	private ContainerFigure defaultModuleHolder;
	private ContainerFigure modulesHolder;
	private ContainerFigure repositoriesHolder;
	
	public PlatformFigure(){
		super(Messages.PlatformFigure_0);
		getMain().setBackgroundColor(col1);
		
		getClient().setLayoutManager(new GridLayout(2, false));
		
		applicationsHolder = new ContainerFigure(Messages.PlatformFigure_1);
		applicationsHolder.getMain().setBackgroundColor(colApps);
		add(applicationsHolder, new GridData(GridData.FILL, GridData.FILL, true, false));
		
		repositoriesHolder = new ContainerFigure(Messages.PlatformFigure_2);
		repositoriesHolder.getMain().setBackgroundColor(colResp);
		repositoriesHolder.getClient().setLayoutManager(new GridLayout());
		
		add(repositoriesHolder, new GridData(GridData.END, GridData.FILL, false, true, 1, 3));

		
		defaultModuleHolder = new ContainerFigure(Messages.PlatformFigure_3);
		add(defaultModuleHolder, new GridData(GridData.FILL, GridData.FILL, true, false));
		
		modulesHolder = new ContainerFigure(Messages.PlatformFigure_4);
		modulesHolder.getMain().setBackgroundColor(colMod);
		modulesHolder.getClient().setLayoutManager(new GridLayout());
		add(modulesHolder, new GridData(GridData.FILL, GridData.FILL, true, false));
	
	}
	
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		if (figure instanceof ClientFigure){
			applicationsHolder.getClient().setLayoutManager(new GridLayout(7, true));
			applicationsHolder.add(figure, new GridData(GridData.BEGINNING, GridData.CENTER, true, false), index);
			
		}
		else if (figure instanceof RuntimeServerFigure){
			
			defaultModuleHolder.add(figure);
		}
		else if (figure instanceof ModuleFigure){
			if (((ModuleFigure)figure).isDefault()){
				defaultModuleHolder.getClient().setLayoutManager(new GridLayout(7, true));
				defaultModuleHolder.add(figure);
			}
			else{
				modulesHolder.getClient().setLayoutManager(new GridLayout(7, true));
				modulesHolder.add(figure);
			}
			
		}
		else if (figure instanceof RepositoryFigure){
			repositoriesHolder.add(figure);
		}
		else{
			super.add(figure, constraint, index);
		}
		
	}
	
}

package bpm.fd.design.ui.component.wizard;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.maps.MapDatas;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentMapWMS;
import bpm.fd.api.core.model.components.definition.maps.openlayers.MapWMSOptions;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.map.pages.MapColorRangePage;
import bpm.fd.design.ui.component.map.pages.MapDatasPage;
import bpm.fd.design.ui.component.map.wms.pages.MapWmsOptionsPage;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class WizardMapWMS extends Wizard implements IWizardComponent {
 
	private ComponentMapWMS def;
	
	private GeneralPage generic;
	private MapWmsOptionsPage optionsPage;
	private MapDatasPage dataPage;
	private MapColorRangePage colorPage;
	
	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		ComponentMapWMS component = new ComponentMapWMS(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setName(prop.getProperty(GeneralPage.PROPERTY_NAME));
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));

		MapWMSOptions opt = optionsPage.getOptions();
		
		component.setComponentOption(opt);
		
		component.setDatas((MapDatas) dataPage.getMapDatas());

		component.setColorRanges(colorPage.getColorRanges());
		
		try {
			dictionaty.addComponent(component);
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardMapWMS_0, Messages.WizardMapWMS_1, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		
		
		Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, component);
		
		
		def = component;
		return true;
	}

	@Override
	public IComponentDefinition getComponent() {
		return def;
	}

	@Override
	public Class<? extends IComponentDefinition> getComponentClass() {
		return ComponentMapWMS.class;
	}

	@Override
	public boolean needRepositoryConnections() {
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}

	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		
		optionsPage = new MapWmsOptionsPage();
		addPage(optionsPage);
		
		colorPage = new MapColorRangePage();
		addPage(colorPage);
		
		dataPage = new MapDatasPage();
		addPage(dataPage);
		
		super.addPages();
	}

	@Override
	public boolean canFinish() {

		return optionsPage.isPageComplete();
	}

	
}

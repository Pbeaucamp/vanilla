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
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmData;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapData;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapDataSerie;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.map.pages.MapDatasPage;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.wizard.IWizardComponent;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapVanilla;

public class WizardMapOsm extends Wizard implements IWizardComponent{

	private GeneralPage generic;
	private MapDatasPage mapDataPage;
	
	private ComponentOsmMap map;
	
	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		
		mapDataPage = new MapDatasPage();
		addPage(mapDataPage);
		
		super.addPages();
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {}

	@Override
	public IComponentDefinition getComponent() {
		return map;
	}

	@Override
	public Class<? extends IComponentDefinition> getComponentClass() {
		return ComponentOsmMap.class;
	}

	@Override
	public boolean needRepositoryConnections() {
		return false;
	}

	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		map = new ComponentOsmMap(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		map.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));
		
		DataSet dss = mapDataPage.getMapDatas().getDataSet();
		Integer valueIndex = mapDataPage.getMapDatas().getValueFieldIndex();
		Integer zoneIndex = ((MapDatas)mapDataPage.getMapDatas()).getZoneIdFieldIndex();
		MapVanilla mapVanilla = ((MapDatas)mapDataPage.getMapDatas()).getMapVanilla();
		
		VanillaMapData data = new VanillaMapData();
		data.setDataset(dss);
		data.setValueFieldIndex(valueIndex);
		data.setZoneFieldIndex(zoneIndex);
		data.setMap(mapVanilla);
		
		map.setData(data);
		
		for(MapDataSet ds : mapVanilla.getDataSetList()) {
			VanillaMapDataSerie serie = new VanillaMapDataSerie();
			serie.setType(ds.getType());
			serie.setName(ds.getName());
			serie.setMapDataset(ds);
			serie.setMaxMarkerSize(ds.getMarkerSizeMax());
			serie.setMinMarkerSize(ds.getMarkerSizeMin());
			data.addSerie(serie);
		}
		
		try {
			dictionaty.addComponent(map);
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardMapWMS_0, Messages.WizardMapWMS_1, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		
		return true;
	}

}

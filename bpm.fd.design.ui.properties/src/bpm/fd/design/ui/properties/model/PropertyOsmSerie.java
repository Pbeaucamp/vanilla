package bpm.fd.design.ui.properties.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.RGB;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapDataSerie;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.map.PropertyMapColor;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class PropertyOsmSerie extends PropertyGroup{

//	private ComboBoxViewerCellEditor dataSourceCbo;
//	private ComboBoxViewerCellEditor dataSetCbo;
//	private ComboBoxViewerCellEditor fieldCbo;
	
	private VanillaMapDataSerie serie;
	private ComponentOsmMap component;
	
//	private Property datasource;
//	private Property dataset;
//	private Property zoneId;
//	
////	private Property markerSize;
//	private Property latitude;
//	private Property longitude;
	
	private Property minMarkerSize;
	private Property maxMarkerSize;
	private Property useColsForColors;
//	private Property markerUrl;
	
//	private Property hasParent;
//	private Property parentId;
//	private ComboBoxViewerCellEditor parentField;
//	private Property parentSerie;
//	private ComboBoxViewerCellEditor parentSerieList;
	
	private ComboBoxViewerCellEditor modelPageList;
	
	private Property minColor;
	private Property maxColor;
	
	private Property isDiplay;
	
//	private Property points;
	private Property targetPage;
	
	private ComboBoxViewerCellEditor fieldEditor;

	public PropertyOsmSerie(VanillaMapDataSerie serie, ComboBoxViewerCellEditor fieldEditor, ComponentOsmMap component) {
		super(serie.getName());
		
		this.fieldEditor = fieldEditor;
		
		this.component = component;
		this.serie = serie;
		
		isDiplay = new Property("IsDisplay", new CheckboxCellEditor(fieldEditor.getControl().getParent()));
		
		add(isDiplay);
		
		if(serie.getType().equals(VanillaMapDataSerie.POLYGON) || serie.getType().equals(VanillaMapDataSerie.LINE)) {
			minColor = new Property("Min color", new ColorCellEditor(fieldEditor.getControl().getParent()));
			maxColor = new Property("Max color", new ColorCellEditor(fieldEditor.getControl().getParent()));
			
			add(minColor);
			add(maxColor);
		}
		else {
			
			minMarkerSize = new Property("Min marker size", new TextCellEditor(fieldEditor.getControl().getParent()));
			maxMarkerSize = new Property("Max marker size", new TextCellEditor(fieldEditor.getControl().getParent()));
			
			add(minMarkerSize);
			add(maxMarkerSize);
			
			for(ColorRange range : serie.getMarkerColors()) {
				addColor(range);
			}
			
//			createColorMenu(fieldEditor.getControl().getParent());
			
//			add(new PropertyMapColor(c, fieldEditor.getControl().getParent()));
			
			
			modelPageList = new ComboBoxViewerCellEditor(fieldEditor.getControl().getParent());
			modelPageList.setContenProvider(new ArrayContentProvider());
			modelPageList.setLabelProvider(new DatasLabelProvider());
			
			targetPage = new Property(Messages.ChartEditor_16, modelPageList);
			add(targetPage);
			
			List<String> modelNames = new ArrayList<String>();
			for(FdModel model : ((MultiPageFdProject)Activator.getDefault().getProject()).getPagesModels()) {
				modelNames.add(model.getName());
			}
			
			useColsForColors = new Property("Use columns for colors", new CheckboxCellEditor(fieldEditor.getControl().getParent()));
			add(useColsForColors);
			
			modelPageList.setInput(modelNames);
		}
	}
	
	public void addColor(ColorRange range) {
		add(new PropertyMapColor(range, fieldEditor.getControl().getParent()));
	}
	
	public VanillaMapDataSerie getSerie() {
		return serie;
	}
	
	public String getPropertyValueString(Object element) {
		
		if(element == minColor) {
			return serie.getMinColor();
		}
		else if(element == maxColor) {
			return serie.getMaxColor();
		}
		else if(element == targetPage) {
			try {
				return ((VanillaMapDataSerie)serie).getTargetPageName();
			} catch(Exception e) {
				return null;
			}
		}
		else if(element == isDiplay) {
			return String.valueOf(serie.isDisplay());
		}
		else if(element == useColsForColors) {
			return String.valueOf(serie.isUseColsForColors());
		}
		else if(element == minMarkerSize) {
			return ((VanillaMapDataSerie)serie).getMinMarkerSize() + "";
		}
		else if(element == maxMarkerSize) {
			return ((VanillaMapDataSerie)serie).getMaxMarkerSize() + "";
		}
		
		return null;
		
	}
	public Object getPropertyValue(Object element) {
		
		if(element == minColor) {
			
			return getRGBFromColor(serie.getMinColor());
		}
		else if(element == maxColor) {
			return getRGBFromColor(serie.getMaxColor());
		}
		
		else if(element == isDiplay) {
			return serie.isDisplay();
		}
		else if(element == useColsForColors) {
			return serie.isUseColsForColors();
		}
		else if(element == targetPage) {
			try {
				return ((VanillaMapDataSerie)serie).getTargetPageName();
			} catch (Exception e) {
				return null;
			}
		}
		else if(element == minMarkerSize) {
			return String.valueOf(((VanillaMapDataSerie)serie).getMinMarkerSize());
		}
		else if(element == maxMarkerSize) {
			return String.valueOf(((VanillaMapDataSerie)serie).getMaxMarkerSize());
		}
		
		return null;
	}
	
	private RGB getRGBFromColor(String value) {
		int r = Integer.parseInt(value.substring(0, 2), 16);
		int g = Integer.parseInt(value.substring(2, 4), 16);
		int b = Integer.parseInt(value.substring(4, 6), 16);
		
		return  new RGB( r, g, b);
	}
	
	private String getColorFromRGB(RGB value) {
		
		String r = Integer.toHexString(value.red);
		if (r.length() == 1){
			r = "0" + r; //$NON-NLS-1$
		}
		String b = Integer.toHexString(value.blue);
		if (b.length() == 1){
			b = "0" + b; //$NON-NLS-1$
		}
		String g = Integer.toHexString(value.green);
		if (g.length() == 1){
			g = "0" + g; //$NON-NLS-1$
		}
		String s = r + g + b;
		return s;
		
	}
	
	public void setPropertyValue(Object element, Object value) {
		
//		if(element instanceof Property && ((Property) element).getParent() instanceof PropertyMapColor) {
//			((PropertyMapColor) ((Property) element).getParent()).setValue((Property) element, value);
//		}
		if(element == minColor) {
			RGB color = (RGB) value;
			
			serie.setMinColor(getColorFromRGB(color));
			
//			return serie.getMinColor();
		}
		else if(element == maxColor) {
			
			RGB color = (RGB) value;
			
			serie.setMaxColor(getColorFromRGB(color));
			
//			return serie.getMaxColor();
		}
		
		else if(element == isDiplay) {
			serie.setDisplay((boolean)value);
		}
		else if(element == useColsForColors) {
			serie.setUseColsForColors((boolean)value);
		}
		else if(element == targetPage) {
			((VanillaMapDataSerie)serie).setTargetPageName((String)value);
		}
		else if(element == minMarkerSize) {
			try {
				((VanillaMapDataSerie)serie).setMinMarkerSize(Integer.parseInt((String) value));
			} catch (NumberFormatException e) {
			}
		}
		else if(element == maxMarkerSize) {
			try {
				((VanillaMapDataSerie)serie).setMaxMarkerSize(Integer.parseInt((String) value));
			} catch (NumberFormatException e) {
			}
		}
//		else if(element == longitude) {
//			if(serie instanceof OsmDataSeriePolygon) {
//				((OsmDataSeriePolygon)serie).setLongitudeFieldIndex(((ComboBoxViewerCellEditor)longitude.getCellEditor()).getViewer().getCCombo().getSelectionIndex() + 1);
//			}
//			else {
//				((OsmDataSerieMarker)serie).setLongitudeFieldIndex(((ComboBoxViewerCellEditor)longitude.getCellEditor()).getViewer().getCCombo().getSelectionIndex() + 1);
//			}
//		}
//		else if(element == minMarkerSize) {
//			try {
//				((OsmDataSerieMarker)serie).setMinMarkerSize(Integer.parseInt((String) value));
//			} catch (NumberFormatException e) {
//			}
//		}
//		else if(element == maxMarkerSize) {
//			try {
//				((OsmDataSerieMarker)serie).setMaxMarkerSize(Integer.parseInt((String) value));
//			} catch (NumberFormatException e) {
//			}
//		}
//		else if(element == markerUrl) {
//			((OsmDataSerieMarker)serie).setMarkerUrl((String) value);
//		}
//		else if(element == zoneId) {
//			((OsmDataSeriePolygon)serie).setZoneFieldIndex(((ComboBoxViewerCellEditor)zoneId.getCellEditor()).getViewer().getCCombo().getSelectionIndex() + 1);
//		}
	}

	public void refresh() {

	}

	public void removeColor(PropertyMapColor range) {
		remove(range);
	}

}

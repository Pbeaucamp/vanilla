package bpm.fd.design.ui.gef.figures;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.components.definition.filter.MenuOptions;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.components.definition.jsp.ComponentD4C;
import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.ComponentJsp;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentMapWMS;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.markdown.ComponentMarkdown;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.styledtext.ComponentStyledTextInput;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.FileJavaScript;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;

public class PictureHelper {
	
	
	public static Image getFullSizePicture(IComponentDefinition def){
		try{
			Image i = null;
			if (def instanceof ComponentFilterDefinition){
				switch(((ComponentFilterDefinition)def).getRenderer().getRendererStyle()){
				case FilterRenderer.DROP_DOWN_LIST_BOX:
					i =  Activator.getDefault().getImageRegistry().get(Icons.combo_big);
					break;
				case FilterRenderer.SLIDER:
					i =  Activator.getDefault().getImageRegistry().get(Icons.slider);
					break;
				case FilterRenderer.DATE_PIKER:
					i =  Activator.getDefault().getImageRegistry().get(Icons.datepicker);
					break;
				case FilterRenderer.RADIOBUTTON:
					i =  Activator.getDefault().getImageRegistry().get(Icons.radio_big);
					break;
				case FilterRenderer.TEXT_FIELD:
					i =  Activator.getDefault().getImageRegistry().get(Icons.field_big);
					break;
				case FilterRenderer.MENU:
					if (((MenuOptions)def.getOptions(MenuOptions.class)).getIsVertical()){
						i =  Activator.getDefault().getImageRegistry().get(Icons.menu_vertical);
					}
					else{
						i =  Activator.getDefault().getImageRegistry().get(Icons.menu_horizontal);
					}
				}
				
				
				
					
			}
			
			if (def instanceof ComponentMap){
				i = Activator.getDefault().getImageRegistry().get(Icons.map32);
			}
			
			if (def instanceof ComponentChartDefinition){
				switch(((ComponentChartDefinition)def).getNature().getNature()){
				case ChartNature.COLUMN_3D:
				case ChartNature.COLUMN:
//					new Image
					i =  Activator.getDefault().getImageRegistry().get(Icons.column3D_big);
					break;
				case ChartNature.BAR:
					i =  Activator.getDefault().getImageRegistry().get(Icons.bar3D_big);
					break;
				case ChartNature.BAR_3D:
					i =  Activator.getDefault().getImageRegistry().get(Icons.bar3D_big);
					break;
				case ChartNature.PIE:
				case ChartNature.PIE_3D:
					i =  Activator.getDefault().getImageRegistry().get(Icons.pie3D_big);
					break;
				case ChartNature.PYRAMID:
					i =  Activator.getDefault().getImageRegistry().get(Icons.pyramid_big);
					break;
				case ChartNature.COLUMN_3D_LINE:
				case ChartNature.COLUMN_3D_LINE_DUAL:
					i =  Activator.getDefault().getImageRegistry().get(Icons.ms_column3D_line_big);
					break;
				case ChartNature.COLUMN_3D_MULTI:
					i =  Activator.getDefault().getImageRegistry().get(Icons.ms_column_3D_big);
					break;
				case ChartNature.COLUMN_MULTI:
					i =  Activator.getDefault().getImageRegistry().get(Icons.ms_column_big);
					break;
				case ChartNature.FUNNEL:
					i =  Activator.getDefault().getImageRegistry().get(Icons.funnel);
					break;
				case ChartNature.RADAR:
					i =  Activator.getDefault().getImageRegistry().get(Icons.radar);
					break;
				case ChartNature.STACKED_BAR:
					i =  Activator.getDefault().getImageRegistry().get(Icons.stacked_bar_big);
					break;
				case ChartNature.STACKED_BAR_3D:
					i =  Activator.getDefault().getImageRegistry().get(Icons.stacked_bar_3D_big);
					break;
				case ChartNature.STACKED_COLUMN:
					i =  Activator.getDefault().getImageRegistry().get(Icons.stacked_col_big);
					break;
				case ChartNature.STACKED_COLUMN_3D:
					i =  Activator.getDefault().getImageRegistry().get(Icons.stacked_col3D_big);
					break;
				case ChartNature.STACKED_COLUMN_3D_LINE_DUAL:
					i =  Activator.getDefault().getImageRegistry().get(Icons.stacked_line_big);
					break;
				}
				
				if (i == null){
					i =  Activator.getDefault().getImageRegistry().get(Icons.pie3D_big);
				}
			}
			if (def instanceof ComponentLink){
				 i = Activator.getDefault().getImageRegistry().get(Icons.hyperlink_big);
			}
			if (def instanceof ComponentGauge){
				 i = Activator.getDefault().getImageRegistry().get(Icons.gauge_big);
			}
			if (def instanceof ComponentDataGrid){
				 i = Activator.getDefault().getImageRegistry().get(Icons.datagrid_big);
			}
			if (def instanceof LabelComponent){
				 i = Activator.getDefault().getImageRegistry().get(Icons.label_big);
			}
			
			if (def instanceof ComponentPicture){
				 i = Activator.getDefault().getImageRegistry().get(Icons.picture_big);
			}
			if (def instanceof ComponentReport){
				 i = Activator.getDefault().getImageRegistry().get(Icons.report_big);
			}
			if (def instanceof ComponentKpi){
				 i = Activator.getDefault().getImageRegistry().get(Icons.report_big);
			}
			if (def instanceof ComponentButtonDefinition){
				 i = Activator.getDefault().getImageRegistry().get(Icons.button_big);
			}
			if (def instanceof ComponentFaView){
				 i = Activator.getDefault().getImageRegistry().get(Icons.cubeBig);
			}
			if (def instanceof ComponentMap){
				 i = Activator.getDefault().getImageRegistry().get(Icons.map_big);
			}
			if(def instanceof ComponentComment) {
				i = Activator.getDefault().getImageRegistry().get(Icons.comment_big);
			}
			if(def instanceof ComponentMapWMS) {
				i = Activator.getDefault().getImageRegistry().get(Icons.wms_big);
			}
			if(def instanceof ComponentOsmMap) {
				i = Activator.getDefault().getImageRegistry().get(Icons.wms_big);
			}
			if (def instanceof ComponentMarkdown){
				 i = Activator.getDefault().getImageRegistry().get(Icons.MARKDOWN_BIG);
			}
			return i;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return null;
		
	}

	public static Image getIcons(Object element) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		if (element instanceof Dictionary){
			return reg.get(Icons.dictionary);
		}
		if (element instanceof DataSource){
			return reg.get(Icons.dataSource);
		}
		else if (element instanceof ColumnDescriptor){
			return reg.get(Icons.column);
		}
		else if (element instanceof ParameterDescriptor || element instanceof ComponentParameter){
			return reg.get(Icons.parameter);
		}
		else if (element instanceof DataSet){
			return reg.get(Icons.table);
		}
		else if (element instanceof Palette){
			return reg.get(Icons.PALETTE_COLORS);
		}
		else if (element instanceof IResource){
			if (element instanceof FileCSS){
				return reg.get(Icons.css);
			}
			else if (element instanceof FileProperties){
				return reg.get(Icons.properties);
			}
			else if (element instanceof FileImage){
				return reg.get(Icons.picture);
			}
			else if (element instanceof FileJavaScript){
				return reg.get(Icons.javascript);
			}
			
		}
		else if (element instanceof IComponentDefinition){
			
			if (element instanceof ComponentChartDefinition){
				switch(((ComponentChartDefinition)element).getNature().getNature()){
				case ChartNature.PIE:
				case ChartNature.PIE_3D:
					return reg.get(Icons.chartPie);
					
					
				case ChartNature.COLUMN:
				case ChartNature.COLUMN_3D:
				case ChartNature.BAR:
					return reg.get(Icons.chartBar);
					
				case ChartNature.LINE:
					return reg.get(Icons.chartLine);
				default:
					return reg.get(Icons.chartPie);
				}
			}
			else if (element instanceof ComponentMap){
				
				return reg.get(Icons.map16);
			}
			else if (element instanceof ComponentFilterDefinition){
				
				return reg.get(Icons.filter);
			}
			else if (element instanceof LabelComponent){
				return reg.get(Icons.label);
			}
			else if (element instanceof ComponentPicture){
				return reg.get(Icons.picture);
			}
			else if (element instanceof ComponentLink){
				return reg.get(Icons.hyperlink);
			}
			else if (element instanceof ComponentReport){
				return reg.get(Icons.report);
			}
			else if (element instanceof ComponentKpi){
				return reg.get(Icons.report);
			}
			else if (element instanceof ComponentJsp){
				return reg.get(Icons.jsp);
			}
			else if (element instanceof ComponentD4C){
				return reg.get(Icons.d4c);
			}
			else if (element instanceof ComponentButtonDefinition){
				return reg.get(Icons.button);
			}
			else if (element instanceof ComponentGauge){
				return reg.get(Icons.gauge_16);
			}
			else if (element instanceof ComponentDataGrid){
				return reg.get(Icons.datagrid);
			}
			else if (element instanceof ComponentStyledTextInput){
				return reg.get(Icons.styledTextInput);
			}
			else if (element instanceof ComponentFaView){
				return reg.get(Icons.cube);
			}
			else if(element instanceof ComponentComment) {
				return reg.get(Icons.comment);
			}
			else if(element instanceof ComponentMapWMS) {
				return reg.get(Icons.wms);
			}
			else if(element instanceof ComponentOsmMap) {
				return reg.get(Icons.wms);
			}
			else if (element instanceof ComponentMarkdown){
				return reg.get(Icons.MARKDOWN_SMALL);
			}
			else if (element instanceof ComponentFlourish){
				return reg.get(Icons.flourish);
			}
			
		}
		else if (element instanceof FdModel){
			return reg.get(Icons.fd_16);
		}
		else if (element instanceof Table){
			return reg.get(Icons.table);
		}
		else if (element instanceof Cell || element instanceof StackableCell){
			return reg.get(Icons.cell);
		}
		else if (element instanceof Folder){
			return reg.get(Icons.folder);
		}
		else if (element instanceof FolderPage){
			return reg.get(Icons.folderPage);
		}
		return null;
		
	}
}

package bpm.fwr.client.utils;

import bpm.fwr.api.beans.components.ChartTypes;
import bpm.fwr.api.beans.components.OptionsFusionChart;
import bpm.fwr.client.images.WysiwygImage;

import com.google.gwt.resources.client.ImageResource;

public class ChartUtils {

	public static ImageResource getImageForChart(ChartTypes type){
		if(type.getType().equals(ChartTypes.BAR.getType())){
			return WysiwygImage.INSTANCE.barchart();
		}
		else if(type.getType().equals(ChartTypes.LINE.getType())){
			return WysiwygImage.INSTANCE.linechart();
		}
		else if(type.getType().equals(ChartTypes.PIE.getType())){
			return WysiwygImage.INSTANCE.piechart();
		}
		else if(type.getType().equals(ChartTypes.SCATTER.getType())){
			return WysiwygImage.INSTANCE.scatterchart();
		}
		else if(type.getType().equals(ChartTypes.AREA.getType())){
			return WysiwygImage.INSTANCE.areachart();
		}
		return null;
	}
	
	public static ImageResource getImageForChart(ChartTypes type, OptionsFusionChart chartOptions){
		if(chartOptions.is3D()){
			if(chartOptions.isStacked()){
				if(type.getType().equals(ChartTypes.COLUMN.getType())){
					return WysiwygImage.INSTANCE.ChartStackedColumn3D();
				}
				else if(type.getType().equals(ChartTypes.BAR.getType())){
					return WysiwygImage.INSTANCE.ChartStackedBar3D();
				}
			}
			else {
				if(type.getType().equals(ChartTypes.COLUMN.getType())){
					return WysiwygImage.INSTANCE.ChartColumn3D();
				}
				else if(type.getType().equals(ChartTypes.BAR.getType())){
					return WysiwygImage.INSTANCE.ChartBar3D();
				}
				else if(type.getType().equals(ChartTypes.PIE.getType())){
					return WysiwygImage.INSTANCE.ChartPie3D();
				}
				else if(type.getType().equals(ChartTypes.DOUGHNUT.getType())){
					return WysiwygImage.INSTANCE.ChartDoughnut3D();
				}
			}
		}
		else {
			if(chartOptions.isStacked()){
				if(chartOptions.isGlassEnabled()){
					if(type.getType().equals(ChartTypes.COLUMN.getType())){
						return WysiwygImage.INSTANCE.ChartStackedColumnGlass();
					}
					else if(type.getType().equals(ChartTypes.BAR.getType())){
						return WysiwygImage.INSTANCE.ChartStackedBarGlass();
					}
				}
				else {
					if(type.getType().equals(ChartTypes.COLUMN.getType())){
						return WysiwygImage.INSTANCE.ChartStackedColumn2D();
					}
					else if(type.getType().equals(ChartTypes.BAR.getType())){
						return WysiwygImage.INSTANCE.ChartStackedBar2D();
					}
				}
			}
			else {
				if(chartOptions.isGlassEnabled()){
					if(type.getType().equals(ChartTypes.COLUMN.getType())){
						return WysiwygImage.INSTANCE.ChartColumnGlass();
					}
					else if(type.getType().equals(ChartTypes.BAR.getType())){
						return WysiwygImage.INSTANCE.ChartBarGlass();
					}
				}
				else {
					if(type.getType().equals(ChartTypes.COLUMN.getType())){
						return WysiwygImage.INSTANCE.ChartColumn2D();
					}
					else if(type.getType().equals(ChartTypes.BAR.getType())){
						return WysiwygImage.INSTANCE.ChartBar2D();
					}
					else if(type.getType().equals(ChartTypes.LINE.getType())){
						return WysiwygImage.INSTANCE.ChartLine();
					}
					else if(type.getType().equals(ChartTypes.PIE.getType())){
						return WysiwygImage.INSTANCE.ChartPie2D();
					}
					else if(type.getType().equals(ChartTypes.DOUGHNUT.getType())){
						return WysiwygImage.INSTANCE.ChartDoughnut2D();
					}
					else if(type.getType().equals(ChartTypes.RADAR.getType())){
						return WysiwygImage.INSTANCE.ChartRadar();
					}
				}
			}
		}
		
		return null;
	}
}

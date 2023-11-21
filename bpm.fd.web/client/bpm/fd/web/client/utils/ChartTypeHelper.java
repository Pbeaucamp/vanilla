package bpm.fd.web.client.utils;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.web.client.images.DashboardImage;
import bpm.fd.web.client.panels.properties.ChartTypeProperties.TypeChart;
import bpm.fd.web.client.panels.properties.ChartTypeProperties.TypeDisplay;

import com.google.gwt.resources.client.ImageResource;

public class ChartTypeHelper {

	public static ChartNature getChartNature(ChartComponent component, TypeChart typeChart, TypeDisplay typeDisplay, boolean is3d) {
		boolean multi = false;
		if ((component.getAggregations() != null && component.getAggregations().size() > 1) || component.getSubGroupFieldIndex() != null) {
			multi = true;
		}

		switch (typeChart) {
		case AREA:
			return ChartNature.getNature(ChartNature.STACKED_AREA_2D);
		case BAR:
			if (multi) {
				if (typeDisplay.equals(TypeDisplay.STACKED)) {
					if (is3d) {
						return ChartNature.getNature(ChartNature.STACKED_BAR_3D);
					}
					else {
						return ChartNature.getNature(ChartNature.STACKED_BAR);
					}
				}
				else {
					if (is3d) {
						return ChartNature.getNature(ChartNature.BAR_3D);
					}
					else {
						return ChartNature.getNature(ChartNature.BAR);
					}
				}
			}
			else {
				if (is3d) {
					return ChartNature.getNature(ChartNature.BAR_3D);
				}
				else {
					return ChartNature.getNature(ChartNature.BAR);
				}
			}
		case COLUMN:
			if (multi) {
				if (typeDisplay.equals(TypeDisplay.STACKED)) {
					if (is3d) {
						return ChartNature.getNature(ChartNature.STACKED_COLUMN_3D);
					}
					else {
						return ChartNature.getNature(ChartNature.STACKED_COLUMN);
					}
				}
				else {
					if (is3d) {
						return ChartNature.getNature(ChartNature.COLUMN_3D_MULTI);
					}
					else {
						return ChartNature.getNature(ChartNature.COLUMN_MULTI);
					}
				}
			}
			else {
				if (is3d) {
					return ChartNature.getNature(ChartNature.COLUMN_3D);
				}
				else {
					return ChartNature.getNature(ChartNature.COLUMN);
				}
			}
		case COMBINATION:
			// FIXME Change this by checking the aggregations

			return ChartNature.getNature(ChartNature.PIE_3D);
//		case FUNNEL:
//			return ChartNature.getNature(ChartNature.FUNNEL);
		case LINE:
			if (multi) {
				return ChartNature.getNature(ChartNature.LINE_MULTI);
			}
			return ChartNature.getNature(ChartNature.LINE);
//		case MARIMEKO:
//			return ChartNature.getNature(ChartNature.MARIMEKO);
		case PIE:
			if (is3d) {
				return ChartNature.getNature(ChartNature.PIE_3D);
			}
			return ChartNature.getNature(ChartNature.PIE);
//		case PYRAMID:
//			return ChartNature.getNature(ChartNature.PYRAMID);
		case RADAR:
			return ChartNature.getNature(ChartNature.RADAR);

		default:
			return ChartNature.getNature(ChartNature.PIE_3D);
		}
	}

	public static Object findImage(ChartNature nature) {
		TypeChart typeChart = findType(nature);
		TypeDisplay typeDisplay = nature.isStacked() ? TypeDisplay.STACKED : TypeDisplay.CLASSIC;
		boolean is3D = nature.is3D();
		
		return findImage(typeChart, typeDisplay, is3D);
	}
	
	public static TypeChart findType(ChartNature nature) {
		switch (nature.getNature()) {
		case ChartNature.PIE:
		case ChartNature.PIE_3D:
			return TypeChart.PIE;
		case ChartNature.COLUMN:
		case ChartNature.COLUMN_3D:
		case ChartNature.COLUMN_MULTI:
		case ChartNature.COLUMN_3D_MULTI:
		case ChartNature.STACKED_COLUMN:
		case ChartNature.STACKED_COLUMN_3D:
		case ChartNature.COLUMN_LINE_DUAL:
		case ChartNature.COLUMN_3D_LINE:
		case ChartNature.COLUMN_3D_LINE_DUAL:
		case ChartNature.STACKED_COLUMN_3D_LINE_DUAL:
		case ChartNature.STACKED_2D_LINE_DUAL_Y:
			return TypeChart.COLUMN;
		case ChartNature.BAR:
		case ChartNature.STACKED_BAR:
		case ChartNature.STACKED_BAR_3D:
		case ChartNature.BAR_3D:
			return TypeChart.BAR;
		case ChartNature.LINE:
		case ChartNature.LINE_MULTI:
			return TypeChart.LINE;
//		case ChartNature.PYRAMID:
//			return TypeChart.PYRAMID;
//		case ChartNature.FUNNEL:
//			return TypeChart.FUNNEL;
		case ChartNature.STACKED_AREA_2D:
			return TypeChart.AREA;
//		case ChartNature.MARIMEKO:
//			return TypeChart.MARIMEKO;
		case ChartNature.SINGLE_Y_2D_COMBINATION:
		case ChartNature.SINGLE_Y_3D_COMBINATION:
		case ChartNature.DUAL_Y_2D_COMBINATION:
			return TypeChart.COMBINATION;
		case ChartNature.RADAR:
			return TypeChart.RADAR;
		case ChartNature.GAUGE:
		case ChartNature.BULLET:

			break;

		default:
			break;
		}
		return TypeChart.PIE;
	}

	public static ImageResource findImage(TypeChart typeChart, TypeDisplay typeDisplay, boolean is3d) {
		switch (typeChart) {
		case PIE:
			return is3d ? DashboardImage.INSTANCE.chart_pie_3D() : DashboardImage.INSTANCE.chart_pie_2D();
		case COLUMN:
			return findColumnImage(typeDisplay, is3d);
		case BAR:
			return findBarImage(typeDisplay, is3d);
		case LINE:
			return DashboardImage.INSTANCE.chart_lines_ms();
//		case FUNNEL:
//			return DashboardImage.INSTANCE.chart_funnel();
//		case PYRAMID:
//			return DashboardImage.INSTANCE.chart_pyramid();
//		case MARIMEKO:
//			return DashboardImage.INSTANCE.chart_marimeko_ms();
		case RADAR:
			return DashboardImage.INSTANCE.chart_radar_ms();
		case COMBINATION:
			return is3d ? DashboardImage.INSTANCE.chart_combination_ms_3D() : DashboardImage.INSTANCE.chart_combination_ms_2D();
		case AREA:
			return DashboardImage.INSTANCE.chart_area_ms_stack_2D();

		default:
			break;
		}
		return null;
	}

	private static ImageResource findColumnImage(TypeDisplay typeDisplay, boolean is3d) {
		switch (typeDisplay) {
		case CLASSIC:
			return is3d ? DashboardImage.INSTANCE.chart_column_ms_3D() : DashboardImage.INSTANCE.chart_column_ms_2D();
		case STACKED:
			return is3d ? DashboardImage.INSTANCE.chart_column_ms_stack_3D() : DashboardImage.INSTANCE.chart_column_ms_stack_2D();
		default:
			break;
		}
		return null;
	}

	private static ImageResource findBarImage(TypeDisplay typeDisplay, boolean is3d) {
		switch (typeDisplay) {
		case CLASSIC:
			return is3d ? DashboardImage.INSTANCE.chart_bar_ms_3D() : DashboardImage.INSTANCE.chart_bar_2D();
		case STACKED:
			return is3d ? DashboardImage.INSTANCE.chart_bar_ms_stack_3D() : DashboardImage.INSTANCE.chart_bar_ms_stack_2D();
		default:
			break;
		}
		return null;
	}
}

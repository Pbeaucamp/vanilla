package bpm.fd.web.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;
import com.google.gwt.resources.client.ImageResource;

public interface DashboardImage extends ClientBundle {

	public static final DashboardImage INSTANCE = GWT.create(DashboardImage.class);

	ImageResource New();
	ImageResource Open();
	ImageResource ic_save_black_18dp();
	ImageResource ic_delete_black_18dp();
	ImageResource start_24dp();
	ImageResource data_grid();
	ImageResource filter_2();
	ImageResource gauge_1();
	ImageResource map();
	ImageResource report();
	ImageResource dashlet();
	ImageResource empty_bin();
	ImageResource combo_big();
	ImageResource tool_chart();
	ImageResource add_page();
	ImageResource markdown();
	ImageResource image();
	ImageResource arrow_down_24();
	ImageResource arrow_up_24();
	ImageResource label();
	ImageResource link();;
	ImageResource arrow_double_down_24();
	ImageResource arrow_double_up_24();
	ImageResource olap_view();
	ImageResource div();
	ImageResource stackable_cell_drill();
	ImageResource stackable_cell();
	ImageResource grid();
	ImageResource align_top();
	ImageResource align_bottom();
	ImageResource align_left();
	ImageResource align_right();
	ImageResource resize_width();
	ImageResource resize_height();
	ImageResource dimension();
	ImageResource obj_measure();
	ImageResource delete_24();
	ImageResource kpi_tool();
	ImageResource d4c_48();
	ImageResource lk_wb_analysis_48();
	ImageResource kpi_chart();
	ImageResource dynamic_label();
	
	//CHARTS
//	@Source("chart_pie_2D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_pie_2D();
//	@Source("chart_pie_3D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_pie_3D();

//	@Source("chart_column_ms_2D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_column_ms_2D();
//	@Source("chart_column_ms_3D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_column_ms_3D();
//	@Source("chart_column_ms_stack_2D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_column_ms_stack_2D();
//	@Source("chart_column_ms_stack_3D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_column_ms_stack_3D();

//	@Source("chart_bar_2D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_bar_2D();
//	@Source("chart_bar_ms_3D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_bar_ms_3D();
//	@Source("chart_bar_ms_stack_2D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_bar_ms_stack_2D();
//	@Source("chart_bar_ms_stack_3D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_bar_ms_stack_3D();
	
//	@Source("chart_lines_ms.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_lines_ms();
	
//	@Source("chart_funnel.svg")
//	@MimeType("image/svg+xml")
//	DataResource chart_funnel();
	
//	@Source("chart_pyramid.svg")
//	@MimeType("image/svg+xml")
//	DataResource chart_pyramid();
	
//	@Source("chart_marimeko_ms.svg")
//	@MimeType("image/svg+xml")
//	DataResource chart_marimeko_ms();
	
//	@Source("chart_radar_ms.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_radar_ms();
	
//	@Source("chart_combination_ms_2D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_combination_ms_2D();
//	@Source("chart_combination_ms_3D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_combination_ms_3D();
	
//	@Source("chart_area_ms_stack_2D.svg")
//	@MimeType("image/svg+xml")
	ImageResource chart_area_ms_stack_2D();
	
	//R Chart FlexdashBoard
	@Source("nuagePoint.svg")
	@MimeType("image/svg+xml")
	DataResource point();
	
	@Source("barverticale.svg")
	@MimeType("image/svg+xml")
	DataResource barVerticale();
	
	@Source("bar_horizontale.svg")
	@MimeType("image/svg+xml")
	DataResource barHorizontale();
	
	@Source("hist.svg")
	@MimeType("image/svg+xml")
	DataResource histogramme();
	
	@Source("line.svg")
	@MimeType("image/svg+xml")
	DataResource line(); 
	
	@Source("chart_pie_2D.svg")
	@MimeType("image/svg+xml")
	DataResource pieR();
	
	@Source("boxplot.png")
	@MimeType("image/png")
	DataResource boxplot();
	
	@Source("heatmap.png")
	@MimeType("image/svg+xml")
	DataResource heatmap();
	
	@Source("treemap.png")
	@MimeType("image/svg+xml")
	DataResource treemap();
	
	@Source("cor.png")
	@MimeType("image/svg+xml")
	DataResource correlation();
	
	@Source("acp.png")
	@MimeType("image/svg+xml")
	DataResource acp();
	
	
	//WORKSPACE
	@Source("olap_view.svg")
	@MimeType("image/svg+xml")
	DataResource olap_view_svg();
	
	@Source("gauge.svg")
	@MimeType("image/svg+xml")
	DataResource gauge_svg();
	
	@Source("report.svg")
	@MimeType("image/svg+xml")
	DataResource report_svg();
	
	@Source("no_image.svg")
	@MimeType("image/svg+xml")
	DataResource no_image_svg();
	
	@Source("markdown.svg")
	@MimeType("image/svg+xml")
	DataResource markdown_big();
	
	@Source("filter.svg")
	@MimeType("image/svg+xml")
	DataResource filter();
	
	@Source("map.svg")
	@MimeType("image/svg+xml")
	DataResource map_svg();
	
	@Source("datagrid.svg")
	@MimeType("image/svg+xml")
	DataResource datagrid();
	
	@Source("kpi.svg")
	@MimeType("image/svg+xml")
	DataResource kpi();
	
	@Source("kpi_tool.svg")
	@MimeType("image/svg+xml")
	DataResource kpi_tool_svg();
	
	@Source("dynamic_label.svg")
	@MimeType("image/svg+xml")
	DataResource dynamic_label_svg();
	
	ImageResource flourish_48();
}

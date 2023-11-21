package bpm.fwr.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface WysiwygImage extends ClientBundle  {
	
	public static final WysiwygImage INSTANCE =  GWT.create(WysiwygImage.class);

	ImageResource colcalc();
	ImageResource apply();
	ImageResource delcol();
	ImageResource folder();
	ImageResource report();
	ImageResource metadata();
	ImageResource table();
	ImageResource column();
	ImageResource object();
	ImageResource fasd();
	ImageResource sql();
	ImageResource asc();
	ImageResource desc();
	ImageResource resource();
	ImageResource wysiwyg();
	ImageResource fmdtdriller();
	ImageResource createds();
	ImageResource createjoinds();
	
	ImageResource empty_bin();
	ImageResource full_bin();
	ImageResource empty_bin64();
	ImageResource full_bin64();
	
	ImageResource home();
	ImageResource save();
	ImageResource Open();
	ImageResource New();

	ImageResource up();
	
	ImageResource preferences();
	ImageResource cancel();
	
	ImageResource arrowDown();
	ImageResource add();
	ImageResource edit();
	
	ImageResource graphique();
	ImageResource barchart();
	ImageResource piechart();
	ImageResource scatterchart();
	ImageResource linechart();
	ImageResource areachart();
	
	ImageResource filter();
	
	ImageResource FWR_Chart();
	ImageResource FWR_Grid();
	ImageResource FWR_Crosstab();
	ImageResource FWR_Image_WaitFor();
	ImageResource FWR_Label();
	ImageResource FWR_Resources();
	ImageResource FWR_Table();
	ImageResource FWR_Image();
	
	ImageResource FWR_UnDO();
	ImageResource FWR_ReDO();
	
	ImageResource refresh();
	
	ImageResource SaveAsFWR();
	ImageResource SaveAsBirt();
	
	ImageResource FWR_Failure();
	ImageResource FWR_Sucess();
	
	ImageResource arrow_left();
	ImageResource arrow_right();
	
	ImageResource chart();
	ImageResource classic();
	ImageResource crosstab();
	ImageResource update();
	
	ImageResource help();
	
	ImageResource style();
	ImageResource FWR_VanillaMap();
	
	ImageResource ChartBar2D();
	ImageResource ChartBar3D();
	ImageResource ChartBarGlass();
	ImageResource ChartColumn2D();
	ImageResource ChartColumn3D();
	ImageResource ChartColumnGlass();
	ImageResource ChartDoughnut2D();
	ImageResource ChartDoughnut3D();
	ImageResource ChartLine();
	ImageResource ChartPie2D();
	ImageResource ChartPie3D();
	ImageResource ChartRadar();
	ImageResource ChartStackedBar2D();
	ImageResource ChartStackedBar3D();
	ImageResource ChartStackedBarGlass();
	ImageResource ChartStackedColumn2D();
	ImageResource ChartStackedColumn3D();
	ImageResource ChartStackedColumnGlass();
	
	ImageResource addColor();
	ImageResource del();
	
	ImageResource VanillaMapBig();
	ImageResource FWR_Hyperlink();
	
	ImageResource Comment_16();
	ImageResource commentBig();
	ImageResource loading();
	ImageResource database_link();
	
	ImageResource fmdt_bq_16();
	
	ImageResource Automatic_List();
	ImageResource Normal_List();
	ImageResource logout();
	ImageResource user_profile();
	ImageResource about_me();
	ImageResource theme_w();
	ImageResource logout_w();
	ImageResource css_v2_32();
	
	ImageResource vanilla_wb_lettering_incrust_64();
	ImageResource template_26();
}
package bpm.freematrix.reborn.web.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

	public static final Images INSTANCE =  GWT.create(Images.class);
	
	public ImageResource dimension();
	public ImageResource add();
	public ImageResource delete();
	public ImageResource help();
	public ImageResource home();
	public ImageResource refresh();
	public ImageResource save();
	public ImageResource assos();
	public ImageResource apply();
	public ImageResource datasource();
	public ImageResource edit();
	public ImageResource level();
	public ImageResource thumb_down();
	public ImageResource thumb_up();
	public ImageResource thumb_equal();
	public ImageResource sub();
	public ImageResource tend_down();
	public ImageResource tend_up();
	public ImageResource tend_equal();
	public ImageResource pie();
	public ImageResource bar();
	public ImageResource donut();
	public ImageResource column();
	public ImageResource line();
	public ImageResource gaugeangular();
	public ImageResource gaugelinear();
	public ImageResource alert_table();
	public ImageResource VanillaKpiUser_LogoLettering_32();
	public ImageResource alert_loadopen();
	public ImageResource alert_loadsolved();
	public ImageResource alert_solve();
	public ImageResource User();
	public ImageResource Password();
	public ImageResource lock();
	public ImageResource logo_32();
	public ImageResource ic_menu();
	public ImageResource red_flag();
	public ImageResource green_flag();
	
	public ImageResource ic_right_expand();
	public ImageResource ic_right_collapse();
	public ImageResource resize_small();
	public ImageResource resize_full();
	public ImageResource calendar_20();
}

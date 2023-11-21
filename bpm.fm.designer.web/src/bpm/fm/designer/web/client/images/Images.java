package bpm.fm.designer.web.client.images;

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
	
	public ImageResource User();
	public ImageResource Password();
	public ImageResource lock();
	public ImageResource folder();
	public ImageResource column();
	public ImageResource security();
	
	public ImageResource up();
	public ImageResource down();
	public ImageResource addTheme();
	
	public ImageResource logo_32();
	public ImageResource database_creation();
	
	public ImageResource VanillaKpiDesigner_LogoLettering_32();
	
	public ImageResource assos_add();
	public ImageResource assos_edit();
	public ImageResource assos_delete();
	ImageResource ic_publish_black_18dp();
	ImageResource ic_add_black_24dp();
	ImageResource ic_link_black_18dp();
	ImageResource ic_play_arrow_black_18dp();
	ImageResource metadataexplorer_vanillaanalysisweb();
}

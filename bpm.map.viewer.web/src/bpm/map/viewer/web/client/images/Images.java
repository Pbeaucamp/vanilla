package bpm.map.viewer.web.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

	public static final Images INSTANCE =  GWT.create(Images.class);
	
	public ImageResource user_profile();
	public ImageResource ic_menu_help();
	public ImageResource logout();
	
	public ImageResource ic_right_expand();
	public ImageResource ic_right_collapse();
	public ImageResource ic_bottom_expand();
	public ImageResource ic_bottom_collapse();
	public ImageResource format_pdf_128();
	public ImageResource format_pdf_16();
	public ImageResource ic_download_pdf();
	public ImageResource ic_download_pdf_white();
	public ImageResource print();
	public ImageResource save();
	public ImageResource ic_export_csv();
	
	public ImageResource thumb_down();
	public ImageResource thumb_up();
	public ImageResource thumb_equal();
	public ImageResource tend_down();
	public ImageResource tend_up();
	public ImageResource tend_equal();
	
	public ImageResource ic_map_layers_64();
	public ImageResource ic_autorenew_white_24dp();
	public ImageResource ic_export();
}

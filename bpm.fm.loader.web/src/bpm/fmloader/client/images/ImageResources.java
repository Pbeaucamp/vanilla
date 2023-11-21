package bpm.fmloader.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ImageResources extends ClientBundle {

	public static final ImageResources INSTANCE =  GWT.create(ImageResources.class);
	
	public ImageResource apply();
	public ImageResource cancel();
	public ImageResource home();
	public ImageResource query();
	public ImageResource save();
	public ImageResource chart();
	public ImageResource largeloading();
	public ImageResource close();
	public ImageResource lock();
	public ImageResource User();
	public ImageResource Password();
	public ImageResource calendar();
	public ImageResource delete_16();
	public ImageResource newadd_16();
	public ImageResource update_16();
	public ImageResource logo_32();
	public ImageResource fmloader_line_delete_16_color();
	public ImageResource fmloader_line_delete_16_grey();
	public ImageResource fmloader_line_restore_16_color();
	public ImageResource fmloader_line_restore_16_grey();
	public ImageResource VanillaKpiLoader_LogoLettering_32();
}

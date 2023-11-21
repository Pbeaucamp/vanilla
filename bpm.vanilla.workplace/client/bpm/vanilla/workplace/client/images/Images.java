package bpm.vanilla.workplace.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle  {
	
	public static final Images INSTANCE =  GWT.create(Images.class);
	
	ImageResource add_user();
	ImageResource remove_user();
	ImageResource write();
	ImageResource valid();
	ImageResource project();
	ImageResource packages();
	ImageResource add_packages();
	ImageResource add_project();
	ImageResource delete_packages();
	ImageResource delete_project();
}
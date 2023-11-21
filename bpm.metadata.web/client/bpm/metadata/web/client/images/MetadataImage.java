package bpm.metadata.web.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface MetadataImage extends ClientBundle {

	public static final MetadataImage INSTANCE = GWT.create(MetadataImage.class);

	ImageResource New();
	ImageResource Open();
	ImageResource ic_save_black_18dp();
	ImageResource data4citizen_logo_only();
}

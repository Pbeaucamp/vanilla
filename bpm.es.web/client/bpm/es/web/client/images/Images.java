package bpm.es.web.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

	public static final Images INSTANCE =  GWT.create(Images.class);

	ImageResource ic_delete_black_18dp();
	ImageResource ic_edit_black_18dp();
	ImageResource planification_green();
	ImageResource planification_red();
	ImageResource ic_export_black_24dp();
	ImageResource logo_GrandLyon();
	ImageResource exit_32();
	ImageResource help_32();
	ImageResource ic_menu_white_24dp();
	ImageResource ic_settings_black_24dp();
	ImageResource ic_folder_shared_black_24dp();
	ImageResource ic_alert_black_24dp();
	ImageResource ic_statistics_black_24dp();
	ImageResource ic_liste_black_24dp();
	ImageResource lyon_enfance_logo();
	ImageResource img_profile_small();
	ImageResource activated();
	ImageResource deactivated();
	ImageResource ic_event_black_16dp();
	ImageResource search_48();
	ImageResource identity_picture();
	ImageResource ic_listings_black_24dp();
	ImageResource ic_document_manager_black_24dp();
	ImageResource ic_print_black_27dp();
	ImageResource ic_list_black_27dp();
	ImageResource ic_visibility_black_27dp();
}

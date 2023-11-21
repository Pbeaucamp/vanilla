package bpm.architect.web.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

	public static final Images INSTANCE = GWT.create(Images.class);

	ImageResource ic_edit_black_18dp();
	ImageResource ic_delete_black_18dp();
	ImageResource ic_flag_black_18dp();
	ImageResource ic_refresh_black_18dp();
	ImageResource add_version();
	ImageResource kml();
	ImageResource ic_search_black_24dp();
	ImageResource ic_play_arrow_black_18dp();
	ImageResource ic_view_list_black_18dp();
	ImageResource ic_link_black_18dp();
	ImageResource ic_add_black_24dp();
	ImageResource ic_database_add_black_36px();
	ImageResource ic_database_view_black_36px();
	ImageResource ic_database_change_black_36px();
	ImageResource ic_visibility_black_18dp();
	ImageResource ic_clear_16px();
	ImageResource ic_check_black_18dp();
	ImageResource ic_my_library_books_black_18dp();
	ImageResource VanillaArchitect_Bandeau_64();
	ImageResource ged_32();
	ImageResource rules_32();
	ImageResource resources_32();
	ImageResource file_csv_64();
	ImageResource file_excel_64();
	ImageResource file_file_64();
	ImageResource file_txt_64();
	ImageResource file_xls_64();
	ImageResource file_shp_64();
	ImageResource ic_assignment_black_18dp();
}

package bpm.gwt.workflow.commons.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

	public static final Images INSTANCE =  GWT.create(Images.class);

	ImageResource start_24dp();
	ImageResource stop_24dp();
	ImageResource down_arrow();
	ImageResource left_arrow();
	ImageResource right_arrow();
	ImageResource up_arrow();
	ImageResource ic_add_circle_outline_black_18dp();
	ImageResource ic_autorenew_black_18dp();
	ImageResource ic_insert_invitation_black_18dp();
	ImageResource ic_play_circle_outline_black_18dp();
	ImageResource ic_view_list_black_18dp();
	ImageResource timepicker_AM();
	ImageResource timepicker_PM();
	ImageResource ic_done_black_18dp();
	ImageResource loadingBig();
	ImageResource ic_dialog_close();
	ImageResource ic_dialog_maximize();
	ImageResource ic_dialog_restore();
	ImageResource ic_redo_black_18dp();
	ImageResource ic_save_black_18dp();
	ImageResource ic_undo_black_18dp();
	ImageResource ic_menu_view();
	ImageResource ic_system_update_black_18dp();
	ImageResource logout();
	ImageResource vanilla_an_lettering_incrust_64();
	ImageResource vanilla_lettering_incrust_64();
	ImageResource about_me();
	ImageResource logout_w();
	ImageResource parameter_w();
	ImageResource user_profile();
	ImageResource ic_menu_view_small();
	ImageResource cellTableLoading();
	ImageResource sortAscending();
	ImageResource sortDescending();
	ImageResource ic_flag_black_18dp();
	ImageResource ic_flag_green_18dp();
	ImageResource ic_flag_grey600_18dp();
	ImageResource ic_flag_red_18dp();
	ImageResource ic_delete_black_18dp();
	ImageResource ic_edit_black_18dp();
	ImageResource planification_gray();
	ImageResource planification_green();
	ImageResource planification_red();
	ImageResource failed();
	ImageResource success();
	ImageResource ic_file_download_black_18dp();
	ImageResource ic_add_box_black_18dp();
	ImageResource collapse();
	ImageResource ic_add_black_24dp();
	ImageResource ic_remove_black_24dp();
	ImageResource ic_folder_open_black_18dp();
	ImageResource ic_open_with_black_18dp();
	ImageResource cible_b_24dp();
	ImageResource ic_polymer_black_18dp();
	ImageResource ic_tune_black_18dp();
	ImageResource ic_launch_black_24dp();
	ImageResource action_b_24dp();
}

package bpm.smart.web.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

	public static final Images INSTANCE =  GWT.create(Images.class);
	
	ImageResource user_profile();
	ImageResource ic_menu_help();
	ImageResource logout();
	
	ImageResource ic_right_expand();
	ImageResource ic_right_collapse();
	ImageResource ic_bottom_expand();
	ImageResource ic_bottom_collapse();
	ImageResource format_pdf_128();
	ImageResource format_pdf_16();
	ImageResource ic_download_pdf();
	ImageResource ic_download_pdf_white();
	ImageResource print();
	ImageResource save();
	ImageResource ic_export_csv();
	
	ImageResource thumb_down();
	ImageResource thumb_up();
	ImageResource thumb_equal();
	ImageResource tend_down();
	ImageResource tend_up();
	ImageResource tend_equal();
	ImageResource ic_trash();
	ImageResource save_version();
	ImageResource run();
	ImageResource run_selection();
	ImageResource file_add_24();
	ImageResource file_delete_24();
	ImageResource ic_checkin_black_24();
	ImageResource ic_checkout_black_24();
	ImageResource ic_Rfile_24();
	ImageResource ic_Rfile_import_24();
	ImageResource ic_Rfile_export_24();
	ImageResource ic_public();
	ImageResource ic_private();
	ImageResource ic_share_group();
	ImageResource ic_add_R_32();
	ImageResource folder_import_64();
	ImageResource archive_zip_64();
	ImageResource save_plus_24();
	ImageResource ic_clear_black_24dp();
	ImageResource ic_content_copy_black_24dp();
	ImageResource ic_content_paste_black_24dp();
	ImageResource ic_delete_black_18dp();
	ImageResource ic_add_black_18dp();
	ImageResource ic_enter_32();
	ImageResource ic_help_black_24dp();
	
	ImageResource action_b_24dp();
	ImageResource action_w_24dp();
	ImageResource ic_social_html();
	ImageResource ic_social_pdf();
	ImageResource ic_social_word();
	ImageResource ic_social_html2();
	ImageResource ic_social_R();
	ImageResource ic_social_markdown();
	
	ImageResource ic_rscript_black_24dp();
	ImageResource ic_rscript_white_24dp();
	ImageResource ic_fullscreen();
	ImageResource ic_fullscreen_round();
	ImageResource ic_fullscreen_exit();
	ImageResource ic_fullscreen_exit_round();
	ImageResource ic_my_library_books_black_24dp();
	ImageResource ic_my_library_books_white_24dp();
	ImageResource ic_simple_lin_reg_black_36();
	ImageResource ic_simple_lin_reg_white_36();
	ImageResource ic_hac_black_36();
	ImageResource sort_activity();
	ImageResource selection_activity();
	ImageResource output_file();
	ImageResource chart_activity();
	ImageResource ic_cormatrix_black_36();
	ImageResource ic_kmeans_black_36();
	ImageResource ic_web_black_18dp();
	ImageResource ic_equalizer_black_18dp();
	ImageResource ic_settings_black_18dp();
	ImageResource ic_cloud_download_black_24dp();
	ImageResource ic_cloud_off_black_24dp();
	ImageResource ic_style_black_36dp();
	ImageResource ic_export_rmd_repo();
	ImageResource ic_template_36();
	ImageResource ic_tools_64();
	
	ImageResource ic_circle_column100();
	ImageResource ic_circle_cube100();
	ImageResource ic_circle_plot100();
	ImageResource ic_circle_row100();
	ImageResource ic_circle_tree100();
	ImageResource ic_circle_summary100();
	ImageResource ic_circle_linreg100();
	ImageResource ic_circle_kmeans100();
	ImageResource ic_circle_matcor100();
	ImageResource ic_circle_hac100();
	ImageResource ic_circle_dectree100();
	ImageResource ic_circle_export100();
}

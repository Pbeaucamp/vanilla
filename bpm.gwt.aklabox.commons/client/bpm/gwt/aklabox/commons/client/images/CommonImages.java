package bpm.gwt.aklabox.commons.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface CommonImages extends ClientBundle  {
	
	public static final CommonImages INSTANCE =  GWT.create(CommonImages.class);

	ImageResource loadingBig();
	ImageResource ic_dialog_close();
	ImageResource ic_dialog_maximize();
	ImageResource ic_dialog_restore();
	ImageResource close_tab();
	
	ImageResource circle_check_128();
	ImageResource circle_cross_128();
	
	ImageResource validate();
	ImageResource logo_header_defaultResult_availability();
	ImageResource logo_header_defaultResult_failure();
	ImageResource logo_header_defaultResult_success();
	ImageResource logo_header_defaultResult_warning();
	ImageResource ic_filter();
	ImageResource ic_browse_grey();
	ImageResource ic_edit();
	ImageResource ic_trash_2();
	ImageResource ic_drop();
	ImageResource ic_cell();
	ImageResource ic_save_check();
	ImageResource ic_enterprise();
	ImageResource ic_folder();
	ImageResource ic_many_docs();
	ImageResource ic_comment48();
	ImageResource cellTableLoading();
	ImageResource sortAscending();
	ImageResource sortDescending();
	ImageResource HTML_20();
	ImageResource object();
	ImageResource timepicker_AM();
	ImageResource timepicker_PM();
	
	ImageResource calendar();
	ImageResource ic_arrow_back_white_36dp();
	ImageResource ic_menu_white_36dp();
	
	ImageResource ic_add_circle_outline_black_24dp();
	ImageResource loading();
	ImageResource document_16();
	ImageResource folder_16();
	ImageResource hierarchy_16();
	ImageResource ic_folder_market();
	ImageResource ic_folder_pesv2();
	ImageResource ic_folder_project();
	ImageResource ic_folder_workflow();
	ImageResource ic_recent();
	ImageResource ic_starred();
	ImageResource ic_validated();
	ImageResource ic_deleted();
	ImageResource ic_userGroups();
	ImageResource ic_btn_sidebar_right_collapse();
	ImageResource ic_btn_sidebar_right_expand();
	ImageResource ic_enterprise_16();
	ImageResource ic_autorenew_black_18dp();
	ImageResource add_16();
	ImageResource ic_delete_black_18dp();
	ImageResource ic_folder_market_complete();
	ImageResource clear_16();
	
	ImageResource file_access_16();
	ImageResource file_access_64();
	ImageResource file_excel_16();
	ImageResource file_excel_64();
	ImageResource file_image_16();
	ImageResource file_image_64();
	ImageResource file_pdf_16();
	ImageResource file_pdf_64();
	ImageResource file_powerpoint_16();
	ImageResource file_powerpoint_64();
	ImageResource file_txt_16();
	ImageResource file_txt_64();
	ImageResource file_word_16();
	ImageResource file_word_64();
	ImageResource file_zip_16();
	ImageResource file_zip_64();
	ImageResource file_audio_16();
	ImageResource file_audio_64();
	ImageResource file_file_16();
	ImageResource file_file_64();
	ImageResource file_url_16();
	ImageResource file_url_64();
	ImageResource file_vanilla_16();
	ImageResource file_vanilla_64();
	ImageResource file_dxf_16();
	ImageResource file_dxf_64();
	ImageResource file_video_16();
	ImageResource file_video_64();
	ImageResource file_xakl_16();
	ImageResource file_xakl_64();
	ImageResource file_dwg_16();
	ImageResource file_dwg_64();
	ImageResource file_scan_16();
	ImageResource file_scan_64();
	ImageResource file_orbeon_16();
	ImageResource file_orbeon_64();

	ImageResource file_folder_classic_100();
	ImageResource file_folder_classic_16();
	ImageResource file_folder_exchange_100();
	ImageResource file_folder_exchange_16();
	ImageResource file_folder_inbox_100();
	ImageResource file_folder_inbox_16();
	ImageResource file_folder_market_100();
	ImageResource file_folder_market_16();
	ImageResource file_folder_market_valid_100();
	ImageResource file_folder_market_valid_16();
	ImageResource file_folder_pesv2_100();
	ImageResource file_folder_pesv2_16();
	ImageResource file_folder_project_100();
	ImageResource file_folder_project_16();
	ImageResource file_folder_rm_100();
	ImageResource file_folder_rm_16();
	ImageResource file_folder_workflow_pause_100();
	ImageResource file_folder_workflow_100();
	ImageResource file_folder_workflow_16();
	ImageResource file_folder_workflow_archive_100();
	ImageResource file_folder_workflow_finish_100();
	ImageResource file_folder_workflow_run_100();
	
	ImageResource file_enterprise_100();
	ImageResource file_enterprise_16();
	ImageResource file_share_group_100();
	ImageResource file_share_group_16();
	
	ImageResource file_action_checkout_36();
	ImageResource file_action_immediate_validation_36();
	ImageResource file_action_lock_36();
	ImageResource file_action_share_36();
	ImageResource file_bill_16();
	ImageResource file_bill_100();

	ImageResource file_checkout();
	ImageResource ic_open_white();
	
	ImageResource ic_settings_48();
	ImageResource ic_brightness();
	ImageResource ic_contrast();
	ImageResource ic_grayscale();
	ImageResource ic_rotate_clockwise();
	ImageResource ic_rotate_counter_clockwise();
	ImageResource ic_check();
	ImageResource ic_delete_white();
	ImageResource ic_restart();
	ImageResource ic_search();
	ImageResource ic_plus();
	ImageResource ic_minus();
	ImageResource ic_zoom_out_white();
	ImageResource ic_zoom_in_white();
	ImageResource ic_blue_star();
	ImageResource ic_export_bill();
	ImageResource collapse_arrow_16();
	ImageResource delete_16();
	ImageResource expand_arrow_16();
	ImageResource ic_send_facture();
	ImageResource ic_dont_send_facture();
	
	ImageResource ic_add_black_24dp();
	ImageResource ic_calculator_24();
	ImageResource ic_euro_100();
	ImageResource ic_certified();
	
	//Aklaflow activities
	ImageResource ic_start();
	ImageResource ic_finish();
	ImageResource ic_scan();
	ImageResource ic_upload();
	ImageResource ic_validation();
	ImageResource ic_retrieve();
	ImageResource ic_analyze();
	ImageResource ic_analyze_form();
	ImageResource ic_display_form();
	ImageResource ic_rename_form();
	ImageResource ic_dispatch();
	ImageResource ic_default_aklabox();
	ImageResource ic_activate();
	ImageResource ic_aggregate();
	ImageResource ic_convert_to_pdf();
	ImageResource ic_create_folder();
	ImageResource ic_deactivate();
	ImageResource ic_copy_document();
	ImageResource ic_delete_document();
	ImageResource ic_rename_document();
	ImageResource ic_alert_signature();
	
	
	ImageResource ic_mail();
	ImageResource ic_timer();
	ImageResource ic_counter();
	ImageResource ic_xor();
	ImageResource ic_ping();
	ImageResource ic_aklabox();
	ImageResource ic_google();
	ImageResource ic_orbeon();
	ImageResource ic_vanilla();
	ImageResource ic_zip();
	ImageResource ic_unzip();
	ImageResource ic_put_file();
	ImageResource ic_get_file();
	ImageResource ic_comment();
	ImageResource ic_vanilla_workflow();
	ImageResource ic_vanilla_gateway();
	ImageResource ic_vanilla_report();
	ImageResource ic_vanilla_sql();
	ImageResource ic_vanilla_starter();
	ImageResource ic_vanilla_aggregate_reports();
	ImageResource ic_vanilla_concat_excel();
	ImageResource ic_vanilla_concat_pdf();
	ImageResource ic_vanilla_calculation();
	
	ImageResource ic_readby();
	ImageResource ic_validatedby();
	ImageResource ic_treatment_date();
	
	ImageResource ic_stamp();
	ImageResource ic_workflow_box();
	ImageResource ic_create_folder_workflow();
	ImageResource ic_send_mail();
	ImageResource ic_form_workflow();
	ImageResource ic_change_status();
	ImageResource ic_change_date();
	ImageResource ic_add_document();
	ImageResource ic_run_command();
	ImageResource ic_task_assignation();
	
	ImageResource ic_group50();
	ImageResource ic_user50();
	
	ImageResource ic_eye();
	ImageResource ic_workflow_black();
	ImageResource ic_instance();
	ImageResource workflow_24();
	ImageResource ic_workflow_attente();
	ImageResource ic_workflow_finalisation();	
	ImageResource ic_workflow_instruction();
	ImageResource ic_workflow_etude();
	ImageResource ic_trash();
	ImageResource ic_validate();
	ImageResource ic_form();
	ImageResource ic_manager_50();
	ImageResource file_action_checkout_48();
	
	ImageResource ic_arrow_next_black_36dp();
	ImageResource ic_arrow_back_black_36dp();
	ImageResource ic_browse_folder_100();
	
	ImageResource logout();
	ImageResource up_arrow();
	ImageResource down_arrow();
	ImageResource ic_add_white();
}
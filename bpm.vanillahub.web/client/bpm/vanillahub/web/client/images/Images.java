package bpm.vanillahub.web.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle  {
	
	public static final Images INSTANCE =  GWT.create(Images.class);

	ImageResource close_tab();
	ImageResource audit_128();
	ImageResource disco128();
	ImageResource master_128();
	ImageResource migration_128();
	ImageResource add_user();

	ImageResource maj_w();
	ImageResource ic_archive_black_18dp();

	ImageResource cible_w_24dp();
	ImageResource source_w_24dp();
	ImageResource source_b_24dp();
	ImageResource validation_w_24dp();
	ImageResource validation_b_24dp();
	ImageResource encryption_w_24dp();
	ImageResource encryption_b_24dp();
	ImageResource mail_w_24dp();
	ImageResource mail_b_24dp();
	ImageResource compress_b_24dp();
	ImageResource compress_w_24dp();
	ImageResource connector_b_24dp();
	ImageResource connector_w_24dp();
	ImageResource action_b_24dp();
	ImageResource action_w_24dp();
	ImageResource ic_open_data_b_24dp();
	ImageResource ic_open_data_w_24dp();
	ImageResource ic_map_b_24dp();
	ImageResource ic_map_w_24dp();
	
	ImageResource flag_united_kingdom();
	ImageResource flag_france();
	ImageResource quandl_24dp();
	ImageResource dataservice_b_24dp();
	ImageResource dataservice_w_24dp();
	ImageResource folder();
	ImageResource biw();
	ImageResource gateway_16();
	ImageResource object();
	ImageResource architect_b_24dp();
	ImageResource architect_w_24dp();
	ImageResource ic_refresh_black_18dp();
	ImageResource ic_cluster();
	
	ImageResource vanilla_hub_bandeau();
	ImageResource ic_speaker_notes_black_24dp();
	ImageResource ic_speaker_notes_white_24dp();
	ImageResource ic_call_merge_black_24dp();
	ImageResource ic_call_merge_white_24dp();
}
package bpm.vanillahub.web.client.tabs.workflow;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;
import bpm.gwt.workflow.commons.client.IWorkflowAppManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.ToolBox;
import bpm.gwt.workflow.commons.client.workflow.ToolBoxCategory;
import bpm.gwt.workflow.commons.client.workflow.ToolBoxFolder;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.images.Images;
import bpm.workflow.commons.beans.TypeActivity;

import com.google.gwt.resources.client.ImageResource;

public class BoxHelper {
	public static final String TYPE_TOOL = "TypeTool";
	
	public static ImageResource getImage(TypeActivity type, boolean black) {
		switch (type) {
		case START:
			return bpm.gwt.workflow.commons.client.images.Images.INSTANCE.start_24dp();
		case STOP:
			return bpm.gwt.workflow.commons.client.images.Images.INSTANCE.stop_24dp();
		case CIBLE:
			return black ? bpm.gwt.workflow.commons.client.images.Images.INSTANCE.cible_b_24dp() : Images.INSTANCE.cible_w_24dp();
		case COMPRESSION:
			return black ? Images.INSTANCE.compress_b_24dp() : Images.INSTANCE.compress_w_24dp();
		case ENCRYPTAGE:
			return black ? Images.INSTANCE.encryption_b_24dp() : Images.INSTANCE.encryption_w_24dp();
		case MAIL:
			return black ? Images.INSTANCE.mail_b_24dp() : Images.INSTANCE.mail_w_24dp();
		case SOURCE:
			return black ? Images.INSTANCE.source_b_24dp() : Images.INSTANCE.source_w_24dp();
		case VALIDATION:
			return black ? Images.INSTANCE.validation_b_24dp() : Images.INSTANCE.validation_w_24dp();
		case CONNECTOR:
			return black ? Images.INSTANCE.connector_b_24dp() : Images.INSTANCE.connector_w_24dp();
		case ACTION:
			return black ? Images.INSTANCE.action_b_24dp() : Images.INSTANCE.action_w_24dp();
		case DATA_SERVICE:
			return black ? Images.INSTANCE.dataservice_b_24dp() : Images.INSTANCE.dataservice_w_24dp();
		case VANILLA_ITEM:
			return black ? Images.INSTANCE.dataservice_b_24dp() : Images.INSTANCE.dataservice_w_24dp();
		case CRAWL:
			return black ? Images.INSTANCE.dataservice_b_24dp() : Images.INSTANCE.dataservice_w_24dp();
		case SOCIAL:
			return black ? Images.INSTANCE.dataservice_b_24dp() : Images.INSTANCE.dataservice_w_24dp();
		case OPEN_DATA:
			return black ? Images.INSTANCE.ic_open_data_b_24dp() : Images.INSTANCE.ic_open_data_w_24dp();
		case MDM:
		case MDM_INPUT:
			return black ? Images.INSTANCE.architect_b_24dp() : Images.INSTANCE.architect_w_24dp();
		case AKLABOX:
			return black ? Images.INSTANCE.architect_b_24dp() : Images.INSTANCE.architect_w_24dp();
		case GEOJSON:
			return black ? Images.INSTANCE.ic_map_b_24dp() : Images.INSTANCE.ic_map_w_24dp();
		case PRECLUSTER:
			return black ? Images.INSTANCE.ic_cluster() : Images.INSTANCE.ic_cluster();
		case OPENDATA_CRAWL:
			return black ? Images.INSTANCE.dataservice_b_24dp() : Images.INSTANCE.dataservice_w_24dp();
		case LIMESURVEY_INPUT:
			return black ? Images.INSTANCE.ic_speaker_notes_black_24dp() : Images.INSTANCE.ic_speaker_notes_white_24dp();
		case MERGE_FILES:
			return black ? Images.INSTANCE.ic_call_merge_black_24dp() : Images.INSTANCE.ic_call_merge_white_24dp();
		default:
			return bpm.gwt.workflow.commons.client.images.Images.INSTANCE.stop_24dp();
		}
	}
	
	public static String getLabel(TypeActivity type) {
		switch (type) {
		case START:
			return LabelsCommon.lblCnst.Start();
		case STOP:
			return LabelsCommon.lblCnst.Stop();
		case CIBLE:
			return LabelsCommon.lblCnst.Cible();
		case COMPRESSION:
			return Labels.lblCnst.Compression();
		case ENCRYPTAGE:
			return Labels.lblCnst.Encryption();
		case MAIL:
			return Labels.lblCnst.Mail();
		case SOURCE:
			return Labels.lblCnst.Source();
		case VALIDATION:
			return Labels.lblCnst.Validation();
		case CONNECTOR:
			return Labels.lblCnst.ConnectorXml();
		case ACTION:
			return Labels.lblCnst.Action();
		case DATA_SERVICE:
			return Labels.lblCnst.DataService();
		case VANILLA_ITEM:
			return Labels.lblCnst.VanillaItem();
		case CRAWL:
			return Labels.lblCnst.Crawler();
		case SOCIAL:
			return Labels.lblCnst.SocialNetwork();
		case OPEN_DATA:
			return Labels.lblCnst.OpenData();
		case MDM:
			return Labels.lblCnst.Architect();
		case AKLABOX:
			return Labels.lblCnst.ApplicationAklabox();
		case GEOJSON:
			return Labels.lblCnst.ConvertGeojson();
		case PRECLUSTER:
			return Labels.lblCnst.ClusterizeData();
		case OPENDATA_CRAWL:
			return Labels.lblCnst.OpenDataCrawl();
		case MDM_INPUT:
			return Labels.lblCnst.ArchitectInput();
		case LIMESURVEY_INPUT:
			return Labels.lblCnst.LimeSurveyInput();
		case MERGE_FILES:
			return Labels.lblCnst.MergeFiles();
		default:
			return LabelsCommon.lblCnst.Unknown();
		}
	}

	public static final String INPUTS = Labels.lblCnst.Inputs();
	public static final String OUTPUTS = Labels.lblCnst.Outputs();
	public static final String TRANSFORMATIONS = Labels.lblCnst.Transformations();

	public enum Folder { 
		FOLDER_INPUTS(INPUTS), 
		FOLDER_OUTPUTS(OUTPUTS),
		FOLDER_TRANSFORMATIONS(TRANSFORMATIONS),;
		
		private String name;
		
		private Folder(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	};

	public static List<StackNavigationPanel> createCategories(IWorkflowAppManager appManager, CollapsePanel collapsePanel) {
		List<StackNavigationPanel> cats = new ArrayList<>();
		cats.add(new ToolBoxCategory(collapsePanel, LabelsCommon.lblCnst.Toolbox(), true, buildFolders(appManager)));
		return cats;
	}

	private static List<ToolBoxFolder> buildFolders(IWorkflowAppManager appManager) {
		List<ToolBoxFolder> folders = new ArrayList<>();
		folders.add(new ToolBoxFolder(Folder.FOLDER_INPUTS.getName(), null, buildTools(appManager, Folder.FOLDER_INPUTS), 1));
		folders.add(new ToolBoxFolder(Folder.FOLDER_OUTPUTS.getName(), null, buildTools(appManager, Folder.FOLDER_OUTPUTS), 1));
		folders.add(new ToolBoxFolder(Folder.FOLDER_TRANSFORMATIONS.getName(), null, buildTools(appManager, Folder.FOLDER_TRANSFORMATIONS), 1));
		return folders;
	}

	private static List<ToolBox> buildTools(IWorkflowAppManager appManager, Folder folder) {
		List<ToolBox> tools = new ArrayList<>();
		
		switch (folder) {
		case FOLDER_INPUTS:
			tools.add(new ToolBox(appManager, TypeActivity.SOURCE));
			tools.add(new ToolBox(appManager, TypeActivity.DATA_SERVICE));
			tools.add(new ToolBox(appManager, TypeActivity.CRAWL));
			tools.add(new ToolBox(appManager, TypeActivity.SOCIAL));
			tools.add(new ToolBox(appManager, TypeActivity.OPEN_DATA));
			tools.add(new ToolBox(appManager, TypeActivity.MDM_INPUT));
			tools.add(new ToolBox(appManager, TypeActivity.LIMESURVEY_INPUT));
			break;
		case FOLDER_OUTPUTS:
			tools.add(new ToolBox(appManager, TypeActivity.CIBLE));
			tools.add(new ToolBox(appManager, TypeActivity.MAIL));
			tools.add(new ToolBox(appManager, TypeActivity.CONNECTOR));
			tools.add(new ToolBox(appManager, TypeActivity.MDM));
			tools.add(new ToolBox(appManager, TypeActivity.AKLABOX));
			break;
		case FOLDER_TRANSFORMATIONS:
			tools.add(new ToolBox(appManager, TypeActivity.VALIDATION));
			tools.add(new ToolBox(appManager, TypeActivity.COMPRESSION));
			tools.add(new ToolBox(appManager, TypeActivity.ENCRYPTAGE));
			tools.add(new ToolBox(appManager, TypeActivity.ACTION));
			tools.add(new ToolBox(appManager, TypeActivity.VANILLA_ITEM));
			tools.add(new ToolBox(appManager, TypeActivity.GEOJSON));
			tools.add(new ToolBox(appManager, TypeActivity.PRECLUSTER));
			tools.add(new ToolBox(appManager, TypeActivity.OPENDATA_CRAWL));
			tools.add(new ToolBox(appManager, TypeActivity.MERGE_FILES));
			break;
		default:
			break;
		}
		return tools;
	}
}

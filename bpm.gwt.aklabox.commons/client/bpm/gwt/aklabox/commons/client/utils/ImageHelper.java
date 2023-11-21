package bpm.gwt.aklabox.commons.client.utils;

import bpm.aklabox.workflow.core.model.Instance;
import bpm.aklabox.workflow.core.model.Workflow;
import bpm.aklabox.workflow.core.model.activities.ActivateDocument;
import bpm.aklabox.workflow.core.model.activities.AggregateActivity;
import bpm.aklabox.workflow.core.model.activities.AklaBoxActivity;
import bpm.aklabox.workflow.core.model.activities.AlertSignatureActivity;
import bpm.aklabox.workflow.core.model.activities.AnalyzeActivity;
import bpm.aklabox.workflow.core.model.activities.AnalyzeFormActivity;
import bpm.aklabox.workflow.core.model.activities.AssignTaskActivity;
import bpm.aklabox.workflow.core.model.activities.AssignTaskActivityPrompt;
import bpm.aklabox.workflow.core.model.activities.CommentActivity;
import bpm.aklabox.workflow.core.model.activities.ConvertToPDFActivity;
import bpm.aklabox.workflow.core.model.activities.CopyDocumentActivity;
import bpm.aklabox.workflow.core.model.activities.CounterActivity;
import bpm.aklabox.workflow.core.model.activities.CreateFolderActivity;
import bpm.aklabox.workflow.core.model.activities.DateTreatmentDocumentActivity;
import bpm.aklabox.workflow.core.model.activities.DeActivateDocument;
import bpm.aklabox.workflow.core.model.activities.DefaultAklaBoxRepositoryActivity;
import bpm.aklabox.workflow.core.model.activities.DeleteDocumentActivity;
import bpm.aklabox.workflow.core.model.activities.DispatchActivity;
import bpm.aklabox.workflow.core.model.activities.DisplayFormActivity;
import bpm.aklabox.workflow.core.model.activities.EndActivity;
import bpm.aklabox.workflow.core.model.activities.GetFileActivity;
import bpm.aklabox.workflow.core.model.activities.GoogleActivity;
import bpm.aklabox.workflow.core.model.activities.IActivity;
import bpm.aklabox.workflow.core.model.activities.MailActivity;
import bpm.aklabox.workflow.core.model.activities.OrbeonActivity;
import bpm.aklabox.workflow.core.model.activities.PingActivity;
import bpm.aklabox.workflow.core.model.activities.PutFileActivity;
import bpm.aklabox.workflow.core.model.activities.ReadByDocumentActivity;
import bpm.aklabox.workflow.core.model.activities.RenameDocumentActivity;
import bpm.aklabox.workflow.core.model.activities.RenameFormActivity;
import bpm.aklabox.workflow.core.model.activities.RetrieveActivity;
import bpm.aklabox.workflow.core.model.activities.ScanActivity;
import bpm.aklabox.workflow.core.model.activities.StartActivity;
import bpm.aklabox.workflow.core.model.activities.TimerActivity;
import bpm.aklabox.workflow.core.model.activities.UnzipFileActivity;
import bpm.aklabox.workflow.core.model.activities.UploadActivity;
import bpm.aklabox.workflow.core.model.activities.ValidateActivity;
import bpm.aklabox.workflow.core.model.activities.ValidateByDocumentActivity;
import bpm.aklabox.workflow.core.model.activities.VanillaActivity;
import bpm.aklabox.workflow.core.model.activities.VanillaAggregateReports;
import bpm.aklabox.workflow.core.model.activities.VanillaBiGateway;
import bpm.aklabox.workflow.core.model.activities.VanillaBiWorkflow;
import bpm.aklabox.workflow.core.model.activities.VanillaCalculation;
import bpm.aklabox.workflow.core.model.activities.VanillaConcatExcel;
import bpm.aklabox.workflow.core.model.activities.VanillaConcatPdf;
import bpm.aklabox.workflow.core.model.activities.VanillaReport;
import bpm.aklabox.workflow.core.model.activities.VanillaSql;
import bpm.aklabox.workflow.core.model.activities.VanillaStarter;
import bpm.aklabox.workflow.core.model.activities.WatermarkActivity;
import bpm.aklabox.workflow.core.model.activities.WorkflowActivity;
import bpm.aklabox.workflow.core.model.activities.WorkflowAddDocumentActivity;
import bpm.aklabox.workflow.core.model.activities.WorkflowChangeDateActivity;
import bpm.aklabox.workflow.core.model.activities.WorkflowChangeStatusActivity;
import bpm.aklabox.workflow.core.model.activities.WorkflowCreateFolderActivity;
import bpm.aklabox.workflow.core.model.activities.WorkflowFormActivity;
import bpm.aklabox.workflow.core.model.activities.WorkflowSendMailActivity;
import bpm.aklabox.workflow.core.model.activities.XorActivity;
import bpm.aklabox.workflow.core.model.activities.ZipFileActivity;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.document.management.core.utils.DocumentUtils;
import bpm.document.management.core.utils.DocumentUtils.FileType;
import bpm.gwt.aklabox.commons.client.images.CommonImages;

import com.google.gwt.resources.client.ImageResource;

public class ImageHelper {
	
	public static ImageResource findResource(Object item, boolean big, DocumentUtils docUtils) {
		if (item instanceof Tree) {
			Tree folder = (Tree) item;
//			if (folder.getFolderStatus().equals(AklaboxConstant.MAIL)) {
//				return big ? CommonImages.INSTANCE.file_folder_inbox_100() : CommonImages.INSTANCE.file_folder_inbox_16();
//			}
//			else if (folder.getFolderStatus().equals(AklaboxConstant.RM)) {
//				return big ? CommonImages.INSTANCE.file_folder_rm_100() : CommonImages.INSTANCE.file_folder_rm_16();
//			}
//			else 
			if (folder.getFolderStatus().equals(AklaboxConstant.EXCHANGE)) {
				return big ? CommonImages.INSTANCE.file_folder_exchange_100() : CommonImages.INSTANCE.file_folder_exchange_16();
			}
			else if (folder.getTreeType().equals(AklaboxConstant.FOLDER_WORKFLOW)) {

				if (folder.getWorkflowProcessStatus() == AklaboxConstant.WORKFLOW_PROCESS_OPEN) {
					return big ? CommonImages.INSTANCE.file_folder_workflow_run_100() : CommonImages.INSTANCE.file_folder_workflow_16();
				}
				else if (folder.getWorkflowProcessStatus() == AklaboxConstant.WORKFLOW_PROCESS_SUSPEND) {
					return big ? CommonImages.INSTANCE.file_folder_workflow_pause_100() : CommonImages.INSTANCE.file_folder_workflow_16();
				}
				else if (folder.getWorkflowProcessStatus() == AklaboxConstant.WORKFLOW_PROCESS_FINISHED) {
					return big ? CommonImages.INSTANCE.file_folder_workflow_finish_100() : CommonImages.INSTANCE.file_folder_workflow_16();
				}
				else if (folder.getWorkflowProcessStatus() == AklaboxConstant.WORKFLOW_PROCESS_ARCHIVED) {
					return big ? CommonImages.INSTANCE.file_folder_workflow_archive_100() : CommonImages.INSTANCE.file_folder_workflow_16();
				}
				else {
					return big ? CommonImages.INSTANCE.file_folder_workflow_100() : CommonImages.INSTANCE.file_folder_workflow_16();
				}

				//TODO REFACTOR - Overlay
				// bckgrd = generateCssBackground(bckgrd, "90%");
				//
				// if (folder.getWorkflowFolderStatus() ==
				// AklaboxConstant.WORKFLOW_FOLDER_STUDY) {
				// icon =
				// ImageIcons.INSTANCE.ic_workflow_etude().getSafeUri().asString();
				// }
				// else if (folder.getWorkflowFolderStatus() ==
				// AklaboxConstant.WORKFLOW_FOLDER_INSTRUCTION) {
				// icon =
				// ImageIcons.INSTANCE.ic_workflow_instruction().getSafeUri().asString();
				// }
				// else if (folder.getWorkflowFolderStatus() ==
				// AklaboxConstant.WORKFLOW_FOLDER_WAITING) {
				// icon =
				// ImageIcons.INSTANCE.ic_workflow_attente().getSafeUri().asString();
				// }
				// else if (folder.getWorkflowFolderStatus() ==
				// AklaboxConstant.WORKFLOW_FOLDER_FINALIZATION) {
				// icon =
				// ImageIcons.INSTANCE.ic_workflow_finalisation().getSafeUri().asString();
				// }
				// else {
				// icon = "";
				// }
				// icon = generateCssBackground(icon, "50%");
				//
				// return icon + ", " + bckgrd;
			}
			else if (folder.getTreeType().equals(AklaboxConstant.FOLDER_PESV2)) {
				return big ? CommonImages.INSTANCE.file_folder_pesv2_100() : CommonImages.INSTANCE.file_folder_pesv2_16();
			}
			else if (folder.getTreeType().equals(AklaboxConstant.FOLDER_PROJECT)) {
				return big ? CommonImages.INSTANCE.file_folder_project_100() : CommonImages.INSTANCE.file_folder_project_16();
			}
			else if (folder.getTreeType().equals(AklaboxConstant.FOLDER_MARKET)) {
				return big ? CommonImages.INSTANCE.file_folder_market_100() : CommonImages.INSTANCE.file_folder_market_16();
			}
			else if (folder.getTreeType().equals(AklaboxConstant.FOLDER_MARKET_FINAL)) {
				return big ? CommonImages.INSTANCE.file_folder_market_valid_100() : CommonImages.INSTANCE.file_folder_market_valid_16();
			}
			else {
				return big ? CommonImages.INSTANCE.file_folder_classic_100() : CommonImages.INSTANCE.file_folder_classic_16();
			}

			// if (folder.isImmediateValidation()) {
			// icon =
			// generateCssBackground(ImageIcons.INSTANCE.ic_blue_synchro50().getSafeUri().asString(),
			// "90%", "90%", "30%");
			// }

			// return ((icon != null) ? icon + ", " : "") + bckgrd;
			// 
		}
		else if (item instanceof Documents) {
			Documents doc = (Documents) item;
			if (doc.isScanned()) {
				return big ? CommonImages.INSTANCE.file_scan_64() : CommonImages.INSTANCE.file_scan_16();
			} else if(doc.getType().equals(DocumentUtils.ORBEON)) {
				return findResource(FileType.ORBEON, big);
			}
			else {
				FileType type = docUtils.getFileType(doc.getFileName());
				return findResource(type, big);
			}
		}
		else if (item instanceof Enterprise || item instanceof Group) {
			return big ? CommonImages.INSTANCE.file_enterprise_100() : CommonImages.INSTANCE.file_enterprise_16();
		}
		return big ? CommonImages.INSTANCE.file_file_64() : CommonImages.INSTANCE.file_file_16();
	}

	private static ImageResource findResource(FileType type, boolean big) {
		switch (type) {
		case ACCESS:
			return big ? CommonImages.INSTANCE.file_access_64() : CommonImages.INSTANCE.file_access_16();
		case AUDIO:
			return big ? CommonImages.INSTANCE.file_audio_64() : CommonImages.INSTANCE.file_audio_16();
		case DOCUMENT:
			return big ? CommonImages.INSTANCE.file_word_64() : CommonImages.INSTANCE.file_word_16();
		case DWG:
			return big ? CommonImages.INSTANCE.file_dwg_64() : CommonImages.INSTANCE.file_dwg_16();
		case DXF:
			return big ? CommonImages.INSTANCE.file_dxf_64() : CommonImages.INSTANCE.file_dxf_16();
		case FILE:
			return big ? CommonImages.INSTANCE.file_file_64() : CommonImages.INSTANCE.file_file_16();
		case IMAGE:
			return big ? CommonImages.INSTANCE.file_image_64() : CommonImages.INSTANCE.file_image_16();
		case ORBEON:
			return big ? CommonImages.INSTANCE.file_orbeon_64() : CommonImages.INSTANCE.file_orbeon_16();
		case PDF:
			return big ? CommonImages.INSTANCE.file_pdf_64() : CommonImages.INSTANCE.file_pdf_16();
		case PRESENTATION:
			return big ? CommonImages.INSTANCE.file_powerpoint_64() : CommonImages.INSTANCE.file_powerpoint_16();
		case SPREADSHEET:
			return big ? CommonImages.INSTANCE.file_excel_64() : CommonImages.INSTANCE.file_excel_16();
		case TEXT:
			return big ? CommonImages.INSTANCE.file_txt_64() : CommonImages.INSTANCE.file_txt_16();
		case URL:
			return big ? CommonImages.INSTANCE.file_url_64() : CommonImages.INSTANCE.file_url_16();
		case VANILLA:
			return big ? CommonImages.INSTANCE.file_vanilla_64() : CommonImages.INSTANCE.file_vanilla_16();
		case VIDEO:
			return big ? CommonImages.INSTANCE.file_video_64() : CommonImages.INSTANCE.file_video_16();
		case XAKL:
			return big ? CommonImages.INSTANCE.file_xakl_64() : CommonImages.INSTANCE.file_xakl_16();
		case ZIP:
			return big ? CommonImages.INSTANCE.file_zip_64() : CommonImages.INSTANCE.file_zip_16();
		default:
			break;
		}
		return big ? CommonImages.INSTANCE.file_file_64() : CommonImages.INSTANCE.file_file_16();
	}
	
	public static ImageResource findResource(IActivity activity) {
		if(activity instanceof StartActivity){
			return CommonImages.INSTANCE.ic_start();
		}else if(activity instanceof EndActivity){
			return CommonImages.INSTANCE.ic_finish();
		}else if(activity instanceof ScanActivity){
			return CommonImages.INSTANCE.ic_scan();
		}else if(activity instanceof UploadActivity){
			return CommonImages.INSTANCE.ic_upload();
		}else if(activity instanceof ValidateActivity){
			return CommonImages.INSTANCE.ic_validation();
		}else if(activity instanceof RetrieveActivity){
			return CommonImages.INSTANCE.ic_retrieve();
		}else if(activity instanceof AnalyzeActivity){
			return CommonImages.INSTANCE.ic_analyze();
		}else if(activity instanceof AnalyzeFormActivity){
			return CommonImages.INSTANCE.ic_analyze_form();
		}else if(activity instanceof DisplayFormActivity){
			return CommonImages.INSTANCE.ic_display_form();
		}else if(activity instanceof RenameFormActivity){
			return CommonImages.INSTANCE.ic_rename_form();
		}else if(activity instanceof DispatchActivity){
			return CommonImages.INSTANCE.ic_dispatch();
		}else if(activity instanceof DefaultAklaBoxRepositoryActivity){
			return CommonImages.INSTANCE.ic_default_aklabox();
		}else if(activity instanceof CommentActivity){
			return CommonImages.INSTANCE.ic_comment();
		}else if(activity instanceof AlertSignatureActivity){
			return CommonImages.INSTANCE.ic_alert_signature();
		}
		
		//DOCUMENT ACTIVITIES
		else if(activity instanceof ActivateDocument){
			return CommonImages.INSTANCE.ic_activate();
		}else if(activity instanceof AggregateActivity){
			return CommonImages.INSTANCE.ic_aggregate();
		}else if(activity instanceof ConvertToPDFActivity){
			return CommonImages.INSTANCE.ic_convert_to_pdf();
		}else if(activity instanceof CreateFolderActivity){
			return CommonImages.INSTANCE.ic_create_folder();
		}else if(activity instanceof DeActivateDocument){
			return CommonImages.INSTANCE.ic_deactivate();
		}else if(activity instanceof CopyDocumentActivity){
			return CommonImages.INSTANCE.ic_copy_document();
		}else if(activity instanceof DeleteDocumentActivity){
			return CommonImages.INSTANCE.ic_delete_document();
		}else if(activity instanceof RenameDocumentActivity){
			return CommonImages.INSTANCE.ic_rename_document();
		}else if(activity instanceof ReadByDocumentActivity){
			return CommonImages.INSTANCE.ic_readby();
		}else if(activity instanceof ValidateByDocumentActivity){
			return CommonImages.INSTANCE.ic_validatedby();
		}else if(activity instanceof DateTreatmentDocumentActivity){
			return CommonImages.INSTANCE.ic_treatment_date();
		}else if(activity instanceof AssignTaskActivityPrompt){
			return CommonImages.INSTANCE.ic_run_command();
		}else if(activity instanceof AssignTaskActivity){
			return CommonImages.INSTANCE.ic_task_assignation();
		}
		
		//EVENT ACTIVITIES
		else if(activity instanceof MailActivity){
			return CommonImages.INSTANCE.ic_mail();
		}else if(activity instanceof TimerActivity){
			return CommonImages.INSTANCE.ic_timer();
		}else if(activity instanceof CounterActivity){
			return CommonImages.INSTANCE.ic_counter();
		}
		
		
		//CONDITION ACTIVITIES
		else if(activity instanceof XorActivity){
			return CommonImages.INSTANCE.ic_xor();
		}else if(activity instanceof PingActivity){
			return CommonImages.INSTANCE.ic_ping();
		}
		
		//INTERFACES
		else if(activity instanceof AklaBoxActivity){
			return CommonImages.INSTANCE.ic_aklabox();
		}else if(activity instanceof GoogleActivity){
			return CommonImages.INSTANCE.ic_google();
		}else if(activity instanceof OrbeonActivity){
			return CommonImages.INSTANCE.ic_orbeon();
		}else if(activity instanceof VanillaActivity){
			return CommonImages.INSTANCE.ic_vanilla();
		}
		
		//FILE
		else if(activity instanceof ZipFileActivity){
			return CommonImages.INSTANCE.ic_zip();
		}else if(activity instanceof UnzipFileActivity){
			return CommonImages.INSTANCE.ic_unzip();
		}else if(activity instanceof PutFileActivity){
			return CommonImages.INSTANCE.ic_put_file();
		}else if(activity instanceof GetFileActivity){
			return CommonImages.INSTANCE.ic_get_file();
		}
		
		else if(activity instanceof VanillaBiWorkflow){
			return CommonImages.INSTANCE.ic_vanilla_workflow();
		}else if(activity instanceof VanillaBiGateway){
			return CommonImages.INSTANCE.ic_vanilla_gateway();
		}else if(activity instanceof VanillaReport){
			return CommonImages.INSTANCE.ic_vanilla_report();
		}else if(activity instanceof VanillaSql){
			return CommonImages.INSTANCE.ic_vanilla_sql();
		}else if(activity instanceof VanillaStarter){
			return CommonImages.INSTANCE.ic_vanilla_starter();
		}else if(activity instanceof VanillaAggregateReports){
			return CommonImages.INSTANCE.ic_vanilla_aggregate_reports();
		}else if(activity instanceof VanillaConcatExcel){
			return CommonImages.INSTANCE.ic_vanilla_concat_excel();
		}else if(activity instanceof VanillaConcatPdf){
			return CommonImages.INSTANCE.ic_vanilla_concat_pdf();
		}else if(activity instanceof VanillaCalculation){
			return CommonImages.INSTANCE.ic_vanilla_calculation();
		}
		
		//WORKFLOW
		else if(activity instanceof WorkflowChangeStatusActivity){
			return CommonImages.INSTANCE.ic_change_status();
		}else if(activity instanceof WorkflowChangeDateActivity){
			return CommonImages.INSTANCE.ic_change_date();
		}else if(activity instanceof WorkflowActivity){
			return CommonImages.INSTANCE.ic_workflow_box();
		}else if(activity instanceof WorkflowSendMailActivity){
			return CommonImages.INSTANCE.ic_send_mail();
		}else if(activity instanceof WorkflowFormActivity){
			return CommonImages.INSTANCE.ic_form_workflow();
		}else if(activity instanceof WorkflowCreateFolderActivity){
			return CommonImages.INSTANCE.ic_create_folder_workflow();
		}else if(activity instanceof WorkflowAddDocumentActivity){
			return CommonImages.INSTANCE.ic_add_document();
		}else if(activity instanceof WatermarkActivity){
			return CommonImages.INSTANCE.ic_stamp();
		}
		
		
		else { return null;}
	}
	
	public static ImageResource findResource(Workflow workflow) {
		switch (workflow.getTypeWorkflow()) {
		case SIMPLE:
			return CommonImages.INSTANCE.ic_instance();
		case MASTER:
			return CommonImages.INSTANCE.workflow_24();
		case ORBEON:
			return CommonImages.INSTANCE.ic_orbeon();
		default:
			return CommonImages.INSTANCE.ic_instance();
		}
	}
}

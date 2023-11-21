package bpm.gwt.aklabox.commons.client.utils;

import bpm.aklabox.workflow.core.model.activities.ActivateDocument;
import bpm.aklabox.workflow.core.model.activities.Activity;
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
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;

import com.google.gwt.resources.client.ImageResource;

public class ActivityHelper {

	
	public static ImageResource findResource(IActivity activity) {
		return ImageHelper.findResource(activity);
	}
	
	public static String getLabel(IActivity activity) {
		if(activity instanceof StartActivity){
			return LabelsConstants.lblCnst.StartActivity();
		}else if(activity instanceof EndActivity){
			return LabelsConstants.lblCnst.EndActivity();
		}else if(activity instanceof ScanActivity){
			return LabelsConstants.lblCnst.ScanActivity();
		}else if(activity instanceof UploadActivity){
			return LabelsConstants.lblCnst.UploadActivity();
		}else if(activity instanceof ValidateActivity){
			return LabelsConstants.lblCnst.ValidateActivity();
		}else if(activity instanceof RetrieveActivity){
			return LabelsConstants.lblCnst.RetrieveActivity();
		}else if(activity instanceof AnalyzeActivity){
			return LabelsConstants.lblCnst.AnalyzeActivity();
		}else if(activity instanceof AnalyzeFormActivity){
			return LabelsConstants.lblCnst.AnalyzeFormActivity();
		}else if(activity instanceof DisplayFormActivity){
			return LabelsConstants.lblCnst.DisplayFormActivity();
		}else if(activity instanceof RenameFormActivity){
			return LabelsConstants.lblCnst.RenameFormActivity();
		}else if(activity instanceof DispatchActivity){
			return LabelsConstants.lblCnst.DispatchActivity();
		}else if(activity instanceof DefaultAklaBoxRepositoryActivity){
			return LabelsConstants.lblCnst.DefaultAklaBoxRepositoryActivity();
		}else if(activity instanceof CommentActivity){
			return LabelsConstants.lblCnst.CommentActivity();
		}else if(activity instanceof AlertSignatureActivity){
			return LabelsConstants.lblCnst.AlertSignatureActivity();
		}
		
		//DOCUMENT ACTIVITIES
		else if(activity instanceof ActivateDocument){
			return LabelsConstants.lblCnst.ActivateDocument();
		}else if(activity instanceof AggregateActivity){
			return LabelsConstants.lblCnst.AggregateActivity();
		}else if(activity instanceof ConvertToPDFActivity){
			return LabelsConstants.lblCnst.ConvertToPDFActivity();
		}else if(activity instanceof CreateFolderActivity){
			return LabelsConstants.lblCnst.CreateFolderActivity();
		}else if(activity instanceof DeActivateDocument){
			return LabelsConstants.lblCnst.DeActivateDocument();
		}else if(activity instanceof CopyDocumentActivity){
			return LabelsConstants.lblCnst.CopyDocumentActivity();
		}else if(activity instanceof DeleteDocumentActivity){
			return LabelsConstants.lblCnst.DeleteDocumentActivity();
		}else if(activity instanceof RenameDocumentActivity){
			return LabelsConstants.lblCnst.RenameDocumentActivity();
		}else if(activity instanceof ReadByDocumentActivity){
			return LabelsConstants.lblCnst.ReadByDocumentActivity();
		}else if(activity instanceof ValidateByDocumentActivity){
			return LabelsConstants.lblCnst.ValidateByDocumentActivity();
		}else if(activity instanceof DateTreatmentDocumentActivity){
			return LabelsConstants.lblCnst.DateTreatmentDocumentActivity();
		}
		
		//EVENT ACTIVITIES
		else if(activity instanceof MailActivity){
			return LabelsConstants.lblCnst.MailActivity();
		}else if(activity instanceof TimerActivity){
			return LabelsConstants.lblCnst.TimerActivity();
		}else if(activity instanceof CounterActivity){
			return LabelsConstants.lblCnst.CounterActivity();
		}else if(activity instanceof AssignTaskActivityPrompt){
			return LabelsConstants.lblCnst.AskAssignTaskActivity();
		}else if(activity instanceof AssignTaskActivityPrompt){
			return LabelsConstants.lblCnst.AssignTaskActivity();
		}
		
		//CONDITION ACTIVITIES
		else if(activity instanceof XorActivity){
			return LabelsConstants.lblCnst.XorActivity();
		}else if(activity instanceof PingActivity){
			return LabelsConstants.lblCnst.PingActivity();
		}
		
		//INTERFACES
		else if(activity instanceof AklaBoxActivity){
			return LabelsConstants.lblCnst.AklaBoxActivity();
		}else if(activity instanceof GoogleActivity){
			return LabelsConstants.lblCnst.GoogleActivity();
		}else if(activity instanceof OrbeonActivity){
			return LabelsConstants.lblCnst.OrbeonActivity();
		}else if(activity instanceof VanillaActivity){
			return LabelsConstants.lblCnst.VanillaActivity();
		}
		
		//FILE
		else if(activity instanceof ZipFileActivity){
			return LabelsConstants.lblCnst.ZipFileActivity();
		}else if(activity instanceof UnzipFileActivity){
			return LabelsConstants.lblCnst.UnzipFileActivity();
		}else if(activity instanceof PutFileActivity){
			return LabelsConstants.lblCnst.PutFileActivity();
		}else if(activity instanceof GetFileActivity){
			return LabelsConstants.lblCnst.GetFileActivity();
		}
		
		else if(activity instanceof VanillaBiWorkflow){
			return LabelsConstants.lblCnst.VanillaBiWorkflow();
		}else if(activity instanceof VanillaBiGateway){
			return LabelsConstants.lblCnst.VanillaBiGateway();
		}else if(activity instanceof VanillaReport){
			return LabelsConstants.lblCnst.VanillaReport();
		}else if(activity instanceof VanillaSql){
			return LabelsConstants.lblCnst.VanillaSql();
		}else if(activity instanceof VanillaStarter){
			return LabelsConstants.lblCnst.VanillaStarter();
		}else if(activity instanceof VanillaAggregateReports){
			return LabelsConstants.lblCnst.VanillaAggregateReports();
		}else if(activity instanceof VanillaConcatExcel){
			return LabelsConstants.lblCnst.VanillaConcatExcel();
		}else if(activity instanceof VanillaConcatPdf){
			return LabelsConstants.lblCnst.VanillaConcatPdf();
		}else if(activity instanceof VanillaCalculation){
			return LabelsConstants.lblCnst.VanillaCalculation();
		}
		
		//WORKFLOW
		else if(activity instanceof WorkflowChangeStatusActivity){
			return LabelsConstants.lblCnst.WorkflowChangeStatusActivity();
		}else if(activity instanceof WorkflowChangeDateActivity){
			return LabelsConstants.lblCnst.WorkflowChangeDateActivity();
		}else if(activity instanceof WorkflowActivity){
			return LabelsConstants.lblCnst.WorkflowActivity();
		}else if(activity instanceof WorkflowSendMailActivity){
			return LabelsConstants.lblCnst.WorkflowSendMailActivity();
		}else if(activity instanceof WorkflowFormActivity){
			return LabelsConstants.lblCnst.WorkflowFormActivity();
		}else if(activity instanceof WorkflowCreateFolderActivity){
			return LabelsConstants.lblCnst.WorkflowCreateFolderActivity();
		}else if(activity instanceof WorkflowAddDocumentActivity){
			return LabelsConstants.lblCnst.WorkflowAddDocumentActivity();
		}else if(activity instanceof WatermarkActivity){
			return LabelsConstants.lblCnst.WatermarkActivity();
		}
		return null;
	}
	
	public static Activity generateActivity(IActivity activity, String name, int workflowId, int posX, int posY, int index){
		if (activity instanceof StartActivity) {
			return new StartActivity(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}
		else if (activity instanceof AnalyzeActivity) {
			return new AnalyzeActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", "", "");
		}
		else if (activity instanceof DispatchActivity) {
			return new DispatchActivity(activity.getActivityId(), name, workflowId, posX, posY,  index/*, 0*/);
		}
		else if (activity instanceof RetrieveActivity) {
			return new RetrieveActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", 0/*, 0*/);
		}
		else if (activity instanceof ScanActivity) {
			return new ScanActivity(activity.getActivityId(), name, workflowId, posX, posY,  index/*, 0*/, 0);
		}
		else if (activity instanceof UploadActivity) {
			return new UploadActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, /*0, */0);
		}
		else if (activity instanceof ValidateActivity) {
			return new ValidateActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", ""/*, 0*/);
		}
		else if (activity instanceof ActivateDocument) {
			return new ActivateDocument(activity.getActivityId(), name, workflowId, posX, posY,  index/*, 0*/);
		}
		else if (activity instanceof AggregateActivity) {
			return new AggregateActivity(activity.getActivityId(), name, workflowId, posX, posY,  index/*, 0*/);
		}
		else if (activity instanceof ConvertToPDFActivity) {
			return new ConvertToPDFActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", 0, "", 0/*, 0*/);
		}
		else if (activity instanceof CreateFolderActivity) {
			return new CreateFolderActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, ""/*, 0*/);
		}
		else if (activity instanceof DeActivateDocument) {
			return new DeActivateDocument(activity.getActivityId(), name, workflowId, posX, posY,  index/*, 0*/);
		}
		else if (activity instanceof MailActivity) {
			return new MailActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", "", "", 0);
		}
		else if (activity instanceof TimerActivity) {
			return new TimerActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, 0);
		}
		else if (activity instanceof CounterActivity) {
			return new CounterActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, 0);
		}
		else if (activity instanceof AklaBoxActivity) {
			return new AklaBoxActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, 0, "", "");
		}
		else if (activity instanceof OrbeonActivity) {
			return new OrbeonActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, 0, "", "");
		}
		else if (activity instanceof GoogleActivity) {
			return new GoogleActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, 0, "", "");
		}
		else if (activity instanceof VanillaActivity) {
			return new VanillaActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, 0, "", "");
		}
		else if (activity instanceof XorActivity) {
			return new XorActivity(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}
		else if (activity instanceof PingActivity) {
			return new PingActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, 0);
		}
		else if (activity instanceof GetFileActivity) {
			return new GetFileActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", 0);
		}
		else if (activity instanceof PutFileActivity) {
			return new PutFileActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", 0);
		}
		else if (activity instanceof ZipFileActivity) {
			return new ZipFileActivity(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}
		else if (activity instanceof UnzipFileActivity) {
			return new UnzipFileActivity(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}
		else if (activity instanceof CopyDocumentActivity) {
			return new CopyDocumentActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", ""/*, 0*/);
		}
		else if (activity instanceof DeleteDocumentActivity) {
			return new DeleteDocumentActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", ""/*, 0*/);
		}
		else if (activity instanceof RenameDocumentActivity) {
			return new RenameDocumentActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", ""/*, 0*/);
		}
		else if (activity instanceof DefaultAklaBoxRepositoryActivity) {
			return new DefaultAklaBoxRepositoryActivity(activity.getActivityId(), name, workflowId, posX, posY,  index/*, 0*/);
		}else if (activity instanceof AnalyzeFormActivity) {
			return new AnalyzeFormActivity(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if (activity instanceof DisplayFormActivity) {
			return new DisplayFormActivity(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if (activity instanceof RenameFormActivity) {
			return new RenameFormActivity(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if (activity instanceof CommentActivity) {
			//TODO Comment Box ReDesign
			return new CommentActivity(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if (activity instanceof AlertSignatureActivity) {
			return new AlertSignatureActivity(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if (activity instanceof VanillaBiWorkflow){
			return new VanillaBiWorkflow(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if(activity instanceof VanillaBiGateway){
			return new VanillaBiGateway(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if(activity instanceof VanillaReport){
			return new VanillaReport(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if(activity instanceof VanillaAggregateReports){
			return new VanillaAggregateReports(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if(activity instanceof VanillaConcatExcel){
			return new VanillaConcatExcel(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if(activity instanceof VanillaConcatPdf){
			return new VanillaConcatPdf(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if(activity instanceof VanillaCalculation){
			return new VanillaCalculation(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}else if(activity instanceof WorkflowActivity){
			return new WorkflowActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, 0);
//		}else if(activity instanceof WorkflowAddDocumentActivity){
//			return new WorkflowAddDocumentActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", 0);
//		}else if(activity instanceof WorkflowChangeDateActivity){
//			return new WorkflowChangeDateActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", 0);
//		}else if(activity instanceof WorkflowCreateFolderActivity){
//			return new WorkflowCreateFolderActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", 0);
//		}else if(activity instanceof WorkflowChangeStatusActivity){
//			return new WorkflowChangeStatusActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", 0);
//		}else if(activity instanceof WorkflowFormActivity){
//			return new WorkflowFormActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", 0);
//		}else if(activity instanceof WorkflowSendMailActivity){
//			return new WorkflowSendMailActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", 0);}
//		else if(activity instanceof WatermarkActivity){
//				return new WatermarkActivity(activity.getActivityId(), name, workflowId, posX, posY,  index, "", "", 0);
		}else if(activity instanceof AssignTaskActivityPrompt){
			return new AssignTaskActivityPrompt(activity.getActivityId(), name, workflowId, posX, posY,  index, 0);
		}else if(activity instanceof AssignTaskActivity){
			return new AssignTaskActivity(activity.getActivityId(), name, workflowId, posX, posY,  index/*, 0*/);

		}else if (activity instanceof EndActivity) {
			return new EndActivity(activity.getActivityId(), name, workflowId, posX, posY,  index);
		}
		return null;
	}
	
	public static String getAssignedUserOrbeon(Activity activity){
		if(activity.getOrgaElementIds() == null) return "";
		if(activity.getOrgaElementIds().size() == 0){
			return  LabelsConstants.lblCnst.NoSpecificRole();
		} else if(activity.getOrgaElementIds().size() == 1){
			if(activity.getOrgaElementIds().get(0) > 0){
				return activity.getOrgaElements().get(0).getName() 
						+ " - " + LabelHelper.functionToLabel(activity.getOrgaFunctions().get(0));
			} else if(activity.getOrgaElementIds().get(0) == 0){
				return LabelsConstants.lblCnst.RoleInHierarchy() + " - " + LabelHelper.functionToLabel(activity.getOrgaFunctions().get(0));
			} else if(activity.getOrgaElementIds().get(0) == -1){
				return LabelsConstants.lblCnst.DirectSuperior();
			} else if(activity.getOrgaElementIds().get(0) == -2){
				return LabelsConstants.lblCnst.DirectSuperior2();
			} else if(activity.getOrgaElementIds().get(0) == -3){
				return LabelsConstants.lblCnst.DirectSuperior3();
			} 
			return "";
		} else {
			return LabelsConstants.lblCnst.ManyRoles();
		}
	}
}

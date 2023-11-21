package bpm.workflow.ui.editors;

import org.eclipse.draw2d.Graphics;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;

import bpm.workflow.runtime.model.activities.AirActivity;
import bpm.workflow.runtime.model.activities.BiWorkFlowActivity;
import bpm.workflow.runtime.model.activities.CalculationActivity;
import bpm.workflow.runtime.model.activities.CancelActivity;
import bpm.workflow.runtime.model.activities.CheckColumnActivity;
import bpm.workflow.runtime.model.activities.CheckPathActivity;
import bpm.workflow.runtime.model.activities.CheckTableActivity;
import bpm.workflow.runtime.model.activities.CommandFTPActivity;
import bpm.workflow.runtime.model.activities.Comment;
import bpm.workflow.runtime.model.activities.CompterActivity;
import bpm.workflow.runtime.model.activities.ConpensationActivity;
import bpm.workflow.runtime.model.activities.DeleteFileActivity;
import bpm.workflow.runtime.model.activities.DeleteFolderActivity;
import bpm.workflow.runtime.model.activities.ErrorActivity;
import bpm.workflow.runtime.model.activities.ExcelAggregateActivity;
import bpm.workflow.runtime.model.activities.InterfaceActivity;
import bpm.workflow.runtime.model.activities.InterfaceGoogleActivity;
import bpm.workflow.runtime.model.activities.KPIActivity;
import bpm.workflow.runtime.model.activities.KettleActivity;
import bpm.workflow.runtime.model.activities.LinkActivity;
import bpm.workflow.runtime.model.activities.MailActivity;
import bpm.workflow.runtime.model.activities.PingHostActivity;
import bpm.workflow.runtime.model.activities.SignalActivity;
import bpm.workflow.runtime.model.activities.SqlActivity;
import bpm.workflow.runtime.model.activities.StarterActivity;
import bpm.workflow.runtime.model.activities.TalendActivity;
import bpm.workflow.runtime.model.activities.TaskListActivity;
import bpm.workflow.runtime.model.activities.TimerActivity;
import bpm.workflow.runtime.model.activities.XorActivity;
import bpm.workflow.runtime.model.activities.filemanagement.BrowseContentActivity;
import bpm.workflow.runtime.model.activities.filemanagement.ConcatExcelActivity;
import bpm.workflow.runtime.model.activities.filemanagement.ConcatPDFActivity;
import bpm.workflow.runtime.model.activities.filemanagement.EncryptFileActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutSFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutSSHActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.model.activities.vanilla.GatewayActivity;
import bpm.workflow.runtime.model.activities.vanilla.GedActivity;
import bpm.workflow.runtime.model.activities.vanilla.MetadataToD4CActivity;
import bpm.workflow.runtime.model.activities.vanilla.ResetGedIndexActivity;
import bpm.workflow.runtime.model.activities.vanilla.ResetUolapCacheActivity;
import bpm.workflow.runtime.model.activities.vanilla.StartStopItemActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.factories.NodeFactory;
import bpm.workflow.ui.icons.IconsNames;

/**
 * The palette providing all the activities and events
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class WorkflowPaletteRoot extends PaletteRoot {

	public WorkflowPaletteRoot() {
		super();

		PaletteDrawer manipGroup = new PaletteDrawer(Messages.WorkflowPaletteRoot_0);

		ToolEntry selectionToolEntry = new PanningSelectionToolEntry();
		manipGroup.add(selectionToolEntry);

		manipGroup.add(new ConnectionCreationToolEntry(Messages.WorkflowPaletteRoot_1, Messages.WorkflowPaletteRoot_2, new CreationFactory() {
			public Object getNewObject() {
				return null;
			}

			public Object getObjectType() {
				return Graphics.LINE_SOLID;
			}
		}, Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.FLECHE), null));

		manipGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_3, Messages.WorkflowPaletteRoot_4, new NodeFactory(Comment.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.NOTE), null));
		this.add(manipGroup);

		PaletteSeparator sep2 = new PaletteSeparator();
		this.add(sep2);

		PaletteDrawer activitiesGroup = new PaletteDrawer(Messages.WorkflowPaletteRoot_5);
		this.add(activitiesGroup);

		activitiesGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_6, Messages.WorkflowPaletteRoot_7, new NodeFactory(SqlActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.SQL_16), null));

		// activitiesGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_10, Messages.WorkflowPaletteRoot_11,
		// new NodeFactory(LoopGlobale.class),
		// Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.LOOP_PALETTE),
		// null));
		//	        
		// activitiesGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_12, Messages.WorkflowPaletteRoot_13,
		// new NodeFactory(MacroProcess.class),
		// Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.MACROPROCESS_16),
		// null));

		activitiesGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_14, Messages.WorkflowPaletteRoot_15, new NodeFactory(StarterActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.STARTER_16), null));

		activitiesGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_16, Messages.WorkflowPaletteRoot_17, new NodeFactory(ExcelAggregateActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.EXCELAGGREGATE_16), null));

		activitiesGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_18, Messages.WorkflowPaletteRoot_19, new NodeFactory(ConcatExcelActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.ADDTOEXCEL_16), null));

		activitiesGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_20, Messages.WorkflowPaletteRoot_21, new NodeFactory(ConcatPDFActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.ADDTOPDF_16), null));

		activitiesGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_22, Messages.WorkflowPaletteRoot_23, new NodeFactory(CalculationActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.CALCULATION_16), null));
		
		activitiesGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_101, Messages.WorkflowPaletteRoot_102, new NodeFactory(KettleActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.KETTLE_16), null));
		activitiesGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_105, Messages.WorkflowPaletteRoot_106, new NodeFactory(TalendActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.TALEND_16), null));

		PaletteDrawer vanillaActGroup = new PaletteDrawer(Messages.WorkflowPaletteRoot_24);
		this.add(vanillaActGroup);

		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_8, Messages.WorkflowPaletteRoot_9, new NodeFactory(BiWorkFlowActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.WORKFLOWACT_16), null));

		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_25, Messages.WorkflowPaletteRoot_26, new NodeFactory(GatewayActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.GATEWAYACT_16), null));

		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_103, Messages.WorkflowPaletteRoot_104, new NodeFactory(MetadataToD4CActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.D4C_16), null));
		
		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_27, Messages.WorkflowPaletteRoot_28, new NodeFactory(ReportActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.BIOBJECT_16), null));

		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_31, Messages.WorkflowPaletteRoot_32, new NodeFactory(GedActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.GED_16), null));

		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_33, Messages.WorkflowPaletteRoot_34, new NodeFactory(KPIActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.KPI_16), null));

		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_35, Messages.WorkflowPaletteRoot_36, new NodeFactory(StartStopItemActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.STARTSTOP_16), null));

		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_37, Messages.WorkflowPaletteRoot_38, new NodeFactory(TaskListActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.TASKLIST), null));

		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_91, Messages.WorkflowPaletteRoot_92, new NodeFactory(ResetGedIndexActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.RESETGED_16), null));

		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_93, Messages.WorkflowPaletteRoot_94, new NodeFactory(ResetUolapCacheActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.RESETUOLAP_16), null));

		vanillaActGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_99, Messages.WorkflowPaletteRoot_100, new NodeFactory(AirActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.AIR_16), null));
		
		PaletteDrawer eventsGroup = new PaletteDrawer(Messages.WorkflowPaletteRoot_39);
		this.add(eventsGroup);

		eventsGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_40, Messages.WorkflowPaletteRoot_41, new NodeFactory(MailActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.MAIL_16), null));

		eventsGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_42, Messages.WorkflowPaletteRoot_43, new NodeFactory(ErrorActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.ERRORIMAGE_16), null));
		eventsGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_44, Messages.WorkflowPaletteRoot_45, new NodeFactory(CancelActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.CANCELIMAGE_16), null));
		eventsGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_46, Messages.WorkflowPaletteRoot_47, new NodeFactory(LinkActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.LINKIMAGE_16), null));
		eventsGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_48, Messages.WorkflowPaletteRoot_49, new NodeFactory(SignalActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.SIGNALIMAGE_16), null));
		eventsGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_50, Messages.WorkflowPaletteRoot_51, new NodeFactory(ConpensationActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.CONPENSIMAGE_16), null));

		eventsGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_52, Messages.WorkflowPaletteRoot_53, new NodeFactory(TimerActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.TIMER_16), null));

		eventsGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_54, Messages.WorkflowPaletteRoot_55, new NodeFactory(CompterActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.COMPTER_16), null));

		PaletteDrawer manualGroup = new PaletteDrawer(Messages.WorkflowPaletteRoot_56);
		this.add(manualGroup);
		manualGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_57, Messages.WorkflowPaletteRoot_58, new NodeFactory(InterfaceActivity.class, 0), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.ORBEONEV_16), null));
		manualGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_59, Messages.WorkflowPaletteRoot_60, new NodeFactory(InterfaceActivity.class, 1), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.FDFORM_16), null));
		manualGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_61, Messages.WorkflowPaletteRoot_62, new NodeFactory(InterfaceGoogleActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.GOOGLEFORM_16), null));
		manualGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_95, Messages.WorkflowPaletteRoot_96, new NodeFactory(CommandFTPActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.PINGHOST_16), null));

		PaletteDrawer conditionGroup = new PaletteDrawer(Messages.WorkflowPaletteRoot_63);
		this.add(conditionGroup);
		conditionGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_64, Messages.WorkflowPaletteRoot_65, new NodeFactory(XorActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.XOR_16), null));

		conditionGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_66, Messages.WorkflowPaletteRoot_67, new NodeFactory(CheckPathActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.CHECKPATH_16), null));
		conditionGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_68, Messages.WorkflowPaletteRoot_69, new NodeFactory(CheckTableActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.CHECKTABLE_16), null));
		conditionGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_70, Messages.WorkflowPaletteRoot_71, new NodeFactory(CheckColumnActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.CHECKCOL_16), null));

		conditionGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_72, Messages.WorkflowPaletteRoot_73, new NodeFactory(PingHostActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.PINGHOST_16), null));

		PaletteDrawer fileGroup = new PaletteDrawer(Messages.WorkflowPaletteRoot_74);
		this.add(fileGroup);
		fileGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_75, Messages.WorkflowPaletteRoot_76, new NodeFactory(BrowseContentActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.BROWSE_16), null));

		// fileGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_77, Messages.WorkflowPaletteRoot_78,
		// new NodeFactory(GetFTPActivity.class),
		// Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.GETFTP_16),
		// null));
		fileGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_79, Messages.WorkflowPaletteRoot_80, new NodeFactory(PutFTPActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.PUTFTP_16), null));

		// fileGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_81, Messages.WorkflowPaletteRoot_82,
		// new NodeFactory(GetSFTPActivity.class),
		// Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.GETFTP_16),
		// null));

		fileGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_83, Messages.WorkflowPaletteRoot_84, new NodeFactory(PutSFTPActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.PUT_SFTP_16), null));

		fileGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_85, Messages.WorkflowPaletteRoot_86, new NodeFactory(PutSSHActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.SSH_16), null));

		fileGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_87, Messages.WorkflowPaletteRoot_88, new NodeFactory(DeleteFileActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.DELETEFILE_16), null));

		fileGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_89, Messages.WorkflowPaletteRoot_90, new NodeFactory(DeleteFolderActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.DELETEFOLDER_16), null));
		fileGroup.add(new CreationToolEntry(Messages.WorkflowPaletteRoot_97, Messages.WorkflowPaletteRoot_98, new NodeFactory(EncryptFileActivity.class), Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.ENCRYPT_16), null));

		this.setDefaultEntry(selectionToolEntry);

	}

}

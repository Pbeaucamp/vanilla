package bpm.workflow.ui.gef.factories;

import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.dialogs.Dialog;

import bpm.workflow.runtime.model.WorkflowObject;
import bpm.workflow.runtime.model.activities.AirActivity;
import bpm.workflow.runtime.model.activities.BiWorkFlowActivity;
import bpm.workflow.runtime.model.activities.CalculationActivity;
import bpm.workflow.runtime.model.activities.CancelActivity;
import bpm.workflow.runtime.model.activities.CheckColumnActivity;
import bpm.workflow.runtime.model.activities.CheckPathActivity;
import bpm.workflow.runtime.model.activities.CheckTableActivity;
import bpm.workflow.runtime.model.activities.Comment;
import bpm.workflow.runtime.model.activities.CompterActivity;
import bpm.workflow.runtime.model.activities.ConpensationActivity;
import bpm.workflow.runtime.model.activities.DeleteFileActivity;
import bpm.workflow.runtime.model.activities.DeleteFolderActivity;
import bpm.workflow.runtime.model.activities.DummyActivity;
import bpm.workflow.runtime.model.activities.ErrorActivity;
import bpm.workflow.runtime.model.activities.ExcelAggregateActivity;
import bpm.workflow.runtime.model.activities.GetMailServActivity;
import bpm.workflow.runtime.model.activities.InterfaceActivity;
import bpm.workflow.runtime.model.activities.InterfaceGoogleActivity;
import bpm.workflow.runtime.model.activities.KPIActivity;
import bpm.workflow.runtime.model.activities.KettleActivity;
import bpm.workflow.runtime.model.activities.LinkActivity;
import bpm.workflow.runtime.model.activities.LoopGlobale;
import bpm.workflow.runtime.model.activities.MacroProcess;
import bpm.workflow.runtime.model.activities.MailActivity;
import bpm.workflow.runtime.model.activities.PingHostActivity;
import bpm.workflow.runtime.model.activities.PutMailServActivity;
import bpm.workflow.runtime.model.activities.SignalActivity;
import bpm.workflow.runtime.model.activities.SqlActivity;
import bpm.workflow.runtime.model.activities.StartActivity;
import bpm.workflow.runtime.model.activities.StarterActivity;
import bpm.workflow.runtime.model.activities.StopActivity;
import bpm.workflow.runtime.model.activities.TalendActivity;
import bpm.workflow.runtime.model.activities.TimerActivity;
import bpm.workflow.runtime.model.activities.XorActivity;
import bpm.workflow.runtime.model.activities.filemanagement.BrowseContentActivity;
import bpm.workflow.runtime.model.activities.filemanagement.ConcatExcelActivity;
import bpm.workflow.runtime.model.activities.filemanagement.ConcatPDFActivity;
import bpm.workflow.runtime.model.activities.filemanagement.GetFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.GetHardDActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutHardDActivity;
import bpm.workflow.runtime.model.activities.reporting.BurstActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.model.activities.vanilla.GatewayActivity;
import bpm.workflow.runtime.model.activities.vanilla.GedActivity;
import bpm.workflow.runtime.model.activities.vanilla.MetadataToD4CActivity;
import bpm.workflow.runtime.model.activities.vanilla.ResetGedIndexActivity;
import bpm.workflow.runtime.model.activities.vanilla.ResetUolapCacheActivity;
import bpm.workflow.runtime.model.activities.vanilla.StartStopItemActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogMacroProcess;
import bpm.workflow.ui.gef.model.LoopModel;
import bpm.workflow.ui.gef.model.MacroProcessModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.model.NodeException;

public class NodeFactory implements CreationFactory {

	private Class<?> template;
	private int type = -1;

	/**
	 * the class is the API class for this Node
	 * 
	 * @param biwModel
	 */

	public NodeFactory(Class<?> biwModel) {
		template = biwModel;
	}

	public NodeFactory(Class<?> biwModel, int _type) {
		template = biwModel;
		type = _type;
	}

	public Object getNewObject() {
		if(template == null) {
			return null;
		}

		try {
			if(template == MailActivity.class) {
				return createMailActivity();
			}
			else if(template == BiWorkFlowActivity.class) {
				return createBIWActivity();
			}
			else if(template == StartActivity.class) {
				return createStartActivity();
			}
			else if(template == CheckPathActivity.class) {
				return createCheckPathActivity();
			}
			else if(template == KPIActivity.class) {
				return createKPIActivity();
			}
			else if(template == StopActivity.class) {
				return createStopActivity();
			}
			else if(template == BurstActivity.class) {
				return createBurstActivity();
			}
			else if(template == SqlActivity.class) {
				return createSqlActivity();
			}
			else if(template == ReportActivity.class) {
				return createReportActivity();
			}
			else if(template == Comment.class) {
				return createComment();
			}
			else if(template == InterfaceActivity.class) {
				if(type == 0) {
					return createInterfaceActivity();
				}
				else {
					return createInterfaceFDActivity();
				}

			}
			else if(template == XorActivity.class) {
				return createXorActivity();
			}
			else if(template == GatewayActivity.class) {
				return createGatewayActivity();
			}
			else if(template == MetadataToD4CActivity.class) {
				return createMetadataToD4CActivity();
			}
			else if(template == KettleActivity.class) {
				return createKettleActivity();
			}
			else if(template == TalendActivity.class) {
				return createTalendActivity();
			}
			else if(template == DummyActivity.class) {
				return createDummyActivity();
			}
			else if(template == StarterActivity.class) {
				return createStarterEXEActivity();
			}
			else if(template == TimerActivity.class) {
				return createTimerActivity();
			}
			else if(template == CompterActivity.class) {
				return createCompterActivity();
			}
			else if(template == GetFTPActivity.class) {
				return createGetFTPActivity();
			}
			else if(template == GetHardDActivity.class) {
				return createGetHardDActivity();
			}
			else if(template == GetMailServActivity.class) {
				return createGetMailServActivity();
			}
			else if(template == InterfaceGoogleActivity.class) {
				return createInterfaceGoogleActivity();
			}
			else if(template == PutFTPActivity.class) {
				return createPutFTPActivity();
			}
			else if(template == PutHardDActivity.class) {
				return createPutHardDActivity();
			}
			else if(template == PutMailServActivity.class) {
				return createPutMailServActivity();
			}
			else if(template == ErrorActivity.class) {
				return createErrorActivity();
			}
			else if(template == CancelActivity.class) {
				return createCancelActivity();
			}
			else if(template == ConpensationActivity.class) {
				return createConpensationActivity();
			}
			else if(template == LinkActivity.class) {
				return createLinkActivity();
			}
			else if(template == SignalActivity.class) {
				return createSignalActivity();
			}
			else if(template == LoopGlobale.class) {
				return createLoopActivity();
			}
			else if(template == MacroProcess.class) {
				return createMacroProcessActivity();
			}
			else if(template == ExcelAggregateActivity.class) {
				return createExcelAggregateActivity();
			}
			else if(template == CheckTableActivity.class) {
				return createCheckTableActivity();
			}
			else if(template == CheckColumnActivity.class) {
				return createCheckColumnActivity();
			}
			else if(template == ConcatExcelActivity.class) {
				return createConcatExcelActivity();
			}
			else if(template == ConcatPDFActivity.class) {
				return createConcatPDFActivity();
			}
			else if(template == CalculationActivity.class) {
				return createCalculationActivity();
			}
			else if(template == DeleteFileActivity.class) {
				return createDeleteFileActivity();
			}
			else if(template == DeleteFolderActivity.class) {
				return createDeleteFolderActivity();
			}
			else if(template == PingHostActivity.class) {
				return createPingHostActivity();
			}
			else if(template == GedActivity.class) {
				return createGedActivity();
			}
			else if(template == BrowseContentActivity.class) {
				return createBrowsContentActivity();
			}
			else if(template == StartStopItemActivity.class) {
				return createStartStopActivity();
			}
			else if(template == ResetGedIndexActivity.class) {
				return createResetGedIndexActivity();
			}
			else if(template == ResetUolapCacheActivity.class) {
				return createResetUolapCacheActivity();
			}
			else if(template == AirActivity.class) {
				return createAirActivity();
			}
			else {
				try {
					Node n = new Node();
					WorkflowObject a = (WorkflowObject) template.newInstance();

					n.setWorkflowObject(a);
					n.setName("New" + template.getSimpleName().substring(0, template.getSimpleName().indexOf("Activity"))); //$NON-NLS-1$ //$NON-NLS-2$
					return n;
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	private Object createTalendActivity() throws NodeException {
		Node n = new Node();
		TalendActivity a = new TalendActivity("TalendActivity"); //$NON-NLS-1$
		n.setWorkflowObject(a);
		return n;
	}

	private Object createMetadataToD4CActivity() throws NodeException {
		Node n = new Node();
		MetadataToD4CActivity a = new MetadataToD4CActivity("MetadataToD4CActivity"); //$NON-NLS-1$
		n.setWorkflowObject(a);
		return n;
	}

	private Object createKettleActivity() throws NodeException {
		Node n = new Node();
		KettleActivity a = new KettleActivity("KettleActivity"); //$NON-NLS-1$
		n.setWorkflowObject(a);
		return n;
	}

	private Object createResetUolapCacheActivity() throws NodeException {
		Node n = new Node();
		ResetUolapCacheActivity a = new ResetUolapCacheActivity(Messages.NodeFactory_2);
		n.setWorkflowObject(a);
		return n;
	}

	private Object createResetGedIndexActivity() throws NodeException {
		Node n = new Node();
		ResetGedIndexActivity a = new ResetGedIndexActivity(Messages.NodeFactory_3);
		n.setWorkflowObject(a);
		return n;
	}

	private Object createStartStopActivity() throws NodeException {
		Node n = new Node();
		StartStopItemActivity a = new StartStopItemActivity(Messages.NodeFactory_4);
		n.setWorkflowObject(a);
		return n;
	}

	private Object createBrowsContentActivity() throws NodeException {
		Node n = new Node();
		BrowseContentActivity a = new BrowseContentActivity(Messages.NodeFactory_5);
		n.setWorkflowObject(a);
		return n;
	}

	private Object createGedActivity() throws NodeException {
		Node n = new Node();
		GedActivity o = new GedActivity(Messages.NodeFactory_6);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createInterfaceFDActivity() throws NodeException {
		Node n = new Node();
		InterfaceActivity o = new InterfaceActivity(Messages.NodeFactory_7);
		o.setType(InterfaceActivity.FD_TYPE);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createMacroProcessActivity() throws NodeException {
		DialogMacroProcess dial = new DialogMacroProcess(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
		if(dial.open() == Dialog.OK) {
			MacroProcessModel m = new MacroProcessModel(Activator.getDefault().getCurrentInput().getWorkflowModel());
			MacroProcess t = new MacroProcess();
			t.setType(dial.getType());

			m.setWorkflowObject(t);
			m.setName(dial.getType());
			((Node) m).setName(dial.getType());
			t.setName(m.getName());
			return m;
		}
		return new Object();
	}

	private Object createPingHostActivity() throws NodeException {
		Node n = new Node();
		PingHostActivity o = new PingHostActivity(Messages.NodeFactory_8);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createDeleteFolderActivity() throws NodeException {
		Node n = new Node();
		DeleteFolderActivity o = new DeleteFolderActivity(Messages.NodeFactory_9);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createDeleteFileActivity() throws NodeException {
		Node n = new Node();
		DeleteFileActivity o = new DeleteFileActivity(Messages.NodeFactory_10);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createCalculationActivity() throws NodeException {
		Node n = new Node();
		CalculationActivity o = new CalculationActivity(Messages.NodeFactory_11);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createConcatPDFActivity() throws NodeException {
		Node n = new Node();
		ConcatPDFActivity o = new ConcatPDFActivity(Messages.NodeFactory_12);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createConcatExcelActivity() throws NodeException {
		Node n = new Node();
		ConcatExcelActivity o = new ConcatExcelActivity(Messages.NodeFactory_13);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createCheckColumnActivity() throws NodeException {
		Node n = new Node();
		CheckColumnActivity o = new CheckColumnActivity(Messages.NodeFactory_14);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createCheckTableActivity() throws NodeException {
		Node n = new Node();
		CheckTableActivity o = new CheckTableActivity(Messages.NodeFactory_15);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createExcelAggregateActivity() throws NodeException {
		Node n = new Node();
		ExcelAggregateActivity o = new ExcelAggregateActivity(Messages.NodeFactory_16);
		n.setWorkflowObject(o);

		return n;
	}

	/**
	 * @return
	 */
	private Object createLoopActivity() throws NodeException {
		LoopModel m = new LoopModel(Activator.getDefault().getCurrentInput().getWorkflowModel());
		LoopGlobale t = new LoopGlobale();
		m.setWorkflowObject(t);
		m.setName(Messages.NodeFactory_17);
		((Node) m).setName(Messages.NodeFactory_18);
		t.setName(m.getName());

		return m;
	}

	private Object createSignalActivity() throws NodeException {
		Node n = new Node();
		SignalActivity o = new SignalActivity(Messages.NodeFactory_19);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createLinkActivity() throws NodeException {
		Node n = new Node();
		LinkActivity o = new LinkActivity();
		n.setWorkflowObject(o);
		n.setName(Messages.NodeFactory_20);
		return n;
	}

	private Object createConpensationActivity() throws NodeException {
		Node n = new Node();
		ConpensationActivity o = new ConpensationActivity(Messages.NodeFactory_21);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createCancelActivity() throws NodeException {
		Node n = new Node();
		CancelActivity o = new CancelActivity(Messages.NodeFactory_22);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createErrorActivity() throws NodeException {
		Node n = new Node();
		ErrorActivity o = new ErrorActivity(Messages.NodeFactory_23);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createPutMailServActivity() throws NodeException {
		Node n = new Node();
		PutMailServActivity o = new PutMailServActivity(Messages.NodeFactory_24);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createPutHardDActivity() throws NodeException {
		Node n = new Node();
		PutHardDActivity o = new PutHardDActivity(Messages.NodeFactory_25);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createPutFTPActivity() throws NodeException {
		Node n = new Node();
		PutFTPActivity o = new PutFTPActivity(Messages.NodeFactory_26);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createInterfaceGoogleActivity() throws NodeException {
		Node n = new Node();
		InterfaceGoogleActivity o = new InterfaceGoogleActivity(Messages.NodeFactory_27);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createGetMailServActivity() throws NodeException {
		Node n = new Node();
		GetMailServActivity o = new GetMailServActivity(Messages.NodeFactory_28);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createGetHardDActivity() throws NodeException {
		Node n = new Node();
		GetHardDActivity o = new GetHardDActivity(Messages.NodeFactory_29);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createGetFTPActivity() throws NodeException {
		Node n = new Node();
		GetFTPActivity o = new GetFTPActivity(Messages.NodeFactory_30);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createCompterActivity() throws NodeException {
		Node n = new Node();
		CompterActivity o = new CompterActivity(Messages.NodeFactory_31);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createTimerActivity() throws NodeException {
		Node n = new Node();
		TimerActivity o = new TimerActivity(Messages.NodeFactory_32);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createStarterEXEActivity() throws NodeException {
		Node n = new Node();
		StarterActivity o = new StarterActivity(Messages.NodeFactory_33);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createDummyActivity() throws NodeException {
		Node n = new Node();
		DummyActivity o = new DummyActivity();
		n.setWorkflowObject(o);
		n.setName(Messages.NodeFactory_34);
		return n;
	}

	private Object createCheckPathActivity() throws NodeException {
		Node n = new Node();
		CheckPathActivity o = new CheckPathActivity(Messages.NodeFactory_35);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createKPIActivity() throws NodeException {
		Node n = new Node();
		KPIActivity o = new KPIActivity(Messages.NodeFactory_36);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createBIWActivity() throws NodeException {
		Node n = new Node();
		BiWorkFlowActivity o = new BiWorkFlowActivity(Messages.NodeFactory_37);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createGatewayActivity() throws NodeException {
		Node n = new Node();
		GatewayActivity o = new GatewayActivity(Messages.NodeFactory_38);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createXorActivity() throws NodeException {
		Node n = new Node();
		XorActivity o = new XorActivity(Messages.NodeFactory_39);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createSqlActivity() throws NodeException {
		Node n = new Node();
		SqlActivity o = new SqlActivity(Messages.NodeFactory_40);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createBurstActivity() throws NodeException {
		Node n = new Node();
		WorkflowObject o = new BurstActivity(Messages.NodeFactory_41);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createInterfaceActivity() throws NodeException {
		Node n = new Node();
		InterfaceActivity o = new InterfaceActivity(Messages.NodeFactory_42);
		o.setType(InterfaceActivity.ORBEON_TYPE);
		n.setWorkflowObject(o);

		return n;
	}

	private Object createComment() throws NodeException {
		Node n = new Node();
		WorkflowObject c = new Comment();
		n.setWorkflowObject(c);
		n.setName(Messages.NodeFactory_43);

		return n;
	}

	private Object createReportActivity() throws NodeException {
		Node n = new Node();
		WorkflowObject a = new ReportActivity(Messages.NodeFactory_44);
		n.setWorkflowObject(a);

		return n;
	}

	private Node createStopActivity() throws NodeException {
		Node n = new Node();
		WorkflowObject a = new StopActivity();
		n.setWorkflowObject(a);
		n.setName(Messages.NodeFactory_45);

		return n;
	}

	private Node createStartActivity() throws NodeException {
		Node n = new Node();
		WorkflowObject a = new StartActivity();
		n.setWorkflowObject(a);
		n.setName(Messages.NodeFactory_46);
		return n;
	}

	private Node createMailActivity() throws NodeException {
		Node n = new Node();
		WorkflowObject activity = new MailActivity(Messages.NodeFactory_47);
		n.setWorkflowObject(activity);

		return n;
	}

	private Node createAirActivity() throws NodeException {
		Node n = new Node();
		WorkflowObject activity = new AirActivity(Messages.NodeFactory_48);
		n.setWorkflowObject(activity);
		return n;

	}
	
	public Object getObjectType() {
		return template;
	}
}

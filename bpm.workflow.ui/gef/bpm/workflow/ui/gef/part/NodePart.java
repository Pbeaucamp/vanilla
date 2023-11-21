package bpm.workflow.ui.gef.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.graphics.Image;

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
import bpm.workflow.runtime.model.activities.MailActivity;
import bpm.workflow.runtime.model.activities.PingHostActivity;
import bpm.workflow.runtime.model.activities.PutMailServActivity;
import bpm.workflow.runtime.model.activities.SignalActivity;
import bpm.workflow.runtime.model.activities.SqlActivity;
import bpm.workflow.runtime.model.activities.StartActivity;
import bpm.workflow.runtime.model.activities.StarterActivity;
import bpm.workflow.runtime.model.activities.StopActivity;
import bpm.workflow.runtime.model.activities.TalendActivity;
import bpm.workflow.runtime.model.activities.TaskListActivity;
import bpm.workflow.runtime.model.activities.TimerActivity;
import bpm.workflow.runtime.model.activities.XorActivity;
import bpm.workflow.runtime.model.activities.filemanagement.BrowseContentActivity;
import bpm.workflow.runtime.model.activities.filemanagement.ConcatExcelActivity;
import bpm.workflow.runtime.model.activities.filemanagement.ConcatPDFActivity;
import bpm.workflow.runtime.model.activities.filemanagement.EncryptFileActivity;
import bpm.workflow.runtime.model.activities.filemanagement.GetFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.GetHardDActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutHardDActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutSFTPActivity;
import bpm.workflow.runtime.model.activities.filemanagement.PutSSHActivity;
import bpm.workflow.runtime.model.activities.reporting.BurstActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.model.activities.vanilla.GatewayActivity;
import bpm.workflow.runtime.model.activities.vanilla.GedActivity;
import bpm.workflow.runtime.model.activities.vanilla.MetadataToD4CActivity;
import bpm.workflow.runtime.model.activities.vanilla.ResetGedIndexActivity;
import bpm.workflow.runtime.model.activities.vanilla.ResetUolapCacheActivity;
import bpm.workflow.runtime.model.activities.vanilla.StartStopItemActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.gef.commands.CopyCommand;
import bpm.workflow.ui.gef.commands.DeleteCommand;
import bpm.workflow.ui.gef.commands.LinkCommand;
import bpm.workflow.ui.gef.editpolicies.NodeEditPolicy;
import bpm.workflow.ui.gef.figure.FigureNode;
import bpm.workflow.ui.gef.figure.FigurePool;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.icons.IconsNames;

public class NodePart extends AbstractGraphicalEditPart implements PropertyChangeListener, NodeEditPart {
	private ConnectionAnchor anchor;
	private boolean isComment = false;
	

	public NodePart() {
		super();
		addEditPartListener(new EditPartListener() {

			@Override
			public void selectedStateChanged(EditPart editpart) {
				if (getSelected() == EditPart.SELECTED_PRIMARY) {
					try {
						LinkCommand cmd = Activator.getDefault().getActiveLinkCommand();
						if (cmd != null) {
							cmd.setTarget((Node) getModel());
							if (cmd.canExecute()) {
								cmd.execute();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Activator.getDefault().setActiveLinkCommand(null);
				}
			}

			@Override
			public void removingChild(EditPart child, int index) {
			}

			@Override
			public void partDeactivated(EditPart editpart) {
			}

			@Override
			public void partActivated(EditPart editpart) {
			}

			@Override
			public void childAdded(EditPart child, int index) {
			}
		});
	}

	@Override
	protected IFigure createFigure() {
		FigureNode figure = null;
		Image pict = null;
		isComment = false;

		WorkflowObject a = ((Node) getModel()).getWorkflowObject();

		if(a instanceof MailActivity) {
			if(((MailActivity) a).getTypeact().equalsIgnoreCase("debut")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.MAIL_START);
			}
			if(((MailActivity) a).getTypeact().equalsIgnoreCase("inter")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.MAILINTER_32);
			}
			if(((MailActivity) a).getTypeact().equalsIgnoreCase("interRe")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.MAILRECINTER_32);
			}
			if(((MailActivity) a).getTypeact().equalsIgnoreCase("fin")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.MAILFIN_32);
			}

		}
		else if(a instanceof KPIActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.METRIC_32);
		}
		else if(a instanceof ExcelAggregateActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.EXCELAGGREGATE_32);
		}
		else if(a instanceof BiWorkFlowActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.BIWORKFLOW);
		}
		else if(a instanceof CheckPathActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.CHECKPATH_32);
		}
		else if(a instanceof StartActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.START);
		}
		else if(a instanceof StopActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.END);
		}
		else if(a instanceof BurstActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.BURST);
		}
		else if(a instanceof SqlActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.SQL);
		}
		else if(a instanceof ReportActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.REPORT);
		}
		else if(a instanceof TaskListActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.TASKLIST_32);
		}
		else if(a instanceof GedActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.GED_32);
		}
		else if(a instanceof Comment) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.NOTE);
			isComment = true;
		}
		else if(a instanceof BrowseContentActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.BROWSE_32);
		}
		else if(a instanceof InterfaceActivity) {
			InterfaceActivity act = (InterfaceActivity) a;
			if(act.getType().equalsIgnoreCase("0")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.ORBEON32);
			}
			else {
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.FDFORM_32);
			}

		}
		else if(a instanceof XorActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.XOR);
		}
		else if(a instanceof GatewayActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.GATEWAY_32);
		}
		else if(a instanceof MetadataToD4CActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.D4C_32);
		}
		else if(a instanceof KettleActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.KETTLE_32);
		}
		else if(a instanceof TalendActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.TALEND_32);
		}
		else if(a instanceof DummyActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.DUMMY_32);
		}
		else if(a instanceof StarterActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.STARTER_32);
		}
		else if(a instanceof TimerActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.TIMER_32);
		}
		else if(a instanceof CompterActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.COMPTEUR_32);
		}
		else if(a instanceof GetHardDActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.GETHARDDISK_32);
		}
		else if(a instanceof GetFTPActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.GETFTP_32);
		}
		else if(a instanceof GetMailServActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.GETEMAIL_32);
		}
		else if(a instanceof PutSFTPActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.PUT_SFTP_32);
		}
		else if(a instanceof PutSSHActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.SSH_32);
		}
		else if(a instanceof PutFTPActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.PUTFTP_32);
		}
		else if(a instanceof PutHardDActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.PUTHARDDISK_32);
		}
		else if(a instanceof PutMailServActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.PUTEMAIL_32);
		}
		else if(a instanceof InterfaceGoogleActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.GOOGLEFORM_32);
		}

		else if(a instanceof CancelActivity) {

			if(((CancelActivity) a).getTypeact().equalsIgnoreCase("inter")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.CANCELIMAGE);
			}
			if(((CancelActivity) a).getTypeact().equalsIgnoreCase("interRe")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.CANCELREIMAGE);
			}
		}
		else if(a instanceof ErrorActivity) {
			if(((ErrorActivity) a).getTypeact().equalsIgnoreCase("inter")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.ERRORIMAGE);
			}
			if(((ErrorActivity) a).getTypeact().equalsIgnoreCase("interRe")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.ERRORREIMAGE);
			}
		}
		else if(a instanceof ConpensationActivity) {
			if(((ConpensationActivity) a).getTypeact().equalsIgnoreCase("inter")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.CONPENSREIMAGE);
			}
			if(((ConpensationActivity) a).getTypeact().equalsIgnoreCase("interRe")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.CONPENSIMAGE);
			}
		}
		else if(a instanceof LinkActivity) {
			if(((LinkActivity) a).getTypeact().equalsIgnoreCase("inter")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.LINKIMAGE);
			}
			if(((LinkActivity) a).getTypeact().equalsIgnoreCase("interRe")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.LINKREIMAGE);
			}
		}
		else if(a instanceof SignalActivity) {
			if(((SignalActivity) a).getTypeact().equalsIgnoreCase("inter")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.SIGNALIMAGE);
			}
			if(((SignalActivity) a).getTypeact().equalsIgnoreCase("interRe")) { //$NON-NLS-1$
				pict = Activator.getDefault().getImageRegistry().get(IconsNames.SIGNALREIMAGE);
			}
		}
		else if(a instanceof CheckColumnActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.CHECKCOL_32);

		}
		else if(a instanceof CheckTableActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.CHECKTABLE_32);

		}

		else if(a instanceof ConcatExcelActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.ADDTOEXCEL_32);

		}

		else if(a instanceof ConcatPDFActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.ADDTOPDF_32);

		}

		else if(a instanceof CalculationActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.CALCULATION_32);

		}

		else if(a instanceof DeleteFileActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.DELETEFILE_32);

		}

		else if(a instanceof DeleteFolderActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.DELETEFOLDER_32);

		}

		else if(a instanceof PingHostActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.PINGHOST_32);

		}

		else if(a instanceof StartStopItemActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.STARTSTOP_32);
		}

		else if(a instanceof ResetGedIndexActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.RESETGED_32);
		}

		else if(a instanceof ResetUolapCacheActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.RESETUOLAP_32);
		}
		else if(a instanceof AirActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.AIR_32);
		}

		else if(a instanceof EncryptFileActivity) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.SQL);
		}

		figure = new FigureNode(pict);

		setColor(figure, a);

		return figure;

	}

	private void setColor(FigureNode figure, WorkflowObject a) {
		if(a instanceof Comment) {
			switch(((Comment) a).getTypeInt()) {
				case Comment.SIMPLE:
					figure.setCommentColor(FigureNode.BLUE);
					break;

				case Comment.QUESTION:
					figure.setCommentColor(FigureNode.GREEN);
					break;

				case Comment.BLOCK:
					figure.setCommentColor(FigureNode.RED);
					break;

				case Comment.SUPPORT:
					figure.setCommentColor(FigureNode.MARRON);
					break;
				default:
					figure.setForegroundColor(ColorConstants.black);
					break;
			}

		}
		else {
			figure.setForegroundColor(ColorConstants.black);
		}

	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new NodeEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {

			protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
				if(!isComment) {
					LinkCommand cmd = (LinkCommand) request.getStartCommand();
					cmd.setTarget((Node) getHost().getModel());
					return cmd;
				}
				else {
					return null;
				}
			}

			protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
				if(!isComment) {
					Node source = (Node) getHost().getModel();
					LinkCommand cmd = new LinkCommand(source);
					request.setStartCommand(cmd);
					return cmd;
				}
				else {
					return null;
				}
			}

			protected Command getReconnectSourceCommand(ReconnectRequest request) {
				// Link conn = (Link) request.getConnectionEditPart().getModel();
				// Node newSource = (Node) getHost().getModel();
				// ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
				// cmd.setNewSource(newSource);
				// return cmd;
				return null;
			}

			protected Command getReconnectTargetCommand(ReconnectRequest request) {
				// Link conn = (Link) request.getConnectionEditPart().getModel();
				// Node newTarget = (Node) getHost().getModel();
				// ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
				// cmd.setNewTarget(newTarget);
				// return cmd;
				return null;
			}

		});
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				DeleteCommand command = new DeleteCommand();
				command.setModel(getHost().getModel());
				// System.out.println(((LoopModel)getHost().getParent().getModel()).getName());
				// if(getHost().getParent().getModel() instanceof LoopModel){
				// command.setParentModel((LoopModel)getHost().getParent().getModel());
				// }
				// else{
				command.setParentModel(getHost().getParent().getModel());
				// }
				return command;
			}

			@Override
			public Command getCommand(Request request) {
				if(request.getType() == ComponentEditPolicy.REQ_CLONE) {
					CopyCommand command = new CopyCommand();
					command.setModel((Node) getHost().getModel());

					return command;
				}

				return super.getCommand(request);
			}
		});

	}

	@Override
	protected void refreshVisuals() {
		Node model = (Node) getModel();
		FigureNode figure = (FigureNode) getFigure();
		figure.setName(model.getName());

		if(getParent() instanceof PoolEditPart) {
			Rectangle r = model.getLayout();
			Rectangle bounds = ((FigurePool) ((PoolEditPart) getParent()).getFigure()).getBounds();
			figure.setLayout(new Rectangle(r.x, bounds.y + r.height / 2, r.width, r.height));
		}
		else {
			figure.setLayout(model.getLayout());
		}
		setColor(figure, model.getWorkflowObject());

	}

	public void changeGraph(String namefigure) {
		Node model = (Node) getModel();
		FigureNode figure = (FigureNode) getFigure();

		if(namefigure.equalsIgnoreCase("start")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.MAIL_START));
		}
		if(namefigure.equalsIgnoreCase("inter")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.MAILINTER_32));
		}
		if(namefigure.equalsIgnoreCase("ReInter")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.MAILRECINTER_32));
		}
		if(namefigure.equalsIgnoreCase("fin")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.MAILFIN_32));
		}
		if(namefigure.equalsIgnoreCase("cancel")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.CANCELIMAGE));
		}
		if(namefigure.equalsIgnoreCase("cancelre")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.CANCELREIMAGE));
		}
		if(namefigure.equalsIgnoreCase("conpens")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.CONPENSREIMAGE));
		}
		if(namefigure.equalsIgnoreCase("conpensre")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.CONPENSIMAGE));
		}
		if(namefigure.equalsIgnoreCase("error")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ERRORIMAGE));
		}
		if(namefigure.equalsIgnoreCase("errorre")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ERRORREIMAGE));
		}
		if(namefigure.equalsIgnoreCase("link")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.LINKIMAGE));
		}
		if(namefigure.equalsIgnoreCase("linkre")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.LINKREIMAGE));
		}
		if(namefigure.equalsIgnoreCase("signal")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.SIGNALIMAGE));
		}
		if(namefigure.equalsIgnoreCase("signalre")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.SIGNALREIMAGE));
		}
		if(namefigure.equalsIgnoreCase("timerstart")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.TIMER_32));
		}
		if(namefigure.equalsIgnoreCase("timerinter")) { //$NON-NLS-1$
			figure.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.TIMERINTER_32));
		}
		figure.setName(model.getName());
		figure.setLayout(model.getLayout());

		setColor(figure, model.getWorkflowObject());

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)) {
			refreshVisuals();
		}

		if(evt.getPropertyName().equals(Node.PROPERTY_RENAME)) {
			refreshVisuals();
		}
		if(evt.getPropertyName().equals(Node.PROPERTY_CHANGE_TYPE)) {
			refreshVisuals();
		}
		if(evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)) {
			refreshSourceConnections();
		}
		if(evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP)) {
			refreshTargetConnections();
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		((Node) getModel()).addPropertyChangeListener(this);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		((Node) getModel()).removePropertyChangeListener(this);
	}

	protected ConnectionAnchor getConnectionAnchor() {
		if(anchor == null) {
			;
			anchor = new ChopboxAnchor(((FigureNode) getFigure()).getFigure());
		}
		return anchor;
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	@Override
	protected List getModelSourceConnections() {
		return ((Node) getModel()).getSourceLink();
	}

	@Override
	protected List getModelTargetConnections() {
		return ((Node) getModel()).getTargetLink();
	}

}

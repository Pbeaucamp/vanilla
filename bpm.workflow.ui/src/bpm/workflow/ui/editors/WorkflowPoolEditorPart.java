package bpm.workflow.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.ui.actions.ActionSave;
import bpm.workflow.ui.gef.model.ContainerPanelPool;
import bpm.workflow.ui.gef.part.NodeEditPartFactory;

/**
 * The pool part of the multi-editor
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class WorkflowPoolEditorPart extends GraphicalEditorWithPalette implements ITabbedPropertySheetPageContributor {
	public static final String ID = "bpm.workflow.ui.editors.workflowpooleditorpart"; //$NON-NLS-1$

	private ScrolledComposite sc;
	private WorkflowMultiEditorPart parent;

	public WorkflowPoolEditorPart(WorkflowMultiEditorPart parent) {
		setEditDomain(new DefaultEditDomain(this));
		this.parent = parent;
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		this.setPartName(input.getName());

	}

	@Override
	protected PaletteRoot getPaletteRoot() {

		return null;
	}

	@Override
	protected void initializeGraphicalViewer() {
		ContainerPanelPool root = new ContainerPanelPool(((WorkflowEditorInput) getEditorInput()));

		getGraphicalViewer().setRootEditPart(new ScalableRootEditPart());
		getGraphicalViewer().setContents(root);
		getSite().setSelectionProvider(getGraphicalViewer());

	}

	public void refresh() {
		initializeGraphicalViewer();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		ActionSave a = new ActionSave();
		a.run();

		if(a.isCancelled()) {
			monitor.setCanceled(true);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().setEditPartFactory(new NodeEditPartFactory());

		WorkflowContextMenuProvider provider = new WorkflowContextMenuProvider(getGraphicalViewer(), parent.getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
	}

	public String getContributorId() {
		return ID;
	}

	@Override
	// to provide the propertySheet associated to that Part
	public Object getAdapter(Class adapter) {
		if(adapter == IPropertySheetPage.class) {
			return new TabbedPropertySheetPage(this);
		}
		return super.getAdapter(adapter);
	}

	@Override
	protected void createGraphicalViewer(Composite parent) {
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setAlwaysShowScrollBars(false);
		sc.setLayoutData(new GridData(GridData.FILL_BOTH));

		super.createGraphicalViewer(sc);

		sc.setContent(getGraphicalViewer().getControl());
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		sc.setMinWidth(Display.getDefault().getPrimaryMonitor().getBounds().width);
		sc.setMinHeight(Display.getDefault().getPrimaryMonitor().getBounds().height);
		sc.setMinSize(getGraphicalViewer().getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT));
		getGraphicalViewer().getControl().setSize(getGraphicalViewer().getControl().computeSize(SWT.MAX, SWT.MAX));
	}

}

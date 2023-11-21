package bpm.fd.design.ui.editor.part;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EventObject;

import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.editor.editparts.FdDesignEditPartFactory;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.fd.design.ui.editors.FdProjectEditorInput;
import bpm.fd.design.ui.editors.IEditDomainProvider;
import bpm.fd.design.ui.editors.StructureEditorContextMenuProvider;

public class LayoutEditor extends GraphicalEditorWithPalette implements IEditDomainProvider {
	public static final String ID = "bpm.fd.design.ui.editor.part.LayoutEditor"; //$NON-NLS-1$

	private FdEditor parent;

	public LayoutEditor(FdEditor parent) {
		setEditDomain(new DefaultEditDomain(this));
		this.parent = parent;
	}

	public EditDomain getDomain() {
		return super.getEditDomain();
	}

	@Override
	public void setPaletteViewer(PaletteViewer paletteViewer) {
		super.setPaletteViewer(paletteViewer);
	}

	@Override
	protected void createPaletteViewer(Composite parent) {

	}

	@Override
	protected void createGraphicalViewer(Composite parent) {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
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

		GraphicalViewerKeyHandler keyHandler = new GraphicalViewerKeyHandler(getGraphicalViewer());
		getGraphicalViewer().setKeyHandler(keyHandler);
		keyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0), getActionRegistry().getAction(ActionFactory.DELETE.getId()));

	}

	@Override
	public void createPartControl(Composite parent) {
		Composite splitter = new Composite(parent, SWT.NONE);
		splitter.setLayoutData(new GridData(GridData.FILL_BOTH));
		splitter.setLayout(new GridLayout());
		createGraphicalViewer(splitter);
	}

	@Override
	protected void initializeGraphicalViewer() {
		StructureEditorContextMenuProvider provider = new StructureEditorContextMenuProvider(getGraphicalViewer(), (ActionRegistry) parent.getAdapter(ActionRegistry.class));
		getGraphicalViewer().setContextMenu(provider);
		ScalableFreeformRootEditPart rootEditPart = new ScalableFreeformRootEditPart();

		// install zoom
		double[] zoomLevels = new double[] { 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 10.0, 20.0 };
		ArrayList<String> zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);

		ZoomManager manager = rootEditPart.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));
		manager.setZoomLevels(zoomLevels);

		zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);

		getGraphicalViewer().setRootEditPart(rootEditPart);

		FdProjectEditorInput ei = (FdProjectEditorInput) getEditorInput();
		getGraphicalViewer().setContents(ei.getModel());
	}

	public ZoomManager getZoomManager() {
		return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().setEditPartFactory(new FdDesignEditPartFactory());
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		FdProject fdProject = Activator.getDefault().getProject();
		IProject p = root.getProject(fdProject.getProjectDescriptor().getProjectName());

		monitor.beginTask(Messages.StructureEditor_1, IProgressMonitor.UNKNOWN);
		monitor.subTask(Messages.StructureEditor_2);
		org.dom4j.Document doc = null;

		monitor.beginTask(Messages.StructureEditor_11, IProgressMonitor.UNKNOWN);
		monitor.subTask(Messages.StructureEditor_12);
		FdModel d = null;

		try {

			d = ((FdProjectEditorInput) getEditorInput()).getModel();

			doc = DocumentHelper.createDocument(d.getElement());
		} catch(Exception e) {
			e.printStackTrace();
			monitor.setCanceled(true);
			ErrorDialog.openError(getSite().getShell(), Messages.StructureEditor_13, Messages.StructureEditor_14, new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.StructureEditor_15, e));
			return;
		}

		monitor.subTask(Messages.StructureEditor_16);

		try {
			XMLWriter writer = new XMLWriter(new FileOutputStream(Platform.getLocation().append(p.getFile(d.getName() + Messages.StructureEditor_17).getFullPath()).toOSString()), OutputFormat.createPrettyPrint());
			writer.write(doc);
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
			monitor.setCanceled(true);
			ErrorDialog.openError(getSite().getShell(), Messages.StructureEditor_18, Messages.StructureEditor_19, new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.StructureEditor_20, e));
			return;
		}

		getCommandStack().flush();

		firePropertyChange(PROP_DIRTY);

	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		if(getPaletteViewer() != null) {
			return getPaletteViewer().getPaletteRoot();
		}
		return null;
	}

	@Override
	public Object getAdapter(Class type) {
		if(type == ZoomManager.class) {
			return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		}
		return super.getAdapter(type);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util.EventObject)
	 */
	@Override
	public void commandStackChanged(EventObject event) {

		super.commandStackChanged(event);
		if(isDirty()) {
			firePropertyChange(PROP_DIRTY);
		}
	}

}

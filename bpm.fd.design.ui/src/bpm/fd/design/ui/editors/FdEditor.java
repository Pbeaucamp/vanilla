package bpm.fd.design.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.wst.jsdt.ui.JavaScriptUI;
import org.eclipse.wst.jsdt.ui.text.JavaScriptSourceViewerConfiguration;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileJavaScript;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.test.actions.RunJsp;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.editor.part.LayoutEditor;
import bpm.fd.design.ui.editor.part.palette.ViewPalette;

public class FdEditor extends MultiPageEditorPart implements PropertyChangeListener {
	private static final Font JAVASCRIPT_FONT = new Font(Display.getDefault(), "Courier New", 10, SWT.NORMAL); //$NON-NLS-1$
	public static final String ID = "bpm.fd.design.ui.editors.MultipleEditor"; //$NON-NLS-1$

	private int structureEditorIndex;
	private int previewIndex;

	private java.util.List<IEditDomainProvider> structureEditor = new ArrayList<IEditDomainProvider>();

	private ActionRegistry actionRegistry;
	private HashMap<Integer, TextEditor> cssEditor = new HashMap<Integer, TextEditor>();
	private HashMap<Integer, IResource> textEditors = new HashMap<Integer, IResource>();
	private int lastSelectedPageIndex = 0;
	private Browser preview;
	private Composite previewContainer;
	private Text previewLabel;

	private class DirtyStateListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			projectDirty = true;
			firePropertyChange(PROP_DIRTY);

		}

	}

	private DirtyStateListener modificationListener = new DirtyStateListener();
	private boolean projectDirty = false;

	public FdEditor() {

	}

	private void createModelPage(FdModel model) throws Exception {
		FdProjectEditorInput input = (FdProjectEditorInput) getEditorInput();
		IEditDomainProvider peditor = null;
		if(FdProjectDescriptor.API_DESIGN_VERSION.equals(input.getModel().getProject().getProjectDescriptor().getInternalApiDesignVersion())) {
			peditor = new LayoutEditor(this);
		}
		else {
			peditor = new StructureEditor(this);
		}
		addPage(structureEditor.size(), peditor, new FdProjectEditorInput(model));
		structureEditor.add(peditor);
		structureEditorIndex = structureEditor.size() - 1;
		setPageText(structureEditorIndex, "Page " + model.getName()); //$NON-NLS-1$
		previewIndex++;

	}

	private void createResourcePage(IResource r) throws Exception {
		if(r instanceof FileCSS) {
			StructuredTextEditor part = null;

			if(r instanceof FileCSS) {
				part = createCssEditor("css"); //$NON-NLS-1$
			}
			else if(r instanceof FileProperties) {
				part = createCssEditor("Java Properties File"); //$NON-NLS-1$
			}
			else if(r instanceof FileJavaScript) {
				part = createCssEditor("js"); //$NON-NLS-1$
			}
			else {
				part = createCssEditor("text"); //$NON-NLS-1$
			}

			addPage(structureEditor.size() + cssEditor.size(), part, loadResourceFile(r));
			cssEditor.put(cssEditor.size(), part);
			setPageText(structureEditor.size() + cssEditor.size() - 1, r.getSmallNameType() + " - " + r.getName()); //$NON-NLS-1$
			previewIndex++;
		}
		else if(r instanceof FileJavaScript) {

			SourceViewer sourceViewer = new SourceViewer(getContainer(), new VerticalRuler(10), SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			sourceViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
			sourceViewer.configure(new JavaScriptSourceViewerConfiguration(JavaScriptUI.getColorManager(), Activator.getDefault().getPreferenceStore(), null, null));
			sourceViewer.setDocument(new Document());
			sourceViewer.getDocument().set(IOUtils.toString(new FileInputStream(r.getFile()), "UTF-8")); //$NON-NLS-1$
			sourceViewer.addTextListener(new ITextListener() {

				public void textChanged(TextEvent event) {
					Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_COMPONENT_CHANGED, null, this);

				}
			});

			sourceViewer.getTextWidget().setFont(JAVASCRIPT_FONT);
			int o = addPage(sourceViewer.getControl());
			textEditors.put(o, r);
			setPageText(o, r.getSmallNameType() + " - " + r.getName()); //$NON-NLS-1$
			previewIndex++;
		}
	}

	private void removeModelPage(FdModel model) {
		for(IEditDomainProvider p : structureEditor) {
			if(((FdProjectEditorInput) p.getEditorInput()).getModel() == model) {
				int i = structureEditor.indexOf(p);

				removePage(i);
				structureEditor.remove(p);
				previewIndex--;
				break;
			}
		}

	}

	@Override
	protected void createPages() {

		getContainer().addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				if(lastSelectedPageIndex == getActivePage()) {
					return;
				}

				if(getActivePage() == previewIndex) {

					getEditorSite().getActionBarContributor().setActiveEditor(null);
					RunJsp r = new RunJsp();
					r.setActiveEditor(null, FdEditor.this);
					r.run(null);
					Browser.clearSessions();
					previewLabel.setText(r.getUrl());
					preview.setUrl(r.getUrl());

				}
				else if(getActiveEditor() instanceof IEditDomainProvider) {
					getEditorSite().getActionBarContributor().setActiveEditor(getActiveEditor());
					for(IViewReference r : Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences()) {
						// refresh the palette
						if(r.getId().equals(ViewPalette.ID)) {
							try {
								((ViewPalette) r.getView(false)).activateForInput((IEditDomainProvider) getActiveEditor(), (FdProjectEditorInput) getActiveEditor().getEditorInput());

							} catch(Exception ex) {
								// ex.printStackTrace();
							}

						}

					}

				}
				lastSelectedPageIndex = getActivePage();
			}

		});

		try {
			FdProjectEditorInput input = (FdProjectEditorInput) getEditorInput();
			createModelPage(input.getModel().getProject().getFdModel());

			this.setPartName(getEditorInput().getName());

			if(input.getModel().getProject() instanceof MultiPageFdProject) {
				for(FdModel m : ((MultiPageFdProject) input.getModel().getProject()).getPagesModels()) {
					createModelPage(m);
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			IEditDomainProvider part = this.getStructureEditor();

			if(part != null) {
				FdProjectEditorInput input = (FdProjectEditorInput) part.getEditorInput();
				((ViewPalette)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewPalette.ID)).activateForInput(part, input);
			}
		} catch(Exception e) {
		}

		try {

			for(IResource r : ((FdProjectEditorInput) getEditorInput()).getModel().getProject().getResources()) {
				createResourcePage(r);
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		previewContainer = new Composite(getContainer(), SWT.NONE);
		previewContainer.setLayout(new GridLayout(3, false));

		final Button l = new Button(previewContainer, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setEnabled(true);
		l.setText(Messages.FdEditor_11);
		l.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				preview.refresh();
				previewLabel.setText(preview.getUrl());
			}
		});

		Button b = new Button(previewContainer, SWT.PUSH);
		b.setLayoutData(new GridData());
		b.setText("OS Browser");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(preview.getUrl()));
				} catch(Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), "Operating System Browser", "Unable to open OS WebBrowser : " + e1.getMessage());
				}
			}
		});

		previewLabel = new Text(previewContainer, SWT.NONE);
		previewLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		try {
			preview = new Browser(previewContainer, SWT.WEBKIT);
			preview.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));

			ProgressListener lst = new ProgressListener() {

				public void completed(ProgressEvent event) {
					l.setEnabled(true);

				}

				public void changed(ProgressEvent event) {

				}
			};
			preview.addProgressListener(lst);

//			initXulRunner();

			try {
				preview.setJavascriptEnabled(true);
			} catch(Exception ex) {

			}

			previewIndex = addPage(previewContainer);
		} catch(Throwable ex) {
			if(preview != null && !preview.isDisposed()) {
				preview.dispose();
			}
			preview = new Browser(previewContainer, SWT.NONE);
			preview.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
			previewIndex = addPage(previewContainer);
		}

		setPageText(previewIndex, "Preview"); //$NON-NLS-1$

	}

	private static boolean xulRunnerInited = false;

//	private static void initXulRunner() {
//		if(xulRunnerInited) {
//			return;
//		}
//
//		// start JavaXPCOM section
//		Mozilla mozilla = Mozilla.getInstance();
//		GREVersionRange[] range = new GREVersionRange[1];
//		range[0] = new GREVersionRange("1.8.0", true, "2.0", false); //$NON-NLS-1$ //$NON-NLS-2$
//		// work with trunk nightly version 1.9a1 ^^
//
//		try {
//			File grePath = Mozilla.getGREPathWithProperties(range, null);
//			mozilla.initialize(grePath);
//			LocationProvider locProvider = new LocationProvider(grePath);
//			mozilla.initEmbedding(grePath, grePath, locProvider);
//		} catch(FileNotFoundException e) {
//			// this exception is thrown if greGREPathWithProperties cannot find a GRE
////			e.printStackTrace();
//		} catch(XPCOMException e) {
//			// this exception is thrown if initEmbedding failed
////			e.printStackTrace();
//		} catch(Throwable t) {
////			t.printStackTrace();
//		}
//
//		/* nsIIOService */nsISupports ioService = /* (nsIIOService) */Mozilla.getInstance().getServiceManager().getServiceByContractID("@mozilla.org/network/io-service;1", nsIIOService2.NS_IIOSERVICE_IID);//nsIIOService.NS_IIOSERVICE_IID); //$NON-NLS-1$
//		nsIIOService2 ioService2 = (nsIIOService2) ioService.queryInterface(nsIIOService2.NS_IIOSERVICE2_IID);// .NS_IIO SERVICE2_IID);
//		ioService2.setManageOfflineStatus(false);
//		ioService2.setOffline(false);
//		// end JavaXPCOM section
//		xulRunnerInited = true;
//	}

	private StructuredTextEditor createCssEditor(String extension) {

		StructuredTextEditor cssEditor = new StructuredTextEditor();
		cssEditor.setEditorPart(this);
		if(extension.equals("js")) { //$NON-NLS-1$

			cssEditor.initializeDocumentProvider(JavaScriptUI.getDocumentProvider());

		}
		return cssEditor;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		FdProject fdProject = Activator.getDefault().getProject();
		IProject p = root.getProject(fdProject.getProjectDescriptor().getProjectName());

		monitor.beginTask(Messages.StructureEditor_1, IProgressMonitor.UNKNOWN);
		monitor.subTask(Messages.StructureEditor_2);
		org.dom4j.Document doc = null;
		try {

			Dictionary d = Activator.getDefault().getProject().getDictionary();

			doc = DocumentHelper.createDocument(d.getElement());
		} catch(Exception e) {
			e.printStackTrace();
			monitor.setCanceled(true);
			ErrorDialog.openError(getSite().getShell(), Messages.StructureEditor_3, Messages.StructureEditor_4, new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.StructureEditor_5, e));
			return;
		}

		monitor.subTask(Messages.StructureEditor_6);

		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setTrimText(false);
			format.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter(new FileOutputStream(Platform.getLocation().append(p.getFile(fdProject.getProjectDescriptor().getDictionaryName() + ".dictionary").getFullPath()).toOSString()), format); //$NON-NLS-1$
			writer.write(doc);
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
			monitor.setCanceled(true);
			ErrorDialog.openError(getSite().getShell(), Messages.StructureEditor_8, Messages.StructureEditor_9, new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.StructureEditor_10, e));
			return;
		}
		monitor.subTask(Messages.StructureEditor_21);
		try {
			IFile f = p.getFile("components.properties"); //$NON-NLS-1$

			Properties lang = new Properties();
			for(IComponentDefinition def : Activator.getDefault().getProject().getDictionary().getComponents()) {
				for(IComponentOptions opt : def.getOptions()) {
					for(String key : opt.getInternationalizationKeys()) {
						String value = opt.getDefaultLabelValue(key);
						if(value != null) {
							lang.setProperty(def.getName() + "." + key, value); //$NON-NLS-1$
						}
						else {
							lang.setProperty(def.getName() + "." + key, ""); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}

			lang.store(new FileOutputStream(Platform.getLocation().toOSString() + f.getFullPath().toOSString()), Messages.StructureEditor_26);
		} catch(Exception e) {
			e.printStackTrace();
		}

		for(IEditDomainProvider part : structureEditor) {
			part.doSave(monitor);
		}
		for(IEditorPart pp : cssEditor.values()) {
			pp.doSave(monitor);
		}

		projectDirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	private IEditorInput loadResourceFile(IResource resource) {

		FdProjectEditorInput input = (FdProjectEditorInput) getEditorInput();
		IProject proj = null;
		for(IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if(p.isOpen() && p.getName().equals(input.getModel().getProject().getProjectDescriptor().getProjectName())) {
				proj = p;
				break;
			}
		}
		IFile f = proj.getFile(resource.getName());
		return new FileEditorInput(f);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#setActivePage(int)
	 */
	@Override
	protected void setActivePage(int pageIndex) {
		super.setActivePage(pageIndex);
		if(pageIndex < structureEditor.size()) {
			getEditorSite().getActionBarContributor().setActiveEditor(structureEditor.get(pageIndex));
		}

	}

	@Override
	public void doSaveAs() {
		structureEditor.get(0).doSaveAs();

	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected ActionRegistry getActionRegistry() {

		if(actionRegistry == null) {
			actionRegistry = new ActionRegistry();
		}
		return actionRegistry;
	}

	protected void updateActions(Iterator iterator) {

		while(iterator.hasNext()) {
			Object o = iterator.next();
			if(o instanceof UpdateAction) {
				try {
					((UpdateAction) o).update();
				} catch(Throwable ex) {

				}

			}

		}
	}

	protected void createActions() {

		IAction action = new DeleteAction((IWorkbenchPart) this);
		addAction(action);

		action = new UndoAction((IWorkbenchPart) this);
		addAction(action);

		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				try {
					updateActions(getActionRegistry().getActions());
				} catch(Throwable t) {}
			}
		});

	}

	protected void addAction(IAction action) {
		getActionRegistry().registerAction(action);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		createActions();
		this.setPartName(getEditorInput().getName());
		((FdProjectEditorInput) input).getModel().getProject().getDictionary().addPropertyChangeListener(modificationListener);
		((FdProjectEditorInput) input).getModel().addPropertyChangeListener(modificationListener);
		((FdProjectEditorInput) input).getModel().getProject().addPropertyChangeListener(this);	
	}

	@Override
	public void dispose() {
		if(getEditorInput() != null) {
			((FdProjectEditorInput) getEditorInput()).getModel().getProject().getDictionary().removePropertyChangeListener(modificationListener);
			((FdProjectEditorInput) getEditorInput()).getModel().getProject().removePropertyChangeListener(modificationListener);
			((FdProjectEditorInput) getEditorInput()).getModel().getProject().removePropertyChangeListener(this);
		}
		super.dispose();
	}

	public IEditDomainProvider getStructureEditor() {
		if(getActiveEditor() instanceof IEditDomainProvider) {
			return (IEditDomainProvider) getActiveEditor();
		}
		return null;
	}

	@Override
	protected IEditorPart getActiveEditor() {
		int i = getActivePage();
		if(i == -1) {
			return structureEditor.get(0);
		}
		if(i < structureEditor.size()) {
			return structureEditor.get(i);
		}
		for(Integer k : cssEditor.keySet()) {
			if(k.intValue() == i) {
				return cssEditor.get(k);
			}
		}

		return null;
	}

	private void removeResourcePage(IResource r) {
		IProject proj = Activator.getDefault().getResourceProject();

		for(Integer i : cssEditor.keySet()) {
			FileEditorInput in = (FileEditorInput) cssEditor.get(i).getEditorInput();
			if(in.getFile().equals(proj.getFile(r.getName()))) {
				cssEditor.get(i).close(false);
				removePage(structureEditor.size() - 1 + i);
				cssEditor.remove(i);
				previewIndex--;
				break;
			}
		}
		for(Integer i : textEditors.keySet()) {
			if(textEditors.get(i) == r) {
				removePage(i);
				textEditors.remove(i);
				previewIndex--;
				break;
			}
		}
	}

	@Override
	public Object getAdapter(Class adapter) {
		if(adapter == ActionRegistry.class) {
			return getActionRegistry();
		}
		return super.getAdapter(adapter);
	}

	public StructuredTextEditor getCssPage(FileCSS resource) {
		for(IEditorPart p : cssEditor.values()) {
			if(((FileEditorInput) p.getEditorInput()).getFile().getFullPath().toOSString().endsWith(resource.getName())) {
				return (StructuredTextEditor) p;
			}
		}
		return null;
	}

//	public static class LocationProvider implements IAppFileLocProvider {
//
//		private final File libXULPath;
//		int counter = 0;
//
//		public LocationProvider(File grePath) {
//			this.libXULPath = grePath;
//		}
//
//		public File getFile(String aProp, boolean[] aPersistent) {
//			File file = null;
//			if(aProp.equals("GreD") || aProp.equals("GreComsD")) { //$NON-NLS-1$ //$NON-NLS-2$
//				file = libXULPath;
//				if(aProp.equals("GreComsD")) { //$NON-NLS-1$
//					file = new File(file, "components"); //$NON-NLS-1$
//				}
//			}
//			else if(aProp.equals("MozBinD") || //$NON-NLS-1$
//					aProp.equals("CurProcD") || //$NON-NLS-1$
//					aProp.equals("ComsD") || //$NON-NLS-1$
//					aProp.equals("ProfD")) //$NON-NLS-1$
//			{
//				file = libXULPath;
//				if(aProp.equals("ComsD")) { //$NON-NLS-1$
//					file = new File(file, "components"); //$NON-NLS-1$
//				}
//			}
//			return file;
//		}
//
//		public File[] getFiles(String aProp) {
//			File[] files = null;
//			if(aProp.equals("APluginsDL")) { //$NON-NLS-1$
//				files = new File[1];
//				files[0] = new File(libXULPath, "plugins"); //$NON-NLS-1$
//			}
//			return files;
//		}
//
//	}

	@Override
	public boolean isDirty() {
		return super.isDirty() || projectDirty;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			if(evt.getPropertyName().equals(FdProject.PROPERTY_ADD_RESOURCE)) {
				createResourcePage((IResource) evt.getNewValue());
			}
			else if(evt.getPropertyName().equals(FdProject.PROPERTY_REMOVE_RESOURCE)) {
				removeResourcePage((IResource) evt.getOldValue());
			}
			else if(evt.getPropertyName().equals(MultiPageFdProject.PROPERTY_ADD_MODEL)) {
				createModelPage((FdModel) evt.getNewValue());
			}
			else if(evt.getPropertyName().equals(MultiPageFdProject.PROPERTY_REMOVE_MODEL)) {
				removeModelPage((FdModel) evt.getOldValue());
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(getSite().getShell(), "Opening Editor Page", "Failed to create a new page in th editor: " + ex.getMessage());
		}

	}

}

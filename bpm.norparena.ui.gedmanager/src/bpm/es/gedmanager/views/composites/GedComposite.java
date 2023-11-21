package bpm.es.gedmanager.views.composites;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import bpm.es.gedmanager.Activator;
import bpm.es.gedmanager.Messages;
import bpm.es.gedmanager.api.GedModel;
import bpm.es.gedmanager.dialogs.DialogCheckout;
import bpm.es.gedmanager.icons.IconsNames;
import bpm.es.gedmanager.wizards.ImportDocumentWizard;
import bpm.norparena.ui.menu.client.trees.TreeContentProvider;
import bpm.norparena.ui.menu.client.trees.TreeLabelProvider;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;

public class GedComposite {
	private static final Color COLOR_INDEXED = new Color(Display.getDefault(), 0, 0, 0);
	private static final Color COLOR_NO_INDEXED = new Color(Display.getDefault(), 250, 0, 0);

	private TreeViewer tree;
	private GedDocumentPropertiesComposite compositeProperties;
	
	private GedModel model;
	private User user;
	private int groupId;
	
	private boolean showOnlyNonIndexedDoc = false;
	
	public GedComposite(final Composite parent, GedModel gedModel) {
		this.model = gedModel;
		
		Composite buttonBar = new Composite(parent, SWT.NONE);
		buttonBar.setLayout(new GridLayout(4, false));
		buttonBar.setLayoutData(new GridData());
		
		Button buttonRefresh = new Button(buttonBar, SWT.PUSH);
		buttonRefresh.setText(Messages.GedDocumentsComposite_0);
		buttonRefresh.setLayoutData(new GridData());
		buttonRefresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					refreshInput();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		Button buttonImportDocument = new Button(buttonBar, SWT.PUSH);
		buttonImportDocument.setText("Import document");
		buttonImportDocument.setLayoutData(new GridData());
		buttonImportDocument.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ImportDocumentWizard wiz = new ImportDocumentWizard(model);
				WizardDialog dial = new WizardDialog(parent.getShell(), wiz);
				dial.open();
				
				try {
					refreshInput();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		Button bDel = new Button(buttonBar, SWT.PUSH);
		bDel.setText(Messages.GedDocumentsComposite_3);
		bDel.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.document_delete));
		bDel.setLayoutData(new GridData());
		bDel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					StructuredSelection ss = (StructuredSelection) tree.getSelection();
					Iterator<Object> it = ss.iterator();
					
					while (it.hasNext()) {
						Object item = it.next();
						if(item instanceof GedDocument){
							GedDocument def = (GedDocument)item;
							model.deleteDocument(def);
						}
					}
					
					refreshInput();
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							Messages.GedDocumentsComposite_4, "" + ex.getMessage()); //$NON-NLS-2$
				}
			}
		});
		
		Button btnResetIndex = new Button(buttonBar, SWT.PUSH);
		btnResetIndex.setText("Reset index");
		btnResetIndex.setLayoutData(new GridData());
		btnResetIndex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if(MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Reset Index", "Would you like to restore the index after deleting it?")){
						model.resetIndex(true);
					}
					else {
						model.resetIndex(false);
					}
					refreshInput();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							Messages.GedDocumentsComposite_4, "" + e1.getMessage()); //$NON-NLS-2$
				}
			}
		});
		

//		Composite filterBar = new Composite(parent, SWT.NONE);
//		filterBar.setLayout(new GridLayout(4, false));
//		filterBar.setLayoutData(new GridData());
//		
//		comboFilter = new Combo(filterBar, SWT.BORDER);
//		comboFilter.setLayoutData(new GridData());
//		definitions = new HashMap<String, Definition>();
//		try {
//			for (Definition fdef : model.getFieldDefinitions()) {
//				definitions.put(fdef.getName(), fdef);
//			}
//		} catch (Exception e2) {
//			e2.printStackTrace();
//		}
//		comboFilter.setItems(definitions.keySet().toArray(new String[0]));
//		
//		textFilter = new Text(filterBar, SWT.BORDER);
//		textFilter.setLayoutData(new GridData());
//		
//		buttonFilter = new Button(filterBar, SWT.PUSH);
//		buttonFilter.setLayoutData(new GridData());
//		buttonFilter.setText(Messages.GedDocumentsComposite_6);
//		buttonFilter.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//
//			}
//		});
//
//		clearFilter = new Button(filterBar, SWT.PUSH);
//		clearFilter.setLayoutData(new GridData());
//		clearFilter.setText(Messages.GedDocumentsComposite_5);
//		clearFilter.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//
//			}
//		});
		
		Button btnOnlyIndexDoc = new Button(parent, SWT.CHECK);
		btnOnlyIndexDoc.setText("Display only non indexed file");
		btnOnlyIndexDoc.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				showOnlyNonIndexedDoc = !showOnlyNonIndexedDoc;
				
				try {
					refreshInput();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		MenuManager menu = new MenuManager();
		menu.add(checkin);
		menu.add(checkout);
		menu.add(indexDocAction);
		
		tree = new TreeViewer(comp);
		tree.getControl().setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, true));
		tree.setLabelProvider(decoProvider);
		tree.setContentProvider(treeContentProvider);
		tree.getControl().setMenu(menu.createContextMenu(tree.getControl()));
		tree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = ((IStructuredSelection)event.getSelection()).getFirstElement();
				GedDocument doc = null;
				if(obj instanceof GedDocument) {
					doc = (GedDocument) obj;
				}
				else if(obj instanceof DocumentVersion) {
					doc = ((DocumentVersion)obj).getParent();
				}
				
				try {
					compositeProperties.initData(doc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		compositeProperties = new GedDocumentPropertiesComposite(comp,SWT.BORDER, model);
		compositeProperties.setLayout(new GridLayout(2,false));
		compositeProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		try {
			initTreeContent();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String userLogin = bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getVanillaContext().getLogin();
		try {
			user = bpm.norparena.ui.menu.Activator.getDefault().getRemote().getVanillaSecurityManager().getUserByLogin(userLogin);
			groupId = bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getGroup().getId();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void refreshInput() throws Exception {
		model.refresh();
		if(showOnlyNonIndexedDoc){
			if(model.getDocuments() != null){
				for(GedDocument doc : model.getDocuments()){
					if(doc.getDocumentVersions() != null){
						List<DocumentVersion> newDocVersions = new ArrayList<DocumentVersion>();
						for(DocumentVersion docVersion : doc.getDocumentVersions()){
							if(!docVersion.isIndexed()){
								newDocVersions.add(docVersion);
							}
						}
						doc.setDocumentVersions(newDocVersions);
					}
				}
			}
		}
		tree.setInput(model.getDocuments());
	}
	
	private void initTreeContent() throws Exception {
		tree.setInput(model.getDocuments());
	}
	
	private DecoratingLabelProvider decoProvider = new DecoratingLabelProvider(new LabelProvider(), 
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()){
		
		@Override
		public Color getForeground(Object element) {
			if (element instanceof DocumentVersion){
				if (((DocumentVersion) element).isIndexed()){
					return COLOR_INDEXED;
				}
				else {
					return COLOR_NO_INDEXED;
				}
			}
			return super.getForeground(element);
		}


		@Override
		public Font getFont(Object element) {
			return super.getFont(element);
		}

		
		@Override
		public String getText(Object obj) {
			if(obj instanceof GedDocument) {
				return ((GedDocument)obj).getName();
			}
			else if(obj instanceof DocumentVersion) {
				return ((GedDocument)((DocumentVersion)obj).getParent()).getName() + " - V" + ((DocumentVersion)obj).getVersion();
			}
			return null;
		}
		
		public Image getImage(Object obj) {
			if(obj instanceof GedDocument) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.field);
			}
			else if(obj instanceof DocumentVersion) {
				return findIconByFormat(((DocumentVersion)obj).getFormat());
			}
			return super.getImage(obj);
		};
	};

	private TreeLabelProvider treeLabelProvider = new TreeLabelProvider() {

		@Override
		public Image getImage(Object obj) {
			if(obj instanceof GedDocument) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.field);
			}
			else if(obj instanceof DocumentVersion) {
				return findIconByFormat(((DocumentVersion)obj).getFormat());
			}
			return super.getImage(obj);
		}

		@Override
		public String getText(Object obj) {
			if(obj instanceof GedDocument) {
				return ((GedDocument)obj).getName();
			}
			else if(obj instanceof DocumentVersion) {
				return ((GedDocument)((DocumentVersion)obj).getParent()).getName() + " - V" + ((DocumentVersion)obj).getVersion();
			}
			return null;
		}
	};
	
	private TreeContentProvider treeContentProvider = new TreeContentProvider() {
		@Override
		public Object[] getChildren(Object parent) {
			if(parent instanceof GedDocument) {
				return ((GedDocument)parent).getDocumentVersions().toArray();
			}
			return null;
		}
		@Override
		public Object[] getElements(Object parent) {
			return ((List<GedDocument>)parent).toArray();
		}
		@Override
		public Object getParent(Object child) {
			if(child instanceof DocumentVersion) {
				return ((DocumentVersion)child).getParent();
			}
			return null;
		}
		@Override
		public boolean hasChildren(Object parent) {
			if(parent instanceof GedDocument) {
				return true;
			}
			return false;
		}
		@Override
		public void dispose() { }
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) { }
		
	};

	private Image findIconByFormat(String format) {
		if(format != null && !format.equals("")) {
			if(format.contains("pdf")) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.document_pdf);
			}
			else if(format.contains("xls")) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.document_excel);
			}
			else if(format.contains("ods")) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.document_ods);
			}
			else if(format.contains("doc")) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.document_word);
			}
			else if(format.contains("odt")) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.document_odt);
			}
			else if(format.contains("zip") || format.contains("rar")) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.document_compressed);
			}
		}
		return Activator.getDefault().getImageRegistry().get(IconsNames.document);
	}
	
	private void checkin(GedDocument document){
		try {
			if(model.canCheckin(document.getId(), user.getId())){
				FileDialog dial = new FileDialog(Display.getDefault().getActiveShell());
				String filePath = dial.open();
				if(filePath != null && !filePath.isEmpty()){

					String format = "";
					try {
						format = filePath.substring(filePath.lastIndexOf("."));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					FileInputStream is = new FileInputStream(new File(filePath));
					
					model.checkin(document.getId(), user.getId(), format, is, true);
					
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Informations", "Your document has been successfully checkin.");
				}
			}
			else {
				MessageDialog.openError(Display.getDefault().getActiveShell(), 
						"Informations", "This document cannot be checkin, probably because it has not been checkout before.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
					"Error", "Error while trying to checkin the document - " + e.getMessage());
		}
	}
	
	private void checkout(GedDocument document) {
		try {
			if(model.canCheckout(document.getId())){
				DialogCheckout dial = new DialogCheckout(Display.getDefault().getActiveShell(), model, document, user.getId(), groupId);
				dial.open();
			}
			else {
				MessageDialog.openError(Display.getDefault().getActiveShell(), 
						"Informations", "This document cannot be checkout, probably because it is lock.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
					"Error", "Error while trying to checkout the document - " + e.getMessage());
		}
	}
	
	private Action checkout = new Action("Checkout document") {
		@Override
		public void run() {
			Object selection = ((IStructuredSelection)tree.getSelection()).getFirstElement();
			if(selection == null){
				MessageDialog.openError(Display.getDefault().getActiveShell(), 
						"Error", "Please select the file to checkout first.");
				return;
			}
			if(selection instanceof DocumentVersion){
				GedDocument doc = ((DocumentVersion)selection).getParent();
				checkout(doc);
			}
			else if(selection instanceof GedDocument){
				GedDocument doc = (GedDocument)selection;
				checkout(doc);
			}
		}
	};
	
	private Action checkin = new Action("Checkin document") {
		@Override
		public void run() {
			Object selection = ((IStructuredSelection)tree.getSelection()).getFirstElement();
			if(selection == null){
				MessageDialog.openError(Display.getDefault().getActiveShell(), 
						"Error", "Please select the file to checkin first.");
				return;
			}
			if(selection instanceof DocumentVersion){
				GedDocument doc = ((DocumentVersion)selection).getParent();
				checkin(doc);
			}
			else if(selection instanceof GedDocument){
				GedDocument doc = (GedDocument)selection;
				checkin(doc);
			}
		}
	};
	
	private Action indexDocAction = new Action("Index document") {
		@Override
		public void run() {
			Object selection = ((IStructuredSelection)tree.getSelection()).getFirstElement();
			if(selection == null){
				MessageDialog.openError(Display.getDefault().getActiveShell(), 
						"Error", "Please select the file to checkin first.");
				return;
			}
			if(selection instanceof DocumentVersion){
				try {
					model.indexVersion((DocumentVersion)selection);
					model.refresh();
					refreshInput();
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Error while trying to index the document - " + e.getMessage());
				}
			}
		}
	};
	
}

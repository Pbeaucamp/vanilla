package bpm.metadata.ui.birtreport.wizards;

import java.util.ArrayList;
import java.util.List;

import metadataclient.Activator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;

import bpm.metadata.MetaData;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.scripting.Script;
import bpm.metadata.scripting.Variable;
import bpm.metadata.ui.birtreport.trees.TreeBusinessTable;
import bpm.metadata.ui.birtreport.trees.TreeDataStreamElement;
import bpm.metadata.ui.birtreport.trees.TreeModel;
import bpm.metadata.ui.birtreport.trees.TreeObject;
import bpm.metadata.ui.birtreport.trees.TreePackage;
import bpm.metadata.ui.birtreport.trees.TreeParent;
import bpm.metadata.ui.birtreport.trees.TreeResource;
import bpm.metadata.ui.birtreport.viewer.TreeContentProvider;
import bpm.metadata.ui.birtreport.viewer.TreeLabelProvider;

public class BirtReportPage1 extends WizardPage{
	
	private MetaData model;
	private TreePackage selectedPack;
	
	private TreeViewer viewer;
	private Button putGroup, removeGroup, putColumn, removeColumn, putRessources, removeRessources;
	private ListViewer groupViewer, columnViewer, ressourcesViewer;
	
	private String groupName = "System";

	protected BirtReportPage1(String pageName, MetaData m) {
		super(pageName);
		this.model = m;
	}

	@Override
	public void createControl(Composite parent) {
		// create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(3, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(false);
		
	}
	
	private void createPageContent(Composite parent){
		viewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new FMDTTreeLabelProvider());
		
		Composite groupButtonComposite = new Composite(parent, SWT.NONE);
		groupButtonComposite.setLayout(new GridLayout());
		groupButtonComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		putGroup = new Button(groupButtonComposite, SWT.NONE);
		putGroup.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		putGroup.setText(">>");
		putGroup.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object[] groupSelected = viewer.getTree().getSelection();
			}
		});
		
		removeGroup = new Button(groupButtonComposite, SWT.NONE);
		removeGroup.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		removeGroup.setText("<<");
		
		Composite groupViewerComposite = new Composite(parent, SWT.NONE);
		groupViewerComposite.setLayout(new GridLayout());
		groupViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label groupViewerName = new Label(groupViewerComposite, SWT.NONE);
		groupViewerName.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		groupViewerName.setText("Groups");
		
		groupViewer = new ListViewer(groupViewerComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		groupViewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		groupViewer.setContentProvider(new ItemContentProvider());
		groupViewer.setLabelProvider(new ItemLabelProvider());
		
		Composite columnButtonComposite = new Composite(parent, SWT.NONE);
		columnButtonComposite.setLayout(new GridLayout());
		columnButtonComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		putColumn = new Button(columnButtonComposite, SWT.NONE);
		putColumn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		putColumn.setText(">>");
		
		removeColumn = new Button(columnButtonComposite, SWT.NONE);
		removeColumn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		removeColumn.setText("<<");
		
		Composite columnViewerComposite = new Composite(parent, SWT.NONE);
		columnViewerComposite.setLayout(new GridLayout());
		columnViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label columnViewerName = new Label(columnViewerComposite, SWT.NONE);
		columnViewerName.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		columnViewerName.setText("Details");

		columnViewer = new ListViewer(columnViewerComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		columnViewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		columnViewer.setContentProvider(new ItemContentProvider());
		columnViewer.setLabelProvider(new ItemLabelProvider());
		
		Composite ressourcesButtonComposite = new Composite(parent, SWT.NONE);
		ressourcesButtonComposite.setLayout(new GridLayout());
		ressourcesButtonComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		putRessources = new Button(ressourcesButtonComposite, SWT.NONE);
		putRessources.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		putRessources.setText(">>");
		
		removeRessources = new Button(ressourcesButtonComposite, SWT.NONE);
		removeRessources.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		removeRessources.setText("<<");
		
		Composite ressourcesViewerComposite = new Composite(parent, SWT.NONE);
		ressourcesViewerComposite.setLayout(new GridLayout());
		ressourcesViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label ressourcesViewerName = new Label(ressourcesViewerComposite, SWT.NONE);
		ressourcesViewerName.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		ressourcesViewerName.setText("Resources");
		
		ressourcesViewer = new ListViewer(ressourcesViewerComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		ressourcesViewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		ressourcesViewer.setContentProvider(new ItemContentProvider());
		ressourcesViewer.setLabelProvider(new ItemLabelProvider());
		
		fillTree();
		addButtonListener();
	}
	
	private void fillTree(){		
		TreeParent root = new TreeParent(""); //$NON-NLS-1$		
		
		TreeParent models = new TreeParent("Business Models"); //$NON-NLS-1$
		root.addChild(models);
		for(IBusinessModel m : model.getBusinessModels()){
			models.addChild(new TreeModel(m, "none")); //$NON-NLS-1$
		}
		viewer.setInput(root);
	}
	
	private void addButtonListener(){
		putGroup.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				//We set the groups already selected
				List<TreeDataStreamElement> selectedCol = (List<TreeDataStreamElement>)groupViewer.getInput();
				if(selectedCol == null){
					selectedCol = new ArrayList<TreeDataStreamElement>();
				}
				
				//We set the columns already selected
				List<TreeDataStreamElement> columnItems = (List<TreeDataStreamElement>)columnViewer.getInput();
				
				Boolean found = false;
				Boolean foundColumn = false;
				Boolean isDifferentTable = false;	

				for(TreeItem tr : viewer.getTree().getSelection()){
					if(tr.getData() instanceof TreeBusinessTable){
						List<TreeDataStreamElement> allElements = new ArrayList<TreeDataStreamElement>();
						allElements = addEntireTable(allElements, ((TreeBusinessTable)tr.getData()), groupName);
						for(TreeDataStreamElement trData : allElements){
							TreePackage pack = (TreePackage)findPackage(trData.getParent());
							for(TreeDataStreamElement gr : selectedCol){
								TreePackage packTmp = (TreePackage)findPackage(gr.getParent());
								if(!pack.getName().equals(packTmp.getName())){
									isDifferentTable = true;
									break;
								}
								if(gr.getName().equals(trData.getName())){
									found = true;
									break;
								}
							}
							if(columnItems != null){
								for(TreeDataStreamElement col : columnItems){
									TreePackage packTmp = (TreePackage)findPackage(col.getParent());
									if(!pack.getName().equals(packTmp.getName())){
										isDifferentTable = true;
										break;
									}
									else if(col.getName().equals(trData.getName())){
										foundColumn = true;
										break;
									}
								}
							}
							if(!found && !foundColumn && !isDifferentTable){
								selectedCol.add(trData);
								found = false;
								foundColumn = false;
								isDifferentTable = false;
							}
							else{
								found = false;
								foundColumn = false;
								isDifferentTable = false;
							}
						}
					}
					else if(tr.getData() instanceof TreeDataStreamElement){
						String name = ((TreeDataStreamElement)tr.getData()).getName();
						TreePackage pack = (TreePackage)findPackage(((TreeDataStreamElement)tr.getData()).getParent());
						for(TreeDataStreamElement tdData : selectedCol){
							TreePackage packTmp = (TreePackage)findPackage(tdData.getParent());
							if(!pack.getName().equals(packTmp.getName())){
								isDifferentTable = true;
								break;
							}
							else if(tdData.getName().equals(name)){
								found = true;
								break;
							}
						}
						if(columnItems != null){
							for(TreeDataStreamElement col : columnItems){
								TreePackage packTmp = (TreePackage)findPackage(col.getParent());
								if(!pack.getName().equals(packTmp.getName())){
									isDifferentTable = true;
									break;
								}
								else if(col.getName().equals(name)){
									foundColumn = true;
									break;
								}
							}
						}
						if(!found && !foundColumn && !isDifferentTable){
							selectedCol.add((TreeDataStreamElement)tr.getData());
							found = false;
							foundColumn = false;
							isDifferentTable = false;
						}
						else{
							found = false;
							foundColumn = false;
							isDifferentTable = false;
						}
					}
				}
				groupViewer.setInput(selectedCol);
				checkIsPageComplete();
			}
		});
		removeGroup.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<TreeDataStreamElement> selectedCol = (List<TreeDataStreamElement>)groupViewer.getInput();
				for(String str : groupViewer.getList().getSelection()){
					int i=0;
					for(TreeDataStreamElement tdData : selectedCol){
						if(tdData.getName().equals(str)){
							selectedCol.remove(i);
							break;
						}
						i++;
					}
				}
				groupViewer.setInput(selectedCol);
				checkIsPageComplete();
			}
		});
		
		putColumn.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				//We set the groups already selected
				List<TreeDataStreamElement> selectedCol = (List<TreeDataStreamElement>)columnViewer.getInput();
				if(selectedCol == null){
					selectedCol = new ArrayList<TreeDataStreamElement>();
				}
				
				//We set the columns already selected
				List<TreeDataStreamElement> groupItems = (List<TreeDataStreamElement>)groupViewer.getInput();
				
				Boolean found = false;
				Boolean foundColumn = false;
				Boolean isDifferentTable = false;	

				for(TreeItem tr : viewer.getTree().getSelection()){
					if(tr.getData() instanceof TreeBusinessTable){
						List<TreeDataStreamElement> allElements = new ArrayList<TreeDataStreamElement>();
						allElements = addEntireTable(allElements, ((TreeBusinessTable)tr.getData()), groupName);
						for(TreeDataStreamElement trData : allElements){
							selectedPack = (TreePackage)findPackage(trData.getParent());
							for(TreeDataStreamElement gr : selectedCol){
								TreePackage packTmp = (TreePackage)findPackage(gr.getParent());
								if(!selectedPack.getName().equals(packTmp.getName())){
									isDifferentTable = true;
									break;
								}
								if(gr.getName().equals(trData.getName())){
									found = true;
									break;
								}
							}
							if(groupItems != null){
								for(TreeDataStreamElement col : groupItems){
									TreePackage packTmp = (TreePackage)findPackage(col.getParent());
									if(!selectedPack.getName().equals(packTmp.getName())){
										isDifferentTable = true;
										break;
									}
									else if(col.getName().equals(trData.getName())){
										foundColumn = true;
										break;
									}
								}
							}
							if(!found && !foundColumn && !isDifferentTable){
								selectedCol.add(trData);
								found = false;
								foundColumn = false;
								isDifferentTable = false;
							}
							else{
								found = false;
								foundColumn = false;
								isDifferentTable = false;
							}
						}
					}
					else if(tr.getData() instanceof TreeDataStreamElement){
						String name = ((TreeDataStreamElement)tr.getData()).getName();
						selectedPack = (TreePackage)findPackage(((TreeDataStreamElement)tr.getData()).getParent());
						for(TreeDataStreamElement tdData : selectedCol){
							TreePackage packTmp = (TreePackage)findPackage(tdData.getParent());
							if(!selectedPack.getName().equals(packTmp.getName())){
								isDifferentTable = true;
								break;
							}
							else if(tdData.getName().equals(name)){
								found = true;
								break;
							}
						}
						if(groupItems != null){
							for(TreeDataStreamElement col : groupItems){
								TreePackage packTmp = (TreePackage)findPackage(col.getParent());
								if(!selectedPack.getName().equals(packTmp.getName())){
									isDifferentTable = true;
									break;
								}
								else if(col.getName().equals(name)){
									foundColumn = true;
									break;
								}
							}
						}
						if(!found && !foundColumn && !isDifferentTable){
							selectedCol.add((TreeDataStreamElement)tr.getData());
							found = false;
							foundColumn = false;
							isDifferentTable = false;
						}
						else{
							found = false;
							foundColumn = false;
							isDifferentTable = false;
						}
					}
				}
				columnViewer.setInput(selectedCol);
				checkIsPageComplete();
			}
		});
		removeColumn.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<TreeDataStreamElement> selectedCol = (List<TreeDataStreamElement>)columnViewer.getInput();
				for(String str : columnViewer.getList().getSelection()){
					int i=0;
					for(TreeDataStreamElement tdData : selectedCol){
						if(tdData.getName().equals(str)){
							selectedCol.remove(i);
							break;
						}
						i++;
					}
				}
				columnViewer.setInput(selectedCol);
				checkIsPageComplete();
			}
		});
		
		putRessources.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				//We set the groups already selected
				List<TreeResource> selectedRes = (List<TreeResource>)ressourcesViewer.getInput();
				if(selectedRes == null){
					selectedRes = new ArrayList<TreeResource>();
				}
				
				Boolean found = false;
				Boolean isDifferentTable = false;	

				for(TreeItem tr : viewer.getTree().getSelection()){
					if(tr.getData() instanceof TreeResource){
						TreeResource ressource = (TreeResource)tr.getData();
						
						TreePackage pack = (TreePackage)findPackage(ressource.getParent());
						for(TreeResource tdData : selectedRes){
							TreePackage packTmp = (TreePackage)findPackage(tdData.getParent());
							if(!pack.getName().equals(packTmp.getName())){
								isDifferentTable = true;
								break;
							}
							else if(tdData.getName().equals(ressource.getName())){
								found = true;
								break;
							}
						}
						if(!found && !isDifferentTable){
							selectedRes.add(ressource);
							found = false;
							isDifferentTable = false;
						}
						else{
							found = false;
							isDifferentTable = false;
						}
					}
				}
				ressourcesViewer.setInput(selectedRes);
				checkIsPageComplete();
			}
		});
		removeRessources.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<TreeResource> selectedRes = (List<TreeResource>)ressourcesViewer.getInput();
				for(String str : ressourcesViewer.getList().getSelection()){
					int i=0;
					for(TreeResource res : selectedRes){
						if(res.getName().equals(str)){
							selectedRes.remove(i);
							break;
						}
						i++;
					}
				}
				ressourcesViewer.setInput(selectedRes);
				checkIsPageComplete();
			}
		});
	}
	
	public List<TreeDataStreamElement> getGroupViewerInput(){
		if((List<TreeDataStreamElement>)groupViewer.getInput() != null)
			return (List<TreeDataStreamElement>)groupViewer.getInput();
		else
			return new ArrayList<TreeDataStreamElement>();
	}
	
	public List<TreeDataStreamElement> getColumnViewerInput(){
		if((List<TreeDataStreamElement>)columnViewer.getInput() != null)
			return (List<TreeDataStreamElement>)columnViewer.getInput();
		else
			return new ArrayList<TreeDataStreamElement>();
	}
	
	public List<TreeResource> getRessourcesViewerInput(){
		if((List<TreeResource>)ressourcesViewer.getInput() != null)
			return (List<TreeResource>)ressourcesViewer.getInput();
		else
			return new ArrayList<TreeResource>();
	}
	
	public String getBusinessPackageName(){
		if(selectedPack != null){
			return selectedPack.getName();
		}
		else{
			return "";
		}
	}
	
	public String getBusinessModelName(){
		if(selectedPack != null){
			TreeModel tModel = (TreeModel)findModel(selectedPack.getParent());
			return tModel.getName();
		}
		return "";
	}
	
	public void checkIsPageComplete(){
		if((groupViewer.getInput() == null || ((List<TreeDataStreamElement>) groupViewer.getInput()).isEmpty())
				&& (columnViewer.getInput() == null || ((List<TreeDataStreamElement>) columnViewer.getInput()).isEmpty())){
			setPageComplete(false);
		}
		else{
			setPageComplete(true);
		}
	}
	
	private List<TreeDataStreamElement> addEntireTable(List<TreeDataStreamElement> elements, TreeBusinessTable table, String groupName){
		for(TreeObject tb : table.getChildren()){
			if(tb instanceof TreeBusinessTable){
				return addEntireTable(elements, (TreeBusinessTable)tb, groupName);
			}
			else if(tb instanceof TreeDataStreamElement){
				elements.add((TreeDataStreamElement)tb);
			}
		}
		return elements;
	}
	
	private TreeParent findPackage(TreeParent tr){
		Boolean foundPackParent = false;
		while(!foundPackParent){
			if(tr instanceof TreePackage){
				foundPackParent = true;
			}
			else{
				return findPackage(tr.getParent());
			}
		}
		return tr;
	}
	
	private TreeParent findModel(TreeParent tr){
		Boolean foundModelParent = false;
		while(!foundModelParent){
			if(tr instanceof TreeModel){
				foundModelParent = true;
			}
			else{
				return findModel(tr.getParent());
			}
		}
		return tr;
	}
	
	protected class FMDTTreeLabelProvider extends  TreeLabelProvider{
		@Override
		public String getText(Object obj) {
			if (obj instanceof Script){
				return ((Script)obj).getName();
			}
			else if (obj instanceof Variable){
				return ((Variable)obj).getName();
			}
			return super.getText(obj);
		}
		
		@Override
		public Image getImage(Object obj) {
			if (obj instanceof Script){
				return Activator.getDefault().getImageRegistry().get("script"); //$NON-NLS-1$
			}
			else if (obj instanceof Variable){
				return Activator.getDefault().getImageRegistry().get("variable"); //$NON-NLS-1$
			}
			return super.getImage(obj);
		}
	}
	
	class ItemLabelProvider implements ILabelProvider {
		
		public String getText(Object arg0) {
			  return arg0.toString();
		}
		  
		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}
		  
		@Override
		public void addListener(ILabelProviderListener listener) {
			
			
		}
		
		@Override
		public void removeListener(ILabelProviderListener listener) {
			
			
		}

		@Override
		public Image getImage(Object element) {
			
			return null;
		}

		@Override
		public void dispose() {
			
			
		}
	}

	class ItemContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object arg0) {
			List<TreeDataStreamElement> l = (ArrayList<TreeDataStreamElement>)arg0; 
			return l.toArray(new TreeObject[l.size()]);
		}

		@Override
		public void dispose() {
			
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			
		}
	}
}

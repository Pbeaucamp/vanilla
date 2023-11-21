package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.trees.TreeBusinessTable;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeFilter;
import metadata.client.trees.TreeParent;
import metadata.client.trees.TreePrompt;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;
import metadataclient.Activator;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.Ordonable;
import bpm.metadata.query.UnitedOlapQuery;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IVanillaContext;

public class DialogOlapQueryBuilder extends Dialog {

	
	private IBusinessPackage pack;
	private TreeViewer viewer;
	private ListViewer selectViewer;
	private ListViewer filterViewer;
	
	private Text queryText;
	
	private boolean queryModified = false;
	
	private Button run;
	private Button query;
	
	private HashMap<Prompt, String> selectedPromptsValues = new HashMap<Prompt, String>();
	
	private Button btnHideNull;
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
	}


	public DialogOlapQueryBuilder(Shell parentShell, IBusinessPackage pack) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.pack = pack;
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TreeViewer(container, SWT.BORDER);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		
		//Buttons
		Composite bar = new Composite(container, SWT.NONE);
		bar.setLayout(new GridLayout());
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 4));
		
		Button b = new Button(bar, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b.setText("Add select"); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				if (ss.isEmpty()){
					return ;
				}
				
				for(Object o : ss.toList()){
					if (o instanceof TreeDataStreamElement){
						List<IDataStreamElement> l = (List<IDataStreamElement>)selectViewer.getInput();
						IDataStreamElement elem = ((TreeDataStreamElement)o).getDataStreamElement();
						if(!l.contains(elem)) {
							l.add(elem);
							selectViewer.refresh();
						}
					}
				}
				
				updateButtonStates();
			}
			
		});
	
		
		
		Button b1 = new Button(bar, SWT.PUSH);
		b1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b1.setText("Add filter"); //$NON-NLS-1$
		b1.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				boolean existingFilter = false;
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if(!ss.isEmpty()) {
					if(ss.getFirstElement() instanceof TreeFilter) {
						List<IResource> l = (List<IResource>)filterViewer.getInput();
						if(!l.contains(((TreeFilter)ss.getFirstElement()).getFilter())) {
							l.add(((TreeFilter)ss.getFirstElement()).getFilter());
							existingFilter = true;
							filterViewer.refresh();
						}
					}
					else if(ss.getFirstElement() instanceof TreePrompt) {
						List<IResource> l = (List<IResource>)filterViewer.getInput();
						if(!l.contains(((TreePrompt)ss.getFirstElement()).getPrompt())) {
							l.add(((TreePrompt)ss.getFirstElement()).getPrompt());
							existingFilter = true;
							InputDialog d = new InputDialog(getShell(),Messages.DialogOlapQueryBuilder_7, ((TreePrompt)ss.getFirstElement()).getPrompt().getQuestion(), "", null); //$NON-NLS-1$ //$NON-NLS-2$
							if (d.open() == InputDialog.OK){
								selectedPromptsValues.put(((TreePrompt)ss.getFirstElement()).getPrompt(), d.getValue());
							}
							filterViewer.refresh();
						}
						
					}
				}
				
				if(!existingFilter) {
					DialogFilter dial = new DialogFilter(getShell(), true);
					if(dial.open() == Dialog.OK) {
						IFilter filter = dial.getFilter();
						List<IResource> l = (List<IResource>)filterViewer.getInput();
						l.add(filter);
						filterViewer.refresh();
					}
				}
				
			}
			
		});

		
		Button del = new Button(bar, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		del.setText(Messages.DialogOlapQueryBuilder_2); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = null;
				boolean select = false;
				if(!selectViewer.getSelection().isEmpty()) {
					ss = (IStructuredSelection) selectViewer.getSelection();
					select = true;
				}
				else if(!filterViewer.getSelection().isEmpty()) {
					ss = (IStructuredSelection) filterViewer.getSelection();
				}
							
				for(Object o : ss.toList()){
					if(select) {
						((List)selectViewer.getInput()).remove(o);
					}
					
					else {
						((List)filterViewer.getInput()).remove(o);
					}
				}
				selectViewer.refresh();
				filterViewer.refresh();
				
				updateButtonStates();
			}
			
		});
		
		Composite c = new Composite(container, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
		
		
		
		Label l0 = new Label(c, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText("Selects"); //$NON-NLS-1$
		
		selectViewer = new ListViewer(c, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		selectViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		selectViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if (element instanceof IDataStreamElement){
					return ((IDataStreamElement)element).getName();
				}
				return null;
			}
		});
		selectViewer.setContentProvider(new IStructuredContentProvider(){


			public Object[] getElements(Object inputElement) {
				List<IDataStreamElement> l = (List<IDataStreamElement>)inputElement;
				return l.toArray(new IDataStreamElement[l.size()]);
			}


			public void dispose() {
				
			}


			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});

		Label l1 = new Label(c, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText("Filters"); //$NON-NLS-1$

		
		filterViewer = new ListViewer(c, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		filterViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		filterViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if (element instanceof IResource){
					return ((IResource)element).getName();
				}
				return null;
			}
		});
		filterViewer.setContentProvider(new IStructuredContentProvider(){


			public Object[] getElements(Object inputElement) {
				List<IResource> l = (List<IResource>)inputElement;
				return l.toArray(new IResource[l.size()]);
			}


			public void dispose() {
				
			}


			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		
		btnHideNull = new Button(container, SWT.CHECK);
		btnHideNull.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1));
		btnHideNull.setText("Hide null values"); //$NON-NLS-1$
		
		query = new Button(container, SWT.PUSH);
		query.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		query.setText(Messages.DialogOlapQueryBuilder_5); //$NON-NLS-1$
		query.addSelectionListener(new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				
				List<IDataStreamElement> selects = (List<IDataStreamElement>) selectViewer.getInput();
				
				List<IResource> ress = (List<IResource>) filterViewer.getInput();
				List<IFilter> wheres = new ArrayList<IFilter>();
				List<Prompt> prompts = new ArrayList<Prompt>();
				if(ress != null) {
					for(IResource res : ress) {
						if(res instanceof IFilter) {
							wheres.add((IFilter) res);
						}
						else {
							prompts.add((Prompt) res);
						}
					}
				}

				IQuery query = new UnitedOlapQuery(selects, wheres, prompts, Activator.getDefault().getRepositoryContext().getGroup().getName(), btnHideNull.getSelection());
				try {
					List<List<String>> prVals = new ArrayList<List<String>>();
					if(prompts != null && prompts.size() > 0) {
						for(Prompt pr : prompts) {
							List<String> s = new ArrayList<String>();
							s.add(selectedPromptsValues.get(pr));
							prVals.add(s);
						}
					}
					IVanillaContext ctx = null;
					
					try{
						ctx = Activator.getDefault().getVanillaContext();
					}catch(Exception ex){
						Logger.getLogger(getClass()).warn(Messages.DialogOlapQueryBuilder_0);
					}
					String q = pack.getQuery(null, ctx, query,  prVals);
					
					queryText.setText(q);
				} catch (Throwable ex) {
					Activator.getLogger().error(Messages.DialogOlapQueryBuilder_7, ex); //$NON-NLS-1$
					ex.printStackTrace();
					
					MessageDialog.openError(getShell(), Messages.DialogOlapQueryBuilder_8, ex.getMessage()); //$NON-NLS-1$
				}
			}
			
		});
		
		
		queryText = new Text(container, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		queryText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		queryText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				queryModified =true;
				
			}
			
		});
		
		
		run = new Button(container, SWT.PUSH);
		run.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		run.setText(Messages.DialogOlapQueryBuilder_9); //$NON-NLS-1$
		run.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<IDataStreamElement> selects = (List<IDataStreamElement>) selectViewer.getInput();
				List<IResource> ress = (List<IResource>) filterViewer.getInput();
				List<IFilter> wheres = new ArrayList<IFilter>();
				List<Prompt> prompts = new ArrayList<Prompt>();
				if(ress != null) {
					for(IResource res : ress) {
						if(res instanceof IFilter) {
							wheres.add((IFilter) res);
						}
						else {
							prompts.add((Prompt) res);
						}
					}
				}
				UnitedOlapQuery query = new UnitedOlapQuery(selects, wheres, prompts, Activator.getDefault().getRepositoryContext().getGroup().getName(), btnHideNull.getSelection());
				
				try {
					List<List<String>>  l = null;
					
					List<List<String>> prVals = new ArrayList<List<String>>();
					if(prompts != null && prompts.size() > 0) {
						for(Prompt pr : prompts) {
							List<String> s = new ArrayList<String>();
							s.add(selectedPromptsValues.get(pr));
							prVals.add(s);
						}
					}
					
					
					IVanillaContext ctx = null;
					
					try{
						ctx = Activator.getDefault().getVanillaContext();
					}catch(Exception ex){
						Logger.getLogger(getClass()).warn(Messages.DialogOlapQueryBuilder_1);
					}
					
					if (queryModified){
						l = pack.executeQuery(null, ctx, "", query, prVals); //$NON-NLS-1$
						queryModified = false;
					}
					else{

						l = pack.executeQuery(null, ctx, "", query, prVals); //$NON-NLS-1$
					}
					
					List<Ordonable> o = new ArrayList<Ordonable>();
					for(IDataStreamElement el : query.getSelect()) {
						o.add(el);
					}
					List<AggregateFormula> aggs = new ArrayList<AggregateFormula>();
					DialogBrowse dial = new DialogBrowse(getShell(), l, o, aggs);
					dial.open();
				} catch (Throwable ex) {
					Activator.getLogger().error(Messages.DialogOlapQueryBuilder_12, ex); //$NON-NLS-1$
					ex.printStackTrace();
					
					MessageDialog.openError(getShell(), Messages.DialogOlapQueryBuilder_13, ex.getMessage()); //$NON-NLS-1$
				}
			}
			
		});

		
		
		fillDatas();
		setLoD();
		return parent;
	}
	
	private void fillDatas(){
		
		
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		for(IBusinessTable t : pack.getBusinessTables("none")){ //$NON-NLS-1$
			root.addChild(new TreeBusinessTable(t, "none")); //$NON-NLS-1$
		}
		
		for(IResource r : pack.getResources()){
			if(r instanceof Filter) {
				root.addChild(new TreeFilter((Filter) r));
			}
			if(r instanceof Prompt) {
				root.addChild(new TreePrompt((Prompt) r));
			}
		}
		
		viewer.setInput(root);
		selectViewer.setInput(new ArrayList<IDataStreamElement>());
		filterViewer.setInput(new ArrayList<IFilter>());
		
		updateButtonStates();
	}
	
	private void setLoD(){
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				
			}
			
		});
	}

	private void updateButtonStates() {
		List<IDataStreamElement> selects = (List<IDataStreamElement>) selectViewer.getInput();
		
		int nbLevel = 0;
		int nbMeasure = 0;
		
		for(IDataStreamElement elem : selects) {
			if(elem.getOrigin().getName().startsWith("[Measures]")) { //$NON-NLS-1$
				nbMeasure++;
			}
			else {
				nbLevel++;
			}
		}
		
		if(nbLevel > 0 && nbMeasure > 0) {
			query.setEnabled(true);
			run.setEnabled(true);
		}
		else {
			query.setEnabled(false);
			run.setEnabled(false);
		}
	}
}

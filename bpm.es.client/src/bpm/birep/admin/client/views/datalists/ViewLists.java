package bpm.birep.admin.client.views.datalists;

import java.util.Calendar;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import adminbirep.icons.Icons;
import bpm.birep.admin.client.views.datalists.dialogs.DialogResultSetBrowser;
import bpm.birep.admin.client.views.datalists.wizards.oda.OdaDataSetWizard;
import bpm.birep.admin.client.views.datalists.wizards.oda.OdaDataSourceWizard;
import bpm.dataprovider.odainput.OdaInputDigester;
import bpm.dataprovider.odainput.consumer.OdaHelper;
import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.core.repository.DatasProvider;


public class ViewLists extends ViewPart {
	public static final String ID = "bpm.birep.admin.client.views.datalists.ViewLists"; //$NON-NLS-1$
	
	private TableViewer listViewer;
	
	private ToolItem add, del, edit, browse, refresh;
	
	public ViewLists() { }
	
	protected IRepositoryApi getRepApi(){
		return Activator.getDefault().getRepositoryApi();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		createToolbar(main);
		createDatasProviderViewer(main);

	}

	@Override
	public void setFocus() { }
	
	private void createDatasProviderViewer(Composite parent) {
		listViewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		listViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		listViewer.setContentProvider(new IStructuredContentProvider(){

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<DatasProvider> l = (List<DatasProvider>) inputElement;
				return l.toArray(new DatasProvider[l.size()]);
			}

			@Override
			public void dispose() {
				
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
				
		
		TableViewerColumn name = new TableViewerColumn(listViewer, SWT.NONE);
		name.getColumn().setText(Messages.Client_Views_ViewLists_3);
		name.getColumn().setWidth(200);
		name.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((DatasProvider)element).getName();
			}
		});
		
		TableViewerColumn description = new TableViewerColumn(listViewer, SWT.NONE);
		description.getColumn().setText(Messages.Client_Views_ViewLists_4);
		description.getColumn().setWidth(200);
		description.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((DatasProvider)element).getDescription();
			}
		});
		
				
		
		listViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		listViewer.getTable().setHeaderVisible(true);
		listViewer.getTable().setLinesVisible(true);
		
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				boolean empty = listViewer.getSelection().isEmpty();
				edit.setEnabled(!empty);
				del.setEnabled(!empty);
				browse.setEnabled(!empty);
				
			}
		});
		try {
			listViewer.setInput(getRepApi().getDatasProviderService().getAll());
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getSite().getShell(), Messages.Client_Views_ViewLists_5, Messages.Client_Views_ViewLists_6 + e.getMessage());
		} 
	
	}
	
	private void createToolbar(Composite main){
		ToolBar bar = new ToolBar(main, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		add = new ToolItem(bar, SWT.NONE);
		add.setToolTipText(Messages.Client_Views_ViewLists_7);
		add.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					/*
					 * open DataSource edition
					 */
					OdaInput input = new OdaInput();
					
					OdaDataSourceWizard wiz = new OdaDataSourceWizard(input);
					WizardDialog dial = new WizardDialog(getSite().getShell(), wiz);
					dial.setMinimumPageSize(800, 600);
					if (dial.open() != WizardDialog.OK){
						return;
					}
					
					/*
					 * open DataSetEdition
					 */
					OdaDataSetWizard wiz2 = new OdaDataSetWizard(input);
					dial = new WizardDialog(getSite().getShell(), wiz2);
					dial.setMinimumPageSize(800, 600);
					if (dial.open() == WizardDialog.OK){
						String xml = input.getElementAsXml();
						DatasProvider dp = new DatasProvider();
						dp.setCreationDate(Calendar.getInstance().getTime());
						dp.setXml(xml);
						dp.setName(input.getName());

						int id = getRepApi().getDatasProviderService().createDatasProvider(dp);
						dp.setId(id);
						
						((List)listViewer.getInput()).add(dp);
						
						listViewer.refresh();
						
					}
				}
				catch (Exception _e) {
					_e.printStackTrace();
				}
			}
		});
		
		del = new ToolItem(bar, SWT.NONE);
		del.setToolTipText(Messages.Client_Views_ViewLists_8);
		del.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL));
		del.setEnabled(false);
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)listViewer.getSelection();
				if (ss.isEmpty()){
					return;
				}
				
				StringBuffer errors = new StringBuffer();
				
				for(Object o : ss.toList()){
					if (o instanceof DatasProvider){
						try {
							getRepApi().getDatasProviderService().delete((DatasProvider)o);
							((List)listViewer.getInput()).remove(o);
						} catch (Exception e1) {
							e1.printStackTrace();
							errors.append(Messages.Client_Views_ViewLists_9 + ((DatasProvider)o).getName() + " : " + e1.getMessage() + "\n") ;  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
							
						}
					}
				}
				
				
				
				listViewer.refresh();
				
				if (errors.length() > 0){
					MessageDialog.openError(getSite().getShell(), Messages.Client_Views_ViewLists_12, errors.toString());
				}
				super.widgetSelected(e);
			}
		});
		
		edit = new ToolItem(bar, SWT.NONE);
		edit.setToolTipText(Messages.Client_Views_ViewLists_13);
		edit.setEnabled(false);
		edit.setImage(Activator.getDefault().getImageRegistry().get(Icons.EDIT));
		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DatasProvider definition = (DatasProvider)((IStructuredSelection)listViewer.getSelection()).getFirstElement();
				
				OdaInputDigester dig = null;
				try {
					dig = new OdaInputDigester(definition.getXmlDataSourceDefinition());
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.Client_Views_ViewLists_14, Messages.Client_Views_ViewLists_15 + e1.getMessage());
					return;
				} 
				OdaInput input = dig.getOdaInput();
				
				try{
					OdaDataSetWizard wiz2 = new OdaDataSetWizard(input);
					WizardDialog dial = new WizardDialog(getSite().getShell(), wiz2);
					dial.setMinimumPageSize(800, 600);
					if (dial.open() == WizardDialog.OK){
						String xml = input.getElementAsXml();
						definition.setXml(xml);
						getRepApi().getDatasProviderService().update(definition);
						
						listViewer.refresh();
					}
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.Client_Views_ViewLists_16, Messages.Client_Views_ViewLists_17 + ex.getMessage());
					return;
				}
				
			}
		});
		
		browse = new ToolItem(bar, SWT.NONE);
		browse.setToolTipText(Messages.Client_Views_ViewLists_18);
		browse.setEnabled(false);
		browse.setImage(Activator.getDefault().getImageRegistry().get(Icons.BROWSE));
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DatasProvider definition = (DatasProvider)((IStructuredSelection)listViewer.getSelection()).getFirstElement();
				
				OdaInputDigester dig = null;
				try {
					dig = new OdaInputDigester(definition.getXmlDataSourceDefinition());
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.Client_Views_ViewLists_19, Messages.Client_Views_ViewLists_20 + e1.getMessage());
					return;
				} 
				OdaInput input = dig.getOdaInput();
				
				try {
					
					IQuery query = QueryHelper.buildquery(input);
					List<List<Object>> values = OdaHelper.getValues(input, -1);
					
					DialogResultSetBrowser d = new DialogResultSetBrowser(getSite().getShell(), query.getMetaData(), values);
					d.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		refresh = new ToolItem(bar, SWT.NONE);
		refresh.setToolTipText(Messages.Client_Views_ViewLists_21);
		refresh.setImage(Activator.getDefault().getImageRegistry().get(Icons.REFRESH));
		refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					listViewer.setInput(getRepApi().getDatasProviderService().getAll());
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.Client_Views_ViewLists_22, Messages.Client_Views_ViewLists_23 + ex.getMessage());
				} 
			}
		});
		
	}

}

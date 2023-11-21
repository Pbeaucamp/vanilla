package bpm.es.gedmanager.views.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import bpm.es.gedmanager.Activator;
import bpm.es.gedmanager.Messages;
import bpm.es.gedmanager.api.GedModel;
import bpm.es.gedmanager.dialogs.DialogAddDefinition;
import bpm.es.gedmanager.icons.IconsNames;
import bpm.vanilla.platform.core.beans.ged.Definition;

public class GedFieldsComposite {
	private Composite parent;
	
	private TableViewer fieldViewer;

	private GedModel model;

	public GedFieldsComposite(Composite par, GedModel mod) {
		this.parent = par;
		this.model = mod;
		
		Composite buttonBar = new Composite(par, SWT.NONE);
		buttonBar.setLayout(new GridLayout(2, false));
		buttonBar.setLayoutData(new GridData());
		
		Button bAdd = new Button(buttonBar, SWT.PUSH);
		bAdd.setText(Messages.GedFieldsComposite_0);
		bAdd.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.field_add));
		bAdd.setLayoutData(new GridData());
		bAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					DialogAddDefinition dial = new DialogAddDefinition(Display.getDefault().getActiveShell());
					if (dial.open() == Window.OK) {
						model.addDefinition(dial.getName());
						
						showData(model);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		Button bDel = new Button(buttonBar, SWT.PUSH);
		bDel.setText(Messages.GedFieldsComposite_1);
		bDel.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.field_delete));
		bDel.setLayoutData(new GridData());
		bDel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)fieldViewer.getSelection();
				
				if (ss.isEmpty()) {
					return;
				}
				Object selection = ss.getFirstElement();
				if (selection instanceof Definition) {
					Definition def = (Definition) selection;
					if (def.system() || def.required()) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.GedFieldsComposite_2, Messages.GedFieldsComposite_3);
					}
					else {
						try {
							model.deleteDefinition(def);
						
							showData(model);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
					}
					
				}
				else {
					return;
				}
			}
		});

		fieldViewer = new TableViewer(parent, SWT.SCROLL_LINE | SWT.BORDER | SWT.MULTI);
		createColumns(fieldViewer);
		fieldViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		fieldViewer.setContentProvider(new IStructuredContentProvider() {
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
			public void dispose() {
			}
			
			public Object[] getElements(Object inputElement) {
				List<Definition> model = (List<Definition>) inputElement;
				//model.getIFieldDefinitions()
				return model.toArray(new Definition[0]);
			}
			
		});
		
		fieldViewer.setLabelProvider(new ITableLabelProvider() {
			public void removeListener(ILabelProviderListener listener) {
			}
			
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			
			public void dispose() {
			}
			
			public void addListener(ILabelProviderListener listener) {
			}
			
			public String getColumnText(Object element, int columnIndex) {
				Definition f = (Definition) element;
				//IFieldDefinitionImpl ff;

				switch (columnIndex) {
					case 0:
						return f.getName();
					case 1:
						return "" + f.getBoostLvl(); //$NON-NLS-1$
					default:
						return ""; //$NON-NLS-1$
				}
			}
			
			public Image getColumnImage(Object element, int columnIndex) {
				//DocumentDefinition doc = (IFieldDefinition) element;
				
				switch (columnIndex) {
					case 0:
						return Activator.getDefault().getImageRegistry().get(IconsNames.field);
					default:
						return null;
				}
			}
		});
		
		try {
			fieldViewer.setInput(model.getFieldDefinitions());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void createColumns(TableViewer viewer) {

		String[] titles = { Messages.GedFieldsComposite_6, Messages.GedFieldsComposite_7};
		int[] bounds = { 150, 150};

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}


	
	public void showData(GedModel model) {
		fieldViewer.setInput(new ArrayList<Definition>());
		try {
			fieldViewer.setInput(model.getFieldDefinitions());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}

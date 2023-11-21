package bpm.fd.design.ui.component.chart;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;

public class DialogSeriesColor extends Dialog{

	private TableViewer viewer;
	private DataAggregation agg;
	
	public DialogSeriesColor(Shell parentShell, DataAggregation agg) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.agg = agg;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ListContentProvider<String>());
		
		TableViewerColumn colFunction = new TableViewerColumn(viewer, SWT.NONE);
		colFunction.getColumn().setText(Messages.DialogSeriesColor_0);
		colFunction.getColumn().setWidth(200);
		colFunction.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getBackground(java.lang.Object)
			 */
			@Override
			public Color getBackground(Object element) {
				int r = Integer.parseInt(((String)element).substring(0, 2), 16);
				int g = Integer.parseInt(((String)element).substring(2, 4), 16);
				int b = Integer.parseInt(((String)element).substring(4, 6), 16);
				
				Color c = new Color(Display.getDefault(), r, g, b);
				return c;
			}
			
		});
		colFunction.setEditingSupport(new EditingSupport(viewer){
			ColorCellEditor editor = new ColorCellEditor((Composite)viewer.getControl());
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				int r = Integer.parseInt(((String)element).substring(0, 2), 16);
				int g = Integer.parseInt(((String)element).substring(2, 4), 16);
				int b = Integer.parseInt(((String)element).substring(4, 6), 16);
				return new RGB(r,g, b);
			}

			@Override
			protected void setValue(Object element, Object value) {
				String r = Integer.toHexString(((RGB)value).red);
				if (r.length() == 1){
					r = "0" + r; //$NON-NLS-1$
				}
				String b = Integer.toHexString(((RGB)value).blue);
				if (b.length() == 1){
					b = "0" + b; //$NON-NLS-1$
				}
				String g = Integer.toHexString(((RGB)value).green);
				if (g.length() == 1){
					g = "0" + g; //$NON-NLS-1$
				}
				
				
				String s = r + g + b;
				int i = agg.getColorsCode().indexOf(element);
				agg.getColorsCode().set(i, s);
				viewer.refresh();
				Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_COMPONENT_CHANGED, null, agg);
			}
			
		});
		
		createMenu();
		
		viewer.setInput(agg.getColorsCode());
		return viewer.getControl();
	}
	
	
	private void createMenu(){
		MenuManager menuManager = new MenuManager();
		
		final Action delete = new Action(Messages.DialogSeriesColor_4){
			public void run(){
				String a = (String)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				((List<?>)viewer.getInput()).remove(a);
				viewer.refresh();
			}
		};
		
		
		menuManager.add(delete);
		menuManager.add(new Action(Messages.DialogSeriesColor_5){
			public void run(){
				String a = new String("FFFFFF"); //$NON-NLS-1$
				((List<String>)viewer.getInput()).add(a);
				
				viewer.refresh();
			}
		});
		menuManager.addMenuListener(new IMenuListener(){

			public void menuAboutToShow(IMenuManager manager) {
				if (viewer.getSelection().isEmpty()){
					delete.setEnabled(false);
					return;
				}
				delete.setEnabled(true);
				
				
			}
			
		});
		Menu m = menuManager.createContextMenu(viewer.getControl());
		
		viewer.getTable().setMenu(m);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogSeriesColor_7);
	}
	
	
}
